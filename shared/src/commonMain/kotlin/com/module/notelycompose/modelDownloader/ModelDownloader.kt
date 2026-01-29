package com.module.notelycompose.modelDownloader

import kotlinx.coroutines.flow.Flow

interface ModelDownloader {
    fun downloadModel(model: ModelSelection.Model): Flow<DownloadStatus>
}

sealed class DownloadStatus {
    object Idle : DownloadStatus()
    data class Downloading(val progress: Float) : DownloadStatus()
    object Finished : DownloadStatus()
    data class Error(val message: String) : DownloadStatus()
}
