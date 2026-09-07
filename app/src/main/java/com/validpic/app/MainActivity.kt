package com.validpic.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.validpic.app.data.model.ProcessingPhase
import com.validpic.app.ui.screens.CameraScreen
import com.validpic.app.ui.screens.HelpFaqScreen
import com.validpic.app.ui.screens.HomeScreen
import com.validpic.app.ui.screens.PresetSelectionScreen
import com.validpic.app.ui.screens.PrivacyPolicyScreen
import com.validpic.app.ui.screens.ProcessingScreen
import com.validpic.app.ui.screens.ResultPreviewScreen
import com.validpic.app.ui.screens.SettingsScreen
import com.validpic.app.ui.screens.TouchUpScreen
import com.validpic.app.ui.theme.ValidPicTheme
import com.validpic.app.ui.viewmodel.PhotoProcessViewModel

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Camera : Screen("camera")
    data object Presets : Screen("presets?tab={tab}") {
        fun createRoute(tab: Int = 0): String = "presets?tab=$tab"
    }
    data object Processing : Screen("processing")
    data object TouchUp : Screen("touch_up")
    data object ResultPreview : Screen("result_preview")
    data object Settings : Screen("settings")
    data object HelpFaq : Screen("help_faq")
    data object PrivacyPolicy : Screen("privacy_policy")
}

class MainActivity : ComponentActivity() {

    private val viewModel: PhotoProcessViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ValidPicTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ValidPicNavHost(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ValidPicNavHost(viewModel: PhotoProcessViewModel) {
    val navController = rememberNavController()
    val processingPhase by viewModel.processingPhase.collectAsState()
    val selectedPreset by viewModel.selectedPreset.collectAsState()
    val whiteBackgroundEnabled by viewModel.whiteBackgroundEnabled.collectAsState()
    val passportSizeEnabled by viewModel.passportSizeEnabled.collectAsState()

    // Observe processing phase changes to navigate to processing and result screens automatically
    LaunchedEffect(processingPhase) {
        when (processingPhase) {
            is ProcessingPhase.Loading,
            is ProcessingPhase.DetectingFace,
            is ProcessingPhase.RemovingBackground,
            is ProcessingPhase.Compressing -> {
                if (navController.currentDestination?.route != Screen.Processing.route) {
                    navController.navigate(Screen.Processing.route)
                }
            }
            is ProcessingPhase.Success -> {
                navController.navigate(Screen.ResultPreview.route) {
                    popUpTo(Screen.Home.route) { inclusive = false }
                }
            }
            is ProcessingPhase.Failure -> {
                // Navigate back home on failure and show message
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            }
            is ProcessingPhase.Idle -> {}
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(250)) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(250)) },
        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(250)) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(250)) }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                selectedPreset = selectedPreset,
                whiteBackgroundEnabled = whiteBackgroundEnabled,
                passportSizeEnabled = passportSizeEnabled,
                onPresetSelected = { preset -> viewModel.selectPreset(preset) },
                onToggleWhiteBackground = { enabled -> viewModel.setWhiteBackgroundEnabled(enabled) },
                onTogglePassportSize = { enabled -> viewModel.setPassportSizeEnabled(enabled) },
                onNavigateToCamera = { navController.navigate(Screen.Camera.route) },
                onPhotoSelected = { uri, preset ->
                    viewModel.startProcessingFromUri(uri, preset)
                },
                onNavigateToPresets = { tabIndex ->
                    navController.navigate(Screen.Presets.createRoute(tabIndex))
                },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToHelp = { navController.navigate(Screen.HelpFaq.route) }
            )
        }

        composable(Screen.Camera.route) {
            CameraScreen(
                onPhotoCaptured = { bitmap ->
                    viewModel.startProcessingFromBitmap(bitmap, selectedPreset)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Presets.route,
            arguments = listOf(
                navArgument("tab") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val tab = backStackEntry.arguments?.getInt("tab") ?: 0
            PresetSelectionScreen(
                currentPreset = selectedPreset,
                initialTabIndex = tab,
                onPresetSelected = { preset ->
                    viewModel.selectPreset(preset)
                    navController.popBackStack()
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Processing.route) {
            ProcessingScreen(phase = processingPhase)
        }

        composable(Screen.ResultPreview.route) {
            val result = (processingPhase as? ProcessingPhase.Success)?.result
            if (result != null) {
                ResultPreviewScreen(
                    result = result,
                    whiteBackgroundEnabled = whiteBackgroundEnabled,
                    passportSizeEnabled = passportSizeEnabled,
                    onToggleWhiteBackground = { viewModel.toggleBackgroundOnCurrentResult() },
                    onTogglePassportSize = { viewModel.toggleFramingOnCurrentResult() },
                    onDownloadClick = { activity ->
                        viewModel.downloadProcessedPhoto(activity) { savedUri ->
                            if (savedUri != null) {
                                Toast.makeText(activity, "Photo saved to Gallery (Pictures/ValidPic)", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(activity, "Failed to save photo. Check storage permission.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onShareClick = {
                        val shareIntent = viewModel.shareProcessedPhoto()
                        if (shareIntent != null) {
                            navController.context.startActivity(shareIntent)
                        }
                    },
                    onCreateAnotherClick = {
                        viewModel.resetState()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onFineTuneClick = {
                        navController.navigate(Screen.TouchUp.route)
                    },
                    onNavigateHome = {
                        viewModel.resetState()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(Screen.TouchUp.route) {
            val result = (processingPhase as? ProcessingPhase.Success)?.result
            if (result != null) {
                TouchUpScreen(
                    baseBitmap = result.segmentedBitmap,
                    aspectRatio = result.preset.aspectRatio,
                    onApplyTouchUp = {
                        // Return to result screen
                        navController.popBackStack()
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPrivacy = { navController.navigate(Screen.PrivacyPolicy.route) },
                onNavigateToHelp = { navController.navigate(Screen.HelpFaq.route) }
            )
        }

        composable(Screen.HelpFaq.route) {
            HelpFaqScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
