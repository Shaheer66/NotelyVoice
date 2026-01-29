package com.module.notelycompose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.module.notelycompose.core.composableWithHorizontalSlide
import com.module.notelycompose.notes.ui.settings.ModelSelectionScreen
import com.module.notelycompose.notes.ui.theme.MyApplicationTheme
import com.module.notelycompose.transcription.TranscriptionScreen

@Composable
fun App() {
    MyApplicationTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "transcription",
            ) {
                composableWithHorizontalSlide("transcription") {
                    TranscriptionScreen(
                        navigateBack = { /* No-op for now */ },
                        navigateToSettings = { navController.navigate("model_selection") },
                        editorViewModel = koinViewModel(),
                    )
                }
                composableWithHorizontalSlide("model_selection") {
                    ModelSelectionScreen(
                        navigateBack = { navController.popBackStack() },
                        navigateToModelExplanation = { /* TODO */ }
                    )
                }
            }
        }
    }
}