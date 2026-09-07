package com.validpic.app.data.model

/**
 * Diagnostic assessment of photo quality against official passport standards.
 * Non-judgmental, purely helpful guidance.
 */
data class QualityCheckReport(
    val isFaceDetected: Boolean = false,
    val isFaceCentered: Boolean = false,
    val faceCoverageRatio: Float = 0f, // typically 0.70 to 0.80 for passport
    val isRatioAcceptable: Boolean = false,
    val headEulerZAngle: Float = 0f, // tilt in degrees
    val isTiltAcceptable: Boolean = true,
    val areEyesOpen: Boolean = true,
    val warnings: List<String> = emptyList(),
    val successes: List<String> = emptyList()
) {
    val isOptimal: Boolean
        get() = isFaceDetected && isFaceCentered && isRatioAcceptable && isTiltAcceptable && areEyesOpen
}
