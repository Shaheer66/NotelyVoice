package com.module.notelycompose.modelDownloader

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DownloaderDialog(
    uiState: DownloaderUiState,
    onDismiss: () -> Unit
) {
    if (uiState.isDownloading) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Downloading Model") },
            text = {
                Column {
                    Text("Downloading ${uiState.selectedModel.name}...")
                    LinearProgressIndicator(
                        progress = uiState.progress,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = { }
        )
    }
}
