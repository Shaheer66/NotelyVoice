package com.module.notelycompose.di

import com.module.notelycompose.modelDownloader.ModelDownloader
import com.module.notelycompose.modelDownloader.ModelDownloaderImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformModule: Module = module {
    single<ModelDownloader> { ModelDownloaderImpl(get(), get()) }
    single { HttpClient(CIO) }
}
