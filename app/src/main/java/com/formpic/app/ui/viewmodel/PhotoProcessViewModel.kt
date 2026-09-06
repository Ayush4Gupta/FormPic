package com.formpic.app.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.formpic.app.ads.AdManager
import com.formpic.app.data.model.PhotoPreset
import com.formpic.app.data.model.ProcessingPhase
import com.formpic.app.data.model.ProcessingResult
import com.formpic.app.data.repository.PresetRepository
import com.formpic.app.engine.BackgroundRemoverEngine
import com.formpic.app.engine.BitmapUtils
import com.formpic.app.engine.ExactKbCompressor
import com.formpic.app.engine.FaceDetectorEngine
import com.formpic.app.engine.StorageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel coordinating image processing pipeline, presets, local storage, and ad monetization.
 */
class PhotoProcessViewModel(application: Application) : AndroidViewModel(application) {

    private val faceDetector = FaceDetectorEngine()
    private val backgroundRemover = BackgroundRemoverEngine()

    private val _processingPhase = MutableStateFlow<ProcessingPhase>(ProcessingPhase.Idle)
    val processingPhase: StateFlow<ProcessingPhase> = _processingPhase.asStateFlow()

    private val _selectedPreset = MutableStateFlow<PhotoPreset>(PresetRepository.defaultPreset)
    val selectedPreset: StateFlow<PhotoPreset> = _selectedPreset.asStateFlow()

    private val _whiteBackgroundEnabled = MutableStateFlow<Boolean>(PresetRepository.defaultPreset.requiresWhiteBackground)
    val whiteBackgroundEnabled: StateFlow<Boolean> = _whiteBackgroundEnabled.asStateFlow()

    private val _passportSizeEnabled = MutableStateFlow<Boolean>(PresetRepository.defaultPreset.isPassportSize)
    val passportSizeEnabled: StateFlow<Boolean> = _passportSizeEnabled.asStateFlow()

    private val _lastSavedUri = MutableStateFlow<Uri?>(null)
    val lastSavedUri: StateFlow<Uri?> = _lastSavedUri.asStateFlow()

    private var rawInputBitmap: Bitmap? = null
    private var originalCroppedBitmap: Bitmap? = null
    private var whiteSegmentedBitmap: Bitmap? = null
    private var currentSourceUri: Uri? = null

    init {
        // Preload AdMob Interstitial in background
        AdManager.preloadInterstitial(application)
    }

    fun selectPreset(preset: PhotoPreset) {
        _selectedPreset.value = preset
        _whiteBackgroundEnabled.value = preset.requiresWhiteBackground
        _passportSizeEnabled.value = preset.isPassportSize
    }

    fun setWhiteBackgroundEnabled(enabled: Boolean) {
        _whiteBackgroundEnabled.value = enabled
    }

    fun setPassportSizeEnabled(enabled: Boolean) {
        _passportSizeEnabled.value = enabled
    }

    /**
     * Entry point from CameraX or Gallery selection.
     */
    fun startProcessingFromUri(uri: Uri, preset: PhotoPreset = _selectedPreset.value) {
        currentSourceUri = uri
        _selectedPreset.value = preset
        viewModelScope.launch {
            processPipeline(sourceUri = uri, sourceBitmap = null, preset = preset)
        }
    }

    fun startProcessingFromBitmap(bitmap: Bitmap, preset: PhotoPreset = _selectedPreset.value) {
        currentSourceUri = null
        _selectedPreset.value = preset
        viewModelScope.launch {
            processPipeline(sourceUri = null, sourceBitmap = bitmap, preset = preset)
        }
    }

    /**
     * Central coroutine pipeline running on Dispatchers.Default / IO:
     * Decode -> Framing / Face Detection -> Background Segmentation (if enabled) -> Exact KB Compression
     */
    private suspend fun processPipeline(
        sourceUri: Uri?,
        sourceBitmap: Bitmap?,
        preset: PhotoPreset
    ) {
        withContext(Dispatchers.Default) {
            try {
                // Step 1: Decode image safely
                _processingPhase.value = ProcessingPhase.Loading("Loading photo safely…")
                val baseBitmap = sourceBitmap ?: sourceUri?.let {
                    BitmapUtils.decodeSampledBitmapFromUri(getApplication(), it)
                } ?: run {
                    _processingPhase.value = ProcessingPhase.Failure("Could not load photo. Please select a valid JPEG or PNG.")
                    return@withContext
                }
                rawInputBitmap = baseBitmap

                // Step 2: Framing & Alignment (Bypass face detection for signatures; allow original framing)
                val detectionResult = when {
                    preset.isSignature -> {
                        _processingPhase.value = ProcessingPhase.DetectingFace("Aligning signature frame…")
                        faceDetector.cropDocumentOrSignature(baseBitmap, preset.aspectRatio)
                    }
                    _passportSizeEnabled.value -> {
                        _processingPhase.value = ProcessingPhase.DetectingFace("Centering frame & aligning face…")
                        faceDetector.processAndCrop(baseBitmap, preset.aspectRatio)
                    }
                    else -> {
                        _processingPhase.value = ProcessingPhase.DetectingFace("Preserving original photo framing…")
                        faceDetector.cropWithOriginalFraming(baseBitmap)
                    }
                }
                originalCroppedBitmap = detectionResult.croppedBitmap

                // Step 3: Background Removal & Pure White Replacement (Bypassed for signatures)
                val applyWhite = !preset.isSignature && _whiteBackgroundEnabled.value
                val activeBitmap = if (applyWhite) {
                    _processingPhase.value = ProcessingPhase.RemovingBackground("Applying clean white background…")
                    val segResult = backgroundRemover.removeAndReplaceBackgroundWithWhite(detectionResult.croppedBitmap)
                    whiteSegmentedBitmap = segResult.outputBitmap
                    segResult.outputBitmap
                } else {
                    _processingPhase.value = ProcessingPhase.RemovingBackground(
                        if (preset.isSignature) "Preserving high-contrast signature paper…" else "Preserving original background…"
                    )
                    whiteSegmentedBitmap = null
                    detectionResult.croppedBitmap
                }

                // Step 4: Intelligent Exact KB Compression
                _processingPhase.value = ProcessingPhase.Compressing("Compressing strictly under ${preset.targetMaxKb} KB…")
                val targetW = if (_passportSizeEnabled.value || preset.isSignature) preset.widthPx else activeBitmap.width
                val targetH = if (_passportSizeEnabled.value || preset.isSignature) preset.heightPx else activeBitmap.height
                val compressionOutput = ExactKbCompressor.compressToExactLimit(
                    sourceBitmap = activeBitmap,
                    targetMaxKb = preset.targetMaxKb,
                    initialWidth = targetW,
                    initialHeight = targetH
                )

                // Step 5: Success Output Assembly
                val finalResult = ProcessingResult(
                    originalUri = sourceUri,
                    originalBitmap = detectionResult.croppedBitmap,
                    segmentedBitmap = activeBitmap,
                    finalBitmap = compressionOutput.finalBitmap,
                    finalBytes = compressionOutput.byteArray,
                    finalSizeBytes = compressionOutput.exactSizeBytes,
                    targetMaxKb = preset.targetMaxKb,
                    width = compressionOutput.finalWidth,
                    height = compressionOutput.finalHeight,
                    preset = preset,
                    qualityReport = detectionResult.qualityReport
                )

                _processingPhase.value = ProcessingPhase.Success(finalResult)
            } catch (e: Exception) {
                e.printStackTrace()
                _processingPhase.value = ProcessingPhase.Failure("Processing error: ${e.localizedMessage ?: "Unexpected error"}")
            }
        }
    }

    /**
     * Swaps between Original Background and Pure White Background directly on the Result Preview screen.
     * Takes < 200 ms since face detection and cropping are already completed.
     */
    fun toggleBackgroundOnCurrentResult() {
        val currentSuccess = (_processingPhase.value as? ProcessingPhase.Success)?.result ?: return
        if (currentSuccess.preset.isSignature) return // Signatures always preserve paper background
        val original = originalCroppedBitmap ?: currentSuccess.originalBitmap ?: return
        val newWhiteState = !_whiteBackgroundEnabled.value
        _whiteBackgroundEnabled.value = newWhiteState

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                try {
                    _processingPhase.value = ProcessingPhase.Compressing(
                        if (newWhiteState) "Applying clean white background…" else "Restoring original background…"
                    )

                    val activeBitmap: Bitmap = if (newWhiteState) {
                        if (whiteSegmentedBitmap == null || whiteSegmentedBitmap?.isRecycled == true) {
                            val seg = backgroundRemover.removeAndReplaceBackgroundWithWhite(original)
                            whiteSegmentedBitmap = seg.outputBitmap
                        }
                        whiteSegmentedBitmap!!
                    } else {
                        original
                    }

                    val targetW = if (_passportSizeEnabled.value) currentSuccess.preset.widthPx else activeBitmap.width
                    val targetH = if (_passportSizeEnabled.value) currentSuccess.preset.heightPx else activeBitmap.height
                    val compressionOutput = ExactKbCompressor.compressToExactLimit(
                        sourceBitmap = activeBitmap,
                        targetMaxKb = currentSuccess.targetMaxKb,
                        initialWidth = targetW,
                        initialHeight = targetH
                    )

                    val updatedResult = currentSuccess.copy(
                        segmentedBitmap = activeBitmap,
                        finalBitmap = compressionOutput.finalBitmap,
                        finalBytes = compressionOutput.byteArray,
                        finalSizeBytes = compressionOutput.exactSizeBytes,
                        width = compressionOutput.finalWidth,
                        height = compressionOutput.finalHeight
                    )

                    _processingPhase.value = ProcessingPhase.Success(updatedResult)
                } catch (e: Exception) {
                    e.printStackTrace()
                    _processingPhase.value = ProcessingPhase.Success(currentSuccess)
                }
            }
        }
    }

    /**
     * Swaps between 3.5×4.5 cm Passport Size and Original Photo Framing live on Result Preview screen.
     */
    fun toggleFramingOnCurrentResult() {
        val currentSuccess = (_processingPhase.value as? ProcessingPhase.Success)?.result ?: return
        val base = rawInputBitmap ?: return
        if (currentSuccess.preset.isSignature) return // Signatures do not use passport framing

        val newPassportState = !_passportSizeEnabled.value
        _passportSizeEnabled.value = newPassportState

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                try {
                    _processingPhase.value = ProcessingPhase.Compressing(
                        if (newPassportState) "Applying 3.5×4.5 cm passport framing…" else "Restoring original photo framing…"
                    )

                    val detectionResult = if (newPassportState) {
                        faceDetector.processAndCrop(base, currentSuccess.preset.aspectRatio)
                    } else {
                        faceDetector.cropWithOriginalFraming(base)
                    }
                    originalCroppedBitmap = detectionResult.croppedBitmap

                    // Re-segment if white background was active
                    val applyWhite = _whiteBackgroundEnabled.value
                    val activeBitmap = if (applyWhite) {
                        val seg = backgroundRemover.removeAndReplaceBackgroundWithWhite(detectionResult.croppedBitmap)
                        whiteSegmentedBitmap = seg.outputBitmap
                        seg.outputBitmap
                    } else {
                        whiteSegmentedBitmap = null
                        detectionResult.croppedBitmap
                    }

                    val targetW = if (newPassportState) currentSuccess.preset.widthPx else activeBitmap.width
                    val targetH = if (newPassportState) currentSuccess.preset.heightPx else activeBitmap.height
                    val compressionOutput = ExactKbCompressor.compressToExactLimit(
                        sourceBitmap = activeBitmap,
                        targetMaxKb = currentSuccess.targetMaxKb,
                        initialWidth = targetW,
                        initialHeight = targetH
                    )

                    val updatedResult = currentSuccess.copy(
                        originalBitmap = detectionResult.croppedBitmap,
                        segmentedBitmap = activeBitmap,
                        finalBitmap = compressionOutput.finalBitmap,
                        finalBytes = compressionOutput.byteArray,
                        finalSizeBytes = compressionOutput.exactSizeBytes,
                        width = compressionOutput.finalWidth,
                        height = compressionOutput.finalHeight,
                        qualityReport = detectionResult.qualityReport
                    )

                    _processingPhase.value = ProcessingPhase.Success(updatedResult)
                } catch (e: Exception) {
                    e.printStackTrace()
                    _processingPhase.value = ProcessingPhase.Success(currentSuccess)
                }
            }
        }
    }

    /**
     * Triggers the policy-compliant ad experience, then reliably executes download.
     */
    fun downloadProcessedPhoto(activity: Activity, onComplete: (Uri?) -> Unit) {
        val currentSuccess = (_processingPhase.value as? ProcessingPhase.Success)?.result ?: return

        AdManager.showDownloadInterstitial(activity) {
            viewModelScope.launch(Dispatchers.IO) {
                val savedUri = StorageManager.saveImageToGallery(
                    context = activity,
                    imageBytes = currentSuccess.finalBytes,
                    prefix = currentSuccess.preset.id
                )
                _lastSavedUri.value = savedUri
                withContext(Dispatchers.Main) {
                    onComplete(savedUri)
                }
            }
        }
    }

    /**
     * Creates an Android share intent for the processed photo.
     */
    fun shareProcessedPhoto(): Intent? {
        val currentSuccess = (_processingPhase.value as? ProcessingPhase.Success)?.result ?: return null
        return StorageManager.createShareIntent(getApplication(), currentSuccess.finalBytes)
    }

    fun resetState() {
        _processingPhase.value = ProcessingPhase.Idle
        _lastSavedUri.value = null
        StorageManager.clearTemporaryCache(getApplication())
    }

    override fun onCleared() {
        super.onCleared()
        rawInputBitmap?.recycle()
        originalCroppedBitmap?.recycle()
        whiteSegmentedBitmap?.recycle()
    }
}
