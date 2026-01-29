
package com.module.notelycompose.notes.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.module.notelycompose.modelDownloader.DownloaderDialog
import com.module.notelycompose.modelDownloader.ModelDownloaderViewModel
import com.module.notelycompose.modelDownloader.ModelSelection
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSelectionScreen(
    navigateBack: () -> Unit,
    navigateToModelExplanation: () -> Unit,
    viewModel: ModelDownloaderViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    DownloaderDialog(
        uiState = uiState,
        onDismiss = { /* No-op */ }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Transcription Model") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(ModelSelection.models) { model ->
                Row {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(model.name)
                        Text("Size: ${model.size}")
                    }
                    Button(onClick = { viewModel.startDownload(model) }) {
                        Text("Download")
                    }
                }
            }
        }
    }
}
