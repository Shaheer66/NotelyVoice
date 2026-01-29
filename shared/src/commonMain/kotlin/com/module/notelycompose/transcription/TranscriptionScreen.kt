package com.module.notelycompose.transcription

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.module.notelycompose.notes.presentation.detail.TextEditorViewModel
import com.module.notelycompose.notes.ui.theme.LocalCustomColors
import com.module.notelycompose.platform.HandlePlatformBackNavigation
import com.module.notelycompose.platform.getPlatform
import com.module.notelycompose.resources.Res
import com.module.notelycompose.resources.top_bar_back
import com.module.notelycompose.resources.transcription_dialog_append
import com.module.notelycompose.resources.transcription_dialog_error_audio_file_desc
import com.module.notelycompose.resources.transcription_dialog_error_audio_file_title
import com.module.notelycompose.resources.transcription_dialog_error_got_it
import com.module.notelycompose.resources.transcription_dialog_original
import com.module.notelycompose.resources.transcription_dialog_summarize
import com.module.notelycompose.resources.vectors.IcChevronLeft
import com.module.notelycompose.resources.vectors.Images
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranscriptionScreen(
    navigateBack: () -> Unit,
    navigateToSettings: () -> Unit,
    viewModel: TranscriptionViewModel = koinViewModel(),
    editorViewModel: TextEditorViewModel
) {
    val scrollState = rememberScrollState()
    val transcriptionUiState by viewModel.uiState.collectAsState()
    val editorState by editorViewModel.editorPresentationState.collectAsState()

    LaunchedEffect(transcriptionUiState.originalText) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    DisposableEffect(Unit) {
        viewModel.requestAudioPermission()
        viewModel.initRecognizer()
        viewModel.startRecognizer(editorState.recording.recordingPath)
        onDispose {
            viewModel.stopRecognizer()
            viewModel.finishRecognizer()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transcribe") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = navigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .border(
                        2.dp,
                        LocalCustomColors.current.bodyContentColor,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = if (transcriptionUiState.viewOriginalText) transcriptionUiState.originalText else transcriptionUiState.summarizedText,
                    color = LocalCustomColors.current.bodyContentColor,
                    style = TextStyle(fontSize = editorState.bodyTextSize.sp)
                )
            }
            if (transcriptionUiState.progress == 0) {
                LinearProgressIndicator(
                    modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
                    strokeCap = StrokeCap.Round
                )
            } else if (transcriptionUiState.progress in 1..99) {
                SmoothLinearProgressBar((transcriptionUiState.progress / 100f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    enabled = !transcriptionUiState.inTranscription,
                    border = BorderStroke(
                        width = 2.dp,
                        color = if (!transcriptionUiState.inTranscription) {
                            LocalCustomColors.current.bodyContentColor
                        } else {
                            LocalCustomColors.current.bodyContentColor.copy(alpha = 0.38f)
                        }
                    ),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = LocalCustomColors.current.bodyContentColor,
                        disabledContentColor = LocalCustomColors.current.bodyContentColor.copy(alpha = 0.38f)
                    ),
                    content = {
                        Text(
                            stringResource(Res.string.transcription_dialog_append)
                        )
                    },
                    onClick = {
                        val result =
                            if (transcriptionUiState.viewOriginalText) transcriptionUiState.originalText else transcriptionUiState.summarizedText
                        editorViewModel.onUpdateContent(TextFieldValue("${editorState.content.text}\n$result"))
                        navigateBack()
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    enabled = !transcriptionUiState.inTranscription,
                    border = BorderStroke(
                        width = 2.dp,
                        color = if (!transcriptionUiState.inTranscription) {
                            LocalCustomColors.current.bodyContentColor
                        } else {
                            LocalCustomColors.current.bodyContentColor.copy(alpha = 0.38f)
                        }
                    ),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = LocalCustomColors.current.bodyContentColor,
                        disabledContentColor = LocalCustomColors.current.bodyContentColor.copy(alpha = 0.38f)
                    ),
                    content = {
                        Text(
                            if (transcriptionUiState.viewOriginalText) stringResource(Res.string.transcription_dialog_summarize) else
                                stringResource(Res.string.transcription_dialog_original),
                            fontSize = 12.sp
                        )
                    }, onClick = {
                        viewModel.summarize()
                    })
            }
        }
    }

    if (transcriptionUiState.hasError) {
        AlertDialog(
            onDismissRequest = navigateBack,
            confirmButton = {
                TextButton(onClick = navigateBack) {
                    Text(stringResource(Res.string.transcription_dialog_error_got_it))
                }
            },
            title = { Text(stringResource(Res.string.transcription_dialog_error_audio_file_title)) },
            text = { Text(stringResource(Res.string.transcription_dialog_error_audio_file_desc)) }
        )
    }
}

@Composable
fun SmoothLinearProgressBar(progress: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500) // Adjust duration as needed
    )

    LinearProgressIndicator(
        progress,
        modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
        strokeCap = StrokeCap.Round
    )
}
