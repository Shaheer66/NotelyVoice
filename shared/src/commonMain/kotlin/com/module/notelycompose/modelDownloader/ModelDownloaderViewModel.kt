package com.module.notelycompose.modelDownloader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.module.notelycompose.onboarding.data.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ModelDownloaderViewModel(
    private val modelDownloader: ModelDownloader,
    private val modelSelection: ModelSelection
):ViewModel(){
    private var _uiState: MutableStateFlow<DownloaderUiState> = MutableStateFlow(DownloaderUiState(modelSelection.getDefaultTranscriptionModel()))

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val selectedModel = modelSelection.getSelectedModel()
            _uiState.value = DownloaderUiState(selectedModel)
        }
    }
    val uiState: StateFlow<DownloaderUiState> = _uiState

    fun startDownload(model: ModelSelection.Model) {
        viewModelScope.launch(Dispatchers.IO) {
            modelDownloader.downloadModel(model).collect {
                when(it) {
                    is DownloadStatus.Downloading -> {
                        _uiState.update {
                            it.copy(progress = it.progress, isDownloading = true)
                        }
                    }
                    is DownloadStatus.Finished -> {
                        _uiState.update {
                            it.copy(isDownloading = false)
                        }
                    }
                    is DownloadStatus.Error -> {
                        _uiState.update {
                            it.copy(isDownloading = false)
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}
