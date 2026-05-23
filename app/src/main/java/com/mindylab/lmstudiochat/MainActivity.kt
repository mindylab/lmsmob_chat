package com.mindylab.lmstudiochat

import android.Manifest
import android.app.Notification
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfRenderer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.StatFs
import android.provider.OpenableColumns
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.DocumentsContract
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.SocketTimeoutException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.TimeUnit
import java.util.UUID
import java.util.zip.ZipInputStream

private const val DefaultApiUrl = "http://10.0.2.2:1234/v1"
private const val DefaultSystemPrompt = "You are a helpful assistant running locally through LM Studio."
private const val DefaultSystemProfileId = "default"
private const val MaxDocumentTextChars = 24000
private const val DefaultContextLength = 8000

private data class IncomingShareIntent(
    val id: Long,
    val intent: Intent,
)

data class NativeToolAction(
    val id: String = UUID.randomUUID().toString(),
    val tool: String,
    val args: JSONObject,
)

private val NativeActionRegex = Regex(
    "<lmsmob_action>(.*?)</lmsmob_action>",
    setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL),
)
private val IntegrationPresets = listOf(
    "mcp/local-web",
    "mcp/gemma4-audio-python",
    "mcp/playwright",
)
private val AllowedToolPresets = listOf(
    "web_search",
    "web_fetch",
    "web_search_and_fetch",
)

class MainActivity : ComponentActivity() {
    private val shareIntentIds = AtomicLong()
    private val shareIntentRequests = MutableStateFlow<IncomingShareIntent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = ViewModelProvider(
            this,
            ChatViewModel.factory(applicationContext),
        )[ChatViewModel::class.java]
        publishShareIntent(intent)

        setContent {
            LmStudioChatTheme {
                val context = LocalContext.current
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                val shareIntentRequest by shareIntentRequests.collectAsStateWithLifecycle()
                val exportLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.CreateDocument("application/json"),
                ) { uri: Uri? ->
                    if (uri != null) {
                        runCatching {
                            context.contentResolver.openOutputStream(uri)?.use { output ->
                                output.write(viewModel.exportChatHistoryJson().toByteArray(Charsets.UTF_8))
                            } ?: error("Could not open export file")
                        }.onFailure { throwable ->
                            viewModel.showError(throwable.friendlyMessage())
                        }
                    }
                }
                val importLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.OpenDocument(),
                ) { uri: Uri? ->
                    if (uri != null) {
                        runCatching {
                            context.contentResolver.openInputStream(uri)?.use { input ->
                                input.bufferedReader(Charsets.UTF_8).readText()
                            } ?: error("Could not open import file")
                        }.onSuccess { json ->
                            viewModel.importChatHistory(json)
                        }.onFailure { throwable ->
                            viewModel.showError(throwable.friendlyMessage())
                        }
                    }
                }
                val imageLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.GetContent(),
                ) { uri: Uri? ->
                    if (uri != null) {
                        runCatching {
                            context.imageUriToAttachment(uri)
                        }.onSuccess { attachment ->
                            viewModel.attachImages(listOf(attachment))
                        }.onFailure { throwable ->
                            viewModel.showError(throwable.friendlyMessage())
                        }
                    }
                }
                var pendingDocumentUri by remember { mutableStateOf<Uri?>(null) }
                val uiScope = rememberCoroutineScope()
                val mainHandler = remember { Handler(Looper.getMainLooper()) }
                var ttsReady by remember { mutableStateOf(false) }
                var speakingMessageId by remember { mutableStateOf<String?>(null) }
                val textToSpeech = remember {
                    TextToSpeech(context.applicationContext) { status ->
                        mainHandler.post {
                            ttsReady = status == TextToSpeech.SUCCESS
                        }
                    }
                }
                DisposableEffect(textToSpeech) {
                    textToSpeech.setOnUtteranceProgressListener(
                        object : UtteranceProgressListener() {
                            override fun onStart(utteranceId: String?) = Unit

                            override fun onDone(utteranceId: String?) {
                                mainHandler.post {
                                    if (speakingMessageId == utteranceId) {
                                        speakingMessageId = null
                                    }
                                }
                            }

                            override fun onError(utteranceId: String?) {
                                mainHandler.post {
                                    if (speakingMessageId == utteranceId) {
                                        speakingMessageId = null
                                    }
                                }
                            }
                        },
                    )
                    onDispose {
                        textToSpeech.stop()
                        textToSpeech.shutdown()
                    }
                }
                fun stopSpeaking() {
                    textToSpeech.stop()
                    speakingMessageId = null
                }
                fun speakAssistantMessage(messageId: String, content: String) {
                    if (!state.voiceOutputEnabled) return
                    val text = content.speakableAnswerText()
                    if (text.isBlank()) return
                    if (!ttsReady) {
                        viewModel.showError("Text-to-speech is not ready yet. Try again in a moment.")
                        return
                    }
                    textToSpeech.language = Locale.getDefault()
                    val result = textToSpeech.speak(
                        text,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        messageId,
                    )
                    if (result == TextToSpeech.ERROR) {
                        speakingMessageId = null
                        viewModel.showError("Could not start text-to-speech on this device.")
                    } else {
                        speakingMessageId = messageId
                    }
                }
                var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
                var isListening by remember { mutableStateOf(false) }
                var startListeningAfterPermission by remember { mutableStateOf(false) }
                val documentLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.OpenDocument(),
                ) { uri: Uri? ->
                    pendingDocumentUri = uri
                }
                val cameraLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.TakePicturePreview(),
                ) { bitmap: Bitmap? ->
                    if (bitmap != null) {
                        runCatching {
                            bitmap.toAttachment("Camera photo")
                        }.onSuccess { attachment ->
                            viewModel.attachImages(listOf(attachment))
                        }.onFailure { throwable ->
                            viewModel.showError(throwable.friendlyMessage())
                        }
                    }
                }
                val audioPermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission(),
                ) { granted ->
                    if (granted) {
                        startListeningAfterPermission = true
                    } else {
                        viewModel.showError("Microphone permission is required for voice input.")
                    }
                }
                fun stopVoiceInput() {
                    speechRecognizer?.stopListening()
                    isListening = false
                }
                fun startVoiceInput() {
                    if (!state.voiceInputEnabled) return
                    if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                        viewModel.showError("Speech recognition is not available on this device.")
                        return
                    }
                    if (!context.hasRecordAudioPermission()) {
                        audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        return
                    }
                    val recognizer = speechRecognizer
                        ?: SpeechRecognizer.createSpeechRecognizer(context.applicationContext).also {
                            speechRecognizer = it
                        }
                    recognizer.setRecognitionListener(
                        object : RecognitionListener {
                            override fun onReadyForSpeech(params: Bundle?) {
                                isListening = true
                            }

                            override fun onBeginningOfSpeech() = Unit
                            override fun onRmsChanged(rmsdB: Float) = Unit
                            override fun onBufferReceived(buffer: ByteArray?) = Unit
                            override fun onEndOfSpeech() {
                                isListening = false
                            }

                            override fun onError(error: Int) {
                                isListening = false
                                viewModel.showError(error.speechRecognitionMessage())
                            }

                            override fun onResults(results: Bundle?) {
                                isListening = false
                                val text = results
                                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                                    ?.firstOrNull()
                                    .orEmpty()
                                if (text.isBlank()) {
                                    viewModel.showError("I did not catch that. Try voice input again.")
                                } else {
                                    viewModel.receiveVoiceText(text)
                                }
                            }

                            override fun onPartialResults(partialResults: Bundle?) = Unit
                            override fun onEvent(eventType: Int, params: Bundle?) = Unit
                        },
                    )
                    recognizer.startListening(
                        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag())
                            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
                        },
                    )
                    isListening = true
                }
                LaunchedEffect(startListeningAfterPermission) {
                    if (startListeningAfterPermission) {
                        startListeningAfterPermission = false
                        startVoiceInput()
                    }
                }
                DisposableEffect(Unit) {
                    onDispose {
                        speechRecognizer?.destroy()
                    }
                }
                var lastAutoSpokenMessageId by remember { mutableStateOf<String?>(null) }
                LaunchedEffect(
                    state.messages.lastOrNull()?.id,
                    state.messages.lastOrNull()?.isStreaming,
                    state.voiceOutputEnabled,
                    state.autoReadAnswersEnabled,
                ) {
                    val lastMessage = state.messages.lastOrNull()
                    if (
                        state.voiceOutputEnabled &&
                        state.autoReadAnswersEnabled &&
                        lastMessage != null &&
                        lastMessage.role == MessageRole.Assistant &&
                        !lastMessage.isStreaming &&
                        lastMessage.id != lastAutoSpokenMessageId
                    ) {
                        lastAutoSpokenMessageId = lastMessage.id
                        speakAssistantMessage(lastMessage.id, lastMessage.content)
                    }
                }
                val contactsPermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission(),
                ) { granted ->
                    viewModel.updateContactsToolEnabled(granted)
                    if (!granted) {
                        viewModel.showError("Contacts permission is required before the contacts lookup tool can be used.")
                    }
                }
                val localFolderLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.OpenDocumentTree(),
                ) { uri: Uri? ->
                    if (uri == null) {
                        viewModel.updateLocalFileSearchToolEnabled(false)
                        return@rememberLauncherForActivityResult
                    }
                    runCatching {
                        context.contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION,
                        )
                    }
                    viewModel.updateLocalFileSearchTreeUri(uri.toString())
                    viewModel.updateLocalFileSearchToolEnabled(true)
                }
                LaunchedEffect(shareIntentRequest?.id) {
                    val request = shareIntentRequest ?: return@LaunchedEffect
                    handleIncomingShareIntent(
                        context = context,
                        intent = request.intent,
                        viewModel = viewModel,
                        onDocumentShared = { uri -> pendingDocumentUri = uri },
                    )
                    shareIntentRequests.value = null
                }
                LmStudioApp(
                    state = state,
                    onInputChange = viewModel::updateInput,
                    onSend = viewModel::sendInput,
                    onCancelSend = viewModel::cancelSending,
                    onSuggestion = viewModel::sendPrompt,
                    onNewChat = viewModel::newChat,
                    onSelectChat = viewModel::selectChat,
                    onChatSearchChange = viewModel::updateChatSearchQuery,
                    onEditMessage = viewModel::editMessage,
                    onDeleteMessage = viewModel::deleteMessage,
                    onDeleteChat = viewModel::deleteChat,
                    onOpenSettings = viewModel::openSettings,
                    onCloseSettings = viewModel::closeSettings,
                    onOpenInfo = viewModel::openInfo,
                    onCloseInfo = viewModel::closeInfo,
                    onApiUrlChange = viewModel::updateApiUrl,
                    onModelChange = viewModel::updateModel,
                    onApiTokenChange = viewModel::updateApiToken,
                    onServerToolsEnabledChange = viewModel::updateServerToolsEnabled,
                    onServerIntegrationsChange = viewModel::updateServerIntegrations,
                    onAllowedToolsChange = viewModel::updateAllowedTools,
                    onNativeIntentToolEnabledChange = viewModel::updateNativeIntentToolEnabled,
                    onCalendarToolEnabledChange = viewModel::updateCalendarToolEnabled,
                    onContactsToolEnabledChange = { enabled ->
                        if (!enabled) {
                            viewModel.updateContactsToolEnabled(false)
                        } else if (context.hasContactsPermission()) {
                            viewModel.updateContactsToolEnabled(true)
                        } else {
                            contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                        }
                    },
                    onNotificationDigestToolEnabledChange = { enabled ->
                        viewModel.updateNotificationDigestToolEnabled(enabled)
                        if (enabled) {
                            context.openNotificationListenerSettings(viewModel)
                        }
                    },
                    onLocalFileSearchToolEnabledChange = { enabled ->
                        if (enabled) {
                            localFolderLauncher.launch(null)
                        } else {
                            viewModel.updateLocalFileSearchToolEnabled(false)
                        }
                    },
                    onPickLocalSearchFolder = { localFolderLauncher.launch(null) },
                    onDeviceStatusToolEnabledChange = viewModel::updateDeviceStatusToolEnabled,
                    onVoiceInputEnabledChange = viewModel::updateVoiceInputEnabled,
                    onVoiceOutputEnabledChange = viewModel::updateVoiceOutputEnabled,
                    onAutoReadAnswersEnabledChange = viewModel::updateAutoReadAnswersEnabled,
                    onAppendDateTimeToSystemPromptChange = viewModel::updateAppendDateTimeToSystemPrompt,
                    onSystemProfileSelect = viewModel::selectSystemProfile,
                    onSystemProfileNameChange = viewModel::updateSystemProfileNameDraft,
                    onSystemPromptChange = viewModel::updateSystemPromptDraft,
                    onTemperatureChange = viewModel::updateTemperature,
                    onTopPChange = viewModel::updateTopP,
                    onMaxTokensChange = viewModel::updateMaxTokens,
                    onContextLengthChange = viewModel::updateContextLength,
                    onPresencePenaltyChange = viewModel::updatePresencePenalty,
                    onFrequencyPenaltyChange = viewModel::updateFrequencyPenalty,
                    onSeedChange = viewModel::updateSeed,
                    onSystemProfileSave = viewModel::saveSystemProfile,
                    onSystemProfileCreate = viewModel::createSystemProfile,
                    onSystemProfileDelete = viewModel::deleteSystemProfile,
                    onReasoningEnabledChange = viewModel::updateReasoningEnabled,
                    onPickImage = { imageLauncher.launch("image/*") },
                    onTakePhoto = { cameraLauncher.launch(null) },
                    onPickDocument = {
                        documentLauncher.launch(
                            arrayOf(
                                "application/pdf",
                                "application/msword",
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                "application/vnd.ms-excel",
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                            ),
                        )
                    },
                    onVoiceInput = {
                        if (isListening) {
                            stopVoiceInput()
                        } else {
                            startVoiceInput()
                        }
                    },
                    isListening = isListening,
                    onSpeakMessage = ::speakAssistantMessage,
                    onStopSpeaking = ::stopSpeaking,
                    speakingMessageId = speakingMessageId,
                    onClearImage = viewModel::clearAttachments,
                    onRefreshModels = { viewModel.refreshModels(silent = false) },
                    onExportChats = { exportLauncher.launch("lm-studio-chat-history.json") },
                    onImportChats = { importLauncher.launch(arrayOf("application/json", "text/*", "*/*")) },
                    onDismissError = viewModel::dismissError,
                )
                val documentUri = pendingDocumentUri
                if (documentUri != null) {
                    DocumentConversionDialog(
                        canConvertToPlainText = !context.isPdfDocument(documentUri),
                        canConvertToImages = state.selectedModelSupportsVision(),
                        onDismiss = { pendingDocumentUri = null },
                        onPlainText = {
                            pendingDocumentUri = null
                            uiScope.launch {
                                runCatching {
                                    withContext(Dispatchers.IO) {
                                        context.documentUriToPlainText(documentUri)
                                    }
                                }.onSuccess { document ->
                                    viewModel.attachDocumentText(document)
                                }.onFailure { throwable ->
                                    viewModel.showError(throwable.friendlyMessage())
                                }
                            }
                        },
                        onImages = {
                            pendingDocumentUri = null
                            uiScope.launch {
                                runCatching {
                                    withContext(Dispatchers.IO) {
                                        context.documentUriToImageAttachments(documentUri)
                                    }
                                }.onSuccess { attachments ->
                                    viewModel.attachImages(attachments)
                                }.onFailure { throwable ->
                                    viewModel.showError(throwable.friendlyMessage())
                                }
                            }
                        },
                    )
                }
                val nativeToolAction = state.pendingNativeToolAction
                if (nativeToolAction != null) {
                    NativeToolActionDialog(
                        action = nativeToolAction,
                        onDismiss = { viewModel.setPendingNativeToolAction(null) },
                        onConfirm = {
                            viewModel.setPendingNativeToolAction(null)
                            uiScope.launch {
                                executeNativeToolAction(
                                    context = context,
                                    state = state,
                                    action = nativeToolAction,
                                    viewModel = viewModel,
                                )
                            }
                        },
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        publishShareIntent(intent)
    }

    private fun publishShareIntent(intent: Intent?) {
        if (intent?.isSupportedShareAction() == true) {
            shareIntentRequests.value = IncomingShareIntent(
                id = shareIntentIds.incrementAndGet(),
                intent = Intent(intent),
            )
        }
    }
}

enum class MessageRole(val apiName: String) {
    User("user"),
    Assistant("assistant"),
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: MessageRole,
    val content: String,
    val attachments: List<ChatImageAttachment> = emptyList(),
    val isStreaming: Boolean = false,
)

data class ChatImageAttachment(
    val dataUrl: String = "",
    val label: String = "Image",
    val mimeType: String = "image/jpeg",
)

data class ChatDocumentAttachment(
    val name: String,
    val text: String,
    val mimeType: String = "text/plain",
)

data class ChatSession(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "New chat",
    val messages: List<ChatMessage> = emptyList(),
    val previousResponseId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

data class SystemPromptProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val prompt: String,
)

data class GenerationSettings(
    val temperature: Double? = 0.7,
    val topP: Double? = null,
    val maxTokens: Int? = null,
    val contextLength: Int? = null,
    val presencePenalty: Double? = null,
    val frequencyPenalty: Double? = null,
    val seed: Int? = null,
)

data class ChatUiState(
    val sessions: List<ChatSession> = emptyList(),
    val activeSessionId: String = "",
    val chatSearchQuery: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val input: String = "",
    val isSending: Boolean = false,
    val apiUrl: String = DefaultApiUrl,
    val model: String = "",
    val apiToken: String = "",
    val availableModels: List<String> = emptyList(),
    val availableModelInfos: List<ModelInfo> = emptyList(),
    val isLoadingModels: Boolean = false,
    val serverToolsEnabled: Boolean = false,
    val serverIntegrations: String = "",
    val allowedTools: String = "",
    val nativeIntentToolEnabled: Boolean = false,
    val calendarToolEnabled: Boolean = false,
    val contactsToolEnabled: Boolean = false,
    val notificationDigestToolEnabled: Boolean = false,
    val localFileSearchToolEnabled: Boolean = false,
    val localFileSearchTreeUri: String = "",
    val deviceStatusToolEnabled: Boolean = false,
    val voiceInputEnabled: Boolean = false,
    val voiceOutputEnabled: Boolean = false,
    val autoReadAnswersEnabled: Boolean = false,
    val appendDateTimeToSystemPrompt: Boolean = false,
    val systemProfiles: List<SystemPromptProfile> = listOf(
        SystemPromptProfile(
            id = DefaultSystemProfileId,
            name = "Default",
            prompt = DefaultSystemPrompt,
        ),
    ),
    val activeSystemProfileId: String = DefaultSystemProfileId,
    val systemProfileNameDraft: String = "Default",
    val systemPromptDraft: String = DefaultSystemPrompt,
    val temperatureDraft: String = "0.7",
    val topPDraft: String = "",
    val maxTokensDraft: String = "",
    val contextLengthDraft: String = "",
    val presencePenaltyDraft: String = "",
    val frequencyPenaltyDraft: String = "",
    val seedDraft: String = "",
    val attachedImages: List<ChatImageAttachment> = emptyList(),
    val attachedDocumentText: ChatDocumentAttachment? = null,
    val reasoningEnabled: Boolean = true,
    val previousResponseId: String? = null,
    val isSettingsOpen: Boolean = false,
    val isInfoOpen: Boolean = false,
    val pendingNativeToolAction: NativeToolAction? = null,
    val error: String? = null,
    val modelLoadError: String? = null,
)

data class ModelInfo(
    val id: String,
    val supportsVision: Boolean = false,
    val contextLength: Int? = null,
    val reasoningOptions: List<String> = emptyList(),
    val defaultReasoning: String = "",
)

private data class ContextUsage(
    val usedTokens: Int,
    val availableTokens: Int,
    val fraction: Float,
)

class ChatViewModel(
    private val settingsStore: SettingsStore,
    private val client: LmStudioClient,
) : ViewModel() {
    private var currentSendJob: Job? = null
    @Volatile
    private var stopRequested = false

    private val initialSessions = settingsStore.loadChatSessions().ifEmpty { listOf(ChatSession()) }
    private val initialActiveSession = initialSessions.firstOrNull { it.id == settingsStore.activeSessionId }
        ?: initialSessions.first()
    private val initialProfiles = settingsStore.loadSystemProfiles()
    private val initialActiveProfile = initialProfiles.firstOrNull { it.id == settingsStore.activeSystemProfileId }
        ?: initialProfiles.first()

    private val _uiState = MutableStateFlow(
        ChatUiState(
            sessions = initialSessions,
            activeSessionId = initialActiveSession.id,
            messages = initialActiveSession.messages,
            previousResponseId = initialActiveSession.previousResponseId,
            apiUrl = settingsStore.apiUrl,
            model = settingsStore.model,
            apiToken = settingsStore.apiToken,
            serverToolsEnabled = settingsStore.serverToolsEnabled,
            serverIntegrations = settingsStore.serverIntegrations,
            allowedTools = settingsStore.allowedTools,
            nativeIntentToolEnabled = settingsStore.nativeIntentToolEnabled,
            calendarToolEnabled = settingsStore.calendarToolEnabled,
            contactsToolEnabled = settingsStore.contactsToolEnabled,
            notificationDigestToolEnabled = settingsStore.notificationDigestToolEnabled,
            localFileSearchToolEnabled = settingsStore.localFileSearchToolEnabled,
            localFileSearchTreeUri = settingsStore.localFileSearchTreeUri,
            deviceStatusToolEnabled = settingsStore.deviceStatusToolEnabled,
            voiceInputEnabled = settingsStore.voiceInputEnabled,
            voiceOutputEnabled = settingsStore.voiceOutputEnabled,
            autoReadAnswersEnabled = settingsStore.autoReadAnswersEnabled,
            appendDateTimeToSystemPrompt = settingsStore.appendDateTimeToSystemPrompt,
            temperatureDraft = settingsStore.temperature,
            topPDraft = settingsStore.topP,
            maxTokensDraft = settingsStore.maxTokens,
            contextLengthDraft = settingsStore.contextLength,
            presencePenaltyDraft = settingsStore.presencePenalty,
            frequencyPenaltyDraft = settingsStore.frequencyPenalty,
            seedDraft = settingsStore.seed,
            systemProfiles = initialProfiles,
            activeSystemProfileId = initialActiveProfile.id,
            systemProfileNameDraft = initialActiveProfile.name,
            systemPromptDraft = initialActiveProfile.prompt,
            reasoningEnabled = settingsStore.reasoningEnabled,
        ),
    )
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        settingsStore.activeSessionId = initialActiveSession.id
        settingsStore.saveChatSessions(initialSessions)
        settingsStore.activeSystemProfileId = initialActiveProfile.id
        settingsStore.saveSystemProfiles(initialProfiles)
        refreshModels(silent = true)
    }

    fun updateInput(value: String) {
        _uiState.update { it.copy(input = value) }
    }

    fun receiveSharedText(value: String) {
        val text = value.trim()
        if (text.isBlank()) return
        _uiState.update { current ->
            val mergedInput = listOf(current.input.trim(), text)
                .filter { it.isNotBlank() }
                .joinToString("\n\n")
            current.copy(
                input = mergedInput,
                error = null,
            )
        }
    }

    fun updateChatSearchQuery(value: String) {
        _uiState.update { it.copy(chatSearchQuery = value) }
    }

    fun selectChat(sessionId: String) {
        val session = _uiState.value.sessions.firstOrNull { it.id == sessionId } ?: return
        settingsStore.activeSessionId = session.id
        _uiState.update {
            it.copy(
                activeSessionId = session.id,
                messages = session.messages,
                previousResponseId = session.previousResponseId,
                input = "",
                error = null,
            )
        }
    }

    fun sendInput() {
        sendPrompt(_uiState.value.input)
    }

    fun cancelSending() {
        if (!_uiState.value.isSending) return
        stopRequested = true
        client.cancelActiveChat()
        currentSendJob?.cancel()
        markCurrentResponseStopped()
    }

    private fun markCurrentResponseStopped() {
        _uiState.update { current ->
            val now = System.currentTimeMillis()
            val responseSessions = current.sessions.map { session ->
                if (session.id == current.activeSessionId) {
                    session.copy(
                        messages = session.messages.map { message ->
                            if (message.isStreaming) {
                                message.copy(
                                    content = message.content.ifBlank { "Request stopped." },
                                    isStreaming = false,
                                )
                            } else {
                                message
                            }
                        },
                        updatedAt = now,
                    )
                } else {
                    session
                }
            }.sortedByDescending { it.updatedAt }
            settingsStore.saveChatSessions(responseSessions)
            val activeSessionNow = responseSessions.firstOrNull { it.id == current.activeSessionId }
            current.copy(
                sessions = responseSessions,
                messages = activeSessionNow?.messages ?: current.messages,
                isSending = false,
                error = null,
            )
        }
    }

    fun sendPrompt(prompt: String) {
        val trimmedPrompt = prompt.trim()
        val state = _uiState.value
        val attachedImages = state.attachedImages
        val attachedDocument = state.attachedDocumentText
        if ((trimmedPrompt.isBlank() && attachedImages.isEmpty() && attachedDocument == null) || state.isSending) return

        if (attachedImages.isNotEmpty() && !state.selectedModelSupportsVision()) {
            _uiState.update {
                it.copy(error = "The selected model does not report vision support. Pick a vision model, then attach images again.")
            }
            return
        }

        if (state.serverToolsEnabled && state.serverIntegrations.isBlank()) {
            _uiState.update {
                it.copy(error = "Add at least one LM Studio integration, for example mcp/playwright.")
            }
            return
        }

        if (state.serverToolsEnabled && state.apiToken.isBlank()) {
            _uiState.update {
                it.copy(error = "Add an LM Studio API token with MCP permissions before using server tools.")
            }
            return
        }

        val userPrompt = trimmedPrompt.ifBlank {
            when {
                attachedDocument != null && attachedImages.isEmpty() -> "Please summarize this document."
                attachedImages.isNotEmpty() -> "Please describe this image."
                else -> ""
            }
        }
        val requestPrompt = buildString {
            append(userPrompt)
            if (attachedDocument != null) {
                append("\n\n[Document: ")
                append(attachedDocument.name)
                append("]\n")
                append(attachedDocument.text)
            }
        }
        val userMessage = ChatMessage(
            role = MessageRole.User,
            content = requestPrompt,
            attachments = attachedImages + listOfNotNull(
                attachedDocument?.let {
                    ChatImageAttachment(
                        dataUrl = "",
                        label = it.name,
                        mimeType = it.mimeType,
                    )
                },
            ),
        )
        val activeSessionId = state.activeSessionId
        val activeSession = state.sessions.firstOrNull { it.id == activeSessionId } ?: ChatSession(id = activeSessionId)
        val requestMessages = activeSession.messages + userMessage
        val reasoning = state.reasoningRequestValue()
        val useNativeChat = state.serverToolsEnabled || attachedImages.isNotEmpty() || reasoning != null
        val assistantPlaceholder = if (useNativeChat) {
            ChatMessage(role = MessageRole.Assistant, content = "", isStreaming = true)
        } else {
            null
        }
        val visibleMessages = if (assistantPlaceholder == null) {
            requestMessages
        } else {
            requestMessages + assistantPlaceholder
        }
        val updatedSession = activeSession.copy(
            title = if (activeSession.messages.isEmpty()) userPrompt.toChatTitle() else activeSession.title,
            messages = visibleMessages,
            updatedAt = System.currentTimeMillis(),
        )
        val updatedSessions = state.sessions.replaceSession(updatedSession)
        val apiUrl = state.apiUrl
        val model = state.model
        val apiToken = state.apiToken
        val serverToolsEnabled = state.serverToolsEnabled
        val serverIntegrations = state.serverIntegrations
        val allowedTools = state.allowedTools
        val previousResponseId = activeSession.previousResponseId
        val promptAttachments = attachedImages
        val systemPrompt = state.activeSystemPrompt()
        val generationSettings = state.generationSettings()
        stopRequested = false

        settingsStore.saveChatSessions(updatedSessions)
        _uiState.update {
            it.copy(
                sessions = updatedSessions,
                messages = visibleMessages,
                input = "",
                attachedImages = emptyList(),
                attachedDocumentText = null,
                isSending = true,
                error = null,
            )
        }

        currentSendJob = viewModelScope.launch {
            try {
                if (useNativeChat && assistantPlaceholder != null) {
                val toolCalls = mutableListOf<String>()
                val toolErrors = mutableListOf<String>()
                val reasoningBuffer = StringBuilder()
                val message = StringBuilder()

                fun renderStreamingContent(): String =
                    buildAssistantContent(
                        tools = toolCalls,
                        reasoning = reasoningBuffer.toString(),
                        message = message.toString(),
                        errors = toolErrors,
                    ).trim()

                fun updateAssistant(
                    content: String,
                    isStreaming: Boolean,
                    answer: ChatCompletionResult? = null,
                    persist: Boolean = false,
                ) {
                    _uiState.update { current ->
                        val now = System.currentTimeMillis()
                        val responseSessions = current.sessions.map { session ->
                            if (session.id == activeSessionId) {
                                session.copy(
                                    messages = session.messages.map { existingMessage ->
                                        if (existingMessage.id == assistantPlaceholder.id) {
                                            existingMessage.copy(
                                                content = content,
                                                isStreaming = isStreaming,
                                            )
                                        } else {
                                            existingMessage
                                        }
                                    },
                                    previousResponseId = answer?.responseId ?: session.previousResponseId,
                                    updatedAt = now,
                                )
                            } else {
                                session
                            }
                        }.sortedByDescending { it.updatedAt }
                        if (persist) {
                            settingsStore.saveChatSessions(responseSessions)
                        }
                        val activeSessionNow = responseSessions.firstOrNull { it.id == current.activeSessionId }
                        current.copy(
                            sessions = responseSessions,
                            messages = activeSessionNow?.messages ?: current.messages,
                            isSending = isStreaming,
                            previousResponseId = activeSessionNow?.previousResponseId ?: current.previousResponseId,
                            model = answer?.modelId ?: current.model,
                            pendingNativeToolAction = if (!isStreaming && answer != null) {
                                answer.content.extractNativeToolAction(current)
                            } else {
                                current.pendingNativeToolAction
                            },
                        )
                    }
                }

                fun removeAssistantWithError(throwable: Throwable) {
                    _uiState.update { current ->
                        val responseSessions = current.sessions.map { session ->
                            if (session.id == activeSessionId) {
                                session.copy(
                                    messages = session.messages.filterNot { it.id == assistantPlaceholder.id },
                                    updatedAt = System.currentTimeMillis(),
                                )
                            } else {
                                session
                            }
                        }.sortedByDescending { it.updatedAt }
                        settingsStore.saveChatSessions(responseSessions)
                        val activeSessionNow = responseSessions.firstOrNull { it.id == current.activeSessionId }
                        current.copy(
                            sessions = responseSessions,
                            messages = activeSessionNow?.messages ?: current.messages,
                            isSending = false,
                            error = throwable.friendlyMessage(),
                        )
                    }
                }

                val result = client.streamNativeChat(
                    apiUrl = apiUrl,
                    model = model,
                    apiToken = apiToken,
                    prompt = requestPrompt,
                    attachments = promptAttachments,
                    systemPrompt = systemPrompt,
                    previousResponseId = previousResponseId,
                    integrations = if (serverToolsEnabled) serverIntegrations else "",
                    allowedTools = allowedTools,
                    reasoning = reasoning,
                    generationSettings = generationSettings,
                ) { event ->
                    when (event.type) {
                        ChatStreamEventType.MessageDelta -> {
                            message.append(event.content)
                        }
                        ChatStreamEventType.ReasoningDelta -> {
                            reasoningBuffer.append(event.content)
                        }
                        ChatStreamEventType.ToolStarted,
                        ChatStreamEventType.ToolSucceeded -> {
                            val toolLabel = event.toolLabel()
                            if (toolLabel.isNotBlank() && toolLabel !in toolCalls) {
                                toolCalls += toolLabel
                            }
                        }
                        ChatStreamEventType.ToolFailed -> {
                            val toolLabel = event.toolLabel().ifBlank { "tool" }
                            toolErrors += if (event.content.isBlank()) {
                                "$toolLabel failed"
                            } else {
                                "$toolLabel: ${event.content}"
                            }
                        }
                        ChatStreamEventType.Error -> {
                            if (event.content.isNotBlank()) {
                                toolErrors += event.content
                            }
                        }
                    }
                    updateAssistant(renderStreamingContent(), isStreaming = true)
                }

                result.fold(
                    onSuccess = { answer ->
                        if (!answer.modelId.isNullOrBlank()) {
                            settingsStore.model = answer.modelId
                        }
                        updateAssistant(
                            content = answer.content,
                            isStreaming = false,
                            answer = answer,
                            persist = true,
                        )
                    },
                        onFailure = { throwable ->
                            if (stopRequested || throwable.isChatCancellation()) {
                                markCurrentResponseStopped()
                            } else {
                                removeAssistantWithError(throwable)
                            }
                        },
                    )
                } else {
                    val result = client.sendChatCompletions(
                    apiUrl = apiUrl,
                    model = model,
                    apiToken = apiToken,
                    messages = requestMessages,
                    systemPrompt = systemPrompt,
                    generationSettings = generationSettings,
                )

                    if (result.exceptionOrNull()?.let { stopRequested || it.isChatCancellation() } == true) {
                        markCurrentResponseStopped()
                    } else {
                        _uiState.update { current ->
                            result.fold(
                                onSuccess = { answer ->
                                    if (!answer.modelId.isNullOrBlank()) {
                                        settingsStore.model = answer.modelId
                                    }
                                    val assistantMessage = ChatMessage(
                                        role = MessageRole.Assistant,
                                        content = answer.content,
                                    )
                                    val responseSessions = current.sessions.map { session ->
                                        if (session.id == activeSessionId) {
                                            session.copy(
                                                messages = session.messages + assistantMessage,
                                                previousResponseId = answer.responseId ?: session.previousResponseId,
                                                updatedAt = System.currentTimeMillis(),
                                            )
                                        } else {
                                            session
                                        }
                                    }
                                    settingsStore.saveChatSessions(responseSessions)
                                    val activeSessionNow = responseSessions.firstOrNull { it.id == current.activeSessionId }
                                    current.copy(
                                        sessions = responseSessions,
                                        messages = activeSessionNow?.messages ?: current.messages,
                                        isSending = false,
                                        previousResponseId = activeSessionNow?.previousResponseId ?: current.previousResponseId,
                                        model = answer.modelId ?: current.model,
                                        pendingNativeToolAction = answer.content.extractNativeToolAction(current),
                                    )
                                },
                                onFailure = { throwable ->
                                    current.copy(
                                        isSending = false,
                                        error = throwable.friendlyMessage(),
                                    )
                                },
                            )
                        }
                    }
                }
            } finally {
                currentSendJob = null
            }
        }
    }

    fun newChat() {
        val newSession = ChatSession()
        val sessions = (listOf(newSession) + _uiState.value.sessions)
            .distinctBy { it.id }
        settingsStore.activeSessionId = newSession.id
        settingsStore.saveChatSessions(sessions)
        _uiState.update {
            it.copy(
                sessions = sessions,
                activeSessionId = newSession.id,
                messages = emptyList(),
                input = "",
                error = null,
                previousResponseId = null,
            )
        }
    }

    fun deleteChat(sessionId: String) {
        val state = _uiState.value
        val remaining = state.sessions.filterNot { it.id == sessionId }
        val sessions = remaining.ifEmpty { listOf(ChatSession()) }
        val activeSession = if (state.activeSessionId == sessionId) {
            sessions.first()
        } else {
            sessions.firstOrNull { it.id == state.activeSessionId } ?: sessions.first()
        }

        settingsStore.activeSessionId = activeSession.id
        settingsStore.saveChatSessions(sessions)
        _uiState.update {
            it.copy(
                sessions = sessions,
                activeSessionId = activeSession.id,
                messages = activeSession.messages,
                input = "",
                previousResponseId = activeSession.previousResponseId,
                error = null,
            )
        }
    }

    fun deleteMessage(messageId: String) {
        val state = _uiState.value
        val activeSession = state.sessions.firstOrNull { it.id == state.activeSessionId } ?: return
        val message = activeSession.messages.firstOrNull { it.id == messageId } ?: return
        if (message.isStreaming) return

        val remainingMessages = activeSession.messages.filterNot { it.id == messageId }
        val updatedSession = activeSession.copy(
            title = remainingMessages.firstOrNull { it.role == MessageRole.User }?.content?.toChatTitle()
                ?: "New chat",
            messages = remainingMessages,
            previousResponseId = null,
            updatedAt = System.currentTimeMillis(),
        )
        val sessions = state.sessions.replaceSession(updatedSession)
        settingsStore.saveChatSessions(sessions)
        _uiState.update {
            it.copy(
                sessions = sessions,
                messages = updatedSession.messages,
                previousResponseId = null,
                pendingNativeToolAction = null,
                error = null,
            )
        }
    }

    fun receiveVoiceText(value: String) {
        val text = value.trim()
        if (text.isBlank()) return
        _uiState.update { current ->
            val mergedInput = if (current.input.isBlank()) {
                text
            } else {
                "${current.input.trimEnd()} $text"
            }
            current.copy(
                input = mergedInput,
                error = null,
            )
        }
    }

    fun editMessage(messageId: String) {
        val state = _uiState.value
        val session = state.sessions.firstOrNull { it.id == state.activeSessionId } ?: return
        val index = session.messages.indexOfFirst { it.id == messageId }
        if (index < 0) return
        val message = session.messages[index]
        if (message.role != MessageRole.User) return

        val editedSession = session.copy(
            messages = session.messages.take(index),
            previousResponseId = null,
            updatedAt = System.currentTimeMillis(),
        )
        val sessions = state.sessions.replaceSession(editedSession)
        settingsStore.saveChatSessions(sessions)
        _uiState.update {
            it.copy(
                sessions = sessions,
                messages = editedSession.messages,
                input = message.content,
                previousResponseId = null,
                error = null,
            )
        }
    }

    fun openSettings() {
        _uiState.update { it.copy(isSettingsOpen = true) }
    }

    fun closeSettings() {
        _uiState.update { it.copy(isSettingsOpen = false) }
    }

    fun openInfo() {
        _uiState.update { it.copy(isInfoOpen = true) }
    }

    fun closeInfo() {
        _uiState.update { it.copy(isInfoOpen = false) }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    fun updateApiUrl(value: String) {
        settingsStore.apiUrl = value
        _uiState.update {
            it.copy(
                apiUrl = value,
                modelLoadError = null,
            )
        }
    }

    fun updateModel(value: String) {
        settingsStore.model = value
        _uiState.update { it.copy(model = value) }
    }

    fun updateApiToken(value: String) {
        settingsStore.apiToken = value.trim()
        _uiState.update {
            it.copy(
                apiToken = value.trim(),
                modelLoadError = null,
            )
        }
    }

    fun updateServerToolsEnabled(value: Boolean) {
        settingsStore.serverToolsEnabled = value
        _uiState.update {
            it.copy(
                serverToolsEnabled = value,
                previousResponseId = null,
            )
        }
        refreshModels(silent = true)
    }

    fun updateServerIntegrations(value: String) {
        settingsStore.serverIntegrations = value
        _uiState.update {
            it.copy(
                serverIntegrations = value,
                previousResponseId = null,
            )
        }
    }

    fun updateAllowedTools(value: String) {
        settingsStore.allowedTools = value
        _uiState.update {
            it.copy(
                allowedTools = value,
                previousResponseId = null,
            )
        }
    }

    fun updateNativeIntentToolEnabled(value: Boolean) {
        settingsStore.nativeIntentToolEnabled = value
        _uiState.update { it.copy(nativeIntentToolEnabled = value, previousResponseId = null) }
    }

    fun updateCalendarToolEnabled(value: Boolean) {
        settingsStore.calendarToolEnabled = value
        _uiState.update { it.copy(calendarToolEnabled = value, previousResponseId = null) }
    }

    fun updateContactsToolEnabled(value: Boolean) {
        settingsStore.contactsToolEnabled = value
        _uiState.update { it.copy(contactsToolEnabled = value, previousResponseId = null) }
    }

    fun updateNotificationDigestToolEnabled(value: Boolean) {
        settingsStore.notificationDigestToolEnabled = value
        _uiState.update { it.copy(notificationDigestToolEnabled = value, previousResponseId = null) }
    }

    fun updateLocalFileSearchToolEnabled(value: Boolean) {
        settingsStore.localFileSearchToolEnabled = value
        _uiState.update { it.copy(localFileSearchToolEnabled = value, previousResponseId = null) }
    }

    fun updateLocalFileSearchTreeUri(value: String) {
        settingsStore.localFileSearchTreeUri = value
        _uiState.update { it.copy(localFileSearchTreeUri = value, previousResponseId = null) }
    }

    fun updateDeviceStatusToolEnabled(value: Boolean) {
        settingsStore.deviceStatusToolEnabled = value
        _uiState.update { it.copy(deviceStatusToolEnabled = value, previousResponseId = null) }
    }

    fun updateVoiceInputEnabled(value: Boolean) {
        settingsStore.voiceInputEnabled = value
        _uiState.update { it.copy(voiceInputEnabled = value) }
    }

    fun updateVoiceOutputEnabled(value: Boolean) {
        settingsStore.voiceOutputEnabled = value
        _uiState.update { it.copy(voiceOutputEnabled = value) }
    }

    fun updateAutoReadAnswersEnabled(value: Boolean) {
        settingsStore.autoReadAnswersEnabled = value
        _uiState.update { it.copy(autoReadAnswersEnabled = value) }
    }

    fun updateAppendDateTimeToSystemPrompt(value: Boolean) {
        settingsStore.appendDateTimeToSystemPrompt = value
        _uiState.update { it.copy(appendDateTimeToSystemPrompt = value, previousResponseId = null) }
    }

    fun setPendingNativeToolAction(action: NativeToolAction?) {
        _uiState.update { it.copy(pendingNativeToolAction = action) }
    }

    fun selectSystemProfile(profileId: String) {
        val profile = _uiState.value.systemProfiles.firstOrNull { it.id == profileId } ?: return
        settingsStore.activeSystemProfileId = profile.id
        _uiState.update {
            it.copy(
                activeSystemProfileId = profile.id,
                systemProfileNameDraft = profile.name,
                systemPromptDraft = profile.prompt,
            )
        }
    }

    fun updateSystemProfileNameDraft(value: String) {
        _uiState.update { it.copy(systemProfileNameDraft = value) }
    }

    fun updateSystemPromptDraft(value: String) {
        _uiState.update { it.copy(systemPromptDraft = value) }
    }

    fun updateTemperature(value: String) {
        settingsStore.temperature = value
        _uiState.update { it.copy(temperatureDraft = value, previousResponseId = null) }
    }

    fun updateTopP(value: String) {
        settingsStore.topP = value
        _uiState.update { it.copy(topPDraft = value, previousResponseId = null) }
    }

    fun updateMaxTokens(value: String) {
        settingsStore.maxTokens = value
        _uiState.update { it.copy(maxTokensDraft = value, previousResponseId = null) }
    }

    fun updateContextLength(value: String) {
        settingsStore.contextLength = value
        _uiState.update { it.copy(contextLengthDraft = value, previousResponseId = null) }
    }

    fun updatePresencePenalty(value: String) {
        settingsStore.presencePenalty = value
        _uiState.update { it.copy(presencePenaltyDraft = value, previousResponseId = null) }
    }

    fun updateFrequencyPenalty(value: String) {
        settingsStore.frequencyPenalty = value
        _uiState.update { it.copy(frequencyPenaltyDraft = value, previousResponseId = null) }
    }

    fun updateSeed(value: String) {
        settingsStore.seed = value
        _uiState.update { it.copy(seedDraft = value, previousResponseId = null) }
    }

    fun saveSystemProfile() {
        val state = _uiState.value
        val name = state.systemProfileNameDraft.trim().ifBlank { "Untitled profile" }
        val prompt = state.systemPromptDraft.trim().ifBlank { DefaultSystemPrompt }
        val activeId = state.activeSystemProfileId.ifBlank { UUID.randomUUID().toString() }
        val profile = SystemPromptProfile(
            id = activeId,
            name = name,
            prompt = prompt,
        )
        val profiles = state.systemProfiles.map {
            if (it.id == activeId) profile else it
        }.let { updated ->
            if (updated.any { it.id == activeId }) updated else updated + profile
        }
        settingsStore.activeSystemProfileId = profile.id
        settingsStore.saveSystemProfiles(profiles)
        _uiState.update {
            it.copy(
                systemProfiles = profiles,
                activeSystemProfileId = profile.id,
                systemProfileNameDraft = profile.name,
                systemPromptDraft = profile.prompt,
            )
        }
    }

    fun createSystemProfile() {
        val profile = SystemPromptProfile(
            name = "New profile",
            prompt = DefaultSystemPrompt,
        )
        val profiles = _uiState.value.systemProfiles + profile
        settingsStore.activeSystemProfileId = profile.id
        settingsStore.saveSystemProfiles(profiles)
        _uiState.update {
            it.copy(
                systemProfiles = profiles,
                activeSystemProfileId = profile.id,
                systemProfileNameDraft = profile.name,
                systemPromptDraft = profile.prompt,
            )
        }
    }

    fun deleteSystemProfile(profileId: String) {
        val state = _uiState.value
        if (state.systemProfiles.size <= 1) {
            _uiState.update { it.copy(error = "Keep at least one system profile.") }
            return
        }
        val profiles = state.systemProfiles.filterNot { it.id == profileId }
        val activeProfile = if (state.activeSystemProfileId == profileId) {
            profiles.first()
        } else {
            profiles.firstOrNull { it.id == state.activeSystemProfileId } ?: profiles.first()
        }
        settingsStore.activeSystemProfileId = activeProfile.id
        settingsStore.saveSystemProfiles(profiles)
        _uiState.update {
            it.copy(
                systemProfiles = profiles,
                activeSystemProfileId = activeProfile.id,
                systemProfileNameDraft = activeProfile.name,
                systemPromptDraft = activeProfile.prompt,
            )
        }
    }

    fun updateReasoningEnabled(value: Boolean) {
        settingsStore.reasoningEnabled = value
        _uiState.update { it.copy(reasoningEnabled = value) }
    }

    fun attachImages(attachments: List<ChatImageAttachment>) {
        if (attachments.isEmpty()) return
        _uiState.update {
            if (it.selectedModelSupportsVision()) {
                it.copy(attachedImages = attachments, attachedDocumentText = null, error = null)
            } else {
                it.copy(error = "The selected model does not report vision support. Pick a vision model first.")
            }
        }
    }

    fun attachDocumentText(document: ChatDocumentAttachment) {
        _uiState.update {
            it.copy(
                attachedDocumentText = document,
                attachedImages = emptyList(),
                error = null,
            )
        }
    }

    fun clearAttachments() {
        _uiState.update {
            it.copy(
                attachedImages = emptyList(),
                attachedDocumentText = null,
            )
        }
    }

    fun refreshModels(silent: Boolean) {
        val apiUrl = _uiState.value.apiUrl
        val apiToken = _uiState.value.apiToken

        _uiState.update {
            it.copy(
                isLoadingModels = true,
                modelLoadError = null,
            )
        }

        viewModelScope.launch {
            val result = client.listModels(
                apiUrl = apiUrl,
                apiToken = apiToken,
            )

            _uiState.update { current ->
                result.fold(
                    onSuccess = { modelInfos ->
                        val models = modelInfos.map { it.id }
                        val selectedModel = when {
                            current.model in models -> current.model
                            models.isNotEmpty() -> models.first()
                            else -> ""
                        }
                        if (selectedModel.isNotBlank()) {
                            settingsStore.model = selectedModel
                        }
                        current.copy(
                            availableModels = models,
                            availableModelInfos = modelInfos,
                            model = selectedModel,
                            attachedImages = current.attachedImages.takeIf {
                                modelInfos.firstOrNull { modelInfo -> modelInfo.id == selectedModel }?.supportsVision == true
                            } ?: emptyList(),
                            isLoadingModels = false,
                            modelLoadError = null,
                        )
                    },
                    onFailure = { throwable ->
                        current.copy(
                            isLoadingModels = false,
                            modelLoadError = if (silent) null else throwable.friendlyMessage(),
                            error = if (silent) current.error else throwable.friendlyMessage(),
                        )
                    },
                )
            }
        }
    }

    fun exportChatHistoryJson(): String =
        SettingsStore.chatSessionsToJson(_uiState.value.sessions)

    fun importChatHistory(json: String) {
        val importedSessions = runCatching {
            SettingsStore.chatSessionsFromJson(json).ifEmpty { listOf(ChatSession()) }
        }.getOrElse { throwable ->
            _uiState.update { it.copy(error = "Could not import chat history: ${throwable.message}") }
            return
        }
        val activeSession = importedSessions.first()
        settingsStore.activeSessionId = activeSession.id
        settingsStore.saveChatSessions(importedSessions)
        _uiState.update {
            it.copy(
                sessions = importedSessions,
                activeSessionId = activeSession.id,
                messages = activeSession.messages,
                previousResponseId = activeSession.previousResponseId,
                input = "",
                error = null,
            )
        }
    }

    fun showError(message: String) {
        _uiState.update { it.copy(error = message) }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ChatViewModel(
                        settingsStore = SettingsStore(context.applicationContext),
                        client = LmStudioClient(),
                    ) as T
                }
            }
    }
}

class SettingsStore(context: Context) {
    private val preferences = context.getSharedPreferences("lm_studio_chat", Context.MODE_PRIVATE)

    var activeSessionId: String
        get() = preferences.getString("active_session_id", "") ?: ""
        set(value) {
            preferences.edit().putString("active_session_id", value).apply()
        }

    var apiUrl: String
        get() = preferences.getString("api_url", DefaultApiUrl) ?: DefaultApiUrl
        set(value) {
            preferences.edit().putString("api_url", value).apply()
        }

    var model: String
        get() = preferences.getString("model", "") ?: ""
        set(value) {
            preferences.edit().putString("model", value).apply()
        }

    var apiToken: String
        get() = preferences.getString("api_token", "") ?: ""
        set(value) {
            preferences.edit().putString("api_token", value).apply()
        }

    var serverToolsEnabled: Boolean
        get() = preferences.getBoolean("server_tools_enabled", false)
        set(value) {
            preferences.edit().putBoolean("server_tools_enabled", value).apply()
        }

    var serverIntegrations: String
        get() = preferences.getString("server_integrations", "") ?: ""
        set(value) {
            preferences.edit().putString("server_integrations", value).apply()
        }

    var allowedTools: String
        get() = preferences.getString("allowed_tools", "") ?: ""
        set(value) {
            preferences.edit().putString("allowed_tools", value).apply()
        }

    var nativeIntentToolEnabled: Boolean
        get() = preferences.getBoolean("native_intent_tool_enabled", false)
        set(value) {
            preferences.edit().putBoolean("native_intent_tool_enabled", value).apply()
        }

    var calendarToolEnabled: Boolean
        get() = preferences.getBoolean("calendar_tool_enabled", false)
        set(value) {
            preferences.edit().putBoolean("calendar_tool_enabled", value).apply()
        }

    var contactsToolEnabled: Boolean
        get() = preferences.getBoolean("contacts_tool_enabled", false)
        set(value) {
            preferences.edit().putBoolean("contacts_tool_enabled", value).apply()
        }

    var notificationDigestToolEnabled: Boolean
        get() = preferences.getBoolean("notification_digest_tool_enabled", false)
        set(value) {
            preferences.edit().putBoolean("notification_digest_tool_enabled", value).apply()
        }

    var localFileSearchToolEnabled: Boolean
        get() = preferences.getBoolean("local_file_search_tool_enabled", false)
        set(value) {
            preferences.edit().putBoolean("local_file_search_tool_enabled", value).apply()
        }

    var localFileSearchTreeUri: String
        get() = preferences.getString("local_file_search_tree_uri", "") ?: ""
        set(value) {
            preferences.edit().putString("local_file_search_tree_uri", value).apply()
        }

    var deviceStatusToolEnabled: Boolean
        get() = preferences.getBoolean("device_status_tool_enabled", false)
        set(value) {
            preferences.edit().putBoolean("device_status_tool_enabled", value).apply()
        }

    var voiceInputEnabled: Boolean
        get() = preferences.getBoolean("voice_input_enabled", false)
        set(value) {
            preferences.edit().putBoolean("voice_input_enabled", value).apply()
        }

    var voiceOutputEnabled: Boolean
        get() = preferences.getBoolean("voice_output_enabled", false)
        set(value) {
            preferences.edit().putBoolean("voice_output_enabled", value).apply()
        }

    var autoReadAnswersEnabled: Boolean
        get() = preferences.getBoolean("auto_read_answers_enabled", false)
        set(value) {
            preferences.edit().putBoolean("auto_read_answers_enabled", value).apply()
        }

    var appendDateTimeToSystemPrompt: Boolean
        get() = preferences.getBoolean("append_date_time_to_system_prompt", false)
        set(value) {
            preferences.edit().putBoolean("append_date_time_to_system_prompt", value).apply()
        }

    var temperature: String
        get() = preferences.getString("temperature", "0.7") ?: "0.7"
        set(value) {
            preferences.edit().putString("temperature", value).apply()
        }

    var topP: String
        get() = preferences.getString("top_p", "") ?: ""
        set(value) {
            preferences.edit().putString("top_p", value).apply()
        }

    var maxTokens: String
        get() = preferences.getString("max_tokens", "") ?: ""
        set(value) {
            preferences.edit().putString("max_tokens", value).apply()
        }

    var contextLength: String
        get() = preferences.getString("context_length", "") ?: ""
        set(value) {
            preferences.edit().putString("context_length", value).apply()
        }

    var presencePenalty: String
        get() = preferences.getString("presence_penalty", "") ?: ""
        set(value) {
            preferences.edit().putString("presence_penalty", value).apply()
        }

    var frequencyPenalty: String
        get() = preferences.getString("frequency_penalty", "") ?: ""
        set(value) {
            preferences.edit().putString("frequency_penalty", value).apply()
        }

    var seed: String
        get() = preferences.getString("seed", "") ?: ""
        set(value) {
            preferences.edit().putString("seed", value).apply()
        }

    var reasoningEnabled: Boolean
        get() = preferences.getBoolean("reasoning_enabled", true)
        set(value) {
            preferences.edit().putBoolean("reasoning_enabled", value).apply()
        }

    var activeSystemProfileId: String
        get() = preferences.getString("active_system_profile_id", DefaultSystemProfileId) ?: DefaultSystemProfileId
        set(value) {
            preferences.edit().putString("active_system_profile_id", value).apply()
        }

    fun loadChatSessions(): List<ChatSession> =
        runCatching {
            chatSessionsFromJson(preferences.getString("chat_history", "").orEmpty())
        }.getOrDefault(emptyList())

    fun saveChatSessions(sessions: List<ChatSession>) {
        preferences.edit()
            .putString("chat_history", chatSessionsToJson(sessions))
            .apply()
    }

    fun loadSystemProfiles(): List<SystemPromptProfile> =
        runCatching {
            systemProfilesFromJson(preferences.getString("system_profiles", "").orEmpty())
        }.getOrDefault(defaultSystemProfiles())

    fun saveSystemProfiles(profiles: List<SystemPromptProfile>) {
        preferences.edit()
            .putString("system_profiles", systemProfilesToJson(profiles.ifEmpty { defaultSystemProfiles() }))
            .apply()
    }

    companion object {
        fun defaultSystemProfiles(): List<SystemPromptProfile> =
            listOf(
                SystemPromptProfile(
                    id = DefaultSystemProfileId,
                    name = "Default",
                    prompt = DefaultSystemPrompt,
                ),
            )

        fun systemProfilesToJson(profiles: List<SystemPromptProfile>): String {
            val profilesJson = JSONArray()
            profiles.forEach { profile ->
                profilesJson.put(
                    JSONObject()
                        .put("id", profile.id)
                        .put("name", profile.name)
                        .put("prompt", profile.prompt),
                )
            }
            return JSONObject()
                .put("version", 1)
                .put("profiles", profilesJson)
                .toString()
        }

        fun systemProfilesFromJson(json: String): List<SystemPromptProfile> {
            if (json.isBlank()) return defaultSystemProfiles()
            val profilesJson = JSONObject(json).optJSONArray("profiles") ?: JSONArray()
            val profiles = buildList {
                for (index in 0 until profilesJson.length()) {
                    val profileJson = profilesJson.optJSONObject(index) ?: continue
                    val prompt = profileJson.optString("prompt").ifBlank { DefaultSystemPrompt }
                    add(
                        SystemPromptProfile(
                            id = profileJson.optString("id").ifBlank { UUID.randomUUID().toString() },
                            name = profileJson.optString("name").ifBlank { "Profile ${index + 1}" },
                            prompt = prompt,
                        ),
                    )
                }
            }
            return profiles.ifEmpty { defaultSystemProfiles() }
        }

        fun chatSessionsToJson(sessions: List<ChatSession>): String {
            val sessionsJson = JSONArray()
            sessions.forEach { session ->
                val messagesJson = JSONArray()
                session.messages.forEach { message ->
                    val attachmentsJson = JSONArray()
                    message.attachments.forEach { attachment ->
                        attachmentsJson.put(
                            JSONObject()
                                .put("label", attachment.label)
                                .put("mime_type", attachment.mimeType),
                        )
                    }
                    messagesJson.put(
                        JSONObject()
                            .put("id", message.id)
                            .put("role", message.role.apiName)
                            .put("content", message.content)
                            .put("attachments", attachmentsJson)
                            .put("is_streaming", false),
                    )
                }
                sessionsJson.put(
                    JSONObject()
                        .put("id", session.id)
                        .put("title", session.title)
                        .put("previous_response_id", session.previousResponseId)
                        .put("created_at", session.createdAt)
                        .put("updated_at", session.updatedAt)
                        .put("messages", messagesJson),
                )
            }
            return JSONObject()
                .put("version", 1)
                .put("exported_at", System.currentTimeMillis())
                .put("sessions", sessionsJson)
                .toString(2)
        }

        fun chatSessionsFromJson(json: String): List<ChatSession> {
            if (json.isBlank()) return emptyList()
            val root = JSONObject(json)
            val sessionsJson = root.optJSONArray("sessions") ?: JSONArray()
            return buildList {
                for (sessionIndex in 0 until sessionsJson.length()) {
                    val sessionJson = sessionsJson.optJSONObject(sessionIndex) ?: continue
                    val messagesJson = sessionJson.optJSONArray("messages") ?: JSONArray()
                    val messages = buildList {
                        for (messageIndex in 0 until messagesJson.length()) {
                            val messageJson = messagesJson.optJSONObject(messageIndex) ?: continue
                            val role = when (messageJson.optString("role")) {
                                MessageRole.User.apiName -> MessageRole.User
                                MessageRole.Assistant.apiName -> MessageRole.Assistant
                                else -> continue
                            }
                            val content = messageJson.optString("content")
                            if (content.isBlank()) continue
                            val attachmentsJson = messageJson.optJSONArray("attachments") ?: JSONArray()
                            val attachments = buildList {
                                for (attachmentIndex in 0 until attachmentsJson.length()) {
                                    val attachmentJson = attachmentsJson.optJSONObject(attachmentIndex) ?: continue
                                    val label = attachmentJson.optString("label").ifBlank { "Image" }
                                    add(
                                        ChatImageAttachment(
                                            dataUrl = "",
                                            label = label,
                                            mimeType = attachmentJson.optString("mime_type").ifBlank { "image/jpeg" },
                                        ),
                                    )
                                }
                            }
                            add(
                                ChatMessage(
                                    id = messageJson.optString("id").ifBlank { UUID.randomUUID().toString() },
                                    role = role,
                                    content = content,
                                    attachments = attachments,
                                ),
                            )
                        }
                    }
                    add(
                        ChatSession(
                            id = sessionJson.optString("id").ifBlank { UUID.randomUUID().toString() },
                            title = sessionJson.optString("title").ifBlank {
                                messages.firstOrNull { it.role == MessageRole.User }?.content?.toChatTitle()
                                    ?: "New chat"
                            },
                            messages = messages,
                            previousResponseId = sessionJson.optString("previous_response_id").takeIf { it.isNotBlank() },
                            createdAt = sessionJson.optLong("created_at", System.currentTimeMillis()),
                            updatedAt = sessionJson.optLong("updated_at", System.currentTimeMillis()),
                        ),
                    )
                }
            }.sortedByDescending { it.updatedAt }
        }
    }
}

data class ChatCompletionResult(
    val content: String,
    val responseId: String? = null,
    val modelId: String? = null,
)

enum class ChatStreamEventType {
    MessageDelta,
    ReasoningDelta,
    ToolStarted,
    ToolSucceeded,
    ToolFailed,
    Error,
}

data class ChatStreamEvent(
    val type: ChatStreamEventType,
    val content: String = "",
    val tool: String = "",
    val provider: String = "",
)

private fun ChatStreamEvent.toolLabel(): String =
    when {
        tool.isBlank() -> ""
        provider.isBlank() -> tool
        else -> "$tool via $provider"
    }

private fun List<ChatSession>.replaceSession(session: ChatSession): List<ChatSession> {
    val replaced = map { if (it.id == session.id) session else it }
    return if (any { it.id == session.id }) {
        replaced.sortedByDescending { it.updatedAt }
    } else {
        (listOf(session) + this).sortedByDescending { it.updatedAt }
    }
}

private fun String.toChatTitle(): String {
    val compact = trim()
        .replace(Regex("\\s+"), " ")
    return when {
        compact.isBlank() -> "New chat"
        compact.length <= 44 -> compact
        else -> compact.take(44).trimEnd() + "..."
    }
}

private fun buildAssistantContent(
    tools: List<String>,
    reasoning: String,
    message: String,
    errors: List<String> = emptyList(),
): String = buildString {
    if (tools.isNotEmpty()) {
        append("Tools used: ")
        append(tools.distinct().joinToString(", "))
        append("\n\n")
    }
    if (reasoning.isNotBlank()) {
        append("<|channel>thought\n<channel|>")
        append(reasoning.trim())
        append("\n\n")
    }
    if (reasoning.isNotBlank() && message.isNotBlank()) {
        append("<|channel>answer\n<channel|>")
    }
    append(message)
    if (errors.isNotEmpty()) {
        if (isNotBlank()) append("\n\n")
        append("Tool errors: ")
        append(errors.joinToString(", "))
    }
}

private fun ChatUiState.selectedModelInfo(): ModelInfo? =
    availableModelInfos.firstOrNull { it.id == model }

private fun ChatUiState.selectedModelSupportsVision(): Boolean =
    selectedModelInfo()?.supportsVision == true

private fun ChatUiState.selectedModelSupportsReasoningToggle(): Boolean {
    val options = selectedModelInfo()?.reasoningOptions.orEmpty()
    return options.any { it != "off" } && options.any { it == "off" }
}

private fun ChatUiState.reasoningRequestValue(): String? {
    val info = selectedModelInfo() ?: return null
    val options = info.reasoningOptions
    if (options.none { it != "off" }) return null

    return if (reasoningEnabled) {
        info.defaultReasoning
            .takeIf { it.isNotBlank() && it != "off" && (options.isEmpty() || it in options) }
            ?: options.firstOrNull { it != "off" }
    } else {
        "off".takeIf { it in options }
    }
}

private fun ChatUiState.generationSettings(): GenerationSettings =
    GenerationSettings(
        temperature = temperatureDraft.toNullableDouble(),
        topP = topPDraft.toNullableDouble(),
        maxTokens = maxTokensDraft.toNullableInt(),
        contextLength = contextLengthDraft.toNullableInt(),
        presencePenalty = presencePenaltyDraft.toNullableDouble(),
        frequencyPenalty = frequencyPenaltyDraft.toNullableDouble(),
        seed = seedDraft.toNullableInt(),
    )

private fun String.toNullableDouble(): Double? =
    trim().takeIf { it.isNotBlank() }?.toDoubleOrNull()

private fun String.toNullableInt(): Int? =
    trim().takeIf { it.isNotBlank() }?.toIntOrNull()

private fun ChatUiState.activeSystemPrompt(): String =
    (systemProfiles.firstOrNull { it.id == activeSystemProfileId }?.prompt
        ?.takeIf { it.isNotBlank() }
        ?: systemPromptDraft.takeIf { it.isNotBlank() }
        ?: DefaultSystemPrompt)
        .withPhoneDateTimePrompt(this)
        .withNativeToolPrompt(this)

private fun String.withPhoneDateTimePrompt(state: ChatUiState): String {
    if (!state.appendDateTimeToSystemPrompt) return this
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())
    val phoneDateTime = LocalDateTime.now()
        .atZone(ZoneId.systemDefault())
        .format(formatter)
    return "$this\n\nCurrent phone date/time: $phoneDateTime."
}

private fun String.withNativeToolPrompt(state: ChatUiState): String {
    val prompt = state.nativeToolSystemPrompt()
    return if (prompt.isBlank()) this else "$this\n\n$prompt"
}

private fun ChatUiState.nativeToolSystemPrompt(): String {
    val tools = mutableListOf<String>()
    if (nativeIntentToolEnabled) {
        tools += "open_url {url}"
        tools += "maps_route {query}"
        tools += "email_draft {to, subject, body}"
        tools += "phone_dial {phone}"
        tools += "sms_draft {phone, body}"
    }
    if (calendarToolEnabled) {
        tools += "calendar_event {title, start, end, location, description}"
        tools += "reminder {title, time, notes}"
    }
    if (contactsToolEnabled) tools += "contacts_lookup {name}"
    if (notificationDigestToolEnabled) tools += "notification_digest {limit}"
    if (localFileSearchToolEnabled) tools += "local_file_search {query}"
    if (deviceStatusToolEnabled) tools += "device_status {}"
    if (tools.isEmpty()) return ""

    return """
        LMSMOB phone-side tools are available, but only when the user confirms inside the Android app.
        If one of these tools is needed, reply with a short explanation and exactly one action block:
        <lmsmob_action>{"tool":"tool_name","args":{"key":"value"}}</lmsmob_action>
        Available phone-side tools: ${tools.joinToString("; ")}.
        Do not claim the action is complete until the app returns a tool result or the user confirms the draft.
        Phone-side tools are drafts only for calls, SMS, email, calendar, maps, and URLs; the user performs the final send/call/save.
    """.trimIndent()
}

private fun String.extractNativeToolAction(state: ChatUiState): NativeToolAction? {
    val match = NativeActionRegex.find(this) ?: return null
    return runCatching {
        val json = JSONObject(match.groupValues[1].trim())
        val tool = json.optString("tool")
            .ifBlank { json.optString("name") }
            .trim()
        val args = json.optJSONObject("args") ?: JSONObject()
        if (tool.isBlank() || !state.isNativeToolEnabled(tool)) {
            null
        } else {
            NativeToolAction(tool = tool, args = args)
        }
    }.getOrNull()
}

private fun String.withoutNativeActionBlocks(): String =
    NativeActionRegex.replace(this, "").trim()

private fun ChatMessage.deletePreview(): String =
    content
        .withoutNativeActionBlocks()
        .replace(Regex("\\s+"), " ")
        .trim()
        .take(220)
        .ifBlank {
            if (attachments.isNotEmpty()) {
                attachments.joinToString(", ") { it.label }
            } else {
                "Empty message"
            }
        }

private fun ChatUiState.isNativeToolEnabled(tool: String): Boolean =
    when (tool) {
        "open_url",
        "maps_route",
        "email_draft",
        "phone_dial",
        "sms_draft" -> nativeIntentToolEnabled
        "calendar_event",
        "reminder" -> calendarToolEnabled
        "contacts_lookup" -> contactsToolEnabled
        "notification_digest" -> notificationDigestToolEnabled
        "local_file_search" -> localFileSearchToolEnabled
        "device_status" -> deviceStatusToolEnabled
        else -> false
    }

private fun NativeToolAction.displayTitle(): String =
    when (tool) {
        "open_url" -> "Open URL"
        "maps_route" -> "Open Maps Route"
        "email_draft" -> "Create Email Draft"
        "phone_dial" -> "Open Phone Dialer"
        "sms_draft" -> "Create SMS Draft"
        "calendar_event" -> "Create Calendar Event"
        "reminder" -> "Create Reminder"
        "contacts_lookup" -> "Look Up Contacts"
        "notification_digest" -> "Read Notification Digest"
        "local_file_search" -> "Search Local Files"
        "device_status" -> "Read Device Status"
        else -> tool
    }

private fun NativeToolAction.displaySummary(): String =
    when (tool) {
        "open_url" -> args.arg("url")
        "maps_route" -> args.arg("query", "destination")
        "email_draft" -> listOf(args.arg("to"), args.arg("subject")).filter { it.isNotBlank() }.joinToString(" - ")
        "phone_dial" -> args.arg("phone", "phone_number", "number")
        "sms_draft" -> listOf(args.arg("phone", "phone_number", "number"), args.arg("body", "message")).filter { it.isNotBlank() }.joinToString(" - ")
        "calendar_event" -> listOf(args.arg("title"), args.arg("start")).filter { it.isNotBlank() }.joinToString(" - ")
        "reminder" -> listOf(args.arg("title"), args.arg("time", "start")).filter { it.isNotBlank() }.joinToString(" - ")
        "contacts_lookup" -> args.arg("name", "query")
        "notification_digest" -> "Summarize recent notifications stored by LMSMOB Chat"
        "local_file_search" -> args.arg("query")
        "device_status" -> "Battery, network, storage, app and LM Studio connectivity"
        else -> args.toString()
    }.ifBlank { "No extra details provided." }

private fun JSONObject.arg(vararg names: String): String {
    names.forEach { name ->
        optString(name).takeIf { it.isNotBlank() }?.let { return it }
    }
    return ""
}

private fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("LM Studio message", text))
}

private suspend fun handleIncomingShareIntent(
    context: Context,
    intent: Intent,
    viewModel: ChatViewModel,
    onDocumentShared: (Uri) -> Unit,
) {
    intent.sharedTextPayload()
        ?.takeIf { it.isNotBlank() }
        ?.let(viewModel::receiveSharedText)

    val uris = intent.sharedStreamUris()
    if (uris.isEmpty()) return

    val imageUris = uris.filter { uri -> context.isSharedImage(uri, intent.type) }
    val documentUris = uris.filterNot { uri -> uri in imageUris }

    if (imageUris.isNotEmpty()) {
        runCatching {
            withContext(Dispatchers.IO) {
                imageUris.map { uri -> context.imageUriToAttachment(uri) }
            }
        }.onSuccess(viewModel::attachImages)
            .onFailure { throwable -> viewModel.showError(throwable.friendlyMessage()) }
    }

    if (documentUris.size > 1) {
        viewModel.showError("Only one shared document can be prepared at a time.")
    }
    documentUris.firstOrNull()?.let(onDocumentShared)
}

private fun Intent.isSupportedShareAction(): Boolean =
    action == Intent.ACTION_SEND ||
        action == Intent.ACTION_SEND_MULTIPLE ||
        action == Intent.ACTION_PROCESS_TEXT

private fun Intent.sharedTextPayload(): String? {
    val processText = getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
    val sharedText = getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
    val subject = getCharSequenceExtra(Intent.EXTRA_SUBJECT)?.toString()
    return listOfNotNull(subject, processText ?: sharedText)
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .distinct()
        .joinToString("\n\n")
        .ifBlank { null }
}

@Suppress("DEPRECATION")
private fun Intent.sharedStreamUris(): List<Uri> =
    when (action) {
        Intent.ACTION_SEND -> listOfNotNull(getParcelableExtra(Intent.EXTRA_STREAM))
        Intent.ACTION_SEND_MULTIPLE -> getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM).orEmpty()
        else -> emptyList()
    }

private fun Context.isSharedImage(uri: Uri, fallbackMimeType: String?): Boolean {
    val mimeType = contentResolver.getType(uri) ?: fallbackMimeType.orEmpty()
    return mimeType.startsWith("image/")
}

private suspend fun executeNativeToolAction(
    context: Context,
    state: ChatUiState,
    action: NativeToolAction,
    viewModel: ChatViewModel,
) {
    runCatching {
        when (action.tool) {
            "open_url" -> context.openUrlDraft(action.args.arg("url"))
            "maps_route" -> context.openMapsDraft(action.args.arg("query", "destination", "address"))
            "email_draft" -> context.openEmailDraft(action.args)
            "phone_dial" -> context.openDialerDraft(action.args.arg("phone", "phone_number", "number"))
            "sms_draft" -> context.openSmsDraft(action.args)
            "calendar_event" -> context.openCalendarDraft(action.args, reminder = false)
            "reminder" -> context.openCalendarDraft(action.args, reminder = true)
            "contacts_lookup" -> {
                val result = withContext(Dispatchers.IO) {
                    context.lookupContacts(action.args.arg("name", "query"))
                }
                viewModel.receiveSharedText(result.asNativeToolResult("contacts_lookup"))
            }
            "notification_digest" -> {
                val result = withContext(Dispatchers.IO) {
                    if (!context.isNotificationListenerEnabled()) {
                        throw IOException("Notification access is not enabled. Turn it on in Android notification access settings.")
                    }
                    NotificationDigestStore.digest(context, action.args.optInt("limit", 20))
                }
                viewModel.receiveSharedText(result.asNativeToolResult("notification_digest"))
            }
            "local_file_search" -> {
                val result = withContext(Dispatchers.IO) {
                    context.searchLocalFiles(
                        treeUriString = state.localFileSearchTreeUri,
                        query = action.args.arg("query"),
                    )
                }
                viewModel.receiveSharedText(result.asNativeToolResult("local_file_search"))
            }
            "device_status" -> {
                val result = withContext(Dispatchers.IO) {
                    context.deviceStatusText(state)
                }
                viewModel.receiveSharedText(result.asNativeToolResult("device_status"))
            }
            else -> throw IOException("Unsupported phone-side tool: ${action.tool}")
        }
    }.onFailure { throwable ->
        viewModel.showError(throwable.friendlyMessage())
    }
}

private fun String.asNativeToolResult(tool: String): String =
    "Phone-side tool result: $tool\n$this\n\nUse this result to continue answering my request."

private fun Context.openUrlDraft(url: String) {
    val normalized = url.trim().let { value ->
        when {
            value.isBlank() -> throw IOException("URL is missing.")
            value.startsWith("http://", true) || value.startsWith("https://", true) -> value
            else -> "https://$value"
        }
    }
    startSafeActivity(Intent(Intent.ACTION_VIEW, Uri.parse(normalized)))
}

private fun Context.openMapsDraft(query: String) {
    if (query.isBlank()) throw IOException("Map destination is missing.")
    val uri = Uri.parse("geo:0,0?q=${Uri.encode(query)}")
    startSafeActivity(Intent(Intent.ACTION_VIEW, uri))
}

private fun Context.openEmailDraft(args: JSONObject) {
    val to = args.arg("to", "email")
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:${Uri.encode(to)}")
        putExtra(Intent.EXTRA_SUBJECT, args.arg("subject"))
        putExtra(Intent.EXTRA_TEXT, args.arg("body", "message"))
    }
    startSafeActivity(intent)
}

private fun Context.openDialerDraft(phone: String) {
    if (phone.isBlank()) throw IOException("Phone number is missing.")
    startSafeActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Uri.encode(phone)}")))
}

private fun Context.openSmsDraft(args: JSONObject) {
    val phone = args.arg("phone", "phone_number", "number")
    if (phone.isBlank()) throw IOException("SMS phone number is missing.")
    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${Uri.encode(phone)}")).apply {
        putExtra("sms_body", args.arg("body", "message"))
    }
    startSafeActivity(intent)
}

private fun Context.openCalendarDraft(args: JSONObject, reminder: Boolean) {
    val title = args.arg("title").ifBlank {
        if (reminder) "Reminder" else "Calendar event"
    }
    val startText = args.arg("start", "time")
    val startMillis = startText.toEventMillis()
    val endMillis = args.arg("end").toEventMillis()
    val intent = Intent(Intent.ACTION_INSERT).apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.Events.TITLE, if (reminder) "Reminder: $title" else title)
        putExtra(CalendarContract.Events.DESCRIPTION, args.arg("description", "notes", "body"))
        putExtra(CalendarContract.Events.EVENT_LOCATION, args.arg("location"))
        if (startMillis != null) putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
        if (endMillis != null) putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
    }
    startSafeActivity(intent)
}

private fun Context.startSafeActivity(intent: Intent) {
    try {
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (_: ActivityNotFoundException) {
        throw IOException("No Android app is available to handle this action.")
    }
}

private fun String.toEventMillis(): Long? {
    val value = trim()
    if (value.isBlank()) return null
    return runCatching { Instant.parse(value).toEpochMilli() }.getOrNull()
        ?: runCatching {
            LocalDateTime.parse(value)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }.getOrNull()
}

private fun Context.imageUriToAttachment(uri: Uri): ChatImageAttachment {
    val bitmap = contentResolver.openInputStream(uri)?.use { input ->
        BitmapFactory.decodeStream(input)
    } ?: throw IOException("Could not read the selected image")
    val label = uri.lastPathSegment
        ?.substringAfterLast('/')
        ?.takeIf { it.isNotBlank() }
        ?: "Gallery image"
    return bitmap.toAttachment(label)
}

private fun Bitmap.toAttachment(label: String): ChatImageAttachment {
    val scaled = scaledForVision()
    val output = ByteArrayOutputStream()
    scaled.compress(Bitmap.CompressFormat.JPEG, 88, output)
    val base64 = Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
    return ChatImageAttachment(
        dataUrl = "data:image/jpeg;base64,$base64",
        label = label,
        mimeType = "image/jpeg",
    )
}

private fun Bitmap.scaledForVision(maxDimension: Int = 1568): Bitmap {
    val longestSide = maxOf(width, height)
    if (longestSide <= maxDimension) return this
    val scale = maxDimension.toFloat() / longestSide.toFloat()
    return Bitmap.createScaledBitmap(
        this,
        (width * scale).toInt().coerceAtLeast(1),
        (height * scale).toInt().coerceAtLeast(1),
        true,
    )
}

private fun Context.documentUriToPlainText(uri: Uri): ChatDocumentAttachment {
    val name = documentDisplayName(uri)
    val mimeType = contentResolver.getType(uri).orEmpty()
    val extension = name.substringAfterLast('.', "").lowercase()
    if (extension == "pdf" || mimeType == "application/pdf") {
        throw IOException("PDF plain-text extraction is not available on-device. Choose Images to render PDF pages for a vision model.")
    }
    val bytes = contentResolver.openInputStream(uri)?.use { input -> input.readBytes() }
        ?: throw IOException("Could not read document")
    val text = when (extension) {
        "docx" -> extractDocxText(bytes)
        "xlsx" -> extractXlsxText(bytes)
        "doc", "xls" -> extractBinaryStrings(bytes)
        else -> bytes.toString(Charsets.UTF_8)
    }.normalizeExtractedText()

    if (text.isBlank()) {
        throw IOException("Could not extract readable text from $name.")
    }

    return ChatDocumentAttachment(
        name = name,
        text = text.take(MaxDocumentTextChars),
        mimeType = mimeType.ifBlank { "text/plain" },
    )
}

private fun Context.documentUriToImageAttachments(uri: Uri): List<ChatImageAttachment> {
    val name = documentDisplayName(uri)
    val mimeType = contentResolver.getType(uri).orEmpty()
    val extension = name.substringAfterLast('.', "").lowercase()
    return if (extension == "pdf" || mimeType == "application/pdf") {
        renderPdfPages(uri, name)
    } else {
        val document = documentUriToPlainText(uri)
        textToImageAttachments(document.text, document.name)
    }
}

private fun Context.documentDisplayName(uri: Uri): String {
    contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (index >= 0 && cursor.moveToFirst()) {
            val name = cursor.getString(index)
            if (!name.isNullOrBlank()) return name
        }
    }
    return uri.lastPathSegment?.substringAfterLast('/')?.ifBlank { null } ?: "Document"
}

private fun Context.isPdfDocument(uri: Uri): Boolean {
    val name = documentDisplayName(uri)
    val mimeType = contentResolver.getType(uri).orEmpty()
    return name.substringAfterLast('.', "").equals("pdf", ignoreCase = true) ||
        mimeType == "application/pdf"
}

private fun Context.renderPdfPages(uri: Uri, name: String): List<ChatImageAttachment> {
    val descriptor = contentResolver.openFileDescriptor(uri, "r")
        ?: throw IOException("Could not open PDF")
    descriptor.use { pfd ->
        PdfRenderer(pfd).use { renderer ->
            if (renderer.pageCount == 0) throw IOException("PDF has no pages")
            return buildList {
                val pagesToRender = minOf(renderer.pageCount, 4)
                for (index in 0 until pagesToRender) {
                    renderer.openPage(index).use { page ->
                        val targetWidth = 1280
                        val ratio = targetWidth.toFloat() / page.width.toFloat()
                        val bitmap = Bitmap.createBitmap(
                            targetWidth,
                            (page.height * ratio).toInt().coerceAtLeast(1),
                            Bitmap.Config.ARGB_8888,
                        )
                        Canvas(bitmap).drawColor(android.graphics.Color.WHITE)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        add(bitmap.toAttachment("$name page ${index + 1}"))
                    }
                }
            }
        }
    }
}

private fun textToImageAttachments(text: String, name: String): List<ChatImageAttachment> {
    val chunks = text.chunked(1800).take(4)
    return chunks.mapIndexed { index, chunk ->
        val bitmap = Bitmap.createBitmap(1280, 1720, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(20, 25, 40)
            textSize = 30f
        }
        val x = 56f
        var y = 76f
        val lineHeight = 42f
        chunk.lines().flatMap { it.wrapForPaint(paint, 1168f) }.forEach { line ->
            if (y < 1660f) {
                canvas.drawText(line, x, y, paint)
                y += lineHeight
            }
        }
        bitmap.toAttachment("$name text ${index + 1}")
    }
}

private fun String.wrapForPaint(paint: Paint, maxWidth: Float): List<String> {
    if (isBlank()) return listOf("")
    val words = split(Regex("\\s+"))
    val lines = mutableListOf<String>()
    var current = ""
    words.forEach { word ->
        val candidate = if (current.isBlank()) word else "$current $word"
        if (paint.measureText(candidate) <= maxWidth) {
            current = candidate
        } else {
            if (current.isNotBlank()) lines += current
            current = word
        }
    }
    if (current.isNotBlank()) lines += current
    return lines.ifEmpty { listOf(this) }
}

private fun extractDocxText(bytes: ByteArray): String {
    val entries = extractZipTextEntries(bytes) { name ->
        name == "word/document.xml" ||
            name.startsWith("word/header") ||
            name.startsWith("word/footer")
    }
    return entries.values.joinToString("\n\n") { it.xmlBodyText() }
}

private fun extractXlsxText(bytes: ByteArray): String {
    val entries = extractZipTextEntries(bytes) { name ->
        name == "xl/sharedStrings.xml" ||
            (name.startsWith("xl/worksheets/") && name.endsWith(".xml"))
    }
    val sharedStrings = entries["xl/sharedStrings.xml"]
        ?.let(::extractSharedStrings)
        .orEmpty()
    val cellRegex = Regex("<c\\b([^>]*)>(.*?)</c>", RegexOption.DOT_MATCHES_ALL)
    val rowRegex = Regex("<row\\b[^>]*>(.*?)</row>", RegexOption.DOT_MATCHES_ALL)
    val valueRegex = Regex("<v[^>]*>(.*?)</v>", RegexOption.DOT_MATCHES_ALL)
    val textRegex = Regex("<t[^>]*>(.*?)</t>", RegexOption.DOT_MATCHES_ALL)

    return entries
        .filterKeys { it.startsWith("xl/worksheets/") && it.endsWith(".xml") }
        .toSortedMap()
        .mapNotNull { (name, xml) ->
            val rows = rowRegex.findAll(xml).mapNotNull { row ->
                val cells = cellRegex.findAll(row.groupValues[1]).map { cell ->
                    val attributes = cell.groupValues[1]
                    val body = cell.groupValues[2]
                    val inlineText = textRegex.findAll(body)
                        .joinToString(" ") { it.groupValues[1].decodeXmlEntities() }
                        .trim()
                    val rawValue = valueRegex.find(body)?.groupValues?.get(1).orEmpty().decodeXmlEntities()
                    when {
                        inlineText.isNotBlank() -> inlineText
                        attributes.contains("t=\"s\"") -> sharedStrings.getOrNull(rawValue.toIntOrNull() ?: -1).orEmpty()
                        else -> rawValue
                    }
                }.filter { it.isNotBlank() }.toList()
                cells.takeIf { it.isNotEmpty() }?.joinToString("\t")
            }.toList()

            if (rows.isEmpty()) {
                null
            } else {
                buildString {
                    appendLine(name.substringAfterLast('/').removeSuffix(".xml"))
                    append(rows.joinToString("\n"))
                }
            }
        }
        .joinToString("\n\n")
}

private fun extractSharedStrings(xml: String): List<String> {
    val itemRegex = Regex("<si\\b[^>]*>(.*?)</si>", RegexOption.DOT_MATCHES_ALL)
    val textRegex = Regex("<t[^>]*>(.*?)</t>", RegexOption.DOT_MATCHES_ALL)
    return itemRegex.findAll(xml)
        .map { item ->
            textRegex.findAll(item.groupValues[1])
                .joinToString("") { it.groupValues[1].decodeXmlEntities() }
                .trim()
        }
        .toList()
}

private fun extractZipTextEntries(
    bytes: ByteArray,
    shouldRead: (String) -> Boolean,
): Map<String, String> {
    val output = linkedMapOf<String, String>()
    ZipInputStream(bytes.inputStream()).use { zip ->
        while (true) {
            val entry = zip.nextEntry ?: break
            val name = entry.name
            if (!entry.isDirectory && shouldRead(name)) {
                output[name] = zip.readBytes().toString(Charsets.UTF_8)
            }
        }
    }
    return output
}

private fun String.xmlBodyText(): String =
    replace(Regex("<(w:p|row|sheetData|si|c|v|t)[^>]*>"), "\n")
        .replace(Regex("<[^>]+>"), " ")
        .decodeXmlEntities()

private fun String.decodeXmlEntities(): String =
    replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&apos;", "'")

private fun extractBinaryStrings(bytes: ByteArray): String {
    val ascii = Regex("[\\x20-\\x7E\\r\\n\\t]{4,}")
        .findAll(bytes.toString(Charsets.ISO_8859_1))
        .joinToString("\n") { it.value }
    val utf16 = runCatching {
        Regex("[\\p{L}\\p{N}\\p{Punct} ]{4,}")
            .findAll(bytes.toString(Charsets.UTF_16LE))
            .joinToString("\n") { it.value }
    }.getOrDefault("")
    return "$ascii\n$utf16"
}

private fun String.normalizeExtractedText(): String =
    lines()
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .joinToString("\n")
        .replace(Regex("[ \\t]{2,}"), " ")

private fun Context.hasContactsPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED

private fun Context.hasRecordAudioPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

private fun Context.lookupContacts(query: String): String {
    if (!hasContactsPermission()) throw IOException("Contacts permission is not granted.")
    if (query.isBlank()) throw IOException("Contact search query is missing.")

    val phones = mutableListOf<String>()
    contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
        ),
        "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} LIKE ?",
        arrayOf("%$query%"),
        "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} ASC",
    )?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY)
        val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        while (cursor.moveToNext() && phones.size < 8) {
            val name = cursor.getString(nameIndex).orEmpty()
            val phone = cursor.getString(phoneIndex).orEmpty()
            if (name.isNotBlank() || phone.isNotBlank()) {
                phones += "$name - $phone"
            }
        }
    }

    val emails = mutableListOf<String>()
    contentResolver.query(
        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
        arrayOf(
            ContactsContract.CommonDataKinds.Email.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
        ),
        "${ContactsContract.CommonDataKinds.Email.DISPLAY_NAME_PRIMARY} LIKE ?",
        arrayOf("%$query%"),
        "${ContactsContract.CommonDataKinds.Email.DISPLAY_NAME_PRIMARY} ASC",
    )?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DISPLAY_NAME_PRIMARY)
        val emailIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
        while (cursor.moveToNext() && emails.size < 8) {
            val name = cursor.getString(nameIndex).orEmpty()
            val email = cursor.getString(emailIndex).orEmpty()
            if (name.isNotBlank() || email.isNotBlank()) {
                emails += "$name - $email"
            }
        }
    }

    return buildString {
        appendLine("Contacts matching \"$query\":")
        if (phones.isEmpty() && emails.isEmpty()) {
            append("No matching contacts found.")
        } else {
            if (phones.isNotEmpty()) {
                appendLine("Phone numbers:")
                phones.distinct().forEach { appendLine("- $it") }
            }
            if (emails.isNotEmpty()) {
                appendLine("Email addresses:")
                emails.distinct().forEach { appendLine("- $it") }
            }
        }
    }
}

private fun Context.openNotificationListenerSettings(viewModel: ChatViewModel) {
    runCatching {
        startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }.onFailure {
        viewModel.showError("Open Android Settings and enable notification access for LMSMOB Chat.")
    }
}

private fun Context.isNotificationListenerEnabled(): Boolean {
    val enabled = Settings.Secure.getString(contentResolver, "enabled_notification_listeners").orEmpty()
    return enabled.contains(packageName, ignoreCase = true)
}

private fun Context.searchLocalFiles(treeUriString: String, query: String): String {
    if (treeUriString.isBlank()) throw IOException("Choose a search folder in settings first.")
    val treeUri = Uri.parse(treeUriString)
    val rootDocumentId = DocumentsContract.getTreeDocumentId(treeUri)
    val normalizedQuery = query.trim()
    val matches = mutableListOf<String>()
    var scanned = 0

    fun visit(parentDocumentId: String, depth: Int) {
        if (depth > 4 || scanned > 180 || matches.size >= 20) return
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, parentDocumentId)
        contentResolver.query(
            childrenUri,
            arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
            ),
            null,
            null,
            null,
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
            val nameIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
            val mimeIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE)
            while (cursor.moveToNext() && scanned <= 180 && matches.size < 20) {
                val documentId = cursor.getString(idIndex).orEmpty()
                val name = cursor.getString(nameIndex).orEmpty()
                val mimeType = cursor.getString(mimeIndex).orEmpty()
                scanned += 1
                if (mimeType == DocumentsContract.Document.MIME_TYPE_DIR) {
                    visit(documentId, depth + 1)
                } else {
                    val documentUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, documentId)
                    val snippet = runCatching {
                        documentUriToPlainText(documentUri).text.take(420)
                    }.getOrDefault("")
                    val matched = normalizedQuery.isBlank() ||
                        name.contains(normalizedQuery, ignoreCase = true) ||
                        snippet.contains(normalizedQuery, ignoreCase = true)
                    if (matched) {
                        matches += buildString {
                            append(name.ifBlank { documentId })
                            if (mimeType.isNotBlank()) append(" [$mimeType]")
                            if (snippet.isNotBlank()) append("\n").append(snippet)
                        }
                    }
                }
            }
        }
    }

    visit(rootDocumentId, depth = 0)
    return buildString {
        appendLine("Local file search query: ${normalizedQuery.ifBlank { "(all readable files)" }}")
        appendLine("Scanned files/folders: $scanned")
        if (matches.isEmpty()) {
            append("No matches found in the selected folder.")
        } else {
            appendLine("Matches:")
            matches.forEachIndexed { index, match ->
                appendLine("${index + 1}. $match")
            }
        }
    }
}

private suspend fun Context.deviceStatusText(state: ChatUiState): String {
    val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    val battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    val connectivity = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities = connectivity.getNetworkCapabilities(connectivity.activeNetwork)
    val network = when {
        capabilities == null -> "offline or unknown"
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "cellular"
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ethernet"
        else -> "connected"
    }
    val storage = StatFs(Environment.getDataDirectory().path)
    val freeGb = storage.availableBytes.toDouble() / 1024.0 / 1024.0 / 1024.0
    val totalGb = storage.totalBytes.toDouble() / 1024.0 / 1024.0 / 1024.0
    val lmStudio = LmStudioClient().listModels(state.apiUrl, state.apiToken).fold(
        onSuccess = { models -> "reachable (${models.size} models returned)" },
        onFailure = { throwable -> "not reachable: ${throwable.friendlyMessage()}" },
    )
    return """
        Device status:
        Battery: $battery%
        Network: $network
        Storage: ${"%.1f".format(freeGb)} GB free of ${"%.1f".format(totalGb)} GB
        App: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})
        LM Studio URL: ${state.apiUrl}
        LM Studio connection: $lmStudio
    """.trimIndent()
}

class LmNotificationListenerService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        NotificationDigestStore.record(applicationContext, sbn)
    }
}

private object NotificationDigestStore {
    private const val PreferencesName = "lm_studio_notification_digest"
    private const val NotificationsKey = "notifications"

    fun record(context: Context, sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString().orEmpty()
        if (title.isBlank() && text.isBlank()) return

        val preferences = context.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE)
        val existing = runCatching {
            JSONArray(preferences.getString(NotificationsKey, "[]").orEmpty())
        }.getOrDefault(JSONArray())
        val next = JSONArray()
        next.put(
            JSONObject()
                .put("time", System.currentTimeMillis())
                .put("package", sbn.packageName)
                .put("title", title)
                .put("text", text)
                .put("sub_text", subText),
        )
        for (index in 0 until minOf(existing.length(), 79)) {
            next.put(existing.optJSONObject(index))
        }
        preferences.edit().putString(NotificationsKey, next.toString()).apply()
    }

    fun digest(context: Context, limit: Int): String {
        val preferences = context.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE)
        val notifications = runCatching {
            JSONArray(preferences.getString(NotificationsKey, "[]").orEmpty())
        }.getOrDefault(JSONArray())
        if (notifications.length() == 0) {
            return "No notifications have been captured yet. Notification access may have just been enabled."
        }
        return buildString {
            appendLine("Recent notifications:")
            for (index in 0 until minOf(notifications.length(), limit.coerceIn(1, 50))) {
                val item = notifications.optJSONObject(index) ?: continue
                appendLine(
                    "- ${item.optString("package")}: ${item.optString("title")} - ${item.optString("text")}",
                )
            }
        }
    }
}

class LmStudioClient(
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(2, TimeUnit.MINUTES)
        .readTimeout(10, TimeUnit.MINUTES)
        .callTimeout(15, TimeUnit.MINUTES)
        .build(),
) {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    private val activeChatCall = AtomicReference<Call?>()

    fun cancelActiveChat() {
        activeChatCall.getAndSet(null)?.cancel()
    }

    private fun executeChatRequest(request: Request) =
        httpClient.newCall(request).also { call ->
            activeChatCall.set(call)
        }.let { call ->
            try {
                call.execute()
            } finally {
                activeChatCall.compareAndSet(call, null)
            }
        }

    suspend fun listModels(
        apiUrl: String,
        apiToken: String,
    ): Result<List<ModelInfo>> = withContext(Dispatchers.IO) {
        runCatching {
            runCatching {
                val nativeModels = listNativeModels(apiUrl, apiToken)
                val loadedIds = nativeModels.loadedModelKeys.toSet()
                val preferredInfos = nativeModels.modelInfos
                    .filter { loadedIds.isEmpty() || it.id in loadedIds }
                preferredInfos.ifEmpty { nativeModels.modelInfos }
            }.getOrElse {
                listOpenAiModelInfos(apiUrl, apiToken)
            }
        }
    }

    private fun listOpenAiModelInfos(
        apiUrl: String,
        apiToken: String,
    ): List<ModelInfo> {
        val request = Request.Builder()
            .url(apiUrl.normalizedOpenAiBaseUrl() + "/models")
            .withApiToken(apiToken)
            .get()
            .build()

        httpClient.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw IOException("LM Studio returned HTTP ${response.code}")
            }

            val data = JSONObject(body).optJSONArray("data") ?: JSONArray()
            return buildList {
                for (index in 0 until data.length()) {
                    val id = data.optJSONObject(index)?.optString("id").orEmpty()
                    if (id.isNotBlank()) add(ModelInfo(id = id))
                }
            }
        }
    }

    private fun resolveNativeModelId(
        apiUrl: String,
        apiToken: String,
        preferredModel: String,
    ): String {
        val nativeModels = listNativeModels(apiUrl, apiToken)
        val preferred = preferredModel.trim()

        if (preferred in nativeModels.loadedModelKeys) return preferred
        nativeModels.instanceIdToModelKey[preferred]?.let { return it }

        return nativeModels.loadedModelKeys.firstOrNull()
            ?: throw IOException("No loaded LLM instance was found in LM Studio's /api/v1/models response. Load the model in the Developer tab, tap refresh in app settings, or enable JIT model loading.")
    }

    private fun listNativeModels(
        apiUrl: String,
        apiToken: String,
    ): NativeModels {
        val request = Request.Builder()
            .url(apiUrl.normalizedNativeApiUrl() + "/models")
            .withApiToken(apiToken)
            .get()
            .build()

        httpClient.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw IOException("LM Studio returned HTTP ${response.code}")
            }

            val models = JSONObject(body).optJSONArray("models") ?: JSONArray()
            val loadedKeys = mutableListOf<String>()
            val instanceIdToKey = mutableMapOf<String, String>()
            val llmKeys = mutableListOf<String>()
            val modelInfos = mutableListOf<ModelInfo>()

            for (index in 0 until models.length()) {
                val model = models.optJSONObject(index) ?: continue
                if (model.optString("type") != "llm") continue

                val key = model.optString("key").orEmpty()
                val capabilities = model.optJSONObject("capabilities")
                val reasoning = capabilities?.optJSONObject("reasoning")
                val reasoningOptions = reasoning?.optJSONArray("allowed_options").toStringList()
                val defaultReasoning = reasoning?.optString("default").orEmpty()
                    .ifBlank { reasoningOptions.firstOrNull().orEmpty() }
                val loadedInstances = model.optJSONArray("loaded_instances") ?: JSONArray()
                if (key.isNotBlank() && loadedInstances.length() > 0) {
                    loadedKeys += key
                }
                for (instanceIndex in 0 until loadedInstances.length()) {
                    val loadedInstance = loadedInstances.optJSONObject(instanceIndex)
                    val id = loadedInstance
                        ?.firstNonBlankString("id", "model_instance_id", "instance_id")
                        .orEmpty()
                    if (id.isNotBlank() && key.isNotBlank()) {
                        instanceIdToKey[id] = key
                    }
                }

                if (key.isNotBlank()) {
                    llmKeys += key
                    modelInfos += ModelInfo(
                        id = key,
                        supportsVision = capabilities?.optBoolean("vision") == true ||
                            model.optBoolean("vision") ||
                            model.optBoolean("supports_vision"),
                        contextLength = model.contextLengthFromModelJson(capabilities, loadedInstances),
                        reasoningOptions = reasoningOptions,
                        defaultReasoning = defaultReasoning,
                    )
                }
            }

            return NativeModels(
                loadedModelKeys = loadedKeys.distinct(),
                instanceIdToModelKey = instanceIdToKey,
                modelKeys = llmKeys.distinct(),
                modelInfos = modelInfos.distinctBy { it.id },
            )
        }
    }

    suspend fun sendChatCompletions(
        apiUrl: String,
        model: String,
        apiToken: String,
        messages: List<ChatMessage>,
        systemPrompt: String,
        generationSettings: GenerationSettings,
    ): Result<ChatCompletionResult> = withContext(Dispatchers.IO) {
        runCatching {
            val messagesJson = JSONArray()
                .put(
                    JSONObject()
                        .put("role", "system")
                        .put("content", systemPrompt.ifBlank { DefaultSystemPrompt }),
                )

            messages.forEach { message ->
                messagesJson.put(
                    JSONObject()
                        .put("role", message.role.apiName)
                        .put("content", message.toOpenAiContent()),
                )
            }

            val payload = JSONObject()
                .put("model", model.ifBlank { "local-model" })
                .put("messages", messagesJson)
                .put("stream", false)
                .applyOpenAiGenerationSettings(generationSettings)

            val request = Request.Builder()
                .url(apiUrl.normalizedOpenAiBaseUrl() + "/chat/completions")
                .withApiToken(apiToken)
                .post(payload.toString().toRequestBody(jsonMediaType))
                .build()

            executeChatRequest(request).use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    throw IOException("LM Studio returned HTTP ${response.code}: ${body.take(160)}")
                }

                val content = JSONObject(body)
                    .optJSONArray("choices")
                    ?.optJSONObject(0)
                    ?.optJSONObject("message")
                    ?.optString("content")
                    .orEmpty()
                    .trim()

                if (content.isBlank()) {
                    throw IOException("LM Studio returned an empty response")
                }

                ChatCompletionResult(content = content)
            }
        }
    }

    suspend fun sendNativeChat(
        apiUrl: String,
        model: String,
        apiToken: String,
        prompt: String,
        attachments: List<ChatImageAttachment>,
        systemPrompt: String,
        previousResponseId: String?,
        integrations: String,
        allowedTools: String,
        reasoning: String?,
        generationSettings: GenerationSettings,
    ): Result<ChatCompletionResult> = withContext(Dispatchers.IO) {
        runCatching {
            val integrationsJson = integrations.toIntegrationsJsonArray(allowedTools)
            val resolvedModel = resolveNativeModelId(
                apiUrl = apiUrl,
                apiToken = apiToken,
                preferredModel = model,
            )
            val payload = JSONObject()
                .put("model", resolvedModel)
                .put("input", prompt.toNativeInput(attachments))
                .put("system_prompt", systemPrompt.ifBlank { DefaultSystemPrompt })
                .put("integrations", integrationsJson)
                .put("store", true)
                .applyNativeGenerationSettings(generationSettings)

            if (!reasoning.isNullOrBlank()) {
                payload.put("reasoning", reasoning)
            }

            if (!previousResponseId.isNullOrBlank()) {
                payload.put("previous_response_id", previousResponseId)
            }

            val request = Request.Builder()
                .url(apiUrl.normalizedNativeApiUrl() + "/chat")
                .withApiToken(apiToken)
                .post(payload.toString().toRequestBody(jsonMediaType))
                .build()

            executeChatRequest(request).use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    throw IOException("LM Studio returned HTTP ${response.code}: ${body.take(160)}")
                }

                parseNativeChatResponse(JSONObject(body), resolvedModel)
            }
        }
    }

    suspend fun streamNativeChat(
        apiUrl: String,
        model: String,
        apiToken: String,
        prompt: String,
        attachments: List<ChatImageAttachment>,
        systemPrompt: String,
        previousResponseId: String?,
        integrations: String,
        allowedTools: String,
        reasoning: String?,
        generationSettings: GenerationSettings,
        onEvent: (ChatStreamEvent) -> Unit,
    ): Result<ChatCompletionResult> = withContext(Dispatchers.IO) {
        runCatching {
            val integrationsJson = integrations.toIntegrationsJsonArray(allowedTools)
            val resolvedModel = resolveNativeModelId(
                apiUrl = apiUrl,
                apiToken = apiToken,
                preferredModel = model,
            )
            val payload = JSONObject()
                .put("model", resolvedModel)
                .put("input", prompt.toNativeInput(attachments))
                .put("system_prompt", systemPrompt.ifBlank { DefaultSystemPrompt })
                .put("integrations", integrationsJson)
                .put("store", true)
                .put("stream", true)
                .applyNativeGenerationSettings(generationSettings)

            if (!reasoning.isNullOrBlank()) {
                payload.put("reasoning", reasoning)
            }

            if (!previousResponseId.isNullOrBlank()) {
                payload.put("previous_response_id", previousResponseId)
            }

            val request = Request.Builder()
                .url(apiUrl.normalizedNativeApiUrl() + "/chat")
                .withApiToken(apiToken)
                .post(payload.toString().toRequestBody(jsonMediaType))
                .build()

            executeChatRequest(request).use { response ->
                if (!response.isSuccessful) {
                    val body = response.body?.string().orEmpty()
                    throw IOException("LM Studio returned HTTP ${response.code}: ${body.take(160)}")
                }

                var eventName = ""
                val dataBuilder = StringBuilder()
                var finalResult: ChatCompletionResult? = null

                fun flushEvent() {
                    val data = dataBuilder.toString().trim()
                    if (data.isBlank()) {
                        eventName = ""
                        dataBuilder.clear()
                        return
                    }

                    val json = JSONObject(data)
                    val type = eventName.ifBlank { json.optString("type") }
                    when (type) {
                        "message.delta" -> onEvent(
                            ChatStreamEvent(
                                type = ChatStreamEventType.MessageDelta,
                                content = json.optString("content"),
                            ),
                        )
                        "reasoning.delta" -> onEvent(
                            ChatStreamEvent(
                                type = ChatStreamEventType.ReasoningDelta,
                                content = json.optString("content"),
                            ),
                        )
                        "tool_call.start" -> onEvent(json.toToolEvent(ChatStreamEventType.ToolStarted))
                        "tool_call.success" -> onEvent(json.toToolEvent(ChatStreamEventType.ToolSucceeded))
                        "tool_call.failure" -> onEvent(
                            ChatStreamEvent(
                                type = ChatStreamEventType.ToolFailed,
                                content = json.optString("reason")
                                    .ifBlank { json.optJSONObject("error")?.optString("message").orEmpty() },
                                tool = json.optJSONObject("metadata")?.optString("tool_name").orEmpty()
                                    .ifBlank { json.optString("tool") },
                                provider = json.optJSONObject("provider_info")?.optString("plugin_id").orEmpty()
                                    .ifBlank { json.optJSONObject("provider_info")?.optString("server_label").orEmpty() },
                            ),
                        )
                        "error" -> onEvent(
                            ChatStreamEvent(
                                type = ChatStreamEventType.Error,
                                content = json.optJSONObject("error")?.optString("message").orEmpty(),
                            ),
                        )
                        "chat.end" -> {
                            val resultJson = json.optJSONObject("result")
                            if (resultJson != null) {
                                finalResult = parseNativeChatResponse(resultJson, resolvedModel)
                            }
                        }
                    }

                    eventName = ""
                    dataBuilder.clear()
                }

                response.body?.charStream()?.buffered()?.useLines { lines ->
                    lines.forEach { line ->
                        when {
                            line.isBlank() -> flushEvent()
                            line.startsWith("event:") -> eventName = line.removePrefix("event:").trim()
                            line.startsWith("data:") -> {
                                dataBuilder.append(line.removePrefix("data:").trimStart())
                                dataBuilder.append('\n')
                            }
                        }
                    }
                }
                flushEvent()

                finalResult ?: throw IOException("LM Studio stream ended without a final chat result")
            }
        }
    }

    private fun JSONObject.toToolEvent(type: ChatStreamEventType): ChatStreamEvent {
        val provider = optJSONObject("provider_info")
        val providerName = provider?.optString("plugin_id").orEmpty()
            .ifBlank { provider?.optString("server_label").orEmpty() }
        return ChatStreamEvent(
            type = type,
            tool = optString("tool"),
            provider = providerName,
        )
    }

    private fun parseNativeChatResponse(
        json: JSONObject,
        fallbackModel: String,
    ): ChatCompletionResult {
        val output = json.optJSONArray("output") ?: JSONArray()
        val messageParts = mutableListOf<String>()
        val reasoningParts = mutableListOf<String>()
        val toolCalls = mutableListOf<String>()
        val invalidToolCalls = mutableListOf<String>()

        for (index in 0 until output.length()) {
            val item = output.optJSONObject(index) ?: continue
            when (item.optString("type")) {
                "message" -> {
                    val content = item.optString("content").trim()
                    if (content.isNotBlank()) messageParts += content
                }
                "reasoning" -> {
                    val content = item.optString("content").trim()
                    if (content.isNotBlank()) reasoningParts += content
                }
                "tool_call" -> {
                    val tool = item.optString("tool", "tool")
                    val provider = item.optJSONObject("provider_info")
                    val providerName = provider?.optString("plugin_id").orEmpty()
                        .ifBlank { provider?.optString("server_label").orEmpty() }
                    toolCalls += if (providerName.isBlank()) tool else "$tool via $providerName"
                }
                "invalid_tool_call" -> {
                    val metadata = item.optJSONObject("metadata")
                    val tool = item.optString("tool_name")
                        .ifBlank { metadata?.optString("tool_name").orEmpty() }
                        .ifBlank { "tool" }
                    val reason = item.optString("reason", "invalid tool call")
                    invalidToolCalls += "$tool: $reason"
                }
            }
        }

        val content = buildAssistantContent(
            tools = toolCalls,
            reasoning = reasoningParts.joinToString("\n\n"),
            message = messageParts.joinToString("\n\n"),
            errors = invalidToolCalls,
        ).trim()

        if (content.isBlank()) {
            throw IOException("LM Studio returned an empty response")
        }

        return ChatCompletionResult(
            content = content,
            responseId = json.optString("response_id").takeIf { it.isNotBlank() },
            modelId = json.optString("model_instance_id").takeIf { it.isNotBlank() } ?: fallbackModel,
        )
    }
}

private data class NativeModels(
    val loadedModelKeys: List<String>,
    val instanceIdToModelKey: Map<String, String>,
    val modelKeys: List<String>,
    val modelInfos: List<ModelInfo>,
)

private fun JSONArray?.toStringList(): List<String> {
    if (this == null) return emptyList()
    return buildList {
        for (index in 0 until length()) {
            val value = optString(index).trim()
            if (value.isNotBlank()) add(value)
        }
    }.distinct()
}

private fun JSONObject.firstNonBlankString(vararg keys: String): String {
    for (key in keys) {
        val value = optString(key).trim()
        if (value.isNotBlank()) return value
    }
    return ""
}

private fun JSONObject.firstPositiveInt(vararg keys: String): Int? {
    for (key in keys) {
        val value = opt(key)
        val intValue = when (value) {
            is Number -> value.toInt()
            is String -> value.trim().toIntOrNull()
            else -> null
        }
        if (intValue != null && intValue > 0) return intValue
    }
    return null
}

private fun JSONObject.contextLengthFromModelJson(
    capabilities: JSONObject?,
    loadedInstances: JSONArray,
): Int? {
    val keys = arrayOf(
        "context_length",
        "contextLength",
        "max_context_length",
        "maxContextLength",
        "context_window",
        "contextWindow",
        "n_ctx",
        "max_context",
        "trained_context_length",
        "loaded_context_length",
    )
    val candidates = mutableListOf<Int>()
    firstPositiveInt(*keys)?.let(candidates::add)
    capabilities?.firstPositiveInt(*keys)?.let(candidates::add)
    optJSONObject("config")?.firstPositiveInt(*keys)?.let(candidates::add)
    optJSONObject("runtime")?.firstPositiveInt(*keys)?.let(candidates::add)
    optJSONObject("params")?.firstPositiveInt(*keys)?.let(candidates::add)

    for (index in 0 until loadedInstances.length()) {
        loadedInstances.optJSONObject(index)
            ?.firstPositiveInt(*keys)
            ?.let(candidates::add)
    }
    return candidates.maxOrNull()
}

private fun Request.Builder.withApiToken(apiToken: String): Request.Builder {
    val trimmedToken = apiToken.trim()
    if (trimmedToken.isNotBlank()) {
        header("Authorization", "Bearer $trimmedToken")
    }
    return this
}

private fun JSONObject.applyOpenAiGenerationSettings(settings: GenerationSettings): JSONObject {
    settings.temperature?.let { put("temperature", it) }
    settings.topP?.let { put("top_p", it) }
    settings.maxTokens?.let { put("max_tokens", it) }
    settings.presencePenalty?.let { put("presence_penalty", it) }
    settings.frequencyPenalty?.let { put("frequency_penalty", it) }
    settings.seed?.let { put("seed", it) }
    return this
}

private fun JSONObject.applyNativeGenerationSettings(settings: GenerationSettings): JSONObject {
    applyOpenAiGenerationSettings(settings)
    settings.contextLength?.let { put("context_length", it) }
    return this
}

private fun String.normalizedOpenAiBaseUrl(): String {
    val trimmed = trim().trimEnd('/')
    return when {
        trimmed.endsWith("/api/v1") -> trimmed.removeSuffix("/api/v1") + "/v1"
        trimmed.endsWith("/v1") -> trimmed
        else -> "$trimmed/v1"
    }
}

private fun String.normalizedNativeApiUrl(): String {
    val trimmed = trim().trimEnd('/')
    return when {
        trimmed.endsWith("/api/v1") -> trimmed
        trimmed.endsWith("/v1") -> trimmed.removeSuffix("/v1") + "/api/v1"
        trimmed.endsWith("/api") -> "$trimmed/v1"
        else -> "$trimmed/api/v1"
    }
}

private fun ChatMessage.toOpenAiContent(): Any {
    val imageAttachments = attachments.filter { it.dataUrl.isNotBlank() }
    if (role != MessageRole.User || imageAttachments.isEmpty()) {
        return content
    }

    val contentParts = JSONArray()
        .put(
            JSONObject()
                .put("type", "text")
                .put("text", content),
        )
    imageAttachments.forEach { attachment ->
        contentParts.put(
            JSONObject()
                .put("type", "image_url")
                .put(
                    "image_url",
                    JSONObject().put("url", attachment.dataUrl),
                ),
        )
    }
    return contentParts
}

private fun String.toNativeInput(attachments: List<ChatImageAttachment>): Any {
    val imageAttachments = attachments.filter { it.dataUrl.isNotBlank() }
    if (imageAttachments.isEmpty()) return this

    val input = JSONArray()
        .put(
            JSONObject()
                .put("type", "text")
                .put("content", this),
        )
    imageAttachments.forEach { attachment ->
        input.put(
            JSONObject()
                .put("type", "image")
                .put("data_url", attachment.dataUrl),
        )
    }
    return input
}

private fun String.toIntegrationsJsonArray(allowedTools: String): JSONArray {
    val trimmed = trim()
    if (trimmed.isBlank()) return JSONArray()

    return when {
        trimmed.startsWith("[") -> JSONArray(trimmed)
        trimmed.startsWith("{") -> JSONArray().put(JSONObject(trimmed))
        else -> {
            val integrations = JSONArray()
            val allowedToolsJson = allowedTools.toAllowedToolsJsonArray()
            trimmed
                .lines()
                .flatMap { line -> line.split(",") }
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .forEach { integrationId ->
                    val normalizedId = integrationId.normalizedIntegrationId()
                    if (allowedToolsJson.length() == 0) {
                        integrations.put(normalizedId)
                    } else {
                        integrations.put(
                            JSONObject()
                                .put("type", "plugin")
                                .put("id", normalizedId)
                                .put("allowed_tools", allowedToolsJson),
                        )
                    }
                }
            integrations
        }
    }
}

private fun String.toAllowedToolsJsonArray(): JSONArray {
    val tools = JSONArray()
    lines()
        .flatMap { line -> line.split(",") }
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .distinct()
        .forEach { tools.put(it) }
    return tools
}

private fun String.normalizedIntegrationId(): String =
    if (contains('/')) this else "mcp/$this"

private fun String.withIntegration(integrationId: String): String =
    lines()
        .plus(integrationId)
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .distinct()
        .joinToString("\n")

@Composable
private fun DocumentConversionDialog(
    canConvertToPlainText: Boolean,
    canConvertToImages: Boolean,
    onDismiss: () -> Unit,
    onPlainText: () -> Unit,
    onImages: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Attach document") },
        text = {
            Text(
                if (canConvertToPlainText || canConvertToImages) {
                    "Choose how to prepare this document before sending it to the model."
                } else {
                    "Select a vision-capable model to attach this PDF as page images."
                },
            )
        },
        confirmButton = {
            TextButton(
                onClick = onPlainText,
                enabled = canConvertToPlainText,
            ) {
                Text("Plain text")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TextButton(
                    onClick = onImages,
                    enabled = canConvertToImages,
                ) {
                    Text("Images")
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        },
    )
}

@Composable
private fun NativeToolActionDialog(
    action: NativeToolAction,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(action.displayTitle()) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("LM Studio requested a phone-side action. Review it before continuing.")
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = action.displaySummary(),
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Text(
                    text = "Draft actions open Android screens only. You still press Send, Call, Save, or Navigate yourself.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Continue")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

private fun Throwable.friendlyMessage(): String {
    val detail = message.orEmpty()
    return when {
        this is SocketTimeoutException ||
            detail.contains("timeout", ignoreCase = true) ->
            "LM Studio is still working or took too long to respond. Tool calls can take longer; try again or use a narrower request."
        detail.contains("Failed to connect", ignoreCase = true) ||
            detail.contains("ECONNREFUSED", ignoreCase = true) ->
            "Could not reach LM Studio. Check that the server is running and the API URL is reachable."
        detail.contains("HTTP 401") ->
            "LM Studio rejected the request. Add a valid API token in settings."
        detail.contains("HTTP 403") ->
            "The API token does not have permission for this LM Studio action. Check the token's MCP/tool permissions."
        detail.contains("model_not_found", ignoreCase = true) ||
            detail.contains("Invalid model identifier", ignoreCase = true) ->
            "LM Studio could not find that loaded model. Turn Server tools on, tap refresh models, and select one of the loaded LLM instance ids."
        detail.isNotBlank() -> detail
        else -> "Something went wrong while talking to LM Studio."
    }
}

private fun Throwable.isChatCancellation(): Boolean {
    val detail = message.orEmpty()
    return this is CancellationException ||
        detail.equals("Canceled", ignoreCase = true) ||
        detail.equals("Cancelled", ignoreCase = true) ||
        detail.contains("canceled", ignoreCase = true) ||
        detail.contains("cancelled", ignoreCase = true)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LmStudioApp(
    state: ChatUiState,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onCancelSend: () -> Unit,
    onSuggestion: (String) -> Unit,
    onNewChat: () -> Unit,
    onSelectChat: (String) -> Unit,
    onChatSearchChange: (String) -> Unit,
    onEditMessage: (String) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onDeleteChat: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onCloseSettings: () -> Unit,
    onOpenInfo: () -> Unit,
    onCloseInfo: () -> Unit,
    onApiUrlChange: (String) -> Unit,
    onModelChange: (String) -> Unit,
    onApiTokenChange: (String) -> Unit,
    onServerToolsEnabledChange: (Boolean) -> Unit,
    onServerIntegrationsChange: (String) -> Unit,
    onAllowedToolsChange: (String) -> Unit,
    onNativeIntentToolEnabledChange: (Boolean) -> Unit,
    onCalendarToolEnabledChange: (Boolean) -> Unit,
    onContactsToolEnabledChange: (Boolean) -> Unit,
    onNotificationDigestToolEnabledChange: (Boolean) -> Unit,
    onLocalFileSearchToolEnabledChange: (Boolean) -> Unit,
    onPickLocalSearchFolder: () -> Unit,
    onDeviceStatusToolEnabledChange: (Boolean) -> Unit,
    onVoiceInputEnabledChange: (Boolean) -> Unit,
    onVoiceOutputEnabledChange: (Boolean) -> Unit,
    onAutoReadAnswersEnabledChange: (Boolean) -> Unit,
    onAppendDateTimeToSystemPromptChange: (Boolean) -> Unit,
    onSystemProfileSelect: (String) -> Unit,
    onSystemProfileNameChange: (String) -> Unit,
    onSystemPromptChange: (String) -> Unit,
    onTemperatureChange: (String) -> Unit,
    onTopPChange: (String) -> Unit,
    onMaxTokensChange: (String) -> Unit,
    onContextLengthChange: (String) -> Unit,
    onPresencePenaltyChange: (String) -> Unit,
    onFrequencyPenaltyChange: (String) -> Unit,
    onSeedChange: (String) -> Unit,
    onSystemProfileSave: () -> Unit,
    onSystemProfileCreate: () -> Unit,
    onSystemProfileDelete: (String) -> Unit,
    onReasoningEnabledChange: (Boolean) -> Unit,
    onPickImage: () -> Unit,
    onTakePhoto: () -> Unit,
    onPickDocument: () -> Unit,
    onVoiceInput: () -> Unit,
    isListening: Boolean,
    onSpeakMessage: (String, String) -> Unit,
    onStopSpeaking: () -> Unit,
    speakingMessageId: String?,
    onClearImage: () -> Unit,
    onRefreshModels: () -> Unit,
    onExportChats: () -> Unit,
    onImportChats: () -> Unit,
    onDismissError: () -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    if (state.isSettingsOpen) {
        SettingsSheet(
            state = state,
            onDismiss = onCloseSettings,
            onApiUrlChange = onApiUrlChange,
            onModelChange = onModelChange,
            onApiTokenChange = onApiTokenChange,
            onServerToolsEnabledChange = onServerToolsEnabledChange,
            onServerIntegrationsChange = onServerIntegrationsChange,
            onAllowedToolsChange = onAllowedToolsChange,
            onNativeIntentToolEnabledChange = onNativeIntentToolEnabledChange,
            onCalendarToolEnabledChange = onCalendarToolEnabledChange,
            onContactsToolEnabledChange = onContactsToolEnabledChange,
            onNotificationDigestToolEnabledChange = onNotificationDigestToolEnabledChange,
            onLocalFileSearchToolEnabledChange = onLocalFileSearchToolEnabledChange,
            onPickLocalSearchFolder = onPickLocalSearchFolder,
            onDeviceStatusToolEnabledChange = onDeviceStatusToolEnabledChange,
            onVoiceInputEnabledChange = onVoiceInputEnabledChange,
            onVoiceOutputEnabledChange = onVoiceOutputEnabledChange,
            onAutoReadAnswersEnabledChange = onAutoReadAnswersEnabledChange,
            onAppendDateTimeToSystemPromptChange = onAppendDateTimeToSystemPromptChange,
            onSystemProfileSelect = onSystemProfileSelect,
            onSystemProfileNameChange = onSystemProfileNameChange,
            onSystemPromptChange = onSystemPromptChange,
            onTemperatureChange = onTemperatureChange,
            onTopPChange = onTopPChange,
            onMaxTokensChange = onMaxTokensChange,
            onContextLengthChange = onContextLengthChange,
            onPresencePenaltyChange = onPresencePenaltyChange,
            onFrequencyPenaltyChange = onFrequencyPenaltyChange,
            onSeedChange = onSeedChange,
            onSystemProfileSave = onSystemProfileSave,
            onSystemProfileCreate = onSystemProfileCreate,
            onSystemProfileDelete = onSystemProfileDelete,
            onRefreshModels = onRefreshModels,
            onExportChats = onExportChats,
            onImportChats = onImportChats,
        )
    }
    if (state.isInfoOpen) {
        InfoSheet(onDismiss = onCloseInfo)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                state = state,
                onNewChat = {
                    onNewChat()
                    scope.launch { drawerState.close() }
                },
                onSelectChat = { sessionId ->
                    onSelectChat(sessionId)
                    scope.launch { drawerState.close() }
                },
                onChatSearchChange = onChatSearchChange,
                onDeleteChat = onDeleteChat,
                onSettings = {
                    onOpenSettings()
                    scope.launch { drawerState.close() }
                },
                onInfo = {
                    onOpenInfo()
                    scope.launch { drawerState.close() }
                },
            )
        },
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "LMSMOB Chat",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = buildString {
                                    append(state.model.ifBlank { "Local model" })
                                    if (state.serverToolsEnabled) append(" - tools")
                                },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Open menu",
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onOpenSettings) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Settings",
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                )
            },
            bottomBar = {
                ChatComposer(
                    state = state,
                    input = state.input,
                    isSending = state.isSending,
                    error = state.error,
                    canAttachImage = state.selectedModelSupportsVision(),
                    attachedImages = state.attachedImages,
                    attachedDocumentText = state.attachedDocumentText,
                    reasoningSupported = state.selectedModelSupportsReasoningToggle(),
                    reasoningEnabled = state.reasoningEnabled,
                    onInputChange = onInputChange,
                    onSend = onSend,
                    onCancelSend = onCancelSend,
                    onPickImage = onPickImage,
                    onTakePhoto = onTakePhoto,
                    onPickDocument = onPickDocument,
                    onVoiceInput = onVoiceInput,
                    isListening = isListening,
                    onClearImage = onClearImage,
                    onReasoningEnabledChange = onReasoningEnabledChange,
                    onDismissError = onDismissError,
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
        ) { padding ->
            MessagesPanel(
                state = state,
                contentPadding = padding,
                onSuggestion = onSuggestion,
                onEditMessage = onEditMessage,
                onDeleteMessage = onDeleteMessage,
                onSpeakMessage = onSpeakMessage,
                onStopSpeaking = onStopSpeaking,
                speakingMessageId = speakingMessageId,
            )
        }
    }
}

@Composable
private fun ContextUsageBar(state: ChatUiState) {
    val usage = state.contextUsage()
    val barColor = when {
        usage.fraction >= 0.92f -> MaterialTheme.colorScheme.error
        usage.fraction >= 0.72f -> Color(0xFFFFB020)
        else -> MaterialTheme.colorScheme.primary
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Context",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${usage.usedTokens.formatTokenCount()} / ${usage.availableTokens.formatTokenCount()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            LinearProgressIndicator(
                progress = { usage.fraction.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(50.dp)),
                color = barColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f),
            )
        }
    }
}

private fun ChatUiState.contextUsage(): ContextUsage {
    val available = contextLengthDraft.toNullableInt()
        ?: selectedModelInfo()?.contextLength
        ?: DefaultContextLength
    val used = estimatedContextTokens().coerceAtLeast(0)
    return ContextUsage(
        usedTokens = used,
        availableTokens = available,
        fraction = used.toFloat() / available.toFloat().coerceAtLeast(1f),
    )
}

private fun ChatUiState.estimatedContextTokens(): Int {
    val messageTokens = messages.fold(0) { total, message ->
        val attachmentTokens = message.attachments.fold(0) { attachmentTotal, attachment ->
            attachmentTotal + if (attachment.dataUrl.isBlank()) 64 else 1100
        }
        total + message.content.estimatedTokenCount() + attachmentTokens + 4
    }
    val pendingInputTokens = input.estimatedTokenCount()
    val pendingAttachmentTokens = attachedImages.size * 1100 +
        (attachedDocumentText?.text?.estimatedTokenCount() ?: 0)
    return activeSystemPrompt().estimatedTokenCount() +
        messageTokens +
        pendingInputTokens +
        pendingAttachmentTokens
}

private fun String.estimatedTokenCount(): Int {
    if (isBlank()) return 0
    return (length / 4.0).toInt().coerceAtLeast(1)
}

private fun Int.formatTokenCount(): String =
    if (this >= 1000) {
        val compact = this / 1000.0
        if (compact >= 10) {
            "${compact.toInt()}k"
        } else {
            "%.1fk".format(compact)
        }
    } else {
        toString()
    }

@Composable
private fun DrawerContent(
    state: ChatUiState,
    onNewChat: () -> Unit,
    onSelectChat: (String) -> Unit,
    onChatSearchChange: (String) -> Unit,
    onDeleteChat: (String) -> Unit,
    onSettings: () -> Unit,
    onInfo: () -> Unit,
) {
    val query = state.chatSearchQuery.trim()
    var pendingDeleteSession by remember { mutableStateOf<ChatSession?>(null) }
    val filteredSessions = state.sessions.filter { session ->
        query.isBlank() ||
            session.title.contains(query, ignoreCase = true) ||
            session.messages.any { it.content.contains(query, ignoreCase = true) }
    }

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = onNewChat,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("New chat")
            }

            OutlinedTextField(
                value = state.chatSearchQuery,
                onValueChange = onChatSearchChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = null)
                },
                placeholder = { Text("Search chats") },
                shape = RoundedCornerShape(12.dp),
            )

            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.45f))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(filteredSessions, key = { it.id }) { session ->
                    val selected = session.id == state.activeSessionId
                    Surface(
                        color = if (selected) {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                        } else {
                            Color.Transparent
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectChat(session.id) },
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = session.title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = "${session.messages.count { it.role == MessageRole.User }} messages",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            leadingContent = { AppMark(modifier = Modifier.size(28.dp)) },
                            trailingContent = {
                                IconButton(
                                    onClick = { pendingDeleteSession = session },
                                    modifier = Modifier.size(34.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete chat",
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            },
                        )
                    }
                }
            }

            TextButton(
                onClick = onSettings,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(imageVector = Icons.Filled.Settings, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Connection settings")
            }
            TextButton(
                onClick = onInfo,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(imageVector = Icons.Filled.Info, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Info & license")
            }
        }
    }

    val sessionToDelete = pendingDeleteSession
    if (sessionToDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteSession = null },
            title = { Text("Delete chat?") },
            text = {
                Text("This will remove \"${sessionToDelete.title}\" from this device.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteChat(sessionToDelete.id)
                        pendingDeleteSession = null
                    },
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteSession = null }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun MessagesPanel(
    state: ChatUiState,
    contentPadding: PaddingValues,
    onSuggestion: (String) -> Unit,
    onEditMessage: (String) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onSpeakMessage: (String, String) -> Unit,
    onStopSpeaking: () -> Unit,
    speakingMessageId: String?,
) {
    val listState = rememberLazyListState()
    var pendingDeleteMessage by remember { mutableStateOf<ChatMessage?>(null) }

    LaunchedEffect(state.messages.size, state.messages.lastOrNull()?.content?.length, state.isSending) {
        if (state.messages.isNotEmpty() || state.isSending) {
            val lastVisibleItem = if (state.isSending) {
                state.messages.size
            } else {
                state.messages.lastIndex
            }
            listState.animateScrollToItem(lastVisibleItem.coerceAtLeast(0))
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        if (state.messages.isEmpty() && !state.isSending) {
            item {
                EmptyConversation(onSuggestion = onSuggestion)
            }
        }

        items(
            items = state.messages,
            key = { it.id },
        ) { message ->
            MessageRow(
                message = message,
                onEditMessage = onEditMessage,
                onDeleteMessage = { pendingDeleteMessage = message },
                voiceOutputEnabled = state.voiceOutputEnabled,
                onSpeakMessage = onSpeakMessage,
                onStopSpeaking = onStopSpeaking,
                speakingMessageId = speakingMessageId,
            )
        }

        if (state.isSending && state.messages.none { it.isStreaming }) {
            item {
                TypingRow()
            }
        }
    }

    val messageToDelete = pendingDeleteMessage
    if (messageToDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteMessage = null },
            title = { Text("Delete message?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("This will remove the message from this chat history.")
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = messageToDelete.deletePreview(),
                            modifier = Modifier.padding(12.dp),
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteMessage(messageToDelete.id)
                        pendingDeleteMessage = null
                    },
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteMessage = null }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun EmptyConversation(onSuggestion: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 440.dp)
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppMark(modifier = Modifier.size(58.dp))
        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = "What can I help with?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier.widthIn(max = 420.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            listOf(
                "Draft a launch plan",
                "Explain this code",
                "Brainstorm product ideas",
            ).forEach { suggestion ->
                OutlinedButton(
                    onClick = { onSuggestion(suggestion) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                ) {
                    Text(
                        text = suggestion,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageRow(
    message: ChatMessage,
    onEditMessage: (String) -> Unit,
    onDeleteMessage: (String) -> Unit,
    voiceOutputEnabled: Boolean,
    onSpeakMessage: (String, String) -> Unit,
    onStopSpeaking: () -> Unit,
    speakingMessageId: String?,
) {
    val isUser = message.role == MessageRole.User
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top,
    ) {
        if (!isUser) {
            AppMark(modifier = Modifier.size(30.dp))
            Spacer(modifier = Modifier.width(10.dp))
        }

        if (isUser) {
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 6.dp,
                        bottomEnd = 20.dp,
                        bottomStart = 20.dp,
                    ),
                    modifier = Modifier.widthIn(max = 310.dp),
                ) {
                    Text(
                        text = message.content,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                if (message.attachments.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    MessageAttachmentSummary(message.attachments)
                }
                MessageActions(
                    canEdit = true,
                    canDelete = !message.isStreaming,
                    canSpeak = false,
                    isSpeaking = false,
                    onCopy = { context.copyToClipboard(message.content) },
                    onEdit = { onEditMessage(message.id) },
                    onDelete = { onDeleteMessage(message.id) },
                    onSpeak = {},
                    onStopSpeaking = {},
                )
            }
        } else {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "LM Studio",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(4.dp))
                SelectionContainer {
                    MessageContent(
                        content = message.content,
                        isStreaming = message.isStreaming,
                    )
                }
                MessageActions(
                    canEdit = false,
                    canDelete = !message.isStreaming,
                    canSpeak = voiceOutputEnabled &&
                        !message.isStreaming &&
                        message.content.speakableAnswerText().isNotBlank(),
                    isSpeaking = speakingMessageId == message.id,
                    onCopy = { context.copyToClipboard(message.content) },
                    onEdit = {},
                    onDelete = { onDeleteMessage(message.id) },
                    onSpeak = { onSpeakMessage(message.id, message.content) },
                    onStopSpeaking = onStopSpeaking,
                )
            }
        }
    }
}

@Composable
private fun MessageAttachmentSummary(attachments: List<ChatImageAttachment>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        attachments.forEach { attachment ->
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(50.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = if (attachment.dataUrl.isBlank()) {
                            Icons.Filled.Description
                        } else {
                            Icons.Filled.AddPhotoAlternate
                        },
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                    )
                    Text(
                        text = attachment.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

private enum class MessageBlockType {
    Text,
    Tools,
    Thought,
    Code,
    Error,
}

private data class MessageBlock(
    val type: MessageBlockType,
    val text: String,
)

private fun parseMessageBlocks(rawContent: String): List<MessageBlock> {
    val content = rawContent.withoutNativeActionBlocks()
        .replace("\r\n", "\n")
        .trim()
    if (content.isBlank()) {
        return if (NativeActionRegex.containsMatchIn(rawContent)) {
            listOf(MessageBlock(MessageBlockType.Text, "Phone-side action requested."))
        } else {
            emptyList()
        }
    }

    val blocks = mutableListOf<MessageBlock>()
    val bodyLines = mutableListOf<String>()
    content.lines().forEach { line ->
        val trimmed = line.trim()
        when {
            trimmed.startsWith("Tools used:", ignoreCase = true) -> {
                blocks += MessageBlock(MessageBlockType.Tools, trimmed.substringAfter(":").trim())
            }
            trimmed.startsWith("Tool errors:", ignoreCase = true) -> {
                blocks += MessageBlock(MessageBlockType.Error, trimmed.substringAfter(":").trim())
            }
            else -> bodyLines += line
        }
    }

    val body = bodyLines.joinToString("\n").trim()
        .replace("<think>", "<|channel>thought\n<channel|>")
        .replace("</think>", "<|channel>answer\n<channel|>")
    val channelPattern = Regex("<\\|channel>([^\\n<]+)\\s*\\n?\\s*<channel\\|>")
    var index = 0
    var currentType = MessageBlockType.Text

    channelPattern.findAll(body).forEach { match ->
        blocks.addTextAndCode(body.substring(index, match.range.first), currentType)
        val channel = match.groupValues.getOrNull(1).orEmpty().trim().lowercase()
        currentType = when {
            channel.contains("thought") || channel.contains("reason") -> MessageBlockType.Thought
            else -> MessageBlockType.Text
        }
        index = match.range.last + 1
    }
    blocks.addTextAndCode(body.substring(index), currentType)

    return blocks.filter { it.text.isNotBlank() }
}

private fun String.speakableAnswerText(): String =
    parseMessageBlocks(this)
        .filter { it.type == MessageBlockType.Text }
        .joinToString("\n\n") { it.text }
        .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), ", ")
        .replace("**", "")
        .replace("__", "")
        .replace(Regex("`([^`]+)`"), "$1")
        .replace(Regex("\\[([^\\]]+)]\\([^)]*\\)"), "$1")
        .replace(Regex("\\|\\s*:?-{3,}:?\\s*"), " ")
        .replace(Regex("[ \\t]{2,}"), " ")
        .trim()

private fun Int.speechRecognitionMessage(): String =
    when (this) {
        SpeechRecognizer.ERROR_AUDIO -> "Voice input had an audio recording problem."
        SpeechRecognizer.ERROR_CLIENT -> "Voice input was cancelled."
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required for voice input."
        SpeechRecognizer.ERROR_NETWORK,
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Voice recognition could not reach the speech service."
        SpeechRecognizer.ERROR_NO_MATCH -> "I did not catch that. Try voice input again."
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech recognizer is busy. Try again in a moment."
        SpeechRecognizer.ERROR_SERVER -> "The speech service returned an error."
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech was detected."
        else -> "Voice input failed. Try again."
    }

private fun MutableList<MessageBlock>.addTextAndCode(
    rawText: String,
    fallbackType: MessageBlockType,
) {
    val text = rawText.trim()
    if (text.isBlank()) return

    text.split("```").forEachIndexed { index, part ->
        val cleaned = part.trim()
        if (cleaned.isBlank()) return@forEachIndexed
        if (index % 2 == 1) {
            add(MessageBlock(MessageBlockType.Code, cleaned.codeWithoutLanguageHint()))
        } else {
            add(MessageBlock(fallbackType, cleaned))
        }
    }
}

private fun String.codeWithoutLanguageHint(): String {
    val lines = lines()
    val firstLine = lines.firstOrNull().orEmpty().trim()
    return if (
        lines.size > 1 &&
        firstLine.length <= 24 &&
        firstLine.matches(Regex("[A-Za-z0-9_+\\-.#]+"))
    ) {
        lines.drop(1).joinToString("\n").trim()
    } else {
        trim()
    }
}

@Composable
private fun MessageContent(
    content: String,
    isStreaming: Boolean,
) {
    val blocks = remember(content) { parseMessageBlocks(content) }
    var toolsExpanded by remember { mutableStateOf(false) }
    var thoughtExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (blocks.isEmpty()) {
            StreamingIndicator(text = "Waiting for LM Studio")
        }

        blocks.forEach { block ->
            when (block.type) {
                MessageBlockType.Text -> RichTextBlock(block.text)
                MessageBlockType.Tools -> ToolUsageBlock(
                    text = block.text,
                    expanded = toolsExpanded,
                    isActive = isStreaming,
                    onToggle = { toolsExpanded = !toolsExpanded },
                )
                MessageBlockType.Thought -> ThoughtBlock(
                    text = block.text,
                    expanded = thoughtExpanded,
                    isActive = isStreaming,
                    onToggle = { thoughtExpanded = !thoughtExpanded },
                )
                MessageBlockType.Code -> CodeBlock(block.text)
                MessageBlockType.Error -> ToolErrorBlock(block.text)
            }
        }

        if (
            isStreaming &&
            blocks.isNotEmpty() &&
            blocks.none { it.type == MessageBlockType.Tools || it.type == MessageBlockType.Thought }
        ) {
            StreamingIndicator(text = "Generating")
        }
    }
}

private enum class RichBlockType {
    Heading,
    Paragraph,
    Bullet,
    Table,
}

private data class RichBlock(
    val type: RichBlockType,
    val text: String = "",
    val table: MarkdownTable? = null,
)

private data class MarkdownTable(
    val headers: List<String>,
    val rows: List<List<String>>,
)

private fun parseRichBlocks(rawText: String): List<RichBlock> {
    val lines = rawText
        .replace("\r\n", "\n")
        .lines()
    val blocks = mutableListOf<RichBlock>()
    var index = 0

    while (index < lines.size) {
        val line = lines[index]
        val trimmed = line.trim()
        when {
            trimmed.isBlank() -> index += 1
            isMarkdownTableAt(lines, index) -> {
                val tableLines = mutableListOf<String>()
                while (index < lines.size && lines[index].trim().startsWith("|")) {
                    tableLines += lines[index]
                    index += 1
                }
                parseMarkdownTable(tableLines)?.let { table ->
                    blocks += RichBlock(type = RichBlockType.Table, table = table)
                }
            }
            trimmed.startsWith("#") && trimmed.contains(" ") -> {
                blocks += RichBlock(
                    type = RichBlockType.Heading,
                    text = trimmed.trimStart('#').trim(),
                )
                index += 1
            }
            trimmed.startsWith("* ") || trimmed.startsWith("- ") -> {
                blocks += RichBlock(
                    type = RichBlockType.Bullet,
                    text = trimmed.drop(2).trim(),
                )
                index += 1
            }
            else -> {
                val paragraph = mutableListOf<String>()
                while (
                    index < lines.size &&
                    lines[index].trim().isNotBlank() &&
                    !isMarkdownTableAt(lines, index) &&
                    !lines[index].trim().startsWith("* ") &&
                    !lines[index].trim().startsWith("- ") &&
                    !(lines[index].trim().startsWith("#") && lines[index].trim().contains(" "))
                ) {
                    paragraph += lines[index].trim()
                    index += 1
                }
                blocks += RichBlock(
                    type = RichBlockType.Paragraph,
                    text = paragraph.joinToString("\n").trim(),
                )
            }
        }
    }

    return blocks.filter { it.text.isNotBlank() || it.table != null }
}

private fun String.normalizeRichText(): String =
    replace("\r\n", "\n")
        .replace("<br>", "\n", ignoreCase = true)
        .replace("<br/>", "\n", ignoreCase = true)
        .replace("<br />", "\n", ignoreCase = true)

private fun String.normalizeInlineRichText(): String =
    normalizeRichText()
        .replace("\\|", "|")

private fun isMarkdownTableAt(lines: List<String>, index: Int): Boolean {
    if (index + 1 >= lines.size) return false
    val current = lines[index].trim()
    val next = lines[index + 1].trim()
    return current.startsWith("|") &&
        current.unescapedPipeCount() >= 2 &&
        parseMarkdownTableRow(next).all { it.matches(Regex(":?-{3,}:?")) }
}

private fun String.unescapedPipeCount(): Int {
    var count = 0
    var escaped = false
    forEach { char ->
        when {
            escaped -> escaped = false
            char == '\\' -> escaped = true
            char == '|' -> count += 1
        }
    }
    return count
}

private fun parseMarkdownTable(lines: List<String>): MarkdownTable? {
    if (lines.size < 2) return null
    val headers = parseMarkdownTableRow(lines.first())
    val rows = lines.drop(2)
        .map { parseMarkdownTableRow(it) }
        .filter { row -> row.any { cell -> cell.isNotBlank() } }
    if (headers.isEmpty() || rows.isEmpty()) return null
    return MarkdownTable(headers = headers, rows = rows)
}

private fun parseMarkdownTableRow(line: String): List<String> {
    val cells = mutableListOf<String>()
    val cell = StringBuilder()
    var escaped = false

    line.trim().forEach { char ->
        when {
            escaped -> {
                cell.append(char)
                escaped = false
            }
            char == '\\' -> escaped = true
            char == '|' -> {
                cells += cell.toString().trim()
                cell.clear()
            }
            else -> cell.append(char)
        }
    }
    cells += cell.toString().trim()

    return cells
        .dropWhile { it.isBlank() }
        .dropLastWhile { it.isBlank() }
        .map { it.normalizeInlineRichText().trim() }
}

private fun markdownAnnotatedString(text: String): AnnotatedString {
    val pattern = Regex("\\*\\*(.+?)\\*\\*")
    val normalized = text.normalizeInlineRichText()
    return buildAnnotatedString {
        var index = 0
        pattern.findAll(normalized).forEach { match ->
            append(normalized.substring(index, match.range.first))
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(match.groupValues[1])
            }
            index = match.range.last + 1
        }
        append(normalized.substring(index))
    }
}

@Composable
private fun RichTextBlock(text: String) {
    val blocks = remember(text) { parseRichBlocks(text) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        blocks.forEach { block ->
            when (block.type) {
                RichBlockType.Heading -> Text(
                    text = markdownAnnotatedString(block.text),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                )
                RichBlockType.Paragraph -> Text(
                    text = markdownAnnotatedString(block.text),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                RichBlockType.Bullet -> Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = markdownAnnotatedString(block.text),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                RichBlockType.Table -> block.table?.let { RichMarkdownTable(it) }
            }
        }
    }
}

@Composable
private fun RichMarkdownTable(table: MarkdownTable) {
    val columnWidths = table.headers.indices.map { column ->
        val maxLength = (listOf(table.headers[column]) + table.rows.map { it.getOrElse(column) { "" } })
            .maxOf { it.length }
        when {
            maxLength >= 44 -> 240.dp
            maxLength >= 26 -> 190.dp
            maxLength >= 14 -> 140.dp
            else -> 96.dp
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .clip(RoundedCornerShape(12.dp)),
    ) {
        RichTableRow(
            cells = table.headers,
            columnWidths = columnWidths,
            isHeader = true,
        )
        table.rows.forEachIndexed { index, row ->
            RichTableRow(
                cells = row,
                columnWidths = columnWidths,
                isHeader = false,
                alternate = index % 2 == 1,
            )
        }
    }
}

@Composable
private fun RichTableRow(
    cells: List<String>,
    columnWidths: List<androidx.compose.ui.unit.Dp>,
    isHeader: Boolean,
    alternate: Boolean = false,
) {
    val background = when {
        isHeader -> MaterialTheme.colorScheme.primary.copy(alpha = 0.13f)
        alternate -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
    }
    Row(modifier = Modifier.background(background)) {
        columnWidths.forEachIndexed { index, width ->
            Text(
                text = markdownAnnotatedString(cells.getOrElse(index) { "" }),
                modifier = Modifier
                    .width(width)
                    .padding(horizontal = 10.dp, vertical = 9.dp),
                style = if (isHeader) {
                    MaterialTheme.typography.labelMedium
                } else {
                    MaterialTheme.typography.bodySmall
                },
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isHeader) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun ToolUsageBlock(
    text: String,
    expanded: Boolean,
    isActive: Boolean,
    onToggle: () -> Unit,
) {
    val tools = text.split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .ifEmpty { listOf(text) }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CollapsibleStatusHeader(
                title = "Tools",
                detail = if (tools.size == 1) tools.first() else "${tools.size} used",
                isActive = isActive,
                expanded = expanded,
            )
            AnimatedVisibility(visible = expanded) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(tools) { tool ->
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.11f),
                            contentColor = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(50.dp),
                        ) {
                            Text(
                                text = tool,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThoughtBlock(
    text: String,
    expanded: Boolean,
    isActive: Boolean,
    onToggle: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f),
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            CollapsibleStatusHeader(
                title = "Thinking",
                detail = if (isActive) "in progress" else "tap to view",
                isActive = isActive,
                expanded = expanded,
            )
            AnimatedVisibility(visible = expanded) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    lineHeight = 18.sp,
                )
            }
        }
    }
}

@Composable
private fun CollapsibleStatusHeader(
    title: String,
    detail: String,
    isActive: Boolean,
    expanded: Boolean,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PulsingDot(active = isActive)
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = detail,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = if (expanded) "Hide" else "Show",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun PulsingDot(active: Boolean) {
    val alpha = if (active) {
        val transition = rememberInfiniteTransition(label = "status-pulse")
        val value by transition.animateFloat(
            initialValue = 0.25f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 720),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "status-alpha",
        )
        value
    } else {
        0.55f
    }

    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha)),
    )
}

@Composable
private fun CodeBlock(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.92f),
        contentColor = MaterialTheme.colorScheme.inverseOnSurface,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun ToolErrorBlock(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "Tool errors",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun StreamingIndicator(text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(14.dp),
            strokeWidth = 2.dp,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MessageActions(
    canEdit: Boolean,
    canDelete: Boolean,
    canSpeak: Boolean,
    isSpeaking: Boolean,
    onCopy: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSpeak: () -> Unit,
    onStopSpeaking: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp),
    ) {
        IconButton(
            onClick = onCopy,
            modifier = Modifier.size(34.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.ContentCopy,
                contentDescription = "Copy message",
                modifier = Modifier.size(17.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (canDelete) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(34.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete message",
                    modifier = Modifier.size(17.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (canSpeak) {
            IconButton(
                onClick = if (isSpeaking) onStopSpeaking else onSpeak,
                modifier = Modifier.size(34.dp),
            ) {
                Icon(
                    imageVector = if (isSpeaking) Icons.Filled.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = if (isSpeaking) "Stop speaking" else "Read aloud",
                    modifier = Modifier.size(17.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (canEdit) {
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(34.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit and resend",
                    modifier = Modifier.size(17.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun TypingRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppMark(modifier = Modifier.size(30.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
            shape = RoundedCornerShape(18.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant),
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageAttachButton(
    onPickImage: () -> Unit,
    onTakePhoto: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded = true },
            colors = IconButtonDefaults.filledTonalIconButtonColors(),
            modifier = Modifier.size(42.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.AddPhotoAlternate,
                contentDescription = "Attach image",
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("Gallery") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.AddPhotoAlternate,
                        contentDescription = null,
                    )
                },
                onClick = {
                    expanded = false
                    onPickImage()
                },
            )
            DropdownMenuItem(
                text = { Text("Camera") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = null,
                    )
                },
                onClick = {
                    expanded = false
                    onTakePhoto()
                },
            )
        }
    }
}

@Composable
private fun DocumentAttachButton(onPickDocument: () -> Unit) {
    IconButton(
        onClick = onPickDocument,
        colors = IconButtonDefaults.filledTonalIconButtonColors(),
        modifier = Modifier.size(42.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.Description,
            contentDescription = "Attach document",
        )
    }
}

@Composable
private fun VoiceInputButton(
    isListening: Boolean,
    onVoiceInput: () -> Unit,
) {
    IconButton(
        onClick = onVoiceInput,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = if (isListening) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            },
            contentColor = if (isListening) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSecondaryContainer
            },
        ),
        modifier = Modifier.size(42.dp),
    ) {
        Icon(
            imageVector = if (isListening) Icons.Filled.Stop else Icons.Filled.Mic,
            contentDescription = if (isListening) "Stop voice input" else "Voice input",
        )
    }
}

@Composable
private fun AttachedImageChip(
    attachment: ChatImageAttachment,
    onClear: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f),
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 7.dp, bottom = 7.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.AddPhotoAlternate,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = attachment.label,
                modifier = Modifier.widthIn(max = 260.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium,
            )
            IconButton(
                onClick = onClear,
                modifier = Modifier.size(30.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Remove image",
                    modifier = Modifier.size(17.dp),
                )
            }
        }
    }
}

@Composable
private fun AttachedDocumentChip(
    document: ChatDocumentAttachment,
    onClear: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f),
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 7.dp, bottom = 7.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Description,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Column(modifier = Modifier.widthIn(max = 260.dp)) {
                Text(
                    text = document.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = "${document.text.length.coerceAtMost(99999)} chars extracted",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(
                onClick = onClear,
                modifier = Modifier.size(30.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Remove document",
                    modifier = Modifier.size(17.dp),
                )
            }
        }
    }
}

@Composable
private fun ChatComposer(
    state: ChatUiState,
    input: String,
    isSending: Boolean,
    error: String?,
    canAttachImage: Boolean,
    attachedImages: List<ChatImageAttachment>,
    attachedDocumentText: ChatDocumentAttachment?,
    reasoningSupported: Boolean,
    reasoningEnabled: Boolean,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onCancelSend: () -> Unit,
    onPickImage: () -> Unit,
    onTakePhoto: () -> Unit,
    onPickDocument: () -> Unit,
    onVoiceInput: () -> Unit,
    isListening: Boolean,
    onClearImage: () -> Unit,
    onReasoningEnabledChange: (Boolean) -> Unit,
    onDismissError: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val canSend = (input.isNotBlank() || attachedImages.isNotEmpty() || attachedDocumentText != null) && !isSending

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AnimatedVisibility(visible = error != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = error.orEmpty(),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall,
                        )
                        TextButton(onClick = onDismissError) {
                            Text("Dismiss")
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = true,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    attachedImages.forEach { attachment ->
                        AttachedImageChip(attachment = attachment, onClear = onClearImage)
                    }
                    if (attachedDocumentText != null) {
                        AttachedDocumentChip(
                            document = attachedDocumentText,
                            onClear = onClearImage,
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        DocumentAttachButton(onPickDocument = onPickDocument)
                        if (canAttachImage) {
                            ImageAttachButton(
                                onPickImage = onPickImage,
                                onTakePhoto = onTakePhoto,
                            )
                        }
                        if (state.voiceInputEnabled) {
                            VoiceInputButton(
                                isListening = isListening,
                                onVoiceInput = onVoiceInput,
                            )
                        }
                        if (reasoningSupported) {
                            FilterChip(
                                selected = reasoningEnabled,
                                onClick = { onReasoningEnabledChange(!reasoningEnabled) },
                                label = {
                                    Text(if (reasoningEnabled) "Thinking on" else "Thinking off")
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Lightbulb,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                    )
                                },
                            )
                        }
                    }
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f),
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(start = 18.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    BasicTextField(
                        value = input,
                        onValueChange = onInputChange,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 32.dp, max = 132.dp)
                            .padding(vertical = 6.dp),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            lineHeight = 22.sp,
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (canSend) {
                                    focusManager.clearFocus()
                                    onSend()
                                }
                            },
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                if (input.isBlank()) {
                                    Text(
                                        text = "Message LM Studio",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                                innerTextField()
                            }
                        },
                    )

                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            if (isSending) {
                                onCancelSend()
                            } else {
                                onSend()
                            }
                        },
                        enabled = isSending || canSend,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (isSending) {
                                MaterialTheme.colorScheme.errorContainer
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            contentColor = if (isSending) {
                                MaterialTheme.colorScheme.onErrorContainer
                            } else {
                                MaterialTheme.colorScheme.onPrimary
                            },
                        ),
                    ) {
                        if (isSending) {
                            Icon(
                                imageVector = Icons.Filled.Stop,
                                contentDescription = "Stop response",
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                            )
                        }
                    }
                }
            }
            ContextUsageBar(state = state)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun GenerationNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    decimal: Boolean,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = true,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(
            keyboardType = if (decimal) KeyboardType.Decimal else KeyboardType.Number,
            imeAction = ImeAction.Next,
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsSheet(
    state: ChatUiState,
    onDismiss: () -> Unit,
    onApiUrlChange: (String) -> Unit,
    onModelChange: (String) -> Unit,
    onApiTokenChange: (String) -> Unit,
    onServerToolsEnabledChange: (Boolean) -> Unit,
    onServerIntegrationsChange: (String) -> Unit,
    onAllowedToolsChange: (String) -> Unit,
    onNativeIntentToolEnabledChange: (Boolean) -> Unit,
    onCalendarToolEnabledChange: (Boolean) -> Unit,
    onContactsToolEnabledChange: (Boolean) -> Unit,
    onNotificationDigestToolEnabledChange: (Boolean) -> Unit,
    onLocalFileSearchToolEnabledChange: (Boolean) -> Unit,
    onPickLocalSearchFolder: () -> Unit,
    onDeviceStatusToolEnabledChange: (Boolean) -> Unit,
    onVoiceInputEnabledChange: (Boolean) -> Unit,
    onVoiceOutputEnabledChange: (Boolean) -> Unit,
    onAutoReadAnswersEnabledChange: (Boolean) -> Unit,
    onAppendDateTimeToSystemPromptChange: (Boolean) -> Unit,
    onSystemProfileSelect: (String) -> Unit,
    onSystemProfileNameChange: (String) -> Unit,
    onSystemPromptChange: (String) -> Unit,
    onTemperatureChange: (String) -> Unit,
    onTopPChange: (String) -> Unit,
    onMaxTokensChange: (String) -> Unit,
    onContextLengthChange: (String) -> Unit,
    onPresencePenaltyChange: (String) -> Unit,
    onFrequencyPenaltyChange: (String) -> Unit,
    onSeedChange: (String) -> Unit,
    onSystemProfileSave: () -> Unit,
    onSystemProfileCreate: () -> Unit,
    onSystemProfileDelete: (String) -> Unit,
    onRefreshModels: () -> Unit,
    onExportChats: () -> Unit,
    onImportChats: () -> Unit,
) {
    val scrollState = rememberScrollState()
    var advancedGenerationExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 22.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Connection",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            OutlinedTextField(
                value = state.apiUrl,
                onValueChange = onApiUrlChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("LM Studio API URL") },
                supportingText = { Text("Emulator default: http://10.0.2.2:1234/v1") },
            )

            OutlinedTextField(
                value = state.apiToken,
                onValueChange = onApiTokenChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("API token") },
                placeholder = { Text("sk-lm-...") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                supportingText = {
                    Text("Required when LM Studio server authentication or MCP permissions are enabled.")
                },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedTextField(
                    value = state.model,
                    onValueChange = onModelChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("Model") },
                )
                IconButton(
                    onClick = onRefreshModels,
                    enabled = !state.isLoadingModels,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(),
                ) {
                    if (state.isLoadingModels) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh models",
                        )
                    }
                }
            }

            if (state.availableModels.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 10.dp),
                ) {
                    items(state.availableModels) { model ->
                        FilterChip(
                            selected = state.model == model,
                            onClick = { onModelChange(model) },
                            label = {
                                Text(
                                    text = model,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            leadingIcon = if (state.model == model) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            } else {
                                null
                            },
                            modifier = Modifier.widthIn(max = 260.dp),
                        )
                    }
                }
            }

            if (state.modelLoadError != null) {
                Text(
                    text = state.modelLoadError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.45f))

            Text(
                text = "System profile",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(end = 10.dp),
            ) {
                items(state.systemProfiles) { profile ->
                    FilterChip(
                        selected = state.activeSystemProfileId == profile.id,
                        onClick = { onSystemProfileSelect(profile.id) },
                        label = {
                            Text(
                                text = profile.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        leadingIcon = if (state.activeSystemProfileId == profile.id) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        } else {
                            null
                        },
                        modifier = Modifier.widthIn(max = 220.dp),
                    )
                }
            }
            OutlinedTextField(
                value = state.systemProfileNameDraft,
                onValueChange = onSystemProfileNameChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Profile name") },
            )
            OutlinedTextField(
                value = state.systemPromptDraft,
                onValueChange = onSystemPromptChange,
                modifier = Modifier.fillMaxWidth(),
                minLines = 5,
                maxLines = 10,
                label = { Text("System prompt") },
                supportingText = { Text("Used as the system prompt for new requests.") },
            )
            ToolToggleRow(
                title = "Append phone date/time",
                description = "Adds the current phone date, time, and time zone to every system prompt.",
                checked = state.appendDateTimeToSystemPrompt,
                onCheckedChange = onAppendDateTimeToSystemPromptChange,
            )
            OutlinedButton(
                onClick = { advancedGenerationExpanded = !advancedGenerationExpanded },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = if (advancedGenerationExpanded) {
                        "Hide advanced generation"
                    } else {
                        "Advanced generation"
                    },
                )
            }
            AnimatedVisibility(visible = advancedGenerationExpanded) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "Blank or invalid values are omitted from the API request.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        GenerationNumberField(
                            value = state.temperatureDraft,
                            onValueChange = onTemperatureChange,
                            label = "Temperature",
                            modifier = Modifier.weight(1f),
                            decimal = true,
                        )
                        GenerationNumberField(
                            value = state.topPDraft,
                            onValueChange = onTopPChange,
                            label = "Top P",
                            modifier = Modifier.weight(1f),
                            decimal = true,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        GenerationNumberField(
                            value = state.maxTokensDraft,
                            onValueChange = onMaxTokensChange,
                            label = "Max tokens",
                            modifier = Modifier.weight(1f),
                            decimal = false,
                        )
                        GenerationNumberField(
                            value = state.contextLengthDraft,
                            onValueChange = onContextLengthChange,
                            label = "Context length",
                            modifier = Modifier.weight(1f),
                            decimal = false,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        GenerationNumberField(
                            value = state.presencePenaltyDraft,
                            onValueChange = onPresencePenaltyChange,
                            label = "Presence penalty",
                            modifier = Modifier.weight(1f),
                            decimal = true,
                        )
                        GenerationNumberField(
                            value = state.frequencyPenaltyDraft,
                            onValueChange = onFrequencyPenaltyChange,
                            label = "Frequency penalty",
                            modifier = Modifier.weight(1f),
                            decimal = true,
                        )
                    }
                    GenerationNumberField(
                        value = state.seedDraft,
                        onValueChange = onSeedChange,
                        label = "Seed",
                        modifier = Modifier.fillMaxWidth(),
                        decimal = false,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = onSystemProfileCreate,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("New")
                }
                OutlinedButton(
                    onClick = { onSystemProfileDelete(state.activeSystemProfileId) },
                    modifier = Modifier.weight(1f),
                    enabled = state.systemProfiles.size > 1,
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Delete")
                }
                Button(
                    onClick = onSystemProfileSave,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Save")
                }
            }

            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.45f))

            Text(
                text = "Phone tools",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Enabled tools are added quietly to the system prompt. Every phone action still asks for confirmation.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            ToolToggleRow(
                title = "Open app drafts",
                description = "Open URL, Maps, email draft, phone dialer, or SMS draft.",
                checked = state.nativeIntentToolEnabled,
                onCheckedChange = onNativeIntentToolEnabledChange,
            )
            ToolToggleRow(
                title = "Calendar and reminders",
                description = "Prepare calendar events or reminder drafts.",
                checked = state.calendarToolEnabled,
                onCheckedChange = onCalendarToolEnabledChange,
            )
            ToolToggleRow(
                title = "Contacts lookup",
                description = "Search contacts by name after Android permission is granted.",
                checked = state.contactsToolEnabled,
                onCheckedChange = onContactsToolEnabledChange,
            )
            ToolToggleRow(
                title = "Notification digest",
                description = "Summarize captured notifications. Android notification access opens when enabled.",
                checked = state.notificationDigestToolEnabled,
                onCheckedChange = onNotificationDigestToolEnabledChange,
            )
            ToolToggleRow(
                title = "Local file search",
                description = "Search a user-selected folder for document names and readable text snippets.",
                checked = state.localFileSearchToolEnabled,
                onCheckedChange = onLocalFileSearchToolEnabledChange,
            )
            AnimatedVisibility(visible = state.localFileSearchToolEnabled) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (state.localFileSearchTreeUri.isBlank()) {
                            "No folder selected."
                        } else {
                            "Folder access granted."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    OutlinedButton(
                        onClick = onPickLocalSearchFolder,
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Change folder")
                    }
                }
            }
            ToolToggleRow(
                title = "Device status",
                description = "Report battery, network, storage, app version, and LM Studio connectivity.",
                checked = state.deviceStatusToolEnabled,
                onCheckedChange = onDeviceStatusToolEnabledChange,
            )

            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.45f))

            Text(
                text = "Voice",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            ToolToggleRow(
                title = "Voice input",
                description = "Use the microphone to transcribe prompts into the message box.",
                checked = state.voiceInputEnabled,
                onCheckedChange = onVoiceInputEnabledChange,
            )
            ToolToggleRow(
                title = "Voice output",
                description = "Show read-aloud controls on assistant messages.",
                checked = state.voiceOutputEnabled,
                onCheckedChange = onVoiceOutputEnabledChange,
            )
            AnimatedVisibility(visible = state.voiceOutputEnabled) {
                ToolToggleRow(
                    title = "Auto-read answers",
                    description = "Read new assistant replies aloud after generation finishes.",
                    checked = state.autoReadAnswersEnabled,
                    onCheckedChange = onAutoReadAnswersEnabledChange,
                )
            }

            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.45f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Server tools",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Use LM Studio MCP/plugins through /api/v1/chat.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = state.serverToolsEnabled,
                    onCheckedChange = onServerToolsEnabledChange,
                )
            }

            AnimatedVisibility(visible = state.serverToolsEnabled) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedTextField(
                        value = state.serverIntegrations,
                        onValueChange = onServerIntegrationsChange,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 6,
                        label = { Text("Integrations") },
                        placeholder = { Text("mcp/playwright") },
                        supportingText = {
                            Text("Use mcp/<server_label> from mcp.json, or paste JSON for custom integrations.")
                        },
                    )
                    OutlinedTextField(
                        value = state.allowedTools,
                        onValueChange = onAllowedToolsChange,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 1,
                        maxLines = 4,
                        label = { Text("Allowed tools") },
                        placeholder = { Text("web_search\nweb_fetch") },
                        supportingText = {
                            Text("Optional. One tool name per line. Keeps broad MCP servers safer over API.")
                        },
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(end = 10.dp),
                    ) {
                        items(AllowedToolPresets) { toolName ->
                            AssistChip(
                                onClick = {
                                    onAllowedToolsChange(
                                        state.allowedTools.withIntegration(toolName),
                                    )
                                },
                                label = {
                                    Text(
                                        text = toolName,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                },
                                modifier = Modifier.widthIn(max = 220.dp),
                            )
                        }
                    }
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(end = 10.dp),
                    ) {
                        items(IntegrationPresets) { integrationId ->
                            AssistChip(
                                onClick = {
                                    onServerIntegrationsChange(
                                        state.serverIntegrations.withIntegration(integrationId),
                                    )
                                },
                                label = {
                                    Text(
                                        text = integrationId,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                },
                                modifier = Modifier.widthIn(max = 280.dp),
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.45f))

            Text(
                text = "Chat history",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = onImportChats,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Import")
                }
                OutlinedButton(
                    onClick = onExportChats,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Export")
                }
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Text("Done")
            }

            Text(
                text = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InfoSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppMark(modifier = Modifier.size(58.dp))
                Column {
                    Text(
                        text = "LMSMOB Chat",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Local LM Studio companion",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "License",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Copyright (c) 2026 MindyLab MB. All rights reserved.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "Official LMSMOB Chat app builds are free for personal, non-commercial use. The source code, project assets, and brand remain proprietary to MindyLab MB.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "Redistribution, modification, commercialization, rebranding, company use, or source-code use requires prior written permission.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "For licensing requests or commercial use, contact MindyLab MB info@mindylab.com.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Text(
                text = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text("Done")
            }
        }
    }
}

@Composable
private fun AppMark(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ml_logo),
        contentDescription = "MindyLab",
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
    )
}

@Composable
private fun LmStudioChatTheme(content: @Composable () -> Unit) {
    val light = lightColorScheme(
        primary = Color(0xFF156DFF),
        onPrimary = Color.White,
        secondary = Color(0xFF8C4DFF),
        tertiary = Color(0xFF00D4FF),
        background = Color(0xFFF4F7FF),
        onBackground = Color(0xFF071026),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF071026),
        surfaceVariant = Color(0xFFE8EEFF),
        onSurfaceVariant = Color(0xFF56617A),
        secondaryContainer = Color(0xFFE8E4FF),
        onSecondaryContainer = Color(0xFF15112F),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
    )
    val dark = darkColorScheme(
        primary = Color(0xFF28D9FF),
        onPrimary = Color(0xFF041225),
        secondary = Color(0xFFA66BFF),
        tertiary = Color(0xFF2C7BFF),
        background = Color(0xFF030716),
        onBackground = Color(0xFFE9F1FF),
        surface = Color(0xFF090F25),
        onSurface = Color(0xFFE9F1FF),
        surfaceVariant = Color(0xFF121A38),
        onSurfaceVariant = Color(0xFFAEB8D8),
        secondaryContainer = Color(0xFF18204A),
        onSecondaryContainer = Color(0xFFE9F1FF),
        errorContainer = Color(0xFF8A1C2C),
        onErrorContainer = Color(0xFFFFDAD6),
    )

    MaterialTheme(
        colorScheme = if (androidx.compose.foundation.isSystemInDarkTheme()) dark else light,
        typography = Typography(
            bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
            bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
            labelSmall = TextStyle(fontSize = 11.sp, lineHeight = 14.sp),
        ),
        content = content,
    )
}
