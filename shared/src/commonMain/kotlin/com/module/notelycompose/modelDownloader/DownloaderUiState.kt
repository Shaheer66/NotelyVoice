package com.module.notelycompose.modelDownloader

data class DownloaderUiState(
    val selectedModel: ModelSelection.Model,
    val isDownloading: Boolean = false,
    val progress: Float = 0f
)
