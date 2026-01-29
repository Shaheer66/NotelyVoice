package com.module.notelycompose.modelDownloader

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.http.contentLength
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class ModelDownloaderImpl(
    private val context: Context,
    private val client: HttpClient
) : ModelDownloader {

    override fun downloadModel(model: ModelSelection.Model): Flow<DownloadStatus> = flow {
        try {
            emit(DownloadStatus.Downloading(0f))

            val url = model.url
            val response = client.get(url)
            val totalBytes = response.contentLength() ?: 1L
            val file = File(context.filesDir, model.name)

            var bytesCopied = 0L
            response.body<ByteArray>().inputStream().use { input ->
                file.outputStream().use { output ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var bytes = input.read(buffer)
                    while (bytes >= 0) {
                        output.write(buffer, 0, bytes)
                        bytesCopied += bytes
                        val progress = (bytesCopied.toFloat() / totalBytes.toFloat())
                        emit(DownloadStatus.Downloading(progress))
                        bytes = input.read(buffer)
                    }
                }
            }
            emit(DownloadStatus.Finished)
        } catch (e: Exception) {
            emit(DownloadStatus.Error(e.message ?: "Unknown error"))
        }
    }
}
