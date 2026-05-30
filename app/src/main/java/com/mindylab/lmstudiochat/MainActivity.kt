package com.mindylab.lmstudiochat

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ActivityNotFoundException
import android.content.ContentValues
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
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.StatFs
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager
import android.provider.OpenableColumns
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Base64
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.material3.Slider
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.TimeUnit
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.math.roundToInt
import java.util.zip.ZipOutputStream

private const val DefaultApiUrl = "http://10.0.2.2:1234/v1"
private const val LegacyDefaultSystemPrompt = "You are a helpful assistant running locally through LM Studio."
private const val LegacyTranslateLtPresetId = "translate_lt"
private const val ChatAppendPresetSampleName = "Summarize"
private const val ChatAppendPresetSampleText = "summarize this text"
private val DefaultSystemPrompt = """
    You are LMSMOB Chat, a polite, practical assistant running locally through LM Studio on the user's Android device.

    Core style:
    - Be warm, concise, and useful. Ask a short clarifying question only when it is needed to avoid a wrong action.
    - Use the user's language when it is clear from the conversation.
    - Explain uncertainty honestly, especially for live, legal, medical, financial, or device-permission-sensitive tasks.
    - When the user's context suggests a useful next step, offer one or two concrete options without overwhelming them.

    You can help with:
    - Chat, drafting, rewriting, translation, summarization, planning, troubleshooting, and coding explanations.
    - Images when the selected LM Studio model supports vision.
    - Documents shared or uploaded by the user, including PDF, DOC, DOCX, XLS, and XLSX after the app converts them to text or images.
    - Rich responses with readable Markdown, tables, lists, and code blocks when appropriate.
    - LM Studio server-side MCP/plugin tools when the app enables integrations for the current request. These are used by LM Studio itself, not by Android action blocks.
    - Android phone-side tools when the app lists them below. These require a <lmsmob_action> block and user confirmation before any action is performed.

    Behavior with tools and actions:
    - Prefer answering directly when no tool is needed.
    - If web search, URL fetching, YouTube transcript, QR generation/scanning, or page screenshot work is needed, use LM Studio server-side tools only when server tools are enabled.
    - If an Android phone-side action is needed, describe what you want to do and emit exactly one action block in the format the app provides.
    - Never use <lmsmob_action> for LM Studio MCP/plugin tools such as web_search, web_fetch, youtube_transcript, qr_generate, qr_scan, or web_page_to_images.
    - Treat calls, SMS, email, calendar, reminders, alarms, URLs, maps, contacts, files, notifications, and watch jobs as sensitive. Never claim they are finished until the app returns a tool result or the user confirms the draft.
    - For monitoring or recurring tasks, prefer clear, narrow watch-job filters such as app, sender, and text condition.
    - If a capability is not enabled or not listed, tell the user how to enable it instead of pretending you used it.
""".trimIndent()
private const val DefaultSystemProfileId = "default"
private const val MaxDocumentTextChars = 24000
private const val DefaultContextLength = 8000
private const val AllChatsFolderId = "__all_chats__"
private const val UnfiledChatsFolderId = "__unfiled_chats__"
private const val VoiceOutputEngineSystem = "system"
private const val VoiceOutputEngineSupertonic = "supertonic"
private const val VoiceOutputLanguageEngineDefault = "engine_default"
private const val VoiceOutputLanguageDevice = "device"
private const val VoiceOutputLanguageLithuanian = "lt"
private const val SupertonicTtsQualityParam = "com.brahmadeo.supertonic.tts.DIFFUSION_STEPS"
private const val SupertonicTtsQualityParamShort = "diffusion_steps"
private const val SupertonicTtsSynthesisTimeoutParam = "com.brahmadeo.supertonic.tts.SYNTHESIS_TIMEOUT_MS"
private const val SupertonicTtsSynthesisTimeoutParamShort = "synthesis_timeout_ms"
private const val SupertonicTtsEnginePackage = "com.brahmadeo.supertonic.tts"
private const val SupertonicTtsInstallUrl = "https://f-droid.org/en/packages/com.brahmadeo.supertonic.tts/"
private const val VoiceOutputDefaultChunkChars = 420
private const val VoiceOutputMinChunkChars = 220
private const val VoiceOutputMaxChunkChars = 900
private const val VoiceOutputChunkStepChars = 20
private const val VoiceOutputDefaultStartBufferChunks = 2
private const val VoiceOutputMinStartBufferChunks = 1
private const val VoiceOutputMaxStartBufferChunks = 3
private const val VoiceOutputSynthesisTimeoutMs = 20000L
private const val VoiceOutputEngineWatchdogTimeoutMs = 18000L
private const val VoiceOutputPreparedWaitTimeoutMs = 45000L
private const val VoiceOutputSynthesisMaxRetries = 1
private const val VoiceOutputSynthesisRetryDelayMs = 350L
private const val TtsChunkUtteranceSeparator = "::lmsmob_tts_chunk::"
private const val TtsSynthesisUtterancePrefix = "synth"
private const val TtsLogTag = "LMSMOB_TTS"

private data class VoiceOutputLanguageOption(
    val tag: String,
    val label: String,
)

data class VoiceOutputVoiceOption(
    val name: String,
    val label: String,
    val languageTag: String,
)

private val VoiceOutputLanguageOptions = listOf(
    VoiceOutputLanguageOption(VoiceOutputLanguageEngineDefault, "TTS engine default"),
    VoiceOutputLanguageOption(VoiceOutputLanguageDevice, "Device language"),
    VoiceOutputLanguageOption("en-US", "English"),
    VoiceOutputLanguageOption("fr-FR", "French"),
    VoiceOutputLanguageOption("pt-PT", "Portuguese"),
    VoiceOutputLanguageOption("es-ES", "Spanish"),
    VoiceOutputLanguageOption("ko-KR", "Korean"),
    VoiceOutputLanguageOption("ja-JP", "Japanese"),
    VoiceOutputLanguageOption("ar", "Arabic"),
    VoiceOutputLanguageOption("bg", "Bulgarian"),
    VoiceOutputLanguageOption("cs", "Czech"),
    VoiceOutputLanguageOption("da", "Danish"),
    VoiceOutputLanguageOption("de-DE", "German"),
    VoiceOutputLanguageOption("el", "Greek"),
    VoiceOutputLanguageOption("et", "Estonian"),
    VoiceOutputLanguageOption("fi", "Finnish"),
    VoiceOutputLanguageOption("hi-IN", "Hindi"),
    VoiceOutputLanguageOption("hr", "Croatian"),
    VoiceOutputLanguageOption("hu", "Hungarian"),
    VoiceOutputLanguageOption("id", "Indonesian"),
    VoiceOutputLanguageOption("it-IT", "Italian"),
    VoiceOutputLanguageOption(VoiceOutputLanguageLithuanian, "Lithuanian"),
    VoiceOutputLanguageOption("lv", "Latvian"),
    VoiceOutputLanguageOption("nl", "Dutch"),
    VoiceOutputLanguageOption("pl", "Polish"),
    VoiceOutputLanguageOption("ro", "Romanian"),
    VoiceOutputLanguageOption("ru", "Russian"),
    VoiceOutputLanguageOption("sk", "Slovak"),
    VoiceOutputLanguageOption("sl", "Slovenian"),
    VoiceOutputLanguageOption("sv", "Swedish"),
    VoiceOutputLanguageOption("tr", "Turkish"),
    VoiceOutputLanguageOption("uk", "Ukrainian"),
    VoiceOutputLanguageOption("vi", "Vietnamese"),
)

private data class IncomingShareIntent(
    val id: Long,
    val intent: Intent,
)

data class PreparedShareContent(
    val text: String = "",
    val imageAttachments: List<ChatImageAttachment> = emptyList(),
    val documentUri: Uri? = null,
)

enum class ShareDestinationKind {
    New,
    Temporary,
    Existing,
}

data class ShareDestinationSelection(
    val kind: ShareDestinationKind,
    val sessionId: String? = null,
)

data class NativeToolAction(
    val id: String = UUID.randomUUID().toString(),
    val tool: String,
    val args: JSONObject,
)

data class WatchJob(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val trigger: String = "notification",
    val appQuery: String = "",
    val senderQuery: String = "",
    val textQuery: String = "",
    val matchMode: String = "filter",
    val aiInstruction: String = "",
    val scheduleMinutes: Int = 0,
    val alertMessage: String = "",
    val alertMode: String = "normal",
    val lifetime: String = "noend",
    val enabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastRunAt: Long = 0L,
    val lastCheckedAt: Long = 0L,
    val lastMatchedAt: Long = 0L,
    val matchCount: Int = 0,
)

data class WatchPromptResult(
    val shouldAlert: Boolean,
    val title: String = "",
    val detail: String = "",
    val summary: String = "",
    val triggerReason: String = "",
    val sources: List<String> = emptyList(),
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
    "youtube_transcript",
    "qr_generate",
    "qr_scan",
    "web_page_to_images",
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
                var pendingShareContent by remember { mutableStateOf<PreparedShareContent?>(null) }
                val uiScope = rememberCoroutineScope()
                val mainHandler = remember { Handler(Looper.getMainLooper()) }
                var ttsReady by remember { mutableStateOf(false) }
                var speakingMessageId by remember { mutableStateOf<String?>(null) }
                var ttsPaused by remember { mutableStateOf(false) }
                var ttsPlaybackJob by remember { mutableStateOf<Job?>(null) }
                var ttsMediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
                val ttsSynthesisResults = remember { mutableMapOf<String, CompletableDeferred<Boolean>>() }
                var ttsPlaybackState by remember { mutableStateOf<TtsPlaybackState?>(null) }
                val ttsRunCounter = remember { AtomicLong(0L) }
                var ttsVoiceOptions by remember { mutableStateOf<List<VoiceOutputVoiceOption>>(emptyList()) }
                val selectedTtsEnginePackage = state.voiceOutputEngine.voiceOutputEnginePackageName()
                val selectedTtsLanguage = state.voiceOutputLanguage.normalizedVoiceOutputLanguage()
                val selectedTtsVoiceName = state.voiceOutputVoiceName.trim()
                val selectedTtsSpeed = state.voiceOutputSpeed.normalizedVoiceOutputSpeed()
                val selectedTtsQuality = state.voiceOutputQuality.normalizedVoiceOutputQuality()
                val selectedTtsChunkSize = state.voiceOutputChunkSize.normalizedVoiceOutputChunkSize()
                val selectedTtsStartBuffer = state.voiceOutputStartBuffer.normalizedVoiceOutputStartBuffer()
                val selectedTtsEngineAvailable = selectedTtsEnginePackage
                    ?.let { context.isPackageInstalled(it) }
                    ?: true
                val effectiveTtsEnginePackage = selectedTtsEnginePackage.takeIf { selectedTtsEngineAvailable }
                val textToSpeech = remember(effectiveTtsEnginePackage, selectedTtsLanguage) {
                    ttsReady = false
                    val listener = TextToSpeech.OnInitListener { status ->
                        mainHandler.post { ttsReady = status == TextToSpeech.SUCCESS }
                    }
                    if (effectiveTtsEnginePackage == null) {
                        TextToSpeech(context.applicationContext, listener)
                    } else {
                        TextToSpeech(context.applicationContext, listener, effectiveTtsEnginePackage)
                    }
                }
                DisposableEffect(textToSpeech) {
                    textToSpeech.setOnUtteranceProgressListener(
                        object : UtteranceProgressListener() {
                            override fun onStart(utteranceId: String?) {
                                mainHandler.post {
                                    val chunkId = utteranceId?.toSynthesisTtsChunkId()
                                    if (chunkId != null) {
                                        ttsPlaybackState = ttsPlaybackState?.withChunkStatus(
                                            chunkId,
                                            TtsChunkStatus.Preparing,
                                        )
                                    }
                                }
                            }

                            override fun onDone(utteranceId: String?) {
                                mainHandler.post {
                                    val chunkId = utteranceId?.toSynthesisTtsChunkId()
                                    if (chunkId == null) {
                                        return@post
                                    }
                                    val pendingResult = ttsSynthesisResults.remove(utteranceId)
                                        ?: return@post
                                    pendingResult.complete(true)
                                    ttsPlaybackState = ttsPlaybackState?.withChunkStatus(
                                        chunkId,
                                        TtsChunkStatus.Ready,
                                    )
                                }
                            }

                            override fun onError(utteranceId: String?) {
                                mainHandler.post {
                                    val chunkId = utteranceId?.toSynthesisTtsChunkId()
                                    if (chunkId == null) {
                                        return@post
                                    }
                                    val pendingResult = ttsSynthesisResults.remove(utteranceId)
                                        ?: return@post
                                    pendingResult.complete(false)
                                    ttsPlaybackState = ttsPlaybackState?.withChunksFromStatus(
                                        chunkId,
                                        TtsChunkStatus.Error,
                                    )
                                }
                            }
                        },
                    )
                    onDispose {
                        ttsPlaybackJob?.cancel()
                        ttsMediaPlayer?.release()
                        ttsMediaPlayer = null
                        ttsPaused = false
                        ttsSynthesisResults.values.forEach { it.complete(false) }
                        ttsSynthesisResults.clear()
                        textToSpeech.stop()
                        textToSpeech.shutdown()
                    }
                }
                LaunchedEffect(textToSpeech, ttsReady) {
                    ttsVoiceOptions = if (ttsReady) {
                        textToSpeech.voiceOutputVoiceOptions()
                    } else {
                        emptyList()
                    }
                }
                fun stopSpeaking() {
                    ttsPlaybackJob?.cancel()
                    ttsMediaPlayer?.release()
                    ttsMediaPlayer = null
                    ttsPaused = false
                    ttsSynthesisResults.values.forEach { it.complete(false) }
                    ttsSynthesisResults.clear()
                    textToSpeech.stop()
                    speakingMessageId = null
                    ttsPlaybackState = null
                }
                fun pauseSpeaking() {
                    val player = ttsMediaPlayer ?: return
                    runCatching {
                        if (player.isPlaying) {
                            player.pause()
                            ttsPaused = true
                        }
                    }
                }
                fun resumeSpeaking() {
                    val player = ttsMediaPlayer ?: return
                    runCatching {
                        player.start()
                        ttsPaused = false
                    }.onFailure {
                        viewModel.showError("Could not resume text-to-speech playback.")
                    }
                }
                fun speakAssistantMessage(messageId: String, content: String) {
                    if (!state.voiceOutputEnabled) return
                    val text = content.speakableAnswerText()
                    if (text.isBlank()) return
                    val chunks = text.toVoiceOutputChunks(selectedTtsChunkSize)
                    if (chunks.isEmpty()) return
                    if (!selectedTtsEngineAvailable) {
                        viewModel.showError("Supertonic TTS engine is not installed. Install it or switch Voice output engine back to System.")
                        return
                    }
                    if (!ttsReady) {
                        viewModel.showError("${state.voiceOutputEngine.voiceOutputEngineLabel()} text-to-speech is not ready yet. Try again in a moment.")
                        return
                    }
                    selectedTtsLanguage.voiceOutputLocale()?.let { locale ->
                        val languageResult = textToSpeech.setLanguage(locale)
                        if (
                            languageResult == TextToSpeech.LANG_MISSING_DATA ||
                            languageResult == TextToSpeech.LANG_NOT_SUPPORTED
                        ) {
                            viewModel.showError("${state.voiceOutputEngine.voiceOutputEngineLabel()} does not support ${selectedTtsLanguage.voiceOutputLanguageLabel()} for this message.")
                            return
                        }
                    }
                    val rateResult = textToSpeech.setSpeechRate(selectedTtsSpeed)
                    if (rateResult == TextToSpeech.ERROR) {
                        viewModel.showError("Could not apply TTS speed ${selectedTtsSpeed.voiceOutputSpeedLabel()}.")
                        return
                    }
                    if (selectedTtsVoiceName.isNotBlank()) {
                        val voice = textToSpeech.voices
                            ?.firstOrNull { it.name == selectedTtsVoiceName }
                        if (voice == null) {
                            viewModel.showError("Selected speaker voice is not available for ${state.voiceOutputEngine.voiceOutputEngineLabel()}.")
                            return
                        }
                        val voiceResult = textToSpeech.setVoice(voice)
                        if (voiceResult == TextToSpeech.ERROR) {
                            viewModel.showError("Could not switch to the selected speaker voice.")
                            return
                        }
                    }
                    val ttsParams = Bundle().apply {
                        putInt(SupertonicTtsQualityParam, selectedTtsQuality)
                        putInt(SupertonicTtsQualityParamShort, selectedTtsQuality)
                        putLong(SupertonicTtsSynthesisTimeoutParam, VoiceOutputEngineWatchdogTimeoutMs)
                        putLong(SupertonicTtsSynthesisTimeoutParamShort, VoiceOutputEngineWatchdogTimeoutMs)
                    }

                    ttsPlaybackJob?.cancel()
                    ttsMediaPlayer?.release()
                    ttsMediaPlayer = null
                    ttsPaused = false
                    ttsSynthesisResults.values.forEach { it.complete(false) }
                    ttsSynthesisResults.clear()
                    val runId = ttsRunCounter.incrementAndGet()
                    ttsPlaybackState = TtsPlaybackState(
                        messageId = messageId,
                        runId = runId,
                        chunks = chunks.mapIndexed { index, chunk ->
                            TtsChunkProgress(
                                index = index,
                                preview = chunk.toTtsChunkPreview(),
                                charCount = chunk.length,
                            )
                        },
                    )

                    fun chunkFile(index: Int, attempt: Int): File =
                        File(context.cacheDir, "lmsmob-tts-$runId-$index-$attempt.wav")

                    suspend fun synthesizeChunkAttempt(index: Int, attempt: Int): File? {
                        val chunkId = TtsChunkId(messageId = messageId, runId = runId, index = index)
                        val file = chunkFile(index, attempt)
                        if (file.exists()) file.delete()
                        val utteranceId = chunkId.toSynthesisUtteranceId(attempt)
                        val done = CompletableDeferred<Boolean>()
                        ttsSynthesisResults[utteranceId] = done
                        ttsPlaybackState = ttsPlaybackState?.withChunkStatus(chunkId, TtsChunkStatus.Preparing)
                        Log.i(
                            TtsLogTag,
                            "Synthesizing TTS chunk ${index + 1} of ${chunks.size}, attempt ${attempt + 1}, chars=${chunks[index].length}",
                        )
                        val result = textToSpeech.synthesizeToFile(
                            chunks[index],
                            Bundle(ttsParams),
                            file,
                            utteranceId,
                        )
                        if (result == TextToSpeech.ERROR) {
                            ttsSynthesisResults.remove(utteranceId)
                            file.delete()
                            Log.w(
                                TtsLogTag,
                                "TTS engine rejected chunk ${index + 1} of ${chunks.size}, attempt ${attempt + 1}",
                            )
                            return null
                        }
                        val success = withTimeoutOrNull(VoiceOutputSynthesisTimeoutMs) {
                            done.await()
                        }
                        ttsSynthesisResults.remove(utteranceId)
                        return if (success == true && file.exists() && file.length() > 0L) {
                            Log.i(
                                TtsLogTag,
                                "Ready TTS chunk ${index + 1} of ${chunks.size}, attempt ${attempt + 1}, bytes=${file.length()}",
                            )
                            file
                        } else {
                            if (success == null) {
                                Log.w(
                                    TtsLogTag,
                                    "Timed out after ${VoiceOutputSynthesisTimeoutMs}ms while synthesizing TTS chunk ${index + 1} of ${chunks.size}, attempt ${attempt + 1}",
                                )
                                textToSpeech.stop()
                            } else {
                                Log.w(
                                    TtsLogTag,
                                    "TTS engine failed chunk ${index + 1} of ${chunks.size}, attempt ${attempt + 1}",
                                )
                            }
                            done.complete(false)
                            file.delete()
                            null
                        }
                    }

                    suspend fun synthesizeChunk(index: Int): File? {
                        val chunkId = TtsChunkId(messageId = messageId, runId = runId, index = index)
                        for (attempt in 0..VoiceOutputSynthesisMaxRetries) {
                            val file = synthesizeChunkAttempt(index, attempt)
                            if (file != null) return file
                            if (attempt < VoiceOutputSynthesisMaxRetries) {
                                Log.w(
                                    TtsLogTag,
                                    "Retrying TTS chunk ${index + 1} of ${chunks.size} after failed attempt ${attempt + 1}",
                                )
                                delay(VoiceOutputSynthesisRetryDelayMs)
                            }
                        }
                        ttsPlaybackState = ttsPlaybackState?.withChunksFromStatus(chunkId, TtsChunkStatus.Error)
                        return null
                    }

                    suspend fun playPreparedChunk(index: Int, file: File): Boolean {
                        val chunkId = TtsChunkId(messageId = messageId, runId = runId, index = index)
                        val done = CompletableDeferred<Boolean>()
                        ttsPlaybackState = ttsPlaybackState?.withChunkStatus(chunkId, TtsChunkStatus.Playing)
                        speakingMessageId = messageId
                        val player = MediaPlayer()
                        ttsMediaPlayer = player
                        player.setAudioAttributes(
                            AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build(),
                        )
                        player.setOnCompletionListener { completedPlayer ->
                            completedPlayer.release()
                            if (ttsMediaPlayer === completedPlayer) ttsMediaPlayer = null
                            ttsPaused = false
                            ttsPlaybackState = ttsPlaybackState?.withChunkStatus(chunkId, TtsChunkStatus.Complete)
                            done.complete(true)
                        }
                        player.setOnErrorListener { errorPlayer, _, _ ->
                            errorPlayer.release()
                            if (ttsMediaPlayer === errorPlayer) ttsMediaPlayer = null
                            ttsPaused = false
                            ttsPlaybackState = ttsPlaybackState?.withChunksFromStatus(chunkId, TtsChunkStatus.Error)
                            done.complete(false)
                            true
                        }
                        return runCatching {
                            player.setDataSource(file.absolutePath)
                            player.prepare()
                            player.start()
                        }.fold(
                            onSuccess = { done.await() },
                            onFailure = {
                                player.release()
                                if (ttsMediaPlayer === player) ttsMediaPlayer = null
                                ttsPaused = false
                                ttsPlaybackState = ttsPlaybackState?.withChunksFromStatus(chunkId, TtsChunkStatus.Error)
                                false
                            },
                        )
                    }

                    ttsPlaybackJob = uiScope.launch {
                        val preparedFiles = mutableMapOf<Int, File>()
                        val preparedSignals = chunks.indices.associateWith {
                            CompletableDeferred<File?>()
                        }
                        var producerJob: Job? = null
                        fun isCurrentRun(): Boolean {
                            val currentPlayback = ttsPlaybackState
                            return currentPlayback?.messageId == messageId &&
                                currentPlayback.runId == runId
                        }
                        suspend fun synthesizeAndStore(index: Int): Boolean {
                            val file = synthesizeChunk(index)
                            if (file != null) preparedFiles[index] = file
                            preparedSignals[index]?.complete(file)
                            return file != null
                        }
                        suspend fun preparedFileFor(index: Int): File? {
                            preparedFiles.remove(index)?.let { return it }
                            return withTimeoutOrNull(VoiceOutputPreparedWaitTimeoutMs) {
                                preparedSignals[index]?.await()
                            }
                        }
                        fun startProducer(fromIndex: Int) {
                            if (fromIndex !in chunks.indices) return
                            producerJob = launch {
                                for (index in fromIndex until chunks.size) {
                                    if (!isCurrentRun()) return@launch
                                    if (!synthesizeAndStore(index)) return@launch
                                }
                            }
                        }
                        try {
                            val preloadCount = chunks.size.coerceAtMost(selectedTtsStartBuffer)
                            repeat(preloadCount) { index ->
                                if (!isCurrentRun()) return@launch
                                if (!synthesizeAndStore(index)) {
                                    speakingMessageId = null
                                    viewModel.showError("Could not prepare text-to-speech chunk ${index + 1}.")
                                    return@launch
                                }
                            }
                            startProducer(preloadCount)
                            speakingMessageId = messageId
                            for (index in chunks.indices) {
                                if (!isCurrentRun()) return@launch
                                val file = preparedFileFor(index)
                                if (file == null) {
                                    speakingMessageId = null
                                    viewModel.showError("Could not prepare text-to-speech chunk ${index + 1}.")
                                    return@launch
                                }
                                val played = playPreparedChunk(index, file)
                                file.delete()
                                if (!played) {
                                    speakingMessageId = null
                                    viewModel.showError("Could not play text-to-speech chunk ${index + 1}.")
                                    return@launch
                                }
                            }
                            speakingMessageId = null
                        } catch (cancelled: CancellationException) {
                            preparedFiles.values.forEach { it.delete() }
                            producerJob?.cancel()
                            throw cancelled
                        } finally {
                            producerJob?.cancel()
                            preparedFiles.values.forEach { it.delete() }
                            preparedSignals.values.forEach { signal ->
                                if (!signal.isCompleted) signal.complete(null)
                            }
                            ttsMediaPlayer?.release()
                            ttsMediaPlayer = null
                            ttsPaused = false
                            ttsSynthesisResults.values.forEach { it.complete(false) }
                            ttsSynthesisResults.clear()
                        }
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
                    state.voiceOutputEngine,
                    state.voiceOutputLanguage,
                    state.voiceOutputVoiceName,
                    state.voiceOutputSpeed,
                    state.voiceOutputQuality,
                    state.voiceOutputChunkSize,
                    state.voiceOutputStartBuffer,
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
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission(),
                ) { granted ->
                    if (!granted) {
                        viewModel.showError("Notification permission is required before Watch Job alerts can appear.")
                    }
                }
                fun ensureWatchJobAccess() {
                    if (!context.hasPostNotificationsPermission()) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    if (!context.isNotificationListenerEnabled()) {
                        context.openNotificationListenerSettings(viewModel)
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
                    val preparedContent = prepareIncomingShareIntent(
                        context = context,
                        intent = request.intent,
                        viewModel = viewModel,
                    )
                    if (preparedContent != null) {
                        if (viewModel.applyDefaultShareDestination(preparedContent)) {
                            preparedContent.documentUri?.let { pendingDocumentUri = it }
                        } else {
                            pendingShareContent = preparedContent
                        }
                    }
                    shareIntentRequests.value = null
                }
                LmStudioApp(
                    state = state,
                    onInputChange = viewModel::updateInput,
                    onSend = viewModel::sendInput,
                    onCancelSend = viewModel::cancelSending,
                    onSuggestion = viewModel::sendPrompt,
                    onNewChat = viewModel::newChat,
                    onTemporaryChat = viewModel::temporaryChat,
                    onSelectChat = viewModel::selectChat,
                    onChatSearchChange = viewModel::updateChatSearchQuery,
                    onSelectChatFolder = viewModel::selectChatFolder,
                    onCreateChatFolder = viewModel::createChatFolder,
                    onDeleteChatFolder = viewModel::deleteChatFolder,
                    onMoveChatToFolder = viewModel::moveChatToFolder,
                    onEditMessage = viewModel::editMessage,
                    onDeleteMessage = viewModel::deleteMessage,
                    onDeleteChat = viewModel::deleteChat,
                    onOpenSettings = viewModel::openSettings,
                    onCloseSettings = viewModel::closeSettings,
                    onOpenInfo = viewModel::openInfo,
                    onCloseInfo = viewModel::closeInfo,
                    onOpenWatchJobs = {
                        ensureWatchJobAccess()
                        viewModel.openWatchJobs()
                    },
                    onCloseWatchJobs = viewModel::closeWatchJobs,
                    onApiUrlChange = viewModel::updateApiUrl,
                    onModelChange = viewModel::updateModel,
                    onApiTokenChange = viewModel::updateApiToken,
                    onServerToolsEnabledChange = viewModel::updateServerToolsEnabled,
                    onServerIntegrationsChange = viewModel::updateServerIntegrations,
                    onAllowedToolsChange = viewModel::updateAllowedTools,
                    onNativeIntentToolEnabledChange = viewModel::updateNativeIntentToolEnabled,
                    onCalendarToolEnabledChange = viewModel::updateCalendarToolEnabled,
                    onAlarmToolEnabledChange = viewModel::updateAlarmToolEnabled,
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
                    onWatchJobToolEnabledChange = { enabled ->
                        viewModel.updateWatchJobToolEnabled(enabled)
                        if (enabled) {
                            ensureWatchJobAccess()
                        }
                    },
                    onVoiceInputEnabledChange = viewModel::updateVoiceInputEnabled,
                    onVoiceOutputEnabledChange = viewModel::updateVoiceOutputEnabled,
                    onVoiceOutputEngineChange = viewModel::updateVoiceOutputEngine,
                    onVoiceOutputLanguageChange = viewModel::updateVoiceOutputLanguage,
                    onVoiceOutputVoiceChange = viewModel::updateVoiceOutputVoiceName,
                    onVoiceOutputSpeedChange = viewModel::updateVoiceOutputSpeed,
                    onVoiceOutputQualityChange = viewModel::updateVoiceOutputQuality,
                    onVoiceOutputChunkSizeChange = viewModel::updateVoiceOutputChunkSize,
                    onVoiceOutputStartBufferChange = viewModel::updateVoiceOutputStartBuffer,
                    voiceOutputVoiceOptions = ttsVoiceOptions,
                    onAutoReadAnswersEnabledChange = viewModel::updateAutoReadAnswersEnabled,
                    onAppendCapabilityGuideToSystemPromptChange = viewModel::updateAppendCapabilityGuideToSystemPrompt,
                    onAppendDateTimeToSystemPromptChange = viewModel::updateAppendDateTimeToSystemPrompt,
                    onShareDefaultSelectionEnabledChange = viewModel::updateShareDefaultSelectionEnabled,
                    onCreateWatchJob = viewModel::createWatchJob,
                    onUpdateWatchJob = viewModel::updateWatchJob,
                    onToggleWatchJob = viewModel::toggleWatchJob,
                    onDeleteWatchJob = viewModel::deleteWatchJob,
                    onSystemProfileSelect = viewModel::selectSystemProfile,
                    onSystemProfileNameChange = viewModel::updateSystemProfileNameDraft,
                    onSystemPromptChange = viewModel::updateSystemPromptDraft,
                    onChatAppendPresetApply = viewModel::applyChatAppendPreset,
                    onChatAppendPresetSelect = viewModel::selectChatAppendPreset,
                    onChatAppendPresetNameChange = viewModel::updateChatAppendPresetNameDraft,
                    onChatAppendPresetTextChange = viewModel::updateChatAppendPresetTextDraft,
                    onChatAppendPresetSave = viewModel::saveChatAppendPreset,
                    onChatAppendPresetCreate = viewModel::createChatAppendPreset,
                    onChatAppendPresetDelete = viewModel::deleteChatAppendPreset,
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
                    onPauseSpeaking = ::pauseSpeaking,
                    onResumeSpeaking = ::resumeSpeaking,
                    speakingMessageId = speakingMessageId,
                    ttsPaused = ttsPaused,
                    ttsPlaybackState = ttsPlaybackState,
                    onClearImage = viewModel::clearAttachments,
                    onRefreshModels = { viewModel.refreshModels(silent = false) },
                    onExportChats = { exportLauncher.launch("lm-studio-chat-history.json") },
                    onImportChats = { importLauncher.launch(arrayOf("application/json", "text/*", "*/*")) },
                    onDismissError = viewModel::dismissError,
                )
                val shareContent = pendingShareContent
                if (shareContent != null) {
                    ShareDestinationDialog(
                        state = state,
                        content = shareContent,
                        onDismiss = { pendingShareContent = null },
                        onDestinationSelected = { destination ->
                            viewModel.applyShareDestination(destination, shareContent)
                            pendingShareContent = null
                            shareContent.documentUri?.let { pendingDocumentUri = it }
                        },
                        onSetDefaultDestination = viewModel::setShareDefaultDestination,
                    )
                }
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

class WatchJobAlertActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
            )
        }
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
        )

        val title = intent.getStringExtra(ExtraTitle).orEmpty().ifBlank { "Watch job alert" }
        val detail = intent.getStringExtra(ExtraDetail).orEmpty()
        val jobId = intent.getStringExtra(ExtraJobId).orEmpty()

        setContent {
            LmStudioChatTheme {
                WatchJobAlarmScreen(
                    title = title,
                    detail = detail,
                    jobId = jobId,
                    onStop = {
                        stopWatchJobNotification(jobId)
                        finish()
                    },
                )
            }
        }
    }

    private fun stopWatchJobNotification(jobId: String) {
        if (jobId.isBlank()) return
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(jobId.hashCode())
    }

    companion object {
        const val ExtraTitle = "watch_job_alert_title"
        const val ExtraDetail = "watch_job_alert_detail"
        const val ExtraJobId = "watch_job_id"
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

enum class TtsChunkStatus {
    Pending,
    Preparing,
    Ready,
    Queued,
    Playing,
    Complete,
    Error,
}

data class TtsChunkProgress(
    val index: Int,
    val preview: String,
    val charCount: Int,
    val status: TtsChunkStatus = TtsChunkStatus.Pending,
) {
    val railWeight: Float = charCount.coerceAtLeast(1).toFloat()
}

data class TtsPlaybackState(
    val messageId: String,
    val runId: Long,
    val chunks: List<TtsChunkProgress>,
) {
    fun withChunkStatus(chunkId: TtsChunkId, status: TtsChunkStatus): TtsPlaybackState {
        if (messageId != chunkId.messageId || runId != chunkId.runId) return this
        return copy(
            chunks = chunks.map { chunk ->
                if (chunk.index == chunkId.index) chunk.copy(status = status) else chunk
            },
        )
    }

    fun withChunksFromStatus(chunkId: TtsChunkId, status: TtsChunkStatus): TtsPlaybackState {
        if (messageId != chunkId.messageId || runId != chunkId.runId) return this
        return copy(
            chunks = chunks.map { chunk ->
                if (chunk.index >= chunkId.index && chunk.status != TtsChunkStatus.Complete) {
                    chunk.copy(status = status)
                } else {
                    chunk
                }
            },
        )
    }

    fun isFinished(): Boolean =
        chunks.all { chunk ->
            chunk.status == TtsChunkStatus.Complete || chunk.status == TtsChunkStatus.Error
        }
}

data class TtsChunkId(
    val messageId: String,
    val runId: Long,
    val index: Int,
)

data class ChatImageAttachment(
    val dataUrl: String = "",
    val remoteUrl: String = "",
    val label: String = "Image",
    val mimeType: String = "image/jpeg",
)

private fun ChatImageAttachment.hasDisplayableImage(): Boolean =
    dataUrl.isNotBlank() || remoteUrl.isNotBlank()

private fun ChatImageAttachment.identityKey(): String =
    dataUrl.ifBlank { remoteUrl.ifBlank { label } }

data class ChatDocumentAttachment(
    val name: String,
    val text: String,
    val mimeType: String = "text/plain",
)

data class ChatSession(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "New chat",
    val messages: List<ChatMessage> = emptyList(),
    val folderId: String? = null,
    val isTemporary: Boolean = false,
    val previousResponseId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

data class ChatFolder(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
)

data class SystemPromptProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val prompt: String,
)

data class ChatAppendPreset(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val template: String,
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
    val chatFolders: List<ChatFolder> = emptyList(),
    val activeChatFolderId: String = AllChatsFolderId,
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
    val alarmToolEnabled: Boolean = false,
    val contactsToolEnabled: Boolean = false,
    val notificationDigestToolEnabled: Boolean = false,
    val localFileSearchToolEnabled: Boolean = false,
    val localFileSearchTreeUri: String = "",
    val deviceStatusToolEnabled: Boolean = false,
    val watchJobToolEnabled: Boolean = false,
    val watchJobs: List<WatchJob> = emptyList(),
    val voiceInputEnabled: Boolean = false,
    val voiceOutputEnabled: Boolean = false,
    val voiceOutputEngine: String = VoiceOutputEngineSystem,
    val voiceOutputLanguage: String = VoiceOutputLanguageEngineDefault,
    val voiceOutputVoiceName: String = "",
    val voiceOutputSpeed: Float = 1f,
    val voiceOutputQuality: Int = 5,
    val voiceOutputChunkSize: Int = VoiceOutputDefaultChunkChars,
    val voiceOutputStartBuffer: Int = VoiceOutputDefaultStartBufferChunks,
    val autoReadAnswersEnabled: Boolean = false,
    val appendCapabilityGuideToSystemPrompt: Boolean = true,
    val appendDateTimeToSystemPrompt: Boolean = false,
    val shareDefaultDestination: String = "",
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
    val chatAppendPresets: List<ChatAppendPreset> = emptyList(),
    val activeChatAppendPresetId: String = "",
    val chatAppendPresetNameDraft: String = ChatAppendPresetSampleName,
    val chatAppendPresetTextDraft: String = ChatAppendPresetSampleText,
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
    val isWatchJobsOpen: Boolean = false,
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

    private val initialFolders = settingsStore.loadChatFolders()
    private val initialSessions = settingsStore.loadChatSessions().ifEmpty { listOf(ChatSession()) }
    private val initialActiveSession = initialSessions.firstOrNull { it.id == settingsStore.activeSessionId }
        ?: initialSessions.first()
    private val initialActiveChatFolderId = settingsStore.activeChatFolderId
        .takeIf { folderId -> folderId.isKnownChatFolder(initialFolders) }
        ?: AllChatsFolderId
    private val initialProfiles = settingsStore.loadSystemProfiles()
    private val initialActiveProfile = initialProfiles.firstOrNull { it.id == settingsStore.activeSystemProfileId }
        ?: initialProfiles.first()
    private val initialChatAppendPresets = settingsStore.loadChatAppendPresets()
    private val initialActiveChatAppendPreset = initialChatAppendPresets
        .firstOrNull { it.id == settingsStore.activeChatAppendPresetId }
        ?: initialChatAppendPresets.firstOrNull()

    private val _uiState = MutableStateFlow(
        ChatUiState(
            sessions = initialSessions,
            activeSessionId = initialActiveSession.id,
            chatFolders = initialFolders,
            activeChatFolderId = initialActiveChatFolderId,
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
            alarmToolEnabled = settingsStore.alarmToolEnabled,
            contactsToolEnabled = settingsStore.contactsToolEnabled,
            notificationDigestToolEnabled = settingsStore.notificationDigestToolEnabled,
            localFileSearchToolEnabled = settingsStore.localFileSearchToolEnabled,
            localFileSearchTreeUri = settingsStore.localFileSearchTreeUri,
            deviceStatusToolEnabled = settingsStore.deviceStatusToolEnabled,
            watchJobToolEnabled = settingsStore.watchJobToolEnabled,
            watchJobs = WatchJobStore.load(settingsStore.context),
            voiceInputEnabled = settingsStore.voiceInputEnabled,
            voiceOutputEnabled = settingsStore.voiceOutputEnabled,
            voiceOutputEngine = settingsStore.voiceOutputEngine,
            voiceOutputLanguage = settingsStore.voiceOutputLanguage,
            voiceOutputVoiceName = settingsStore.voiceOutputVoiceName,
            voiceOutputSpeed = settingsStore.voiceOutputSpeed,
            voiceOutputQuality = settingsStore.voiceOutputQuality,
            voiceOutputChunkSize = settingsStore.voiceOutputChunkSize,
            voiceOutputStartBuffer = settingsStore.voiceOutputStartBuffer,
            autoReadAnswersEnabled = settingsStore.autoReadAnswersEnabled,
            appendCapabilityGuideToSystemPrompt = settingsStore.appendCapabilityGuideToSystemPrompt,
            appendDateTimeToSystemPrompt = settingsStore.appendDateTimeToSystemPrompt,
            shareDefaultDestination = settingsStore.shareDefaultDestination,
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
            chatAppendPresets = initialChatAppendPresets,
            activeChatAppendPresetId = initialActiveChatAppendPreset?.id.orEmpty(),
            chatAppendPresetNameDraft = initialActiveChatAppendPreset?.name ?: ChatAppendPresetSampleName,
            chatAppendPresetTextDraft = initialActiveChatAppendPreset?.template ?: ChatAppendPresetSampleText,
            reasoningEnabled = settingsStore.reasoningEnabled,
        ),
    )
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        settingsStore.activeSessionId = initialActiveSession.id
        settingsStore.activeChatFolderId = initialActiveChatFolderId
        settingsStore.saveChatSessions(initialSessions)
        settingsStore.saveChatFolders(initialFolders)
        settingsStore.activeSystemProfileId = initialActiveProfile.id
        settingsStore.saveSystemProfiles(initialProfiles)
        settingsStore.activeChatAppendPresetId = initialActiveChatAppendPreset?.id.orEmpty()
        settingsStore.saveChatAppendPresets(initialChatAppendPresets)
        WatchJobStore.load(settingsStore.context).forEach { WatchJobRunner.schedule(settingsStore.context, it) }
        refreshModels(silent = true)
    }

    fun updateInput(value: String) {
        _uiState.update { it.copy(input = value) }
    }

    fun applyChatAppendPreset(presetId: String) {
        _uiState.update { current ->
            val preset = current.chatAppendPresets.firstOrNull { it.id == presetId }
                ?: return@update current
            current.copy(input = preset.applyToChatText(current.input))
        }
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

    fun applySharedContent(content: PreparedShareContent) {
        if (content.text.isBlank() && content.imageAttachments.isEmpty()) return
        _uiState.update { current ->
            val mergedInput = listOf(current.input.trim(), content.text.trim())
                .filter { it.isNotBlank() }
                .joinToString("\n\n")
            current.copy(
                input = mergedInput,
                attachedImages = current.attachedImages + content.imageAttachments,
                error = null,
            )
        }
    }

    fun updateChatSearchQuery(value: String) {
        _uiState.update { it.copy(chatSearchQuery = value) }
    }

    fun selectChatFolder(folderId: String) {
        val state = _uiState.value
        val normalizedFolderId = folderId.takeIf { it.isKnownChatFolder(state.chatFolders) } ?: AllChatsFolderId
        settingsStore.activeChatFolderId = normalizedFolderId
        _uiState.update { it.copy(activeChatFolderId = normalizedFolderId) }
    }

    fun createChatFolder(name: String) {
        val cleanName = name.trim().replace(Regex("\\s+"), " ")
        if (cleanName.isBlank()) return
        val state = _uiState.value
        val existing = state.chatFolders.firstOrNull { it.name.equals(cleanName, ignoreCase = true) }
        val folders = if (existing != null) {
            state.chatFolders
        } else {
            (state.chatFolders + ChatFolder(name = cleanName)).sortedBy { it.name.lowercase(Locale.getDefault()) }
        }
        val selectedFolderId = existing?.id ?: folders.firstOrNull { it.name == cleanName }?.id ?: AllChatsFolderId
        settingsStore.saveChatFolders(folders)
        settingsStore.activeChatFolderId = selectedFolderId
        _uiState.update {
            it.copy(
                chatFolders = folders,
                activeChatFolderId = selectedFolderId,
                error = null,
            )
        }
    }

    fun moveChatToFolder(sessionId: String, folderId: String?) {
        val state = _uiState.value
        val targetFolderId = folderId?.takeIf { id -> state.chatFolders.any { it.id == id } }
        val session = state.sessions.firstOrNull { it.id == sessionId } ?: return
        if (session.folderId == targetFolderId) return
        val updatedSession = session.copy(
            folderId = targetFolderId,
            updatedAt = System.currentTimeMillis(),
        )
        val sessions = state.sessions.replaceSession(updatedSession)
        val activeFolderId = if (
            sessionId == state.activeSessionId &&
            !updatedSession.matchesChatFolder(state.activeChatFolderId)
        ) {
            targetFolderId ?: UnfiledChatsFolderId
        } else {
            state.activeChatFolderId
        }
        settingsStore.activeChatFolderId = activeFolderId
        settingsStore.saveChatSessions(sessions)
        _uiState.update {
            it.copy(
                sessions = sessions,
                activeChatFolderId = activeFolderId,
                error = null,
            )
        }
    }

    fun deleteChatFolder(folderId: String) {
        if (folderId == AllChatsFolderId || folderId == UnfiledChatsFolderId) return
        val state = _uiState.value
        if (state.chatFolders.none { it.id == folderId }) return
        val folders = state.chatFolders.filterNot { it.id == folderId }
        val sessions = state.sessions.map { session ->
            if (session.folderId == folderId) {
                session.copy(folderId = null, updatedAt = System.currentTimeMillis())
            } else {
                session
            }
        }
        val activeFolderId = if (state.activeChatFolderId == folderId) {
            AllChatsFolderId
        } else {
            state.activeChatFolderId
        }
        settingsStore.saveChatFolders(folders)
        settingsStore.saveChatSessions(sessions)
        settingsStore.activeChatFolderId = activeFolderId
        _uiState.update {
            it.copy(
                chatFolders = folders,
                sessions = sessions,
                activeChatFolderId = activeFolderId,
                error = null,
            )
        }
    }

    fun selectChat(sessionId: String) {
        val session = _uiState.value.sessions.firstOrNull { it.id == sessionId } ?: return
        if (!session.isTemporary) {
            settingsStore.activeSessionId = session.id
        }
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
                val streamImageAttachments = mutableListOf<ChatImageAttachment>()

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
                    attachments: List<ChatImageAttachment> = answer?.attachments.orEmpty(),
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
                                                attachments = attachments,
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
                            event.imageAttachments.forEach { attachment ->
                                if (attachment.hasDisplayableImage() && streamImageAttachments.none { it.identityKey() == attachment.identityKey() }) {
                                    streamImageAttachments += attachment
                                }
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
                    updateAssistant(
                        content = renderStreamingContent(),
                        isStreaming = true,
                        attachments = streamImageAttachments,
                    )
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
                            attachments = (answer.attachments + streamImageAttachments)
                                .distinctBy { it.identityKey() },
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
                                        attachments = answer.attachments,
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
        createChat(temporary = false)
    }

    fun temporaryChat() {
        createChat(temporary = true)
    }

    private fun createChat(
        temporary: Boolean,
        sharedContent: PreparedShareContent? = null,
    ) {
        val state = _uiState.value
        val folderId = if (temporary) null else state.activeChatFolderId.takeIf { activeFolderId ->
            state.chatFolders.any { it.id == activeFolderId }
        }
        val title = when {
            temporary -> "Temporary chat"
            !sharedContent?.text.isNullOrBlank() -> sharedContent?.text.orEmpty().toChatTitle()
            sharedContent?.imageAttachments?.isNotEmpty() == true -> "Shared image"
            else -> "New chat"
        }
        val newSession = ChatSession(
            title = title,
            folderId = folderId,
            isTemporary = temporary,
        )
        val sessions = (listOf(newSession) + state.sessions)
            .distinctBy { it.id }
        if (!temporary) {
            settingsStore.activeSessionId = newSession.id
        }
        settingsStore.saveChatSessions(sessions)
        _uiState.update {
            it.copy(
                sessions = sessions,
                activeSessionId = newSession.id,
                messages = emptyList(),
                input = sharedContent?.text?.trim().orEmpty(),
                attachedImages = sharedContent?.imageAttachments.orEmpty(),
                error = null,
                previousResponseId = null,
            )
        }
    }

    fun newChatWithShare(content: PreparedShareContent) {
        createChat(temporary = false, sharedContent = content)
    }

    fun temporaryChatWithShare(content: PreparedShareContent) {
        createChat(temporary = true, sharedContent = content)
    }

    fun applyShareToExistingChat(sessionId: String, content: PreparedShareContent) {
        selectChat(sessionId)
        applySharedContent(content)
    }

    fun applyShareDestination(destination: ShareDestinationSelection, content: PreparedShareContent): Boolean =
        when (destination.kind) {
            ShareDestinationKind.New -> {
                newChatWithShare(content)
                true
            }
            ShareDestinationKind.Temporary -> {
                temporaryChatWithShare(content)
                true
            }
            ShareDestinationKind.Existing -> {
                val sessionId = destination.sessionId
                if (sessionId == null || _uiState.value.sessions.none { it.id == sessionId && !it.isTemporary }) {
                    false
                } else {
                    applyShareToExistingChat(sessionId, content)
                    true
                }
            }
        }

    fun applyDefaultShareDestination(content: PreparedShareContent): Boolean {
        val storedDestination = settingsStore.shareDefaultDestination
        val destination = storedDestination.toShareDestinationSelection(_uiState.value.sessions)
        if (destination == null) {
            if (storedDestination.isNotBlank()) {
                settingsStore.shareDefaultDestination = ""
                _uiState.update { it.copy(shareDefaultDestination = "") }
            }
            return false
        }
        return applyShareDestination(destination, content)
    }

    fun setShareDefaultDestination(destination: ShareDestinationSelection) {
        val storedDestination = destination.toStoredShareDefault()
        settingsStore.shareDefaultDestination = storedDestination
        _uiState.update { it.copy(shareDefaultDestination = storedDestination, error = null) }
    }

    fun updateShareDefaultSelectionEnabled(enabled: Boolean) {
        if (enabled) {
            _uiState.update {
                it.copy(error = "Choose Set default selection from the share popup first.")
            }
        } else {
            settingsStore.shareDefaultDestination = ""
            _uiState.update { it.copy(shareDefaultDestination = "", error = null) }
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

    fun updateAlarmToolEnabled(value: Boolean) {
        settingsStore.alarmToolEnabled = value
        _uiState.update { it.copy(alarmToolEnabled = value, previousResponseId = null) }
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

    fun updateWatchJobToolEnabled(value: Boolean) {
        settingsStore.watchJobToolEnabled = value
        _uiState.update { it.copy(watchJobToolEnabled = value, previousResponseId = null) }
    }

    fun openWatchJobs() {
        _uiState.update {
            it.copy(
                watchJobs = WatchJobStore.load(settingsStore.context),
                isWatchJobsOpen = true,
            )
        }
    }

    fun closeWatchJobs() {
        _uiState.update { it.copy(isWatchJobsOpen = false) }
    }

    fun createWatchJob(
        title: String,
        trigger: String,
        appQuery: String,
        senderQuery: String,
        textQuery: String,
        matchMode: String,
        aiInstruction: String,
        scheduleMinutesText: String,
        alertMessage: String,
        alertMode: String,
        lifetime: String,
    ) {
        val normalizedTitle = title.trim().ifBlank { "Watch job" }
        val normalizedTrigger = trigger.trim().lowercase(Locale.getDefault()).let {
            if (it == "schedule") "schedule" else "notification"
        }
        val normalizedAppQuery = appQuery.trim()
        val normalizedSenderQuery = senderQuery.trim()
        val normalizedTextQuery = textQuery.trim()
        val normalizedMatchMode = matchMode.normalizedWatchMatchMode()
        val normalizedAiInstruction = aiInstruction.trim()
        val effectiveTrigger = if (normalizedMatchMode == "prompt") "schedule" else normalizedTrigger
        if ((normalizedMatchMode == "ai" || normalizedMatchMode == "prompt") && normalizedAiInstruction.isBlank()) {
            _uiState.update { it.copy(error = if (normalizedMatchMode == "prompt") "Add a scheduled prompt." else "Add AI instructions for AI mode.") }
            return
        }
        if (normalizedMatchMode == "filter" && listOf(normalizedAppQuery, normalizedSenderQuery, normalizedTextQuery).all { it.isBlank() }) {
            _uiState.update { it.copy(error = "Add at least one Watch Job filter.") }
            return
        }
        val scheduleMinutes = scheduleMinutesText.toIntOrNull()?.coerceIn(1, 1440) ?: 15
        val job = WatchJob(
            title = normalizedTitle,
            trigger = effectiveTrigger,
            appQuery = if (normalizedMatchMode == "prompt") "" else normalizedAppQuery,
            senderQuery = if (normalizedMatchMode == "prompt") "" else normalizedSenderQuery,
            textQuery = if (normalizedMatchMode == "prompt") "" else normalizedTextQuery,
            matchMode = normalizedMatchMode,
            aiInstruction = normalizedAiInstruction,
            scheduleMinutes = if (effectiveTrigger == "schedule") scheduleMinutes else 0,
            alertMessage = alertMessage.trim(),
            alertMode = alertMode.normalizedWatchAlertMode(),
            lifetime = lifetime.normalizedWatchLifetime(),
        )
        addWatchJob(job)
    }

    fun createWatchJobFromArgs(args: JSONObject): WatchJob {
        val trigger = args.arg("trigger", "mode")
            .lowercase(Locale.getDefault())
            .let { if (it == "schedule" || it == "scheduled") "schedule" else "notification" }
        val scheduleMinutes = args.optInt("schedule_minutes", args.optInt("interval_minutes", 15))
            .coerceIn(1, 1440)
        val appQuery = args.arg("app", "package", "app_query")
        val senderQuery = args.arg("sender", "from", "sender_query")
        val textQuery = args.arg("text", "contains", "query", "text_query")
        val promptTask = args.arg("prompt", "schedule_prompt", "scheduled_prompt", "task_prompt")
        val rawMatchMode = args.arg("match_mode", "matchMode", "mode_type")
        val matchMode = if (rawMatchMode.isBlank() && trigger == "schedule" && promptTask.isNotBlank()) {
            "prompt"
        } else {
            rawMatchMode.normalizedWatchMatchMode()
        }
        val effectiveTrigger = if (matchMode == "prompt") "schedule" else trigger
        val aiInstruction = args.arg("ai_instruction", "instruction", "instructions", "task", "prompt", "schedule_prompt", "scheduled_prompt")
        if ((matchMode == "ai" || matchMode == "prompt") && aiInstruction.isBlank()) {
            throw IOException(if (matchMode == "prompt") "Scheduled prompt Watch Job needs prompt." else "AI Watch Job needs ai_instruction.")
        }
        if (matchMode == "filter" && listOf(appQuery, senderQuery, textQuery).all { it.isBlank() }) {
            throw IOException("Watch Job needs at least one app, sender, or text filter.")
        }
        val job = WatchJob(
            title = args.arg("title", "name").ifBlank { "Watch job" },
            trigger = effectiveTrigger,
            appQuery = if (matchMode == "prompt") "" else appQuery,
            senderQuery = if (matchMode == "prompt") "" else senderQuery,
            textQuery = if (matchMode == "prompt") "" else textQuery,
            matchMode = matchMode,
            aiInstruction = aiInstruction,
            scheduleMinutes = if (effectiveTrigger == "schedule") scheduleMinutes else 0,
            alertMessage = args.arg("alert", "alert_message", "message"),
            alertMode = args.arg("alert_mode", "alertMode", "mode").normalizedWatchAlertMode(),
            lifetime = args.arg("lifetime", "life_time", "expires").normalizedWatchLifetime(),
        )
        addWatchJob(job)
        return job
    }

    fun updateWatchJob(
        jobId: String,
        title: String,
        trigger: String,
        appQuery: String,
        senderQuery: String,
        textQuery: String,
        matchMode: String,
        aiInstruction: String,
        scheduleMinutesText: String,
        alertMessage: String,
        alertMode: String,
        lifetime: String,
        enabled: Boolean,
    ) {
        val existing = WatchJobStore.load(settingsStore.context).firstOrNull { it.id == jobId } ?: return
        val normalizedTitle = title.trim().ifBlank { existing.title }
        val normalizedTrigger = trigger.trim().lowercase(Locale.getDefault()).let {
            if (it == "schedule") "schedule" else "notification"
        }
        val normalizedAppQuery = appQuery.trim()
        val normalizedSenderQuery = senderQuery.trim()
        val normalizedTextQuery = textQuery.trim()
        val normalizedMatchMode = matchMode.normalizedWatchMatchMode()
        val normalizedAiInstruction = aiInstruction.trim()
        val effectiveTrigger = if (normalizedMatchMode == "prompt") "schedule" else normalizedTrigger
        if ((normalizedMatchMode == "ai" || normalizedMatchMode == "prompt") && normalizedAiInstruction.isBlank()) {
            _uiState.update { it.copy(error = if (normalizedMatchMode == "prompt") "Add a scheduled prompt." else "Add AI instructions for AI mode.") }
            return
        }
        if (normalizedMatchMode == "filter" && listOf(normalizedAppQuery, normalizedSenderQuery, normalizedTextQuery).all { it.isBlank() }) {
            _uiState.update { it.copy(error = "Add at least one Watch Job filter.") }
            return
        }
        val scheduleMinutes = scheduleMinutesText.toIntOrNull()?.coerceIn(1, 1440) ?: existing.scheduleMinutes.coerceAtLeast(15)
        val updated = existing.copy(
            title = normalizedTitle,
            trigger = effectiveTrigger,
            appQuery = if (normalizedMatchMode == "prompt") "" else normalizedAppQuery,
            senderQuery = if (normalizedMatchMode == "prompt") "" else normalizedSenderQuery,
            textQuery = if (normalizedMatchMode == "prompt") "" else normalizedTextQuery,
            matchMode = normalizedMatchMode,
            aiInstruction = normalizedAiInstruction,
            scheduleMinutes = if (effectiveTrigger == "schedule") scheduleMinutes else 0,
            alertMessage = alertMessage.trim(),
            alertMode = alertMode.normalizedWatchAlertMode(),
            lifetime = lifetime.normalizedWatchLifetime(),
            enabled = enabled,
        )
        replaceWatchJob(updated)
    }

    fun listWatchJobsForTool(): String {
        val jobs = WatchJobStore.load(settingsStore.context)
        if (jobs.isEmpty()) return "No watch jobs are configured."
        return buildString {
            appendLine("Watch jobs:")
            jobs.forEachIndexed { index, job ->
                appendLine("${index + 1}. ${job.title}")
                appendLine("   id: ${job.id}")
                appendLine("   enabled: ${job.enabled}")
                appendLine("   trigger: ${job.trigger}${if (job.trigger == "schedule") " every ${job.scheduleMinutes} min" else ""}")
                appendLine("   match mode: ${job.matchMode.watchMatchModeLabel()}")
                appendLine("   filters: ${job.watchJobFiltersText()}")
                if (job.matchMode.normalizedWatchMatchMode() == "ai") {
                    appendLine("   ai instruction: ${job.aiInstruction}")
                } else if (job.matchMode.normalizedWatchMatchMode() == "prompt") {
                    appendLine("   scheduled prompt: ${job.aiInstruction}")
                }
                appendLine("   alert: ${job.alertMode.watchAlertModeLabel()}, lifetime: ${job.lifetime.watchLifetimeLabel()}")
                appendLine("   matches: ${job.matchCount}, last run: ${job.lastRunAt.toWatchJobTime()}, last checked: ${job.lastCheckedAt.toWatchJobTime()}, last match: ${job.lastMatchedAt.toWatchJobTime()}")
            }
        }.trim()
    }

    fun editWatchJobFromArgs(args: JSONObject): WatchJob {
        val existing = findWatchJobFromArgs(args)
        val newTitle = args.optionalArg("new_title", "new_name")
            ?: if (args.arg("id", "job_id", "watch_job_id").isNotBlank()) {
                args.optionalArg("title", "name")
            } else {
                null
            }
        val updated = existing.copy(
            title = newTitle?.trim()?.ifBlank { existing.title } ?: existing.title,
            trigger = (args.optionalArg("trigger", "mode") ?: existing.trigger)
                .lowercase(Locale.getDefault())
                .let { if (it == "schedule" || it == "scheduled") "schedule" else "notification" },
            appQuery = args.optionalArg("app", "package", "app_query")?.trim() ?: existing.appQuery,
            senderQuery = args.optionalArg("sender", "from", "sender_query")?.trim() ?: existing.senderQuery,
            textQuery = args.optionalArg("text", "contains", "query", "text_query")?.trim() ?: existing.textQuery,
            matchMode = (
                args.optionalArg("match_mode", "matchMode")
                    ?: if (args.optionalArg("prompt", "schedule_prompt", "scheduled_prompt") != null) "prompt" else existing.matchMode
                ).normalizedWatchMatchMode(),
            aiInstruction = args.optionalArg("ai_instruction", "instruction", "instructions", "task", "prompt", "schedule_prompt", "scheduled_prompt")?.trim() ?: existing.aiInstruction,
            scheduleMinutes = args.optInt("schedule_minutes", args.optInt("interval_minutes", existing.scheduleMinutes.coerceAtLeast(15))).coerceIn(1, 1440),
            alertMessage = args.optionalArg("alert", "alert_message", "message")?.trim() ?: existing.alertMessage,
            alertMode = (args.optionalArg("alert_mode", "alertMode") ?: existing.alertMode).normalizedWatchAlertMode(),
            lifetime = (args.optionalArg("lifetime", "life_time", "expires") ?: existing.lifetime).normalizedWatchLifetime(),
            enabled = if (args.has("enabled")) args.optBoolean("enabled", existing.enabled) else existing.enabled,
        ).let { job ->
            when {
                job.matchMode.normalizedWatchMatchMode() == "prompt" -> job.copy(
                    trigger = "schedule",
                    appQuery = "",
                    senderQuery = "",
                    textQuery = "",
                )
                job.trigger == "schedule" -> job
                else -> job.copy(scheduleMinutes = 0)
            }
        }
        if ((updated.matchMode == "ai" || updated.matchMode == "prompt") && updated.aiInstruction.isBlank()) {
            throw IOException(if (updated.matchMode == "prompt") "Scheduled prompt Watch Job needs prompt." else "AI Watch Job needs ai_instruction.")
        }
        if (updated.matchMode == "filter" && listOf(updated.appQuery, updated.senderQuery, updated.textQuery).all { it.isBlank() }) {
            throw IOException("Watch Job needs at least one app, sender, or text filter.")
        }
        replaceWatchJob(updated)
        return updated
    }

    fun deleteWatchJobFromArgs(args: JSONObject): WatchJob {
        val job = findWatchJobFromArgs(args)
        deleteWatchJob(job.id)
        return job
    }

    fun setWatchJobEnabledFromArgs(args: JSONObject): WatchJob {
        val job = findWatchJobFromArgs(args)
        val enabled = when {
            args.has("enabled") -> args.optBoolean("enabled", job.enabled)
            args.optString("state").equals("enabled", ignoreCase = true) -> true
            args.optString("state").equals("disabled", ignoreCase = true) -> false
            else -> throw IOException("watch_job_set_enabled needs enabled=true or enabled=false.")
        }
        val updated = job.copy(enabled = enabled)
        replaceWatchJob(updated)
        return updated
    }

    private fun addWatchJob(job: WatchJob) {
        val jobs = WatchJobStore.load(settingsStore.context) + job
        saveWatchJobs(jobs)
        WatchJobRunner.schedule(settingsStore.context, job)
    }

    private fun replaceWatchJob(job: WatchJob) {
        WatchJobRunner.cancel(settingsStore.context, job.id)
        val jobs = WatchJobStore.load(settingsStore.context).map { existing ->
            if (existing.id == job.id) job else existing
        }
        saveWatchJobs(jobs)
        WatchJobRunner.schedule(settingsStore.context, job)
    }

    private fun findWatchJobFromArgs(args: JSONObject): WatchJob {
        val id = args.arg("id", "job_id", "watch_job_id")
        val title = args.arg("title", "name")
        val jobs = WatchJobStore.load(settingsStore.context)
        return jobs.firstOrNull { id.isNotBlank() && it.id == id }
            ?: jobs.firstOrNull { title.isNotBlank() && it.title.equals(title, ignoreCase = true) }
            ?: throw IOException("Watch job not found. Use watch_job_list first to get the id.")
    }

    fun toggleWatchJob(jobId: String, enabled: Boolean) {
        val jobs = WatchJobStore.load(settingsStore.context).map { job ->
            if (job.id == jobId) job.copy(enabled = enabled) else job
        }
        saveWatchJobs(jobs)
        jobs.firstOrNull { it.id == jobId }?.let { job ->
            if (enabled) {
                WatchJobRunner.schedule(settingsStore.context, job)
            } else {
                WatchJobRunner.cancel(settingsStore.context, job.id)
            }
        }
    }

    fun deleteWatchJob(jobId: String) {
        val jobs = WatchJobStore.load(settingsStore.context).filterNot { it.id == jobId }
        WatchJobRunner.cancel(settingsStore.context, jobId)
        saveWatchJobs(jobs)
    }

    private fun saveWatchJobs(jobs: List<WatchJob>) {
        WatchJobStore.save(settingsStore.context, jobs)
        _uiState.update { it.copy(watchJobs = jobs.sortedByDescending { job -> job.createdAt }) }
    }

    fun updateVoiceInputEnabled(value: Boolean) {
        settingsStore.voiceInputEnabled = value
        _uiState.update { it.copy(voiceInputEnabled = value) }
    }

    fun updateVoiceOutputEnabled(value: Boolean) {
        settingsStore.voiceOutputEnabled = value
        _uiState.update { it.copy(voiceOutputEnabled = value) }
    }

    fun updateVoiceOutputEngine(value: String) {
        val normalized = value.normalizedVoiceOutputEngine()
        settingsStore.voiceOutputEngine = normalized
        _uiState.update { it.copy(voiceOutputEngine = normalized) }
    }

    fun updateVoiceOutputLanguage(value: String) {
        val normalized = value.normalizedVoiceOutputLanguage()
        settingsStore.voiceOutputLanguage = normalized
        _uiState.update { it.copy(voiceOutputLanguage = normalized) }
    }

    fun updateVoiceOutputVoiceName(value: String) {
        val normalized = value.trim()
        settingsStore.voiceOutputVoiceName = normalized
        _uiState.update { it.copy(voiceOutputVoiceName = normalized) }
    }

    fun updateVoiceOutputSpeed(value: Float) {
        val normalized = value.normalizedVoiceOutputSpeed()
        settingsStore.voiceOutputSpeed = normalized
        _uiState.update { it.copy(voiceOutputSpeed = normalized) }
    }

    fun updateVoiceOutputQuality(value: Int) {
        val normalized = value.normalizedVoiceOutputQuality()
        settingsStore.voiceOutputQuality = normalized
        _uiState.update { it.copy(voiceOutputQuality = normalized) }
    }

    fun updateVoiceOutputChunkSize(value: Int) {
        val normalized = value.normalizedVoiceOutputChunkSize()
        settingsStore.voiceOutputChunkSize = normalized
        _uiState.update { it.copy(voiceOutputChunkSize = normalized) }
    }

    fun updateVoiceOutputStartBuffer(value: Int) {
        val normalized = value.normalizedVoiceOutputStartBuffer()
        settingsStore.voiceOutputStartBuffer = normalized
        _uiState.update { it.copy(voiceOutputStartBuffer = normalized) }
    }

    fun updateAutoReadAnswersEnabled(value: Boolean) {
        settingsStore.autoReadAnswersEnabled = value
        _uiState.update { it.copy(autoReadAnswersEnabled = value) }
    }

    fun updateAppendCapabilityGuideToSystemPrompt(value: Boolean) {
        settingsStore.appendCapabilityGuideToSystemPrompt = value
        _uiState.update { it.copy(appendCapabilityGuideToSystemPrompt = value, previousResponseId = null) }
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

    fun selectChatAppendPreset(presetId: String) {
        val preset = _uiState.value.chatAppendPresets.firstOrNull { it.id == presetId } ?: return
        settingsStore.activeChatAppendPresetId = preset.id
        _uiState.update {
            it.copy(
                activeChatAppendPresetId = preset.id,
                chatAppendPresetNameDraft = preset.name,
                chatAppendPresetTextDraft = preset.template,
            )
        }
    }

    fun updateChatAppendPresetNameDraft(value: String) {
        _uiState.update { it.copy(chatAppendPresetNameDraft = value) }
    }

    fun updateChatAppendPresetTextDraft(value: String) {
        _uiState.update { it.copy(chatAppendPresetTextDraft = value) }
    }

    fun saveChatAppendPreset() {
        val state = _uiState.value
        val name = state.chatAppendPresetNameDraft.trim().ifBlank { ChatAppendPresetSampleName }
        val template = state.chatAppendPresetTextDraft.trim().ifBlank { ChatAppendPresetSampleText }
        val activeId = state.activeChatAppendPresetId.ifBlank { UUID.randomUUID().toString() }
        val preset = ChatAppendPreset(
            id = activeId,
            name = name,
            template = template,
        )
        val presets = state.chatAppendPresets.map {
            if (it.id == activeId) preset else it
        }.let { updated ->
            if (updated.any { it.id == activeId }) updated else updated + preset
        }
        settingsStore.activeChatAppendPresetId = preset.id
        settingsStore.saveChatAppendPresets(presets)
        _uiState.update {
            it.copy(
                chatAppendPresets = presets,
                activeChatAppendPresetId = preset.id,
                chatAppendPresetNameDraft = preset.name,
                chatAppendPresetTextDraft = preset.template,
            )
        }
    }

    fun createChatAppendPreset() {
        val preset = ChatAppendPreset(
            name = ChatAppendPresetSampleName,
            template = ChatAppendPresetSampleText,
        )
        val presets = _uiState.value.chatAppendPresets + preset
        settingsStore.activeChatAppendPresetId = preset.id
        settingsStore.saveChatAppendPresets(presets)
        _uiState.update {
            it.copy(
                chatAppendPresets = presets,
                activeChatAppendPresetId = preset.id,
                chatAppendPresetNameDraft = preset.name,
                chatAppendPresetTextDraft = preset.template,
            )
        }
    }

    fun deleteChatAppendPreset(presetId: String) {
        val state = _uiState.value
        val presets = state.chatAppendPresets.filterNot { it.id == presetId }
        if (presets.size == state.chatAppendPresets.size) return
        if (presets.isEmpty()) {
            settingsStore.activeChatAppendPresetId = ""
            settingsStore.saveChatAppendPresets(emptyList())
            _uiState.update {
                it.copy(
                    chatAppendPresets = emptyList(),
                    activeChatAppendPresetId = "",
                    chatAppendPresetNameDraft = ChatAppendPresetSampleName,
                    chatAppendPresetTextDraft = ChatAppendPresetSampleText,
                )
            }
            return
        }
        val activePreset = if (state.activeChatAppendPresetId == presetId) {
            presets.first()
        } else {
            presets.firstOrNull { it.id == state.activeChatAppendPresetId } ?: presets.first()
        }
        settingsStore.activeChatAppendPresetId = activePreset.id
        settingsStore.saveChatAppendPresets(presets)
        _uiState.update {
            it.copy(
                chatAppendPresets = presets,
                activeChatAppendPresetId = activePreset.id,
                chatAppendPresetNameDraft = activePreset.name,
                chatAppendPresetTextDraft = activePreset.template,
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
        SettingsStore.chatSessionsToJson(
            sessions = _uiState.value.sessions,
            folders = _uiState.value.chatFolders,
        )

    fun importChatHistory(json: String) {
        val imported = runCatching {
            val importedFolders = SettingsStore.chatFoldersFromJson(json)
            val folderIds = importedFolders.map { it.id }.toSet()
            val importedSessions = SettingsStore.chatSessionsFromJson(json)
                .map { session ->
                    if (session.folderId != null && session.folderId !in folderIds) {
                        session.copy(folderId = null)
                    } else {
                        session
                    }
                }
                .ifEmpty { listOf(ChatSession()) }
            importedFolders to importedSessions
        }.getOrElse { throwable ->
            _uiState.update { it.copy(error = "Could not import chat history: ${throwable.message}") }
            return
        }
        val (importedFolders, importedSessions) = imported
        val activeSession = importedSessions.first()
        settingsStore.activeSessionId = activeSession.id
        settingsStore.activeChatFolderId = AllChatsFolderId
        settingsStore.saveChatFolders(importedFolders)
        settingsStore.saveChatSessions(importedSessions)
        _uiState.update {
            it.copy(
                sessions = importedSessions,
                chatFolders = importedFolders,
                activeChatFolderId = AllChatsFolderId,
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
    val context: Context = context.applicationContext
    private val preferences = this.context.getSharedPreferences("lm_studio_chat", Context.MODE_PRIVATE)

    var activeSessionId: String
        get() = preferences.getString("active_session_id", "") ?: ""
        set(value) {
            preferences.edit().putString("active_session_id", value).apply()
        }

    var activeChatFolderId: String
        get() = preferences.getString("active_chat_folder_id", AllChatsFolderId) ?: AllChatsFolderId
        set(value) {
            preferences.edit().putString("active_chat_folder_id", value).apply()
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

    var alarmToolEnabled: Boolean
        get() = preferences.getBoolean("alarm_tool_enabled", false)
        set(value) {
            preferences.edit().putBoolean("alarm_tool_enabled", value).apply()
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

    var watchJobToolEnabled: Boolean
        get() = preferences.getBoolean("watch_job_tool_enabled", false)
        set(value) {
            preferences.edit().putBoolean("watch_job_tool_enabled", value).apply()
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

    var voiceOutputEngine: String
        get() = preferences.getString("voice_output_engine", VoiceOutputEngineSystem)
            ?.normalizedVoiceOutputEngine()
            ?: VoiceOutputEngineSystem
        set(value) {
            preferences.edit().putString("voice_output_engine", value.normalizedVoiceOutputEngine()).apply()
        }

    var voiceOutputLanguage: String
        get() = preferences.getString("voice_output_language", VoiceOutputLanguageEngineDefault)
            ?.normalizedVoiceOutputLanguage()
            ?: VoiceOutputLanguageEngineDefault
        set(value) {
            preferences.edit().putString("voice_output_language", value.normalizedVoiceOutputLanguage()).apply()
        }

    var voiceOutputVoiceName: String
        get() = preferences.getString("voice_output_voice_name", "").orEmpty()
        set(value) {
            preferences.edit().putString("voice_output_voice_name", value.trim()).apply()
        }

    var voiceOutputSpeed: Float
        get() = preferences.getFloat("voice_output_speed", 1f).normalizedVoiceOutputSpeed()
        set(value) {
            preferences.edit().putFloat("voice_output_speed", value.normalizedVoiceOutputSpeed()).apply()
        }

    var voiceOutputQuality: Int
        get() = preferences.getInt("voice_output_quality", 5).normalizedVoiceOutputQuality()
        set(value) {
            preferences.edit().putInt("voice_output_quality", value.normalizedVoiceOutputQuality()).apply()
        }

    var voiceOutputChunkSize: Int
        get() = preferences.getInt(
            "voice_output_chunk_size",
            VoiceOutputDefaultChunkChars,
        ).normalizedVoiceOutputChunkSize()
        set(value) {
            preferences.edit().putInt(
                "voice_output_chunk_size",
                value.normalizedVoiceOutputChunkSize(),
            ).apply()
        }

    var voiceOutputStartBuffer: Int
        get() = preferences.getInt(
            "voice_output_start_buffer",
            VoiceOutputDefaultStartBufferChunks,
        ).normalizedVoiceOutputStartBuffer()
        set(value) {
            preferences.edit().putInt(
                "voice_output_start_buffer",
                value.normalizedVoiceOutputStartBuffer(),
            ).apply()
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

    var appendCapabilityGuideToSystemPrompt: Boolean
        get() = preferences.getBoolean("append_capability_guide_to_system_prompt", true)
        set(value) {
            preferences.edit().putBoolean("append_capability_guide_to_system_prompt", value).apply()
        }

    var shareDefaultDestination: String
        get() = preferences.getString("share_default_destination", "") ?: ""
        set(value) {
            preferences.edit().putString("share_default_destination", value).apply()
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

    var activeChatAppendPresetId: String
        get() = preferences.getString("active_chat_append_preset_id", "").orEmpty()
        set(value) {
            preferences.edit().putString("active_chat_append_preset_id", value).apply()
        }

    fun loadChatSessions(): List<ChatSession> =
        runCatching {
            chatSessionsFromJson(preferences.getString("chat_history", "").orEmpty())
                .filterNot { it.isTemporary }
        }.getOrDefault(emptyList())

    fun saveChatSessions(sessions: List<ChatSession>) {
        preferences.edit()
            .putString("chat_history", chatSessionsToJson(sessions.filterNot { it.isTemporary }))
            .apply()
    }

    fun loadChatFolders(): List<ChatFolder> =
        runCatching {
            chatFoldersFromJson(preferences.getString("chat_folders", "").orEmpty())
        }.getOrDefault(emptyList())

    fun saveChatFolders(folders: List<ChatFolder>) {
        preferences.edit()
            .putString("chat_folders", chatFoldersToJson(folders))
            .apply()
    }

    fun loadSystemProfiles(): List<SystemPromptProfile> {
        val profiles = runCatching {
            systemProfilesFromJson(preferences.getString("system_profiles", "").orEmpty())
        }.getOrDefault(defaultSystemProfiles())
        val migratedProfiles = migrateDefaultSystemProfiles(profiles)
        if (migratedProfiles != profiles) {
            saveSystemProfiles(migratedProfiles)
        }
        return migratedProfiles
    }

    fun saveSystemProfiles(profiles: List<SystemPromptProfile>) {
        preferences.edit()
            .putString("system_profiles", systemProfilesToJson(profiles.ifEmpty { defaultSystemProfiles() }))
            .apply()
    }

    fun loadChatAppendPresets(): List<ChatAppendPreset> =
        runCatching {
            chatAppendPresetsFromJson(preferences.getString("chat_append_presets", "").orEmpty())
        }.getOrDefault(defaultChatAppendPresets())

    fun saveChatAppendPresets(presets: List<ChatAppendPreset>) {
        preferences.edit()
            .putString("chat_append_presets", chatAppendPresetsToJson(presets))
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

        fun defaultChatAppendPresets(): List<ChatAppendPreset> = emptyList()

        fun chatAppendPresetsToJson(presets: List<ChatAppendPreset>): String {
            val presetsJson = JSONArray()
            presets.forEach { preset ->
                presetsJson.put(
                    JSONObject()
                        .put("id", preset.id)
                        .put("name", preset.name)
                        .put("template", preset.template),
                )
            }
            return JSONObject()
                .put("version", 1)
                .put("presets", presetsJson)
                .toString()
        }

        fun chatAppendPresetsFromJson(json: String): List<ChatAppendPreset> {
            if (json.isBlank()) return defaultChatAppendPresets()
            val presetsJson = JSONObject(json).optJSONArray("presets") ?: JSONArray()
            val presets = buildList {
                for (index in 0 until presetsJson.length()) {
                    val presetJson = presetsJson.optJSONObject(index) ?: continue
                    add(
                        ChatAppendPreset(
                            id = presetJson.optString("id").ifBlank { UUID.randomUUID().toString() },
                            name = presetJson.optString("name").ifBlank { "Preset ${index + 1}" },
                            template = presetJson.optString("template")
                                .ifBlank { presetJson.optString("text") }
                                .ifBlank { ChatAppendPresetSampleText },
                        ),
                    )
                }
            }
            return presets
                .filterNot { it.isLegacyTranslateLtPreset() }
        }

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

        fun migrateDefaultSystemProfiles(profiles: List<SystemPromptProfile>): List<SystemPromptProfile> =
            profiles.map { profile ->
                if (
                    profile.id == DefaultSystemProfileId &&
                    (
                        profile.prompt.trim() == LegacyDefaultSystemPrompt ||
                            profile.prompt.looksLikePreviousBundledDefaultSystemPrompt()
                        )
                ) {
                    profile.copy(prompt = DefaultSystemPrompt)
                } else {
                    profile
                }
            }

        private fun String.looksLikePreviousBundledDefaultSystemPrompt(): Boolean =
            contains("You are LMSMOB Chat, a polite, practical assistant running locally through LM Studio") &&
                contains("- LM Studio server-side MCP/plugin tools when the app enables integrations for the current request.") &&
                contains("- Android phone-side tools when the app lists them below; these always require user confirmation before any action is performed.") &&
                !contains("Never use <lmsmob_action> for LM Studio MCP/plugin tools")

        fun chatFoldersToJson(folders: List<ChatFolder>): String {
            val foldersJson = JSONArray()
            folders.forEach { folder ->
                foldersJson.put(
                    JSONObject()
                        .put("id", folder.id)
                        .put("name", folder.name)
                        .put("created_at", folder.createdAt),
                )
            }
            return JSONObject()
                .put("version", 1)
                .put("folders", foldersJson)
                .toString()
        }

        fun chatFoldersFromJson(json: String): List<ChatFolder> {
            if (json.isBlank()) return emptyList()
            val foldersJson = JSONObject(json).optJSONArray("folders") ?: JSONArray()
            return buildList {
                for (index in 0 until foldersJson.length()) {
                    val folderJson = foldersJson.optJSONObject(index) ?: continue
                    val name = folderJson.optString("name").trim()
                    if (name.isBlank()) continue
                    add(
                        ChatFolder(
                            id = folderJson.optString("id").ifBlank { UUID.randomUUID().toString() },
                            name = name,
                            createdAt = folderJson.optLong("created_at", System.currentTimeMillis()),
                        ),
                    )
                }
            }.distinctBy { it.id }
                .sortedBy { it.name.lowercase(Locale.getDefault()) }
        }

        fun chatSessionsToJson(
            sessions: List<ChatSession>,
            folders: List<ChatFolder> = emptyList(),
        ): String {
            val foldersJson = JSONArray()
            folders.forEach { folder ->
                foldersJson.put(
                    JSONObject()
                        .put("id", folder.id)
                        .put("name", folder.name)
                        .put("created_at", folder.createdAt),
                )
            }
            val sessionsJson = JSONArray()
            sessions.forEach { session ->
                val messagesJson = JSONArray()
                session.messages.forEach { message ->
                    val attachmentsJson = JSONArray()
                    message.attachments.forEach { attachment ->
                        attachmentsJson.put(
                            JSONObject()
                                .put("label", attachment.label)
                                .put("mime_type", attachment.mimeType)
                                .put("remote_url", attachment.remoteUrl),
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
                val sessionJson = JSONObject()
                    .put("id", session.id)
                    .put("title", session.title)
                    .put("previous_response_id", session.previousResponseId)
                    .put("created_at", session.createdAt)
                    .put("updated_at", session.updatedAt)
                    .put("messages", messagesJson)
                session.folderId?.takeIf { it.isNotBlank() }?.let { sessionJson.put("folder_id", it) }
                sessionsJson.put(sessionJson)
            }
            return JSONObject()
                .put("version", 1)
                .put("exported_at", System.currentTimeMillis())
                .put("folders", foldersJson)
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
                                            remoteUrl = attachmentJson.optString("remote_url"),
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
                            folderId = sessionJson.optString("folder_id").takeIf { it.isNotBlank() },
                            isTemporary = false,
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
    val attachments: List<ChatImageAttachment> = emptyList(),
)

private data class SourceLink(
    val title: String,
    val url: String,
)

private data class ExtractedToolOutput(
    val textParts: List<String> = emptyList(),
    val imageAttachments: List<ChatImageAttachment> = emptyList(),
    val sourceLinks: List<SourceLink> = emptyList(),
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
    val imageAttachments: List<ChatImageAttachment> = emptyList(),
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

private fun String.isKnownChatFolder(folders: List<ChatFolder>): Boolean =
    this == AllChatsFolderId || this == UnfiledChatsFolderId || folders.any { it.id == this }

private fun ChatSession.matchesChatFolder(folderId: String): Boolean =
    when (folderId) {
        AllChatsFolderId -> true
        UnfiledChatsFolderId -> this.folderId.isNullOrBlank()
        else -> this.folderId == folderId
    }

private fun ChatSession.folderName(folders: List<ChatFolder>): String =
    folders.firstOrNull { it.id == folderId }?.name ?: "Unfiled"

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
    sources: List<SourceLink> = emptyList(),
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
    append(message.withoutLmStudioImagePlaceholders())
    val uniqueSources = sources.distinctBy { it.url }.take(12)
    if (uniqueSources.isNotEmpty()) {
        if (isNotBlank()) append("\n\n")
        append("Sources:\n")
        uniqueSources.forEach { source ->
            append("- [")
            append(source.title.toMarkdownLinkLabel())
            append("](")
            append(source.url)
            append(")\n")
        }
    }
    if (errors.isNotEmpty()) {
        if (isNotBlank()) append("\n\n")
        append("Tool errors: ")
        append(errors.joinToString(", "))
    }
}

private fun String.toMarkdownLinkLabel(): String =
    replace("[", "(")
        .replace("]", ")")
        .replace("\n", " ")
        .trim()
        .ifBlank { "Source" }

private fun String.withoutLmStudioImagePlaceholders(): String =
    replace(LmStudioImagePlaceholderRegex, "")
        .replace(Regex("\\n{3,}"), "\n\n")
        .trim()

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
        .withCapabilityGuidePrompt(this)

private fun String.withPhoneDateTimePrompt(state: ChatUiState): String {
    if (!state.appendDateTimeToSystemPrompt) return this
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())
    val phoneDateTime = LocalDateTime.now()
        .atZone(ZoneId.systemDefault())
        .format(formatter)
    return "$this\n\nCurrent phone date/time: $phoneDateTime."
}

private fun String.withCapabilityGuidePrompt(state: ChatUiState): String {
    val prompt = state.capabilityGuideSystemPrompt()
    return if (prompt.isBlank()) this else "$this\n\n$prompt"
}

private fun String.withNativeToolPrompt(state: ChatUiState): String {
    val prompt = state.nativeToolSystemPrompt()
    return if (prompt.isBlank()) this else "$this\n\n$prompt"
}

private fun String.withServerToolsPrompt(state: ChatUiState): String {
    if (!state.serverToolsEnabled) return this
    val integrations = state.serverIntegrations
        .split('\n', ',', ';')
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .joinToString("; ")
    val allowedTools = state.allowedTools
        .split('\n', ',', ';')
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .joinToString("; ")
    val prompt = buildString {
        appendLine("LM Studio server-side tools may be available for this request.")
        if (integrations.isNotBlank()) {
            appendLine("Enabled server integrations: $integrations.")
        }
        if (allowedTools.isNotBlank()) {
            appendLine("Allowed server tool names: $allowedTools.")
        }
        append("Use server-side tools only when they directly help the user's request, and summarize any tool output clearly.")
    }
    return "$this\n\n$prompt"
}

private fun ChatUiState.capabilityGuideSystemPrompt(): String {
    if (!appendCapabilityGuideToSystemPrompt) return ""
    val modelInfo = selectedModelInfo()
    val visionStatus = if (selectedModelSupportsVision()) "ENABLED" else "DISABLED"
    val reasoningStatus = if (selectedModelSupportsReasoningToggle()) "ENABLED" else "DISABLED"
    val serverIntegrationsList = serverIntegrations.toCapabilityList()
    val allowedToolsList = allowedTools.toCapabilityList()
    val serverToolHints = serverIntegrationToolHints(serverIntegrations, allowedTools)

    fun Boolean.status(): String = if (this) "ENABLED" else "DISABLED"

    fun phoneToolLine(
        enabled: Boolean,
        name: String,
        schema: String,
        description: String,
        settingsName: String,
        limitation: String = "",
    ): String {
        val availability = if (enabled) {
            "Use only after user confirmation. Action schema: $schema."
        } else {
            "Do not emit this action. Tell the user to enable Settings > Phone tools > $settingsName first if they want to allow it."
        }
        return buildString {
            append("- [${enabled.status()}] $name: $description $availability")
            if (limitation.isNotBlank()) append(" Limitation: $limitation")
        }
    }

    return buildString {
        appendLine("LMSMOB capability guide appended by the app.")
        appendLine("Use this guide as the current source of truth. Enabled means the user has allowed the capability in app settings or by selecting a compatible model. Disabled means you must not pretend to use it; politely tell the user what setting/model permission is needed.")
        appendLine()
        appendLine("General app capabilities:")
        appendLine("- [ENABLED] Conversation: answer questions, draft text, rewrite, translate, summarize, reason through plans, explain code, and help troubleshoot LM Studio or Android setup.")
        appendLine("- [ENABLED] Rich formatting: use Markdown, headings, bullet lists, code blocks, and Markdown tables. Tables should be emitted as normal Markdown tables when tabular data is present.")
        appendLine("- [ENABLED] Chat context: use the visible conversation context. The app shows context usage, but you should stay concise when context is high.")
        appendLine("- [ENABLED] Document uploads: the user can attach PDF, DOC, DOCX, XLS, and XLSX files. The app converts them to plain text or images before sending; answer only from content actually provided.")
        appendLine("- [$visionStatus] Image understanding: ${if (selectedModelSupportsVision()) "the selected model reports vision support, so the user can attach camera/gallery images." else "the selected model does not report vision support; ask the user to select a vision-capable model before image analysis."}")
        appendLine("- [$reasoningStatus] Thinking toggle: ${if (selectedModelSupportsReasoningToggle()) "the selected model supports on/off reasoning control." else "the selected model does not expose an on/off reasoning control."}")
        appendLine("- [${voiceInputEnabled.status()}] Voice input: user-facing speech-to-text for prompts. You do not directly listen; the app transcribes when the user presses the mic.")
        appendLine("- [${voiceOutputEnabled.status()}] Voice output (${voiceOutputEngine.voiceOutputEngineLabel()}, ${voiceOutputLanguage.voiceOutputLanguageLabel()}, ${voiceOutputVoiceName.ifBlank { "default voice" }}, ${voiceOutputSpeed.voiceOutputSpeedLabel()}, ${voiceOutputQuality.voiceOutputQualityLabel()}, ${voiceOutputChunkSize.voiceOutputChunkSizeLabel()} chunks, ${voiceOutputStartBuffer.voiceOutputStartBufferLabel()} start buffer): user-facing text-to-speech for assistant answers. You do not directly speak; the app reads answers aloud when enabled.")
        appendLine("- [${autoReadAnswersEnabled.status()}] Auto-read answers: the app can read completed replies aloud when voice output is enabled.")
        appendLine("- [${appendDateTimeToSystemPrompt.status()}] Current date/time in prompt: ${if (appendDateTimeToSystemPrompt) "the app appends the phone date/time separately." else "not appended; ask the user to enable it if exact current phone time matters."}")
        appendLine("- [ENABLED] Chat management UI: the user can search chats, copy/edit/delete messages, delete chats, and import/export history in the app UI. Do not claim to operate these UI controls yourself.")
        appendLine()
        appendLine("Tool routing rules:")
        appendLine("- LM Studio server-side tools are MCP/plugin tools invoked by LM Studio through the integrations field. Never output <lmsmob_action> for server tools.")
        appendLine("- Android phone-side tools are local device actions invoked only by a <lmsmob_action> block. Never expect LM Studio MCP/plugin tools to open phone apps, read contacts, change alarms, or manage watch jobs.")
        appendLine("- Use server tools for web/search/fetch/YouTube/QR/page screenshot work. Use phone tools for Android intents, contacts, notifications, files, device status, alarms, calendar/reminders, and watch jobs.")
        appendLine("- If both sides could apply, choose by intent: read or analyze online content = server tool; open something on the phone or change phone state = phone action after confirmation.")
        appendLine()
        appendLine("LM Studio server-side tools and integrations:")
        if (serverToolsEnabled) {
            appendLine("- [ENABLED] Server tools: LM Studio MCP/plugin integrations may be used by the /api/v1/chat request.")
            appendLine("- Integrations configured for this request: ${serverIntegrationsList.ifBlank { "none listed; ask the user to add at least one integration in Settings > Server tools." }}")
            appendLine("- Allowed server tool filter: ${allowedToolsList.ifBlank { "none set; LM Studio decides from enabled integrations." }}")
            if (serverToolHints.isNotBlank()) {
                appendLine("- Known server-side tool map:")
                appendLine(serverToolHints)
            }
            appendLine("- Use server tools only when they directly help. Summarize tool results clearly, include direct source links when tool output provides URLs, and show generated images when the tool returns image content. If a requested server tool is missing, ask the user to enable/install it in LM Studio and add it to Settings > Server tools.")
        } else {
            appendLine("- [DISABLED] Server tools: do not assume web/MCP/plugin access. If the user asks for web browsing, Playwright, local-web, or other LM Studio plugins, tell them to enable Settings > Server tools, add integrations, and provide an API token with MCP permissions.")
        }
        appendLine()
        appendLine("Phone-side action format:")
        appendLine("- For enabled phone-side actions, reply with a short explanation and exactly one block: <lmsmob_action>{\"tool\":\"tool_name\",\"args\":{\"key\":\"value\"}}</lmsmob_action>")
        appendLine("- The only valid tool names inside <lmsmob_action> are the phone-side tools listed below. Do not put server-side names such as web_search, web_fetch, youtube_transcript, qr_generate, qr_scan, or web_page_to_images inside <lmsmob_action>.")
        appendLine("- For disabled phone-side actions, do not emit an action block. Ask the user to enable the specific tool in Settings > Phone tools. Enabling a tool is the user's consent to allow the app to offer that action with confirmation.")
        appendLine("- Calls, SMS, email, maps, URLs, calendar, reminders, alarms, contacts, files, notifications, and watch jobs are sensitive. The app asks for confirmation; never say the final action is complete until the app returns a tool result or the user confirms a draft.")
        appendLine()
        appendLine("Phone-side tools:")
        appendLine(phoneToolLine(nativeIntentToolEnabled, "open_url", "{\"tool\":\"open_url\",\"args\":{\"url\":\"https://example.com\"}}", "open a web URL in Android after confirmation.", "Open app drafts"))
        appendLine(phoneToolLine(nativeIntentToolEnabled, "maps_route", "{\"tool\":\"maps_route\",\"args\":{\"query\":\"address or destination\"}}", "open Google Maps or a compatible maps app for a destination/search.", "Open app drafts"))
        appendLine(phoneToolLine(nativeIntentToolEnabled, "email_draft", "{\"tool\":\"email_draft\",\"args\":{\"to\":\"name@example.com\",\"subject\":\"Subject\",\"body\":\"Message\"}}", "prepare an email draft; the user sends it manually.", "Open app drafts"))
        appendLine(phoneToolLine(nativeIntentToolEnabled, "phone_dial", "{\"tool\":\"phone_dial\",\"args\":{\"phone\":\"+370...\"}}", "open the phone dialer with a number; the user starts the call manually.", "Open app drafts"))
        appendLine(phoneToolLine(nativeIntentToolEnabled, "sms_draft", "{\"tool\":\"sms_draft\",\"args\":{\"phone\":\"+370...\",\"body\":\"Message\"}}", "prepare an SMS draft; the user sends it manually.", "Open app drafts"))
        appendLine(phoneToolLine(calendarToolEnabled, "calendar_event", "{\"tool\":\"calendar_event\",\"args\":{\"title\":\"Meeting\",\"start\":\"2026-05-23T14:00\",\"end\":\"2026-05-23T15:00\",\"location\":\"Office\",\"description\":\"Notes\"}}", "prepare a calendar event draft.", "Calendar and reminders"))
        appendLine(phoneToolLine(calendarToolEnabled, "reminder", "{\"tool\":\"reminder\",\"args\":{\"title\":\"Task\",\"time\":\"2026-05-24T09:00\",\"notes\":\"Details\"}}", "prepare a reminder-style calendar item.", "Calendar and reminders"))
        appendLine(phoneToolLine(alarmToolEnabled, "alarm_read", "{\"tool\":\"alarm_read\",\"args\":{}}", "read the next scheduled Android alarm if Android exposes one.", "Alarms", "Android does not expose the full alarm database to third-party apps."))
        appendLine(phoneToolLine(alarmToolEnabled, "alarm_list", "{\"tool\":\"alarm_list\",\"args\":{}}", "open the Clock alarms screen so the user can view alarms.", "Alarms", "The app cannot read all alarms directly."))
        appendLine(phoneToolLine(alarmToolEnabled, "alarm_create", "{\"tool\":\"alarm_create\",\"args\":{\"hour\":7,\"minute\":30,\"message\":\"Wake up\",\"days\":[\"MONDAY\"],\"vibrate\":true}}", "open an Android alarm creation draft.", "Alarms"))
        appendLine(phoneToolLine(alarmToolEnabled, "alarm_edit", "{\"tool\":\"alarm_edit\",\"args\":{\"label\":\"Wake up\"}}", "open the Clock alarms screen for manual editing.", "Alarms", "Android does not allow direct edit by label."))
        appendLine(phoneToolLine(alarmToolEnabled, "alarm_delete", "{\"tool\":\"alarm_delete\",\"args\":{\"label\":\"Wake up\"}}", "open the Clock alarms screen for manual deletion.", "Alarms", "Android does not allow direct delete by label."))
        appendLine(phoneToolLine(contactsToolEnabled, "contacts_lookup", "{\"tool\":\"contacts_lookup\",\"args\":{\"name\":\"Rasa\"}}", "search granted phone contacts by name and return matching phone/email entries.", "Contacts lookup"))
        appendLine(phoneToolLine(notificationDigestToolEnabled, "notification_digest", "{\"tool\":\"notification_digest\",\"args\":{\"limit\":20}}", "summarize recent notifications captured by LMSMOB notification access.", "Notification digest", "Requires Android Notification Listener access."))
        appendLine(phoneToolLine(localFileSearchToolEnabled, "local_file_search", "{\"tool\":\"local_file_search\",\"args\":{\"query\":\"invoice May\"}}", "search the user-selected folder for matching file names and readable text snippets.", "Local file search", "Only the folder granted by the user is searchable."))
        appendLine(phoneToolLine(deviceStatusToolEnabled, "device_status", "{\"tool\":\"device_status\",\"args\":{}}", "return battery, network, storage, app version, and LM Studio connection status.", "Device status"))
        appendLine(phoneToolLine(watchJobToolEnabled, "watch_job_list", "{\"tool\":\"watch_job_list\",\"args\":{}}", "return configured watch jobs with ids, filters, alert modes, lifetime, and enabled state.", "Watch jobs"))
        appendLine(phoneToolLine(watchJobToolEnabled, "watch_job_create", "{\"tool\":\"watch_job_create\",\"args\":{\"title\":\"AI news watch\",\"trigger\":\"schedule\",\"match_mode\":\"prompt\",\"prompt\":\"Search the web for major Lithuanian AI policy news. Alert only when a credible new item appears and include source URLs.\",\"schedule_minutes\":60,\"alert\":\"AI news found\",\"alert_mode\":\"normal\",\"lifetime\":\"noend\"}}", "create a local notification, notification-history, or scheduled prompt watch job. match_mode can be filter, ai, or prompt. In AI mode, ai_instruction is required and app/sender/text are optional pre-filters. In prompt mode, prompt or ai_instruction is required, trigger is schedule, and LM Studio server tools may be used for web/news tasks. alert_mode can be normal or alarm. lifetime can be once, today, or noend.", "Watch jobs", "Requires notification permission for alerts, Notification Listener access for notification-based jobs, and a reachable LM Studio model. Prompt mode needs Settings > Server tools configured when it should search/fetch the web. Alarm mode is full-screen and intrusive, so use it only when the user clearly wants strong alerts."))
        appendLine(phoneToolLine(watchJobToolEnabled, "watch_job_edit", "{\"tool\":\"watch_job_edit\",\"args\":{\"id\":\"watch-job-id\",\"title\":\"New title\",\"match_mode\":\"ai\",\"ai_instruction\":\"Alert only for Mindylab offers\",\"app\":\"Telegram\",\"sender\":\"Mom\",\"text\":\"urgent\",\"trigger\":\"notification\",\"alert_mode\":\"normal\",\"lifetime\":\"noend\",\"enabled\":true}}", "edit an existing watch job by id or exact title. Omitted fields keep existing values.", "Watch jobs"))
        appendLine(phoneToolLine(watchJobToolEnabled, "watch_job_set_enabled", "{\"tool\":\"watch_job_set_enabled\",\"args\":{\"id\":\"watch-job-id\",\"enabled\":false}}", "enable or disable an existing watch job by id or exact title.", "Watch jobs"))
        appendLine(phoneToolLine(watchJobToolEnabled, "watch_job_delete", "{\"tool\":\"watch_job_delete\",\"args\":{\"id\":\"watch-job-id\"}}", "delete an existing watch job by id or exact title.", "Watch jobs"))
    }.trim()
}

private fun String.toCapabilityList(): String =
    split('\n', ',', ';')
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .distinct()
        .joinToString("; ")

private fun serverIntegrationToolHints(integrations: String, allowedTools: String): String {
    val normalized = integrations.lowercase()
    val hints = mutableListOf<String>()
    if ("local-web" in normalized) {
        hints += "mcp/local-web: web_search finds public web results; web_fetch reads one URL; web_search_and_fetch searches then reads top results; youtube_transcript reads YouTube transcripts; qr_generate creates QR images; qr_scan reads QR images; web_page_to_images captures webpage screenshots. For webpage screenshots, capture the full scrollable page by default. Use viewportOnly=true only when the user explicitly asks for only the top/current visible viewport."
    }
    if ("youtube" in normalized && "local-web" !in normalized) {
        hints += "YouTube MCP/plugin integration: use it for YouTube video lookup or transcript tasks when available."
    }
    if ("playwright" in normalized) {
        hints += "mcp/playwright: browser automation and rendered webpage inspection on the LM Studio server side, not on the Android phone."
    }
    if ("gemma4-audio-python" in normalized || "audio" in normalized) {
        hints += "Audio MCP/plugin integration: audio processing happens on the LM Studio server side when enabled."
    }
    val allowed = allowedTools.toCapabilityList()
    if (allowed.isNotBlank()) {
        hints += "Allowed server tool filter is active, so only these server tool names should be expected: $allowed."
    }
    return hints.joinToString("\n") { "- $it" }
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
    if (alarmToolEnabled) {
        tools += "alarm_read {}"
        tools += "alarm_list {}"
        tools += "alarm_create {hour, minute, message, days, vibrate}"
        tools += "alarm_edit {label}"
        tools += "alarm_delete {label}"
    }
    if (contactsToolEnabled) tools += "contacts_lookup {name}"
    if (notificationDigestToolEnabled) tools += "notification_digest {limit}"
    if (localFileSearchToolEnabled) tools += "local_file_search {query}"
    if (deviceStatusToolEnabled) tools += "device_status {}"
    if (watchJobToolEnabled) {
        tools += "watch_job_list {}"
        tools += "watch_job_create {title, trigger, match_mode, prompt, ai_instruction, app, sender, text, schedule_minutes, alert, alert_mode, lifetime}"
        tools += "watch_job_edit {id, title, trigger, match_mode, prompt, ai_instruction, app, sender, text, schedule_minutes, alert, alert_mode, lifetime, enabled}"
        tools += "watch_job_set_enabled {id, enabled}"
        tools += "watch_job_delete {id}"
    }
    if (tools.isEmpty()) return ""

    return """
        LMSMOB phone-side tools are available, but only when the user confirms inside the Android app.
        If one of these tools is needed, reply with a short explanation and exactly one action block:
        <lmsmob_action>{"tool":"tool_name","args":{"key":"value"}}</lmsmob_action>
        Available phone-side tools: ${tools.joinToString("; ")}.
        Alarm limitations: Android only lets this app read the next scheduled alarm and create alarm drafts. alarm_list, alarm_edit, and alarm_delete open the Clock alarms screen for the user to view or finish manually.
        Watch job limitations: watch_job_list can read configured jobs; watch_job_create, watch_job_edit, watch_job_set_enabled, and watch_job_delete manage them after user confirmation. Use watch_job_list first if you do not know a job id. match_mode may be "filter", "ai", or "prompt". AI mode sends notification content to LM Studio and requires ai_instruction; app, sender, and text become optional pre-filters. Prompt mode runs a scheduled LM Studio task and requires prompt or ai_instruction; use it for recurring web/news checks only when Server tools are enabled/configured for web access. alert_mode may be "normal" or "alarm"; alarm mode opens a full-screen alert with repeating sound/vibration until stopped. lifetime may be "once", "today", or "noend". It needs Android notification access and alert permission, and it should be used only for clear user-requested monitoring tasks.
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
        "alarm_read",
        "alarm_list",
        "alarm_create",
        "alarm_edit",
        "alarm_delete" -> alarmToolEnabled
        "contacts_lookup" -> contactsToolEnabled
        "notification_digest" -> notificationDigestToolEnabled
        "local_file_search" -> localFileSearchToolEnabled
        "device_status" -> deviceStatusToolEnabled
        "watch_job_list",
        "watch_job_create",
        "watch_job_edit",
        "watch_job_set_enabled",
        "watch_job_delete" -> watchJobToolEnabled
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
        "alarm_read" -> "Read Next Alarm"
        "alarm_list" -> "Show All Alarms"
        "alarm_create" -> "Create Alarm"
        "alarm_edit" -> "Edit Alarm"
        "alarm_delete" -> "Delete Alarm"
        "contacts_lookup" -> "Look Up Contacts"
        "notification_digest" -> "Read Notification Digest"
        "local_file_search" -> "Search Local Files"
        "device_status" -> "Read Device Status"
        "watch_job_list" -> "List Watch Jobs"
        "watch_job_create" -> "Create Watch Job"
        "watch_job_edit" -> "Edit Watch Job"
        "watch_job_set_enabled" -> "Enable or Disable Watch Job"
        "watch_job_delete" -> "Delete Watch Job"
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
        "alarm_read" -> "Read the next scheduled Android alarm"
        "alarm_list" -> "Open the Android Clock alarms list"
        "alarm_create" -> listOf(args.arg("time").ifBlank { "${args.arg("hour")}:${args.arg("minute")}" }, args.arg("message", "label", "title")).filter { it.isNotBlank() }.joinToString(" - ")
        "alarm_edit" -> args.arg("label", "message", "title").ifBlank { "Open Clock alarms screen" }
        "alarm_delete" -> args.arg("label", "message", "title").ifBlank { "Open Clock alarms screen for manual deletion" }
        "contacts_lookup" -> args.arg("name", "query")
        "notification_digest" -> "Summarize recent notifications stored by LMSMOB Chat"
        "local_file_search" -> args.arg("query")
        "device_status" -> "Battery, network, storage, app and LM Studio connectivity"
        "watch_job_list" -> "Read configured watch jobs"
        "watch_job_create" -> listOf(
            args.arg("title", "name"),
            args.arg("trigger", "mode").ifBlank { "notification" },
            args.arg("match_mode").ifBlank {
                if (args.arg("prompt", "schedule_prompt", "scheduled_prompt", "task_prompt").isNotBlank()) {
                    "prompt"
                } else {
                    "filter"
                }
            }.watchMatchModeLabel(),
            args.arg("prompt", "schedule_prompt", "scheduled_prompt", "app", "sender", "text", "query", "contains"),
            args.arg("alert_mode").normalizedWatchAlertMode().watchAlertModeLabel(),
            args.arg("lifetime").normalizedWatchLifetime().watchLifetimeLabel(),
        ).filter { it.isNotBlank() }.joinToString(" - ")
        "watch_job_edit" -> listOf(args.arg("id", "job_id"), args.arg("title", "name")).filter { it.isNotBlank() }.joinToString(" - ")
        "watch_job_set_enabled" -> listOf(args.arg("id", "job_id"), "enabled=${args.optBoolean("enabled", false)}").filter { it.isNotBlank() }.joinToString(" - ")
        "watch_job_delete" -> listOf(args.arg("id", "job_id"), args.arg("title", "name")).filter { it.isNotBlank() }.joinToString(" - ")
        else -> args.toString()
    }.ifBlank { "No extra details provided." }

private fun JSONObject.arg(vararg names: String): String {
    names.forEach { name ->
        optString(name).takeIf { it.isNotBlank() }?.let { return it }
    }
    return ""
}

private fun JSONObject.optionalArg(vararg names: String): String? {
    names.forEach { name ->
        if (has(name)) return optString(name)
    }
    return null
}

private fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("LM Studio message", text))
}

private fun Context.openUrl(url: String) {
    runCatching {
        startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
    }.onFailure {
        Toast.makeText(this, "Could not open link", Toast.LENGTH_SHORT).show()
    }
}

private fun ChatAppendPreset.applyToChatText(chatText: String): String {
    val baseText = chatText.trimEnd()
    val appendText = template.trim()
    if (appendText.isBlank()) return chatText
    if (baseText.isBlank()) return appendText
    return "$baseText $appendText"
}

private fun ChatAppendPreset.isLegacyTranslateLtPreset(): Boolean =
    id == LegacyTranslateLtPresetId &&
        name == "Translate LT" &&
        template == "{chat text} translate text to Lithuanian language"

private suspend fun prepareIncomingShareIntent(
    context: Context,
    intent: Intent,
    viewModel: ChatViewModel,
): PreparedShareContent? {
    val text = intent.sharedTextPayload().orEmpty()
    val uris = intent.sharedStreamUris()
    val imageUris = uris.filter { uri -> context.isSharedImage(uri, intent.type) }
    val documentUris = uris.filterNot { uri -> uri in imageUris }
    val imageAttachments = if (imageUris.isNotEmpty()) {
        runCatching {
            withContext(Dispatchers.IO) {
                imageUris.map { uri -> context.imageUriToAttachment(uri) }
            }
        }.onFailure { throwable ->
            viewModel.showError(throwable.friendlyMessage())
        }.getOrDefault(emptyList())
    } else {
        emptyList()
    }

    if (documentUris.size > 1) {
        viewModel.showError("Only one shared document can be prepared at a time.")
    }
    val documentUri = documentUris.firstOrNull()

    return PreparedShareContent(
        text = text,
        imageAttachments = imageAttachments,
        documentUri = documentUri,
    ).takeIf {
        it.text.isNotBlank() || it.imageAttachments.isNotEmpty() || it.documentUri != null
    }
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
            "alarm_read" -> {
                val result = withContext(Dispatchers.IO) {
                    context.nextAlarmText()
                }
                viewModel.receiveSharedText(result.asNativeToolResult("alarm_read"))
            }
            "alarm_list" -> context.openAlarmManageScreen()
            "alarm_create" -> context.openAlarmCreateDraft(action.args)
            "alarm_edit" -> context.openAlarmManageScreen()
            "alarm_delete" -> context.openAlarmManageScreen()
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
            "watch_job_list" -> {
                val result = viewModel.listWatchJobsForTool()
                viewModel.receiveSharedText(result.asNativeToolResult("watch_job_list"))
            }
            "watch_job_create" -> {
                val job = viewModel.createWatchJobFromArgs(action.args)
                val result = "Created watch job.\n${job.watchJobToolDescription()}"
                viewModel.receiveSharedText(result.asNativeToolResult("watch_job_create"))
            }
            "watch_job_edit" -> {
                val job = viewModel.editWatchJobFromArgs(action.args)
                val result = "Updated watch job.\n${job.watchJobToolDescription()}"
                viewModel.receiveSharedText(result.asNativeToolResult("watch_job_edit"))
            }
            "watch_job_set_enabled" -> {
                val job = viewModel.setWatchJobEnabledFromArgs(action.args)
                val result = "Watch job ${if (job.enabled) "enabled" else "disabled"}.\n${job.watchJobToolDescription()}"
                viewModel.receiveSharedText(result.asNativeToolResult("watch_job_set_enabled"))
            }
            "watch_job_delete" -> {
                val job = viewModel.deleteWatchJobFromArgs(action.args)
                val result = "Deleted watch job: ${job.title}\nid: ${job.id}"
                viewModel.receiveSharedText(result.asNativeToolResult("watch_job_delete"))
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

private fun Context.nextAlarmText(): String {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val nextAlarm = alarmManager.nextAlarmClock
        ?: return "No next scheduled Android alarm was reported by the phone."
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())
    val triggerTime = Instant.ofEpochMilli(nextAlarm.triggerTime)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
    val source = nextAlarm.showIntent?.creatorPackage.orEmpty()
    return buildString {
        appendLine("Next phone alarm:")
        appendLine("- Time: $triggerTime")
        if (source.isNotBlank()) appendLine("- Clock app: $source")
        appendLine("- Note: Android exposes only the next scheduled alarm to third-party apps.")
    }.trim()
}

private fun Context.openAlarmCreateDraft(args: JSONObject) {
    val (hour, minute) = args.toAlarmHourMinute()
    val label = args.arg("message", "label", "title").ifBlank { "LMSMOB alarm" }
    val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
        putExtra(AlarmClock.EXTRA_HOUR, hour)
        putExtra(AlarmClock.EXTRA_MINUTES, minute)
        putExtra(AlarmClock.EXTRA_MESSAGE, label)
        putExtra(AlarmClock.EXTRA_VIBRATE, args.optBoolean("vibrate", true))
        putExtra(AlarmClock.EXTRA_SKIP_UI, false)
        args.toAlarmDays().takeIf { it.isNotEmpty() }?.let { days ->
            putIntegerArrayListExtra(AlarmClock.EXTRA_DAYS, ArrayList(days))
        }
    }
    startSafeActivity(intent)
}

private fun Context.openAlarmManageScreen() {
    startSafeActivity(Intent(AlarmClock.ACTION_SHOW_ALARMS))
}

private fun JSONObject.toAlarmHourMinute(): Pair<Int, Int> {
    val hour = optionalInt("hour")
    val minute = optionalInt("minute", "minutes")
    if (hour != null && minute != null) {
        return hour.toAlarmHour() to minute.toAlarmMinute()
    }

    val time = arg("time", "start")
    val timeMatch = Regex("""\b(\d{1,2}):(\d{2})\b""").find(time)
    if (timeMatch != null) {
        return timeMatch.groupValues[1].toInt().toAlarmHour() to
            timeMatch.groupValues[2].toInt().toAlarmMinute()
    }
    val parsedMillis = time.toEventMillis()
    if (parsedMillis != null) {
        val dateTime = Instant.ofEpochMilli(parsedMillis).atZone(ZoneId.systemDefault())
        return dateTime.hour to dateTime.minute
    }
    throw IOException("Alarm time is missing. Provide hour/minute or time like 07:30.")
}

private fun JSONObject.toAlarmDays(): List<Int> {
    val rawArray = optJSONArray("days")
    if (rawArray != null) {
        return buildList {
            for (index in 0 until rawArray.length()) {
                rawArray.opt(index).toAlarmDayOrNull()?.let(::add)
            }
        }.distinct()
    }
    return arg("days")
        .split(",", ";", " ")
        .mapNotNull { it.toAlarmDayOrNull() }
        .distinct()
}

private fun JSONObject.optionalInt(vararg names: String): Int? {
    for (name in names) {
        val value = opt(name)
        val intValue = when (value) {
            is Number -> value.toInt()
            is String -> value.trim().toIntOrNull()
            else -> null
        }
        if (intValue != null) return intValue
    }
    return null
}

private fun Any?.toAlarmDayOrNull(): Int? =
    when (this) {
        is Number -> toInt().takeIf { it in Calendar.SUNDAY..Calendar.SATURDAY }
        is String -> trim().lowercase(Locale.getDefault()).let { value ->
            value.toIntOrNull()?.takeIf { it in Calendar.SUNDAY..Calendar.SATURDAY }
                ?: when (value.take(3)) {
                    "sun" -> Calendar.SUNDAY
                    "mon" -> Calendar.MONDAY
                    "tue" -> Calendar.TUESDAY
                    "wed" -> Calendar.WEDNESDAY
                    "thu" -> Calendar.THURSDAY
                    "fri" -> Calendar.FRIDAY
                    "sat" -> Calendar.SATURDAY
                    else -> null
                }
        }
        else -> null
    }

private fun Int.toAlarmHour(): Int =
    takeIf { it in 0..23 } ?: throw IOException("Alarm hour must be between 0 and 23.")

private fun Int.toAlarmMinute(): Int =
    takeIf { it in 0..59 } ?: throw IOException("Alarm minute must be between 0 and 59.")

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
                for (index in 0 until renderer.pageCount) {
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

private fun Context.hasPostNotificationsPermission(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

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
        WatchJobRunner.onNotificationPosted(applicationContext, sbn)
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

    fun recent(context: Context): JSONArray {
        val preferences = context.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE)
        return runCatching {
            JSONArray(preferences.getString(NotificationsKey, "[]").orEmpty())
        }.getOrDefault(JSONArray())
    }

    fun digest(context: Context, limit: Int): String {
        val notifications = recent(context)
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

private object WatchJobStore {
    private const val PreferencesName = "lm_studio_watch_jobs"
    private const val JobsKey = "jobs"

    fun load(context: Context): List<WatchJob> {
        val preferences = context.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE)
        return runCatching {
            fromJson(preferences.getString(JobsKey, "").orEmpty())
        }.getOrDefault(emptyList())
    }

    fun save(context: Context, jobs: List<WatchJob>) {
        val preferences = context.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE)
        preferences.edit()
            .putString(JobsKey, toJson(jobs))
            .apply()
    }

    private fun toJson(jobs: List<WatchJob>): String {
        val jobsJson = JSONArray()
        jobs.forEach { job ->
            jobsJson.put(
                JSONObject()
                    .put("id", job.id)
                    .put("title", job.title)
                    .put("trigger", job.trigger)
                    .put("app_query", job.appQuery)
                    .put("sender_query", job.senderQuery)
                    .put("text_query", job.textQuery)
                    .put("match_mode", job.matchMode)
                    .put("ai_instruction", job.aiInstruction)
                    .put("schedule_minutes", job.scheduleMinutes)
                    .put("alert_message", job.alertMessage)
                    .put("alert_mode", job.alertMode)
                    .put("lifetime", job.lifetime)
                    .put("enabled", job.enabled)
                    .put("created_at", job.createdAt)
                    .put("last_run_at", job.lastRunAt)
                    .put("last_checked_at", job.lastCheckedAt)
                    .put("last_matched_at", job.lastMatchedAt)
                    .put("match_count", job.matchCount),
            )
        }
        return JSONObject()
            .put("version", 1)
            .put("jobs", jobsJson)
            .toString()
    }

    private fun fromJson(json: String): List<WatchJob> {
        if (json.isBlank()) return emptyList()
        val root = JSONObject(json)
        val jobsJson = root.optJSONArray("jobs") ?: JSONArray()
        return buildList {
            for (index in 0 until jobsJson.length()) {
                val item = jobsJson.optJSONObject(index) ?: continue
                val trigger = item.optString("trigger").lowercase(Locale.getDefault()).let {
                    if (it == "schedule") "schedule" else "notification"
                }
                add(
                    WatchJob(
                        id = item.optString("id").ifBlank { UUID.randomUUID().toString() },
                        title = item.optString("title").ifBlank { "Watch job" },
                        trigger = trigger,
                        appQuery = item.optString("app_query"),
                        senderQuery = item.optString("sender_query"),
                        textQuery = item.optString("text_query"),
                        matchMode = item.optString("match_mode").normalizedWatchMatchMode(),
                        aiInstruction = item.optString("ai_instruction"),
                        scheduleMinutes = item.optInt("schedule_minutes", 0),
                        alertMessage = item.optString("alert_message"),
                        alertMode = item.optString("alert_mode").normalizedWatchAlertMode(),
                        lifetime = item.optString("lifetime").normalizedWatchLifetime(),
                        enabled = item.optBoolean("enabled", true),
                        createdAt = item.optLong("created_at", System.currentTimeMillis()),
                        lastRunAt = item.optLong("last_run_at", 0L),
                        lastCheckedAt = item.optLong("last_checked_at", 0L),
                        lastMatchedAt = item.optLong("last_matched_at", 0L),
                        matchCount = item.optInt("match_count", 0),
                    ),
                )
            }
        }.sortedByDescending { it.createdAt }
    }
}

class WatchJobReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == WatchJobRunner.Action) {
            val jobId = intent.getStringExtra(WatchJobRunner.ExtraJobId) ?: return
            val pendingResult = goAsync()
            WatchJobRunner.runScheduledAsync(context.applicationContext, jobId, pendingResult)
        }
    }
}

private object WatchJobRunner {
    const val Action = "com.mindylab.lmstudiochat.WATCH_JOB"
    const val ExtraJobId = "job_id"
    private const val NormalChannelId = "watch_jobs"
    private const val AlarmChannelId = "watch_jobs_alarm"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun runScheduledAsync(
        context: Context,
        jobId: String,
        pendingResult: BroadcastReceiver.PendingResult,
    ) {
        scope.launch {
            try {
                runScheduled(context, jobId)
            } catch (throwable: Throwable) {
                Log.w("WatchJobRunner", "Scheduled watch job failed: $jobId", throwable)
            } finally {
                pendingResult.finish()
            }
        }
    }

    fun schedule(context: Context, job: WatchJob) {
        if (!job.enabled || job.trigger != "schedule" || job.scheduleMinutes <= 0) {
            cancel(context, job.id)
            return
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            job.id.hashCode(),
            Intent(context, WatchJobReceiver::class.java)
                .setAction(Action)
                .putExtra(ExtraJobId, job.id),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val triggerAt = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(job.scheduleMinutes.toLong())
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
    }

    fun cancel(context: Context, jobId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            jobId.hashCode(),
            Intent(context, WatchJobReceiver::class.java).setAction(Action),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun onNotificationPosted(context: Context, sbn: StatusBarNotification) {
        if (sbn.packageName == context.packageName) return
        val item = sbn.toWatchNotificationJson()
        val now = System.currentTimeMillis()
        var changed = false
        val jobs = WatchJobStore.load(context).map { job ->
            if (!job.enabled || job.trigger != "notification") return@map job
            if (job.isWatchJobExpired(now)) {
                changed = true
                return@map job.copy(enabled = false, lastRunAt = now, lastCheckedAt = now)
            }
            val itemTime = item.optLong("time", now)
            val checkedJob = job.copy(lastRunAt = now, lastCheckedAt = maxOf(job.lastCheckedAt, itemTime))
            changed = true
            if (checkedJob.matchMode.normalizedWatchMatchMode() == "ai") {
                scope.launch {
                    evaluateAiWatchNotification(context, checkedJob, item)
                }
                checkedJob
            } else if (notificationMatches(context, checkedJob, item)) {
                showAlert(
                    context = context,
                    job = checkedJob,
                    detail = item.watchNotificationDescription(context),
                )
                checkedJob.copy(
                    lastMatchedAt = item.optLong("time", now),
                    matchCount = checkedJob.matchCount + 1,
                ).afterWatchJobMatch()
            } else {
                checkedJob
            }
        }
        if (changed) {
            WatchJobStore.save(context, jobs)
        }
    }

    fun runScheduled(context: Context, jobId: String) {
        val now = System.currentTimeMillis()
        val jobs = WatchJobStore.load(context)
        val job = jobs.firstOrNull { it.id == jobId } ?: return
        if (!job.enabled || job.trigger != "schedule") {
            cancel(context, jobId)
            return
        }
        if (job.isWatchJobExpired(now)) {
            WatchJobStore.save(
                context,
                jobs.map { existing -> if (existing.id == jobId) job.copy(enabled = false, lastRunAt = now) else existing },
            )
            cancel(context, jobId)
            return
        }

        if (job.matchMode.normalizedWatchMatchMode() == "prompt") {
            runScheduledPrompt(context, job, now, jobs)
            return
        }

        val notifications = NotificationDigestStore.recent(context)
        val checkAfter = if (job.lastCheckedAt > 0L) job.lastCheckedAt else job.createdAt
        val recentItems = (0 until notifications.length())
            .mapNotNull { index -> notifications.optJSONObject(index) }
            .filter { item -> item.optLong("time", 0L) > checkAfter }
            .sortedBy { item -> item.optLong("time", 0L) }
        var lastCheckedAt = job.lastCheckedAt
        var matched: JSONObject? = null
        for (item in recentItems) {
            val itemTime = item.optLong("time", 0L)
            lastCheckedAt = maxOf(lastCheckedAt, itemTime)
            val isMatch = job.watchJobPrefilterMatches(context, item) &&
                if (job.matchMode.normalizedWatchMatchMode() == "ai") {
                    runCatching { runBlockingWatchAiEvaluation(context, job, item) }.getOrDefault(false)
                } else {
                    notificationMatches(context, job, item)
                }
            if (isMatch) {
                matched = item
                break
            }
        }
        val updatedJob = if (matched != null) {
            showAlert(
                context = context,
                job = job,
                detail = matched.watchNotificationDescription(context),
            )
            job.copy(
                lastRunAt = now,
                lastCheckedAt = maxOf(lastCheckedAt, matched.optLong("time", now)),
                lastMatchedAt = matched.optLong("time", now),
                matchCount = job.matchCount + 1,
            ).afterWatchJobMatch()
        } else {
            job.copy(lastRunAt = now, lastCheckedAt = lastCheckedAt)
        }
        val finalJob = updatedJob.afterWatchJobRun(now)
        WatchJobStore.save(
            context,
            jobs.map { existing -> if (existing.id == jobId) finalJob else existing },
        )
        schedule(context, finalJob)
    }

    private fun runScheduledPrompt(
        context: Context,
        job: WatchJob,
        now: Long,
        jobs: List<WatchJob>,
    ) {
        val result = runCatching {
            kotlinx.coroutines.runBlocking {
                LmStudioClient().evaluateWatchJobPrompt(
                    settingsStore = SettingsStore(context),
                    job = job,
                )
            }
        }.onFailure { throwable ->
            Log.w("WatchJobRunner", "Scheduled prompt Watch Job failed: ${job.id}", throwable)
        }.getOrElse {
            WatchPromptResult(shouldAlert = false)
        }

        val updatedJob = if (result.shouldAlert) {
            showAlert(
                context = context,
                job = job,
                detail = result.toWatchPromptNotificationDetail(job),
                titleOverride = result.title.ifBlank { null },
            )
            job.copy(
                lastRunAt = now,
                lastCheckedAt = now,
                lastMatchedAt = now,
                matchCount = job.matchCount + 1,
            ).afterWatchJobMatch()
        } else {
            job.copy(lastRunAt = now, lastCheckedAt = now)
        }
        val finalJob = updatedJob.afterWatchJobRun(now)
        WatchJobStore.save(
            context,
            jobs.map { existing -> if (existing.id == job.id) finalJob else existing },
        )
        schedule(context, finalJob)
    }

    private fun notificationMatches(context: Context, job: WatchJob, item: JSONObject): Boolean {
        if (!job.watchJobPrefilterMatches(context, item)) return false
        if (job.matchMode.normalizedWatchMatchMode() == "ai") return false
        return true
    }

    private fun WatchJob.watchJobPrefilterMatches(context: Context, item: JSONObject): Boolean {
        val packageName = item.optString("package")
        val appLabel = context.appLabelForPackage(packageName)
        val title = item.optString("title")
        val text = item.optString("text")
        val subText = item.optString("sub_text")
        val appHaystack = "$packageName $appLabel"
        val senderHaystack = "$title $subText $text"
        val textHaystack = "$title $text $subText"
        return appHaystack.matchesFilter(appQuery) &&
            senderHaystack.matchesFilter(senderQuery) &&
            textHaystack.matchesFilter(textQuery)
    }

    private suspend fun evaluateAiWatchNotification(context: Context, job: WatchJob, item: JSONObject) {
        if (!job.enabled || job.matchMode.normalizedWatchMatchMode() != "ai") return
        if (!job.watchJobPrefilterMatches(context, item)) return
        if (job.isWatchJobExpired()) return
        val shouldAlert = runCatching {
            LmStudioClient().evaluateWatchJobNotification(
                settingsStore = SettingsStore(context),
                job = job,
                notification = item.watchNotificationDescription(context),
            )
        }.getOrDefault(false)
        val jobs = WatchJobStore.load(context)
        val current = jobs.firstOrNull { it.id == job.id } ?: return
        val itemTime = item.optLong("time", 0L)
        if (!current.enabled || current.lastMatchedAt >= itemTime) return
        val now = System.currentTimeMillis()
        val updated = if (shouldAlert) {
            showAlert(
                context = context,
                job = current,
                detail = item.watchNotificationDescription(context),
            )
            current.copy(
                lastRunAt = now,
                lastCheckedAt = maxOf(current.lastCheckedAt, itemTime),
                lastMatchedAt = item.optLong("time", now),
                matchCount = current.matchCount + 1,
            ).afterWatchJobMatch()
        } else {
            current.copy(lastRunAt = now, lastCheckedAt = maxOf(current.lastCheckedAt, itemTime))
        }.afterWatchJobRun(now)
        WatchJobStore.save(
            context,
            jobs.map { existing -> if (existing.id == current.id) updated else existing },
        )
        schedule(context, updated)
    }

    private fun runBlockingWatchAiEvaluation(context: Context, job: WatchJob, item: JSONObject): Boolean {
        val result = kotlinx.coroutines.runBlocking {
            LmStudioClient().evaluateWatchJobNotification(
                settingsStore = SettingsStore(context),
                job = job,
                notification = item.watchNotificationDescription(context),
            )
        }
        return result
    }

    private fun showAlert(
        context: Context,
        job: WatchJob,
        detail: String,
        titleOverride: String? = null,
    ) {
        if (!context.hasPostNotificationsPermission()) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val isAlarm = job.alertMode.normalizedWatchAlertMode() == "alarm"
        val channelId = if (isAlarm) AlarmChannelId else NormalChannelId
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        manager.createNotificationChannel(
            NotificationChannel(
                NormalChannelId,
                "Watch jobs",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Alerts from LMSMOB notification and schedule watch jobs."
                enableVibration(true)
            },
        )
        manager.createNotificationChannel(
            NotificationChannel(
                AlarmChannelId,
                "Watch job alarms",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Full-screen alarm alerts from LMSMOB Watch Jobs."
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 700, 350, 900, 350, 900)
                setSound(
                    alarmSound,
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build(),
                )
            },
        )
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val title = titleOverride?.trim()?.ifBlank { null }
            ?: job.alertMessage.ifBlank { "Watch job matched: ${job.title}" }
        val alertIntent = Intent(context, WatchJobAlertActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .putExtra(WatchJobAlertActivity.ExtraTitle, title)
            .putExtra(WatchJobAlertActivity.ExtraDetail, detail)
            .putExtra(WatchJobAlertActivity.ExtraJobId, job.id)
        val fullScreenIntent = PendingIntent.getActivity(
            context,
            job.id.hashCode() xor 0x51A7,
            alertIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = Notification.Builder(context, channelId)
            .setSmallIcon(R.drawable.ml_logo)
            .setContentTitle(title)
            .setContentText(detail.take(90))
            .setStyle(Notification.BigTextStyle().bigText(detail))
            .setContentIntent(if (isAlarm) fullScreenIntent else pendingIntent)
            .setAutoCancel(!isAlarm)
            .setCategory(if (isAlarm) Notification.CATEGORY_ALARM else Notification.CATEGORY_REMINDER)
            .setPriority(if (isAlarm) Notification.PRIORITY_MAX else Notification.PRIORITY_HIGH)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setVibrate(
                if (isAlarm) {
                    longArrayOf(0, 700, 350, 900, 350, 900)
                } else {
                    longArrayOf(0, 300, 160, 300)
                },
            )
            .apply {
                if (isAlarm) {
                    setOngoing(true)
                    setSound(alarmSound)
                    setFullScreenIntent(fullScreenIntent, true)
                }
            }
            .build()
        manager.notify(job.id.hashCode(), notification)
    }
}

private fun WatchJob.isWatchJobExpired(now: Long = System.currentTimeMillis()): Boolean =
    when (lifetime.normalizedWatchLifetime()) {
        "today" -> {
            val zone = ZoneId.systemDefault()
            Instant.ofEpochMilli(createdAt).atZone(zone).toLocalDate() !=
                Instant.ofEpochMilli(now).atZone(zone).toLocalDate()
        }
        else -> false
    }

private fun WatchJob.afterWatchJobMatch(): WatchJob =
    if (lifetime.normalizedWatchLifetime() == "once") {
        copy(enabled = false)
    } else {
        this
    }

private fun WatchJob.afterWatchJobRun(now: Long): WatchJob =
    if (isWatchJobExpired(now)) copy(enabled = false, lastRunAt = now) else this

private fun StatusBarNotification.toWatchNotificationJson(): JSONObject {
    val extras = notification.extras
    return JSONObject()
        .put("time", System.currentTimeMillis())
        .put("package", packageName)
        .put("title", extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty())
        .put("text", extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty())
        .put("sub_text", extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString().orEmpty())
}

@Suppress("DEPRECATION")
private fun Context.appLabelForPackage(packageName: String): String =
    runCatching {
        val appInfo = packageManager.getApplicationInfo(packageName, 0)
        packageManager.getApplicationLabel(appInfo).toString()
    }.getOrDefault(packageName)

private fun String.matchesFilter(query: String): Boolean =
    query.isBlank() || contains(query, ignoreCase = true)

private fun JSONObject.watchNotificationDescription(context: Context): String {
    val packageName = optString("package")
    val app = context.appLabelForPackage(packageName)
    val title = optString("title")
    val text = optString("text")
    val subText = optString("sub_text")
    return listOf(
        "App: $app ($packageName)",
        "Title: $title".takeIf { title.isNotBlank() },
        "Text: $text".takeIf { text.isNotBlank() },
        "Subtext: $subText".takeIf { subText.isNotBlank() },
    ).filterNotNull().joinToString("\n")
}

private fun String.toWatchPromptResult(job: WatchJob): WatchPromptResult {
    val json = extractFirstJsonObject()
    if (json != null) {
        val decision = json.firstNonBlankString(
            "decision",
            "final_decision",
            "status",
            "result",
        )
        val shouldAlert = decision.equals("ALERT", ignoreCase = true) ||
            decision.equals("YES", ignoreCase = true) ||
            json.optBoolean("should_alert", false) ||
            json.optBoolean("alert", false)
        val title = json.firstNonBlankString(
            "title",
            "notification_title",
            "alert_title",
        ).take(90)
        val detail = json.firstNonBlankString(
            "detail",
            "message",
            "notification_detail",
            "alert_detail",
        ).take(1800)
        val summary = json.firstNonBlankString(
            "summary",
            "short_summary",
            "resume",
            "finding",
            "findings",
            "what_found",
            "what_model_found",
        ).take(700)
        val triggerReason = json.firstNonBlankString(
            "why_triggered",
            "trigger_reason",
            "match_reason",
            "reason",
            "why",
        ).take(500)
        val sources = json.optJSONArray("sources").toStringList().ifEmpty {
            listOf(
                json.firstNonBlankString("source_url", "url", "link"),
            ).filter { it.isNotBlank() }
        }.take(4)
        return WatchPromptResult(
            shouldAlert = shouldAlert,
            title = title,
            detail = detail,
            summary = summary,
            triggerReason = triggerReason,
            sources = sources,
        )
    }

    val explicitDecision = Regex(
        "FINAL[_\\s-]*DECISION\\s*[:=\\-]?\\s*(ALERT|IGNORE)\\b",
        RegexOption.IGNORE_CASE,
    ).findAll(this).lastOrNull()?.groupValues?.getOrNull(1)
    val shouldAlert = explicitDecision?.equals("ALERT", ignoreCase = true) == true
    val detail = lineSequence()
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .filterNot { it.contains("FINAL", ignoreCase = true) && it.contains("DECISION", ignoreCase = true) }
        .joinToString("\n")
        .take(1800)
        .ifBlank { "Scheduled prompt matched:\n${job.aiInstruction}" }
    return WatchPromptResult(
        shouldAlert = shouldAlert,
        title = "",
        detail = detail,
        summary = detail.take(700),
    )
}

private fun WatchPromptResult.toWatchPromptNotificationDetail(job: WatchJob): String =
    buildList {
        val summaryText = summary.ifBlank { detail }.trim()
        if (summaryText.isNotBlank()) {
            add(summaryText)
        }
        if (triggerReason.isNotBlank()) {
            add("Why: $triggerReason")
        }
        if (detail.isNotBlank() && detail != summaryText) {
            add(detail)
        }
        if (sources.isNotEmpty()) {
            add("Sources: ${sources.take(3).joinToString(" ")}")
        }
    }.joinToString("\n")
        .take(1800)
        .ifBlank { "Scheduled prompt matched:\n${job.aiInstruction}" }

private fun String.extractFirstJsonObject(): JSONObject? {
    val start = indexOf('{')
    if (start < 0) return null
    var depth = 0
    var inString = false
    var escaped = false
    for (index in start until length) {
        val char = this[index]
        if (escaped) {
            escaped = false
            continue
        }
        if (char == '\\' && inString) {
            escaped = true
            continue
        }
        if (char == '"') {
            inString = !inString
            continue
        }
        if (inString) continue
        when (char) {
            '{' -> depth += 1
            '}' -> {
                depth -= 1
                if (depth == 0) {
                    return runCatching {
                        JSONObject(substring(start, index + 1))
                    }.getOrNull()
                }
            }
        }
    }
    return null
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

    suspend fun evaluateWatchJobNotification(
        settingsStore: SettingsStore,
        job: WatchJob,
        notification: String,
    ): Boolean = withContext(Dispatchers.IO) {
        val apiUrl = settingsStore.apiUrl
        val model = settingsStore.model.ifBlank {
            throw IOException("Choose a loaded LM Studio model before AI Watch Jobs can evaluate notifications.")
        }
        val systemPrompt = buildString {
            appendLine("You are a strict notification classifier for an Android watch job.")
            appendLine("Use ALERT only when the notification clearly satisfies the user's watch instruction and the user should be interrupted.")
            appendLine("Use IGNORE for unrelated, routine, weak, or uncertain matches.")
            appendLine("If uncertain, choose IGNORE.")
            appendLine("You may think if your model requires it, but your response must end with exactly one final marker.")
            appendLine("The final marker must be exactly one of these lines:")
            appendLine("FINAL_DECISION: ALERT")
            appendLine("FINAL_DECISION: IGNORE")
            appendLine("Do not put ALERT or IGNORE anywhere else except the final marker.")
        }.trim()
        val userPrompt = buildString {
            appendLine("USER WATCH INSTRUCTION:")
            appendLine(job.aiInstruction.ifBlank { "Alert only when this notification clearly needs the user's attention." })
            appendLine()
            appendLine("NOTIFICATION TEXT AND METADATA:")
            appendLine(notification.ifBlank { "(empty notification text)" })
            appendLine()
            appendLine("Return the final marker after any internal evaluation.")
            append("FINAL_DECISION:")
        }
        val payload = JSONObject()
            .put("model", model)
            .put(
                "messages",
                JSONArray()
                    .put(JSONObject().put("role", "system").put("content", systemPrompt))
                    .put(JSONObject().put("role", "user").put("content", userPrompt)),
            )
            .put("stream", false)
            .put("temperature", 0)
            .put("max_tokens", 3000)

        val request = Request.Builder()
            .url(apiUrl.normalizedOpenAiBaseUrl() + "/chat/completions")
            .withApiToken(settingsStore.apiToken)
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()

        httpClient.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw IOException("LM Studio returned HTTP ${response.code}: ${body.take(160)}")
            }
            val message = JSONObject(body)
                .optJSONArray("choices")
                ?.optJSONObject(0)
                ?.optJSONObject("message")
            val content = listOf(
                message?.optString("content").orEmpty(),
                message?.optString("reasoning_content").orEmpty(),
            ).joinToString("\n")
            val explicitDecision = Regex(
                "FINAL[_\\s-]*DECISION\\s*[:=\\-]?\\s*(ALERT|IGNORE)\\b",
                RegexOption.IGNORE_CASE,
            ).findAll(content).lastOrNull()?.groupValues?.getOrNull(1)
            val lineDecision = content
                .lineSequence()
                .map { it.trim().trim('.', ':', '-', '*', ' ') }
                .filter { it.equals("ALERT", ignoreCase = true) || it.equals("IGNORE", ignoreCase = true) }
                .lastOrNull()
            (explicitDecision ?: lineDecision)
                ?.uppercase(Locale.getDefault()) == "ALERT"
        }
    }

    suspend fun evaluateWatchJobPrompt(
        settingsStore: SettingsStore,
        job: WatchJob,
    ): WatchPromptResult = withContext(Dispatchers.IO) {
        val apiUrl = settingsStore.apiUrl
        val model = settingsStore.model.ifBlank {
            throw IOException("Choose a loaded LM Studio model before scheduled prompt Watch Jobs can run.")
        }
        val resolvedModel = resolveNativeModelId(
            apiUrl = apiUrl,
            apiToken = settingsStore.apiToken,
            preferredModel = model,
        )
        val integrationsJson = if (settingsStore.serverToolsEnabled) {
            settingsStore.serverIntegrations.toIntegrationsJsonArray(settingsStore.allowedTools)
        } else {
            JSONArray()
        }
        val nowText = LocalDateTime.now()
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.getDefault()))
        val systemPrompt = """
            You are a scheduled alert evaluator for LMSMOB Chat.
            Use available LM Studio server tools when the scheduled prompt needs current information.
            For news, web pages, YouTube, or other live information, use web/search/fetch/transcript tools if they are available.
            Choose ALERT only when the scheduled task finds a concrete, relevant result worth notifying the user about.
            Choose IGNORE when there is no relevant result, the information is stale, the task is uncertain, or required tools are unavailable.
            For ALERT, write a short summary of what you found and a short reason explaining why it triggered the user's scheduled prompt.
            Return exactly one JSON object and no Markdown:
            {"decision":"ALERT|IGNORE","title":"short notification title","summary":"1-2 short sentences describing what was found","why_triggered":"short reason this matched the user's prompt","detail":"optional extra key facts","sources":["https://source.example"]}
        """.trimIndent()
        val userPrompt = buildString {
            appendLine("Current phone time: $nowText")
            appendLine("Watch job title: ${job.title}")
            appendLine("Alert mode requested by user: ${job.alertMode.watchAlertModeLabel()}")
            appendLine()
            appendLine("Scheduled prompt:")
            appendLine(job.aiInstruction.ifBlank { "Run this scheduled check and alert only for a clear match." })
            appendLine()
            if (!settingsStore.serverToolsEnabled) {
                appendLine("Server tools are disabled for this scheduled run. If the prompt requires live web access, choose IGNORE and explain that web tools are disabled in the detail.")
            } else if (integrationsJson.length() == 0) {
                appendLine("Server tools are enabled but no integrations are configured. If the prompt requires live web access, choose IGNORE and explain that no web integration is configured in the detail.")
            }
        }
        val payload = JSONObject()
            .put("model", resolvedModel)
            .put("input", userPrompt)
            .put("system_prompt", systemPrompt)
            .put("integrations", integrationsJson)
            .put("store", false)
            .put("temperature", 0.2)

        val request = Request.Builder()
            .url(apiUrl.normalizedNativeApiUrl() + "/chat")
            .withApiToken(settingsStore.apiToken)
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()

        httpClient.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw IOException("LM Studio returned HTTP ${response.code}: ${body.take(160)}")
            }
            val responseJson = JSONObject(body)
            val output = responseJson.optJSONArray("output") ?: JSONArray()
            val messageContent = buildString {
                for (index in 0 until output.length()) {
                    val item = output.optJSONObject(index) ?: continue
                    if (item.optString("type") == "message") {
                        val content = item.optString("content").trim()
                        if (content.isNotBlank()) {
                            if (isNotBlank()) append("\n\n")
                            append(content)
                        }
                    }
                }
            }
            val content = messageContent.ifBlank {
                parseNativeChatResponse(responseJson, resolvedModel).content
            }
            content.toWatchPromptResult(job)
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
                        "tool_call.start",
                        "tool_call.name",
                        "tool_call.arguments" -> onEvent(json.toToolEvent(ChatStreamEventType.ToolStarted))
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
        val toolName = optString("tool")
            .ifBlank { optString("tool_name") }
            .ifBlank { optJSONObject("metadata")?.optString("tool_name").orEmpty() }
        val extractedOutput = if (type == ChatStreamEventType.ToolSucceeded) {
            extractToolOutput(toolName.ifBlank { "tool" })
        } else {
            ExtractedToolOutput()
        }
        return ChatStreamEvent(
            type = type,
            tool = toolName,
            provider = providerName,
            imageAttachments = extractedOutput.imageAttachments,
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
        val toolImageAttachments = mutableListOf<ChatImageAttachment>()
        val sourceLinks = linkedMapOf<String, SourceLink>()

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
                    val extractedOutput = item.extractToolOutput(tool)
                    toolImageAttachments += extractedOutput.imageAttachments
                    extractedOutput.sourceLinks.forEach { source ->
                        sourceLinks.putIfAbsent(source.url, source)
                    }
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
            sources = sourceLinks.values.toList(),
        ).trim()

        if (content.isBlank() && toolImageAttachments.isEmpty()) {
            throw IOException("LM Studio returned an empty response")
        }

        return ChatCompletionResult(
            content = content,
            responseId = json.optString("response_id").takeIf { it.isNotBlank() },
            modelId = json.optString("model_instance_id").takeIf { it.isNotBlank() } ?: fallbackModel,
            attachments = toolImageAttachments,
        )
    }
}

private fun JSONObject.extractToolOutput(tool: String): ExtractedToolOutput {
    val output = opt("output") ?: return ExtractedToolOutput()
    if (output == JSONObject.NULL) return ExtractedToolOutput()
    return parseToolOutputValue(output, tool)
}

private fun parseToolOutputValue(value: Any, tool: String): ExtractedToolOutput =
    when (value) {
        is JSONArray -> value.extractToolContent(tool)
        is JSONObject -> value.extractToolObject(tool)
        else -> parseToolOutputString(value.toString(), tool)
    }

private fun parseToolOutputString(value: String, tool: String): ExtractedToolOutput {
    val trimmed = value.trim()
    if (trimmed.isBlank()) return ExtractedToolOutput()
    val parsedJson = runCatching {
        when {
            trimmed.startsWith("[") -> JSONArray(trimmed).extractToolContent(tool)
            trimmed.startsWith("{") -> JSONObject(trimmed).extractToolObject(tool)
            else -> null
        }
    }.getOrNull()
    if (parsedJson != null) return parsedJson

    val images = trimmed.extractToolImageAttachments(tool.readableToolImageLabel())
    val visibleText = trimmed.withoutDataImageUrls()
    return ExtractedToolOutput(
        textParts = listOfNotNull(visibleText.takeIf { it.isNotBlank() }),
        imageAttachments = images,
        sourceLinks = visibleText.extractSourceLinks(),
    )
}

private fun JSONObject.extractToolObject(tool: String): ExtractedToolOutput {
    val content = optJSONArray("content")
    if (content != null) {
        val fromContent = content.extractToolContent(tool)
        val structured = optJSONObject("structuredContent")
        return if (structured != null) {
            fromContent + structured.extractStructuredToolContent(tool)
        } else {
            fromContent
        }
    }
    return extractStructuredToolContent(tool)
}

private fun JSONObject.extractStructuredToolContent(tool: String): ExtractedToolOutput {
    val textParts = mutableListOf<String>()
    optString("text").takeIf { it.isNotBlank() }?.let { text ->
        textParts += text.withoutDataImageUrls()
    }
    optString("formattedText").takeIf { it.isNotBlank() }?.let { text ->
        textParts += text.withoutDataImageUrls()
    }
    val images = mutableListOf<ChatImageAttachment>()
    images += optString("text").extractToolImageAttachments(tool.readableToolImageLabel())
    images += optString("formattedText").extractToolImageAttachments(tool.readableToolImageLabel())
    val imageBase64 = optString("imageBase64")
        .ifBlank { optString("data") }
        .ifBlank { optString("data_url") }
        .ifBlank { optString("dataUrl") }
        .takeIf { it.isNotBlank() }
    if (imageBase64 != null) {
        imageBase64.toImageAttachment(
            label = tool.readableToolImageLabel(),
            mimeType = optString("mimeType").ifBlank { "image/png" },
        )?.let { images += it }
    }
    optString("assetUrl")
        .ifBlank { optString("imageUrl") }
        .takeIf { it.isNotBlank() }
        ?.toRemoteImageAttachment(tool.readableToolImageLabel())
        ?.let { images += it }
    optJSONArray("images")?.let { imageArray ->
        for (index in 0 until imageArray.length()) {
            val image = imageArray.optJSONObject(index) ?: continue
            image.optString("assetUrl")
                .ifBlank { image.optString("imageUrl") }
                .ifBlank { image.optString("url") }
                .takeIf { it.isNotBlank() }
                ?.toRemoteImageAttachment("${tool.readableToolImageLabel()} ${images.size + 1}")
                ?.let { images += it }
        }
    }
    val sourceText = textParts.joinToString("\n\n")
    return ExtractedToolOutput(
        textParts = textParts,
        imageAttachments = images,
        sourceLinks = sourceText.extractSourceLinks(),
    )
}

private fun JSONArray.extractToolContent(tool: String): ExtractedToolOutput {
    val textParts = mutableListOf<String>()
    val images = mutableListOf<ChatImageAttachment>()
    for (index in 0 until length()) {
        val item = optJSONObject(index) ?: continue
        when (item.optString("type")) {
            "text" -> item.optString("text").takeIf { it.isNotBlank() }?.let { text ->
                images += text.extractToolImageAttachments("${tool.readableToolImageLabel()} ${images.size + 1}")
                text.withoutDataImageUrls().takeIf { it.isNotBlank() }?.let { textParts += it }
            }
            "image" -> {
                val data = item.optString("data")
                    .ifBlank { item.optString("imageBase64") }
                    .ifBlank { item.optString("data_url") }
                val mimeType = item.optString("mimeType")
                    .ifBlank { item.optString("mime_type") }
                    .ifBlank { "image/png" }
                data.toImageAttachment(
                    label = "${tool.readableToolImageLabel()} ${images.size + 1}",
                    mimeType = mimeType,
                )?.let { images += it }
                item.optString("assetUrl")
                    .ifBlank { item.optString("imageUrl") }
                    .ifBlank { item.optString("url") }
                    .takeIf { it.isNotBlank() }
                    ?.toRemoteImageAttachment("${tool.readableToolImageLabel()} ${images.size + 1}")
                    ?.let { images += it }
            }
        }
    }
    val sourceText = textParts.joinToString("\n\n")
    return ExtractedToolOutput(
        textParts = textParts,
        imageAttachments = images,
        sourceLinks = sourceText.extractSourceLinks(),
    )
}

private operator fun ExtractedToolOutput.plus(other: ExtractedToolOutput): ExtractedToolOutput =
    ExtractedToolOutput(
        textParts = textParts + other.textParts,
        imageAttachments = (imageAttachments + other.imageAttachments).distinctBy { it.identityKey() },
        sourceLinks = (sourceLinks + other.sourceLinks).distinctBy { it.url },
    )

private fun String.toImageAttachment(label: String, mimeType: String): ChatImageAttachment? {
    val trimmed = trim()
    if (trimmed.isBlank()) return null
    val dataUrl = if (trimmed.startsWith("data:image/", ignoreCase = true)) {
        trimmed
    } else {
        "data:${mimeType.ifBlank { "image/png" }};base64,$trimmed"
    }
    if (dataUrl.substringAfter(",", missingDelimiterValue = "").isBlank()) return null
    val resolvedMimeType = dataUrl.substringAfter("data:", "")
        .substringBefore(";", "")
        .ifBlank { mimeType.ifBlank { "image/png" } }
    return ChatImageAttachment(
        dataUrl = dataUrl,
        label = label,
        mimeType = resolvedMimeType,
    )
}

private fun String.toRemoteImageAttachment(label: String): ChatImageAttachment? {
    val trimmed = trim().trimEnd('.', ',', ';')
    if (!trimmed.startsWith("http://", ignoreCase = true) && !trimmed.startsWith("https://", ignoreCase = true)) {
        return null
    }
    val mimeType = when (trimmed.substringBefore("?").substringAfterLast(".", "").lowercase(Locale.getDefault())) {
        "jpg", "jpeg" -> "image/jpeg"
        "webp" -> "image/webp"
        else -> "image/png"
    }
    return ChatImageAttachment(
        remoteUrl = trimmed,
        label = label,
        mimeType = mimeType,
    )
}

private fun String.extractToolImageAttachments(labelBase: String): List<ChatImageAttachment> =
    (extractDataImageAttachments(labelBase) + extractRemoteImageAttachments(labelBase))
        .distinctBy { it.identityKey() }

private fun String.extractDataImageAttachments(labelBase: String): List<ChatImageAttachment> =
    DataImageRegex.findAll(this)
        .mapIndexedNotNull { index, match ->
            val dataUrl = match.value.replace(Regex("\\s+"), "")
            val mimeType = dataUrl.substringAfter("data:", "")
                .substringBefore(";")
                .ifBlank { "image/png" }
            dataUrl.toImageAttachment(
                label = if (index == 0) labelBase else "$labelBase ${index + 1}",
                mimeType = mimeType,
            )
        }
        .toList()

private fun String.extractRemoteImageAttachments(labelBase: String): List<ChatImageAttachment> =
    RemoteImageUrlRegex.findAll(this)
        .mapIndexedNotNull { index, match ->
            match.value.toRemoteImageAttachment(
                label = if (index == 0) labelBase else "$labelBase ${index + 1}",
            )
        }
        .toList()

private fun String.withoutDataImageUrls(): String =
    replace(DataImageRegex, "[generated image]")
        .replace(Regex("\\n{3,}"), "\n\n")
        .trim()

private fun String.readableToolImageLabel(): String =
    split("_", "-", ".")
        .filter { it.isNotBlank() }
        .joinToString(" ") { part -> part.replaceFirstChar { it.uppercase(Locale.getDefault()) } }
        .ifBlank { "Generated image" }

private fun String.extractSourceLinks(): List<SourceLink> {
    val links = linkedMapOf<String, SourceLink>()
    var pendingTitle = ""
    lines().forEach { rawLine ->
        val line = rawLine.trim()
        if (line.isBlank()) return@forEach
        when {
            line.startsWith("Title:", ignoreCase = true) ->
                pendingTitle = line.substringAfter(":").trim()
            Regex("^\\d+\\.\\s+.+").matches(line) ->
                pendingTitle = line.substringAfter(".").trim()
            Regex("^Page\\s+\\d+:", RegexOption.IGNORE_CASE).containsMatchIn(line) ->
                pendingTitle = line.substringAfter(":").trim()
            line.startsWith("URL:", ignoreCase = true) -> {
                val url = line.substringAfter(":").trim().cleanSourceUrl()
                if (url.startsWith("http")) {
                    links[url] = SourceLink(
                        title = pendingTitle.ifBlank { url.toSourceTitle() },
                        url = url,
                    )
                }
            }
        }
        MarkdownLinkRegex.findAll(line).forEach { match ->
            val title = match.groupValues.getOrNull(1).orEmpty().trim()
            val url = match.groupValues.getOrNull(2).orEmpty().cleanSourceUrl()
            if (url.startsWith("http")) {
                links[url] = SourceLink(title = title.ifBlank { url.toSourceTitle() }, url = url)
            }
        }
        UrlRegex.findAll(line).forEach { match ->
            val url = match.value.cleanSourceUrl()
            if (url.startsWith("http") && url !in links) {
                links[url] = SourceLink(title = pendingTitle.ifBlank { url.toSourceTitle() }, url = url)
            }
        }
    }
    return links.values.toList()
}

private val MarkdownLinkRegex = Regex("\\[([^\\]]+)]\\((https?://[^)\\s]+)\\)")
private val UrlRegex = Regex("https?://[^\\s)\\]>\"']+")
private val DataImageRegex = Regex("data:image/(?:png|jpe?g|webp);base64,[A-Za-z0-9+/=]+")
private val RemoteImageUrlRegex = Regex("https?://[^\\s)\\]>\"']+\\.(?:png|jpe?g|webp)(?:\\?[^\\s)\\]>\"']*)?", RegexOption.IGNORE_CASE)
private val LmStudioImagePlaceholderRegex = Regex("<image-[^>]+\\.(?:png|jpe?g|webp)>", RegexOption.IGNORE_CASE)

private fun String.cleanSourceUrl(): String =
    trim().trimEnd('.', ',', ';', ':')

private fun String.toSourceTitle(): String =
    runCatching { Uri.parse(this).host.orEmpty().removePrefix("www.") }
        .getOrDefault("")
        .ifBlank { this }

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

private fun String.normalizedVoiceOutputEngine(): String =
    when (trim().lowercase(Locale.ROOT)) {
        VoiceOutputEngineSupertonic -> VoiceOutputEngineSupertonic
        else -> VoiceOutputEngineSystem
    }

private fun String.voiceOutputEnginePackageName(): String? =
    when (normalizedVoiceOutputEngine()) {
        VoiceOutputEngineSupertonic -> SupertonicTtsEnginePackage
        else -> null
    }

private fun String.voiceOutputEngineLabel(): String =
    when (normalizedVoiceOutputEngine()) {
        VoiceOutputEngineSupertonic -> "Supertonic"
        else -> "System"
    }

private fun String.normalizedVoiceOutputLanguage(): String {
    val raw = trim()
    val normalized = raw.lowercase(Locale.ROOT).replace("-", "_")
    return when (normalized) {
        VoiceOutputLanguageEngineDefault,
        "default",
        "engine",
        "engine_default",
        "tts_default" -> VoiceOutputLanguageEngineDefault
        VoiceOutputLanguageDevice,
        "device_language",
        "phone",
        "locale" -> VoiceOutputLanguageDevice
        VoiceOutputLanguageLithuanian,
        "lt_lt",
        "lit",
        "lithuanian" -> VoiceOutputLanguageLithuanian
        else -> {
            val byTag = VoiceOutputLanguageOptions.firstOrNull { option ->
                option.tag.lowercase(Locale.ROOT).replace("-", "_") == normalized ||
                    option.label.lowercase(Locale.ROOT).replace(" ", "_") == normalized
            }
            val byLanguage = VoiceOutputLanguageOptions.firstOrNull { option ->
                option.tag !in listOf(VoiceOutputLanguageEngineDefault, VoiceOutputLanguageDevice) &&
                    Locale.forLanguageTag(option.tag).language.equals(normalized, ignoreCase = true)
            }
            byTag?.tag ?: byLanguage?.tag ?: VoiceOutputLanguageEngineDefault
        }
    }
}

private fun String.voiceOutputLanguageLabel(): String =
    VoiceOutputLanguageOptions
        .firstOrNull { it.tag == normalizedVoiceOutputLanguage() }
        ?.label
        ?: Locale.forLanguageTag(normalizedVoiceOutputLanguage()).getDisplayName(Locale.getDefault()).ifBlank {
            normalizedVoiceOutputLanguage()
        }

private fun String.voiceOutputLocale(): Locale? =
    when (normalizedVoiceOutputLanguage()) {
        VoiceOutputLanguageDevice -> Locale.getDefault()
        VoiceOutputLanguageEngineDefault -> null
        else -> Locale.forLanguageTag(normalizedVoiceOutputLanguage())
    }

private fun Float.normalizedVoiceOutputSpeed(): Float =
    if (isFinite()) coerceIn(0.5f, 2.0f) else 1f

private fun Float.voiceOutputSpeedLabel(): String =
    "${String.format(Locale.US, "%.2f", normalizedVoiceOutputSpeed())}x"

private fun Int.normalizedVoiceOutputQuality(): Int =
    coerceIn(1, 10)

private fun Int.voiceOutputQualityLabel(): String =
    "${normalizedVoiceOutputQuality()} steps"

private fun Int.normalizedVoiceOutputChunkSize(): Int {
    val rounded = (this.toFloat() / VoiceOutputChunkStepChars).roundToInt() * VoiceOutputChunkStepChars
    return rounded.coerceIn(VoiceOutputMinChunkChars, VoiceOutputMaxChunkChars)
}

private fun Int.voiceOutputChunkSizeLabel(): String =
    "${normalizedVoiceOutputChunkSize()} chars"

private fun Int.normalizedVoiceOutputStartBuffer(): Int =
    coerceIn(VoiceOutputMinStartBufferChunks, VoiceOutputMaxStartBufferChunks)

private fun Int.voiceOutputStartBufferLabel(): String =
    "${normalizedVoiceOutputStartBuffer()} chunks"

private fun Voice.toVoiceOutputVoiceOption(): VoiceOutputVoiceOption {
    val languageTag = locale.toLanguageTag()
    val speaker = name.substringAfter("-supertonic-", name)
    val languageLabel = locale.getDisplayName(Locale.getDefault()).ifBlank { languageTag }
    val engineLabel = if (name.contains("-supertonic-", ignoreCase = true)) "Supertonic" else "TTS"
    return VoiceOutputVoiceOption(
        name = name,
        label = "$speaker - $languageLabel ($engineLabel)",
        languageTag = languageTag,
    )
}

private fun TextToSpeech.voiceOutputVoiceOptions(): List<VoiceOutputVoiceOption> =
    runCatching {
        voices
            .orEmpty()
            .map { it.toVoiceOutputVoiceOption() }
            .distinctBy { it.name }
            .sortedWith(compareBy<VoiceOutputVoiceOption> { it.languageTag }.thenBy { it.label })
    }.getOrDefault(emptyList())

private fun String.voiceOutputVoiceLabel(options: List<VoiceOutputVoiceOption>): String =
    if (isBlank()) {
        "Engine default voice"
    } else {
        options.firstOrNull { it.name == this }?.label ?: this
    }

private fun List<VoiceOutputVoiceOption>.filteredForLanguage(language: String): List<VoiceOutputVoiceOption> {
    val locale = language.voiceOutputLocale() ?: return this
    val filtered = filter {
        Locale.forLanguageTag(it.languageTag).language.equals(locale.language, ignoreCase = true)
    }
    return filtered.ifEmpty { this }
}

private fun String.voiceOutputLanguageDescription(): String =
    when (normalizedVoiceOutputLanguage()) {
        VoiceOutputLanguageEngineDefault -> "Uses the selected TTS engine's own voice and language settings."
        VoiceOutputLanguageDevice -> "Uses this phone's current Android language."
        VoiceOutputLanguageLithuanian -> "Forces Lithuanian for LMSMOB read-aloud playback."
        else -> "Forces ${voiceOutputLanguageLabel()} for LMSMOB read-aloud playback."
    }

private fun Context.isPackageInstalled(packageName: String): Boolean =
    runCatching {
        packageManager.getPackageInfo(packageName, 0)
    }.isSuccess

private fun Context.openAndroidTtsSettings() {
    runCatching {
        startActivity(Intent("com.android.settings.TTS_SETTINGS").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }.recoverCatching {
        startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}

private const val ShareDefaultNew = "new"
private const val ShareDefaultTemporary = "temporary"
private const val ShareDefaultExistingPrefix = "existing:"

private fun ShareDestinationSelection.toStoredShareDefault(): String =
    when (kind) {
        ShareDestinationKind.New -> ShareDefaultNew
        ShareDestinationKind.Temporary -> ShareDefaultTemporary
        ShareDestinationKind.Existing -> sessionId
            ?.takeIf { it.isNotBlank() }
            ?.let { "$ShareDefaultExistingPrefix$it" }
            .orEmpty()
    }

private fun String.toShareDestinationSelection(sessions: List<ChatSession>): ShareDestinationSelection? {
    val normalized = trim()
    return when {
        normalized == ShareDefaultNew -> ShareDestinationSelection(ShareDestinationKind.New)
        normalized == ShareDefaultTemporary -> ShareDestinationSelection(ShareDestinationKind.Temporary)
        normalized.startsWith(ShareDefaultExistingPrefix) -> {
            val sessionId = normalized.removePrefix(ShareDefaultExistingPrefix)
            if (sessions.any { it.id == sessionId && !it.isTemporary }) {
                ShareDestinationSelection(ShareDestinationKind.Existing, sessionId)
            } else {
                null
            }
        }
        else -> null
    }
}

private fun ShareDestinationSelection.label(sessions: List<ChatSession>): String =
    when (kind) {
        ShareDestinationKind.New -> "a new chat"
        ShareDestinationKind.Temporary -> "a temporary chat"
        ShareDestinationKind.Existing -> {
            val title = sessions.firstOrNull { it.id == sessionId }?.title.orEmpty()
            if (title.isBlank()) "the selected existing chat" else "\"$title\""
        }
    }

private fun String.shareDefaultDestinationLabel(sessions: List<ChatSession>): String =
    toShareDestinationSelection(sessions)?.label(sessions) ?: "Not set"

@Composable
private fun ShareDestinationDialog(
    state: ChatUiState,
    content: PreparedShareContent,
    onDismiss: () -> Unit,
    onDestinationSelected: (ShareDestinationSelection) -> Unit,
    onSetDefaultDestination: (ShareDestinationSelection) -> Unit,
) {
    var showExisting by remember { mutableStateOf(false) }
    var setDefaultSelection by remember { mutableStateOf(false) }
    var pendingDefaultDestination by remember { mutableStateOf<ShareDestinationSelection?>(null) }
    val sessions = remember(state.sessions) {
        state.sessions.filterNot { it.isTemporary }
    }
    val summary = remember(content) {
        buildList {
            if (content.text.isNotBlank()) add("text")
            if (content.imageAttachments.isNotEmpty()) add("${content.imageAttachments.size} image(s)")
            if (content.documentUri != null) add("document")
        }.joinToString(", ").ifBlank { "shared content" }
    }
    fun selectDestination(destination: ShareDestinationSelection) {
        if (setDefaultSelection) {
            pendingDefaultDestination = destination
        } else {
            onDestinationSelected(destination)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add shared content") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Choose where to place this $summary.")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Set default selection",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = "Use the selected destination automatically for future shares.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(
                        checked = setDefaultSelection,
                        onCheckedChange = { setDefaultSelection = it },
                    )
                }
                if (showExisting) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(sessions, key = { it.id }) { session ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectDestination(
                                            ShareDestinationSelection(
                                                kind = ShareDestinationKind.Existing,
                                                sessionId = session.id,
                                            ),
                                        )
                                    },
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    Text(
                                        text = session.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    Text(
                                        text = "${session.messages.count { it.role == MessageRole.User }} messages",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { selectDestination(ShareDestinationSelection(ShareDestinationKind.New)) }) {
                Text("New")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = { selectDestination(ShareDestinationSelection(ShareDestinationKind.Temporary)) }) {
                    Text("Temporary")
                }
                TextButton(
                    onClick = {
                        if (sessions.isEmpty()) {
                            selectDestination(ShareDestinationSelection(ShareDestinationKind.New))
                        } else {
                            showExisting = !showExisting
                        }
                    },
                ) {
                    Text(if (showExisting) "Hide chats" else "Existing")
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        },
    )

    val defaultDestination = pendingDefaultDestination
    if (defaultDestination != null) {
        AlertDialog(
            onDismissRequest = { pendingDefaultDestination = null },
            title = { Text("Set default share destination?") },
            text = {
                Text(
                    "Future shared content will always go to ${defaultDestination.label(sessions)} without asking. You can turn this off in Settings.",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onSetDefaultDestination(defaultDestination)
                        pendingDefaultDestination = null
                        onDestinationSelected(defaultDestination)
                    },
                ) {
                    Text("Set default")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDefaultDestination = null }) {
                    Text("Cancel")
                }
            },
        )
    }
}

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
                    text = "Phone-side actions require your confirmation. Draft actions still leave final Send, Call, Save, or Navigate to you.",
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

private enum class UtilityTab {
    Settings,
    WatchJobs,
    Info,
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
    onTemporaryChat: () -> Unit,
    onSelectChat: (String) -> Unit,
    onChatSearchChange: (String) -> Unit,
    onSelectChatFolder: (String) -> Unit,
    onCreateChatFolder: (String) -> Unit,
    onDeleteChatFolder: (String) -> Unit,
    onMoveChatToFolder: (String, String?) -> Unit,
    onEditMessage: (String) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onDeleteChat: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onCloseSettings: () -> Unit,
    onOpenInfo: () -> Unit,
    onCloseInfo: () -> Unit,
    onOpenWatchJobs: () -> Unit,
    onCloseWatchJobs: () -> Unit,
    onApiUrlChange: (String) -> Unit,
    onModelChange: (String) -> Unit,
    onApiTokenChange: (String) -> Unit,
    onServerToolsEnabledChange: (Boolean) -> Unit,
    onServerIntegrationsChange: (String) -> Unit,
    onAllowedToolsChange: (String) -> Unit,
    onNativeIntentToolEnabledChange: (Boolean) -> Unit,
    onCalendarToolEnabledChange: (Boolean) -> Unit,
    onAlarmToolEnabledChange: (Boolean) -> Unit,
    onContactsToolEnabledChange: (Boolean) -> Unit,
    onNotificationDigestToolEnabledChange: (Boolean) -> Unit,
    onLocalFileSearchToolEnabledChange: (Boolean) -> Unit,
    onPickLocalSearchFolder: () -> Unit,
    onDeviceStatusToolEnabledChange: (Boolean) -> Unit,
    onWatchJobToolEnabledChange: (Boolean) -> Unit,
    onVoiceInputEnabledChange: (Boolean) -> Unit,
    onVoiceOutputEnabledChange: (Boolean) -> Unit,
    onVoiceOutputEngineChange: (String) -> Unit,
    onVoiceOutputLanguageChange: (String) -> Unit,
    onVoiceOutputVoiceChange: (String) -> Unit,
    onVoiceOutputSpeedChange: (Float) -> Unit,
    onVoiceOutputQualityChange: (Int) -> Unit,
    onVoiceOutputChunkSizeChange: (Int) -> Unit,
    onVoiceOutputStartBufferChange: (Int) -> Unit,
    voiceOutputVoiceOptions: List<VoiceOutputVoiceOption>,
    onAutoReadAnswersEnabledChange: (Boolean) -> Unit,
    onAppendCapabilityGuideToSystemPromptChange: (Boolean) -> Unit,
    onAppendDateTimeToSystemPromptChange: (Boolean) -> Unit,
    onShareDefaultSelectionEnabledChange: (Boolean) -> Unit,
    onCreateWatchJob: (String, String, String, String, String, String, String, String, String, String, String) -> Unit,
    onUpdateWatchJob: (String, String, String, String, String, String, String, String, String, String, String, String, Boolean) -> Unit,
    onToggleWatchJob: (String, Boolean) -> Unit,
    onDeleteWatchJob: (String) -> Unit,
    onSystemProfileSelect: (String) -> Unit,
    onSystemProfileNameChange: (String) -> Unit,
    onSystemPromptChange: (String) -> Unit,
    onChatAppendPresetApply: (String) -> Unit,
    onChatAppendPresetSelect: (String) -> Unit,
    onChatAppendPresetNameChange: (String) -> Unit,
    onChatAppendPresetTextChange: (String) -> Unit,
    onChatAppendPresetSave: () -> Unit,
    onChatAppendPresetCreate: () -> Unit,
    onChatAppendPresetDelete: (String) -> Unit,
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
    onPauseSpeaking: () -> Unit,
    onResumeSpeaking: () -> Unit,
    speakingMessageId: String?,
    ttsPaused: Boolean,
    ttsPlaybackState: TtsPlaybackState?,
    onClearImage: () -> Unit,
    onRefreshModels: () -> Unit,
    onExportChats: () -> Unit,
    onImportChats: () -> Unit,
    onDismissError: () -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    fun showSettingsTab() {
        onCloseWatchJobs()
        onCloseInfo()
        onOpenSettings()
    }
    fun showWatchJobsTab() {
        onCloseSettings()
        onCloseInfo()
        onOpenWatchJobs()
    }
    fun showInfoTab() {
        onCloseSettings()
        onCloseWatchJobs()
        onOpenInfo()
    }
    fun showUtilityTab(tab: UtilityTab) {
        when (tab) {
            UtilityTab.Settings -> showSettingsTab()
            UtilityTab.WatchJobs -> showWatchJobsTab()
            UtilityTab.Info -> showInfoTab()
        }
    }

    if (state.isSettingsOpen) {
        SettingsSheet(
            state = state,
            onDismiss = onCloseSettings,
            selectedUtilityTab = UtilityTab.Settings,
            onUtilityTabSelected = ::showUtilityTab,
            onApiUrlChange = onApiUrlChange,
            onModelChange = onModelChange,
            onApiTokenChange = onApiTokenChange,
            onServerToolsEnabledChange = onServerToolsEnabledChange,
            onServerIntegrationsChange = onServerIntegrationsChange,
            onAllowedToolsChange = onAllowedToolsChange,
            onNativeIntentToolEnabledChange = onNativeIntentToolEnabledChange,
            onCalendarToolEnabledChange = onCalendarToolEnabledChange,
            onAlarmToolEnabledChange = onAlarmToolEnabledChange,
            onContactsToolEnabledChange = onContactsToolEnabledChange,
            onNotificationDigestToolEnabledChange = onNotificationDigestToolEnabledChange,
            onLocalFileSearchToolEnabledChange = onLocalFileSearchToolEnabledChange,
            onPickLocalSearchFolder = onPickLocalSearchFolder,
            onDeviceStatusToolEnabledChange = onDeviceStatusToolEnabledChange,
            onWatchJobToolEnabledChange = onWatchJobToolEnabledChange,
            onVoiceInputEnabledChange = onVoiceInputEnabledChange,
            onVoiceOutputEnabledChange = onVoiceOutputEnabledChange,
            onVoiceOutputEngineChange = onVoiceOutputEngineChange,
            onVoiceOutputLanguageChange = onVoiceOutputLanguageChange,
            onVoiceOutputVoiceChange = onVoiceOutputVoiceChange,
            onVoiceOutputSpeedChange = onVoiceOutputSpeedChange,
            onVoiceOutputQualityChange = onVoiceOutputQualityChange,
            onVoiceOutputChunkSizeChange = onVoiceOutputChunkSizeChange,
            onVoiceOutputStartBufferChange = onVoiceOutputStartBufferChange,
            voiceOutputVoiceOptions = voiceOutputVoiceOptions,
            onAutoReadAnswersEnabledChange = onAutoReadAnswersEnabledChange,
            onAppendCapabilityGuideToSystemPromptChange = onAppendCapabilityGuideToSystemPromptChange,
            onAppendDateTimeToSystemPromptChange = onAppendDateTimeToSystemPromptChange,
            onShareDefaultSelectionEnabledChange = onShareDefaultSelectionEnabledChange,
            onSystemProfileSelect = onSystemProfileSelect,
            onSystemProfileNameChange = onSystemProfileNameChange,
            onSystemPromptChange = onSystemPromptChange,
            onChatAppendPresetSelect = onChatAppendPresetSelect,
            onChatAppendPresetNameChange = onChatAppendPresetNameChange,
            onChatAppendPresetTextChange = onChatAppendPresetTextChange,
            onChatAppendPresetSave = onChatAppendPresetSave,
            onChatAppendPresetCreate = onChatAppendPresetCreate,
            onChatAppendPresetDelete = onChatAppendPresetDelete,
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
        InfoSheet(
            onDismiss = onCloseInfo,
            selectedUtilityTab = UtilityTab.Info,
            onUtilityTabSelected = ::showUtilityTab,
        )
    }
    if (state.isWatchJobsOpen) {
        WatchJobsSheet(
            state = state,
            onDismiss = onCloseWatchJobs,
            selectedUtilityTab = UtilityTab.WatchJobs,
            onUtilityTabSelected = ::showUtilityTab,
            onCreateWatchJob = onCreateWatchJob,
            onToggleWatchJob = onToggleWatchJob,
            onDeleteWatchJob = onDeleteWatchJob,
            onUpdateWatchJob = onUpdateWatchJob,
        )
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
                onTemporaryChat = {
                    onTemporaryChat()
                    scope.launch { drawerState.close() }
                },
                onSelectChat = { sessionId ->
                    onSelectChat(sessionId)
                    scope.launch { drawerState.close() }
                },
                onChatSearchChange = onChatSearchChange,
                onSelectChatFolder = onSelectChatFolder,
                onCreateChatFolder = onCreateChatFolder,
                onDeleteChatFolder = onDeleteChatFolder,
                onMoveChatToFolder = onMoveChatToFolder,
                onDeleteChat = onDeleteChat,
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
                                    if (state.sessions.firstOrNull { it.id == state.activeSessionId }?.isTemporary == true) {
                                        append(" - TEMP")
                                    }
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
                        IconButton(onClick = { showSettingsTab() }) {
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
                    onChatAppendPresetApply = onChatAppendPresetApply,
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
                onPauseSpeaking = onPauseSpeaking,
                onResumeSpeaking = onResumeSpeaking,
                speakingMessageId = speakingMessageId,
                ttsPaused = ttsPaused,
                ttsPlaybackState = ttsPlaybackState,
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

@Composable
private fun UtilityTabs(
    selected: UtilityTab,
    onSelected: (UtilityTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = selected == UtilityTab.Settings,
            onClick = { onSelected(UtilityTab.Settings) },
            label = { Text("Settings") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            },
        )
        FilterChip(
            selected = selected == UtilityTab.WatchJobs,
            onClick = { onSelected(UtilityTab.WatchJobs) },
            label = { Text("Watch jobs") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            },
        )
        FilterChip(
            selected = selected == UtilityTab.Info,
            onClick = { onSelected(UtilityTab.Info) },
            label = { Text("Info & license") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            },
        )
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
            attachmentTotal + if (attachment.hasDisplayableImage()) 1100 else 64
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
    onTemporaryChat: () -> Unit,
    onSelectChat: (String) -> Unit,
    onChatSearchChange: (String) -> Unit,
    onSelectChatFolder: (String) -> Unit,
    onCreateChatFolder: (String) -> Unit,
    onDeleteChatFolder: (String) -> Unit,
    onMoveChatToFolder: (String, String?) -> Unit,
    onDeleteChat: (String) -> Unit,
) {
    val query = state.chatSearchQuery.trim()
    var pendingDeleteSession by remember { mutableStateOf<ChatSession?>(null) }
    var isCreateFolderOpen by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }
    var pendingDeleteFolder by remember { mutableStateOf<ChatFolder?>(null) }
    val activeFolder = state.chatFolders.firstOrNull { it.id == state.activeChatFolderId }
    val folderFilteredSessions = state.sessions.filter { it.matchesChatFolder(state.activeChatFolderId) }
    val filteredSessions = folderFilteredSessions.filter { session ->
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = onNewChat,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp),
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New", maxLines = 1)
                }
                OutlinedButton(
                    onClick = onTemporaryChat,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp),
                ) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Temp", maxLines = 1)
                }
                OutlinedButton(
                    onClick = { isCreateFolderOpen = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp),
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Folder", maxLines = 1)
                }
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

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(end = 4.dp),
            ) {
                item {
                    ChatFolderChip(
                        label = "All",
                        count = state.sessions.size,
                        selected = state.activeChatFolderId == AllChatsFolderId,
                        onClick = { onSelectChatFolder(AllChatsFolderId) },
                    )
                }
                item {
                    ChatFolderChip(
                        label = "Unfiled",
                        count = state.sessions.count { it.folderId.isNullOrBlank() },
                        selected = state.activeChatFolderId == UnfiledChatsFolderId,
                        onClick = { onSelectChatFolder(UnfiledChatsFolderId) },
                    )
                }
                items(state.chatFolders, key = { it.id }) { folder ->
                    ChatFolderChip(
                        label = folder.name,
                        count = state.sessions.count { it.folderId == folder.id },
                        selected = state.activeChatFolderId == folder.id,
                        onClick = { onSelectChatFolder(folder.id) },
                    )
                }
            }

            if (activeFolder != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = activeFolder.name,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = { pendingDeleteFolder = activeFolder }) {
                        Text("Delete folder")
                    }
                }
            }

            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.45f))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(filteredSessions, key = { it.id }) { session ->
                    val selected = session.id == state.activeSessionId
                    var moveMenuExpanded by remember(session.id) { mutableStateOf(false) }
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
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(
                                        text = session.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f),
                                    )
                                    if (session.isTemporary) {
                                        TemporaryChatBadge()
                                    }
                                }
                            },
                            supportingContent = {
                                val messageCount = session.messages.count { it.role == MessageRole.User }
                                val folderSuffix = if (session.isTemporary) {
                                    " - not saved"
                                } else if (state.activeChatFolderId == AllChatsFolderId) {
                                    " - ${session.folderName(state.chatFolders)}"
                                } else {
                                    ""
                                }
                                Text(
                                    text = "$messageCount messages$folderSuffix",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            leadingContent = { AppMark(modifier = Modifier.size(28.dp)) },
                            trailingContent = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box {
                                        IconButton(
                                            onClick = { moveMenuExpanded = true },
                                            modifier = Modifier.size(34.dp),
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.MoreVert,
                                                contentDescription = "Move chat",
                                                modifier = Modifier.size(18.dp),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                        DropdownMenu(
                                            expanded = moveMenuExpanded,
                                            onDismissRequest = { moveMenuExpanded = false },
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Move to Unfiled") },
                                                onClick = {
                                                    onMoveChatToFolder(session.id, null)
                                                    moveMenuExpanded = false
                                                },
                                            )
                                            state.chatFolders.forEach { folder ->
                                                DropdownMenuItem(
                                                    text = { Text("Move to ${folder.name}") },
                                                    onClick = {
                                                        onMoveChatToFolder(session.id, folder.id)
                                                        moveMenuExpanded = false
                                                    },
                                                )
                                            }
                                        }
                                    }
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
                                }
                            },
                        )
                    }
                }
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

    if (isCreateFolderOpen) {
        AlertDialog(
            onDismissRequest = {
                isCreateFolderOpen = false
                newFolderName = ""
            },
            title = { Text("New folder") },
            text = {
                OutlinedTextField(
                    value = newFolderName,
                    onValueChange = { newFolderName = it },
                    singleLine = true,
                    label = { Text("Folder name") },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCreateChatFolder(newFolderName)
                        isCreateFolderOpen = false
                        newFolderName = ""
                    },
                    enabled = newFolderName.isNotBlank(),
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        isCreateFolderOpen = false
                        newFolderName = ""
                    },
                ) {
                    Text("Cancel")
                }
            },
        )
    }

    val folderToDelete = pendingDeleteFolder
    if (folderToDelete != null) {
        val chatCount = state.sessions.count { it.folderId == folderToDelete.id }
        AlertDialog(
            onDismissRequest = { pendingDeleteFolder = null },
            title = { Text("Delete folder?") },
            text = {
                Text(
                    "This removes \"${folderToDelete.name}\". $chatCount chats will stay on this device and move to Unfiled.",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteChatFolder(folderToDelete.id)
                        pendingDeleteFolder = null
                    },
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteFolder = null }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun ChatFolderChip(
    label: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = "$label ($count)",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
    )
}

@Composable
private fun TemporaryChatBadge(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(50.dp),
        modifier = modifier,
    ) {
        Text(
            text = "TEMP",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
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
    onPauseSpeaking: () -> Unit,
    onResumeSpeaking: () -> Unit,
    speakingMessageId: String?,
    ttsPaused: Boolean,
    ttsPlaybackState: TtsPlaybackState?,
) {
    val listState = rememberLazyListState()
    var pendingDeleteMessage by remember { mutableStateOf<ChatMessage?>(null) }
    val isTemporaryChat = state.sessions.firstOrNull { it.id == state.activeSessionId }?.isTemporary == true

    LaunchedEffect(
        state.messages.size,
        state.messages.lastOrNull()?.content?.length,
        state.isSending,
        isTemporaryChat,
    ) {
        if (state.messages.isNotEmpty() || state.isSending) {
            val leadingItems = if (isTemporaryChat) 1 else 0
            val lastVisibleItem = if (state.isSending) {
                state.messages.size
            } else {
                state.messages.lastIndex
            }
            listState.animateScrollToItem((leadingItems + lastVisibleItem).coerceAtLeast(0))
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
        if (isTemporaryChat) {
            item(key = "temporary-chat-notice") {
                TemporaryChatNotice()
            }
        }

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
                onPauseSpeaking = onPauseSpeaking,
                onResumeSpeaking = onResumeSpeaking,
                speakingMessageId = speakingMessageId,
                ttsPaused = ttsPaused,
                ttsPlaybackState = ttsPlaybackState?.takeIf { it.messageId == message.id },
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
private fun TemporaryChatNotice() {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.78f),
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Temporary chat",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "This chat is not saved to chat history.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f),
                )
            }
        }
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
    onPauseSpeaking: () -> Unit,
    onResumeSpeaking: () -> Unit,
    speakingMessageId: String?,
    ttsPaused: Boolean,
    ttsPlaybackState: TtsPlaybackState?,
) {
    val isUser = message.role == MessageRole.User
    val context = LocalContext.current
    val density = LocalDensity.current
    var messageContentHeight by remember(message.id, message.content) { mutableStateOf(0.dp) }
    var previewAttachment by remember(message.id) { mutableStateOf<ChatImageAttachment?>(null) }

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
                    MessageAttachmentSummary(
                        attachments = message.attachments,
                        onPreview = { previewAttachment = it },
                        inlineSingleImage = false,
                    )
                }
                MessageActions(
                    canEdit = true,
                    canDelete = !message.isStreaming,
                    canSpeak = false,
                    isSpeaking = false,
                    isSpeechPaused = false,
                    onCopy = { context.copyToClipboard(message.content) },
                    onEdit = { onEditMessage(message.id) },
                    onDelete = { onDeleteMessage(message.id) },
                    onSpeak = {},
                    onStopSpeaking = {},
                    onPauseSpeaking = {},
                    onResumeSpeaking = {},
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    val visibleTtsPlaybackState = ttsPlaybackState
                        ?.takeIf { it.chunks.size > 1 }
                    val chunkMarkerHeightModifier = if (messageContentHeight > 0.dp) {
                        Modifier.height(messageContentHeight)
                    } else {
                        Modifier.heightIn(min = 28.dp)
                    }
                    if (visibleTtsPlaybackState != null) {
                        TtsChunkProgressRail(
                            playbackState = visibleTtsPlaybackState,
                            modifier = chunkMarkerHeightModifier
                                .padding(top = 2.dp, bottom = 2.dp),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .onGloballyPositioned { coordinates ->
                                messageContentHeight = with(density) {
                                    coordinates.size.height.toDp()
                                }
                            },
                    ) {
                        SelectionContainer {
                            MessageContent(
                                content = message.content,
                                isStreaming = message.isStreaming,
                            )
                        }
                    }
                    if (visibleTtsPlaybackState != null) {
                        TtsActiveChunkMarker(
                            playbackState = visibleTtsPlaybackState,
                            modifier = chunkMarkerHeightModifier
                                .padding(top = 2.dp, bottom = 2.dp),
                        )
                    }
                }
                if (message.attachments.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    MessageAttachmentSummary(
                        attachments = message.attachments,
                        onPreview = { previewAttachment = it },
                        inlineSingleImage = true,
                    )
                }
                MessageActions(
                    canEdit = false,
                    canDelete = !message.isStreaming,
                    canSpeak = voiceOutputEnabled &&
                        !message.isStreaming &&
                        message.content.speakableAnswerText().isNotBlank(),
                    isSpeaking = speakingMessageId == message.id,
                    isSpeechPaused = ttsPaused && speakingMessageId == message.id,
                    onCopy = { context.copyToClipboard(message.content) },
                    onEdit = {},
                    onDelete = { onDeleteMessage(message.id) },
                    onSpeak = { onSpeakMessage(message.id, message.content) },
                    onStopSpeaking = onStopSpeaking,
                    onPauseSpeaking = onPauseSpeaking,
                    onResumeSpeaking = onResumeSpeaking,
                )
            }
        }
    }

    previewAttachment?.let { attachment ->
        ImagePreviewDialog(
            attachment = attachment,
            onDismiss = { previewAttachment = null },
        )
    }
}

@Composable
private fun TtsChunkProgressRail(
    playbackState: TtsPlaybackState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(5.dp)
            .heightIn(min = 28.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        playbackState.chunks.forEach { chunk ->
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .weight(chunk.railWeight)
                    .clip(RoundedCornerShape(50.dp))
                    .background(chunk.status.chunkStatusColor()),
            )
        }
    }
}

@Composable
private fun TtsActiveChunkMarker(
    playbackState: TtsPlaybackState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(4.dp)
            .heightIn(min = 28.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        playbackState.chunks.forEach { chunk ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .weight(chunk.railWeight)
                    .clip(RoundedCornerShape(50.dp))
                    .background(
                        if (chunk.status == TtsChunkStatus.Playing) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        },
                    ),
            )
        }
    }
}

@Composable
private fun TtsChunkStatus.chunkStatusColor(): Color =
    when (this) {
        TtsChunkStatus.Pending -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)
        TtsChunkStatus.Preparing -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.62f)
        TtsChunkStatus.Ready -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.78f)
        TtsChunkStatus.Queued -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.58f)
        TtsChunkStatus.Playing -> MaterialTheme.colorScheme.primary
        TtsChunkStatus.Complete -> Color(0xFF4CCB7F)
        TtsChunkStatus.Error -> MaterialTheme.colorScheme.error
    }

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MessageAttachmentSummary(
    attachments: List<ChatImageAttachment>,
    onPreview: (ChatImageAttachment) -> Unit,
    inlineSingleImage: Boolean = false,
) {
    val singleImage = attachments.singleOrNull()?.takeIf {
        inlineSingleImage && it.hasDisplayableImage()
    }
    if (singleImage != null) {
        InlineMessageImage(
            attachment = singleImage,
            onPreview = onPreview,
        )
        return
    }

    FlowRow(
        modifier = Modifier.widthIn(max = 310.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        attachments.forEach { attachment ->
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .then(
                        if (attachment.hasDisplayableImage()) {
                            Modifier.clickable { onPreview(attachment) }
                        } else {
                            Modifier
                        },
                    ),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = if (!attachment.hasDisplayableImage()) {
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

@Composable
private fun InlineMessageImage(
    attachment: ChatImageAttachment,
    onPreview: (ChatImageAttachment) -> Unit,
) {
    val bitmap by rememberAttachmentBitmap(attachment)
    val currentBitmap = bitmap
    if (currentBitmap == null) {
        MessageAttachmentSummary(
            attachments = listOf(attachment),
            onPreview = onPreview,
            inlineSingleImage = false,
        )
        return
    }

    Surface(
        modifier = Modifier
            .widthIn(max = 310.dp)
            .clickable { onPreview(attachment) },
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        tonalElevation = 1.dp,
    ) {
        Box {
            Image(
                bitmap = currentBitmap.asImageBitmap(),
                contentDescription = attachment.label,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp, max = 360.dp)
                    .clip(RoundedCornerShape(18.dp)),
                contentScale = ContentScale.Fit,
            )
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                shape = RoundedCornerShape(50.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddPhotoAlternate,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
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

@Composable
private fun ImagePreviewDialog(
    attachment: ChatImageAttachment,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val bitmap by rememberAttachmentBitmap(attachment)
    val currentBitmap = bitmap
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = attachment.label,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close image preview",
                        )
                    }
                }
                if (currentBitmap != null) {
                    ScrollableImagePreview(
                        bitmap = currentBitmap,
                        label = attachment.label,
                    )
                    Button(
                        onClick = {
                            scope.launch {
                                val saveResult = withContext(Dispatchers.IO) {
                                    context.saveImageAttachmentToDownloads(attachment)
                                }
                                saveResult
                                    .onSuccess {
                                        Toast.makeText(context, "Image saved to Downloads", Toast.LENGTH_SHORT).show()
                                    }
                                    .onFailure { throwable ->
                                        Toast.makeText(
                                            context,
                                            throwable.friendlyMessage(),
                                            Toast.LENGTH_LONG,
                                        ).show()
                                    }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Download image")
                    }
                } else {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AddPhotoAlternate,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                            )
                            Text(
                                text = "Image preview is not available for this attachment.",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScrollableImagePreview(
    bitmap: Bitmap,
    label: String,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val imageHeight = (maxWidth * bitmap.height.toFloat() / bitmap.width.toFloat())
            .coerceAtLeast(160.dp)
        val viewportHeight = imageHeight.coerceIn(180.dp, 560.dp)
        val verticalScroll = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(viewportHeight)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                .verticalScroll(verticalScroll),
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = label,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight),
                contentScale = ContentScale.FillBounds,
            )
        }
    }
}

@Composable
private fun rememberAttachmentBitmap(attachment: ChatImageAttachment) =
    produceState<Bitmap?>(initialValue = null, attachment.dataUrl, attachment.remoteUrl) {
        value = withContext(Dispatchers.IO) {
            attachment.decodeImageBitmap()
        }
    }

private val ImageAttachmentHttpClient: OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build()

private fun ChatImageAttachment.decodeImageBitmap(): Bitmap? {
    val bytes = decodeImageBytes() ?: return null
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

private fun ChatImageAttachment.decodeImageBytes(): ByteArray? {
    if (dataUrl.isNotBlank()) {
        val encoded = dataUrl.substringAfter(",", missingDelimiterValue = dataUrl)
        return runCatching {
            Base64.decode(encoded, Base64.DEFAULT)
        }.getOrNull()
    }

    if (remoteUrl.isBlank()) return null
    return runCatching {
        val request = Request.Builder().url(remoteUrl).build()
        ImageAttachmentHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Image download failed with HTTP ${response.code}")
            }
            val body = response.body ?: throw IOException("Image response was empty.")
            val contentLength = body.contentLength()
            if (contentLength > 25_000_000L) {
                throw IOException("Image is too large to download.")
            }
            body.bytes()
        }
    }.getOrNull()
}

private fun Context.saveImageAttachmentToDownloads(attachment: ChatImageAttachment): Result<Uri> =
    runCatching {
        val bytes = attachment.decodeImageBytes()
            ?: throw IOException("Image data is not available.")
        val extension = when (attachment.mimeType.lowercase(Locale.getDefault())) {
            "image/jpeg", "image/jpg" -> "jpg"
            "image/webp" -> "webp"
            else -> "png"
        }
        val displayName = "${attachment.label.safeFileBaseName()}-${System.currentTimeMillis()}.$extension"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, attachment.mimeType.ifBlank { "image/png" })
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    "${Environment.DIRECTORY_DOWNLOADS}/LMSMOB Chat",
                )
            }
            val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: throw IOException("Could not create download file.")
            contentResolver.openOutputStream(uri)?.use { output -> output.write(bytes) }
                ?: throw IOException("Could not write download file.")
            uri
        } else {
            @Suppress("DEPRECATION")
            val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "LMSMOB Chat")
            if (!directory.exists() && !directory.mkdirs()) {
                throw IOException("Could not create Downloads folder.")
            }
            val file = File(directory, displayName)
            file.writeBytes(bytes)
            Uri.fromFile(file)
        }
    }

private fun Context.saveMarkdownTableToDownloads(table: MarkdownTable): Result<Uri> =
    runCatching {
        val bytes = table.toXlsxBytes()
        val displayName = "chat-table-${System.currentTimeMillis()}.xlsx"
        val mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    "${Environment.DIRECTORY_DOWNLOADS}/LMSMOB Chat",
                )
            }
            val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: throw IOException("Could not create download file.")
            contentResolver.openOutputStream(uri)?.use { output -> output.write(bytes) }
                ?: throw IOException("Could not write download file.")
            uri
        } else {
            @Suppress("DEPRECATION")
            val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "LMSMOB Chat")
            if (!directory.exists() && !directory.mkdirs()) {
                throw IOException("Could not create Downloads folder.")
            }
            val file = File(directory, displayName)
            file.writeBytes(bytes)
            Uri.fromFile(file)
        }
    }

private fun MarkdownTable.toXlsxBytes(): ByteArray {
    val columnCount = maxOf(
        headers.size,
        rows.maxOfOrNull { it.size } ?: 0,
        1,
    )
    val tableRows = listOf(headers) + rows
    val output = ByteArrayOutputStream()
    ZipOutputStream(output).use { zip ->
        zip.writeTextEntry(
            "[Content_Types].xml",
            """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
              <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
              <Default Extension="xml" ContentType="application/xml"/>
              <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
              <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
              <Override PartName="/docProps/core.xml" ContentType="application/vnd.openxmlformats-package.core-properties+xml"/>
              <Override PartName="/docProps/app.xml" ContentType="application/vnd.openxmlformats-officedocument.extended-properties+xml"/>
            </Types>
            """.trimIndent(),
        )
        zip.writeTextEntry(
            "_rels/.rels",
            """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
              <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
              <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties" Target="docProps/core.xml"/>
              <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties" Target="docProps/app.xml"/>
            </Relationships>
            """.trimIndent(),
        )
        zip.writeTextEntry(
            "xl/workbook.xml",
            """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
              <sheets>
                <sheet name="Table" sheetId="1" r:id="rId1"/>
              </sheets>
            </workbook>
            """.trimIndent(),
        )
        zip.writeTextEntry(
            "xl/_rels/workbook.xml.rels",
            """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
              <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
            </Relationships>
            """.trimIndent(),
        )
        zip.writeTextEntry(
            "docProps/app.xml",
            """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <Properties xmlns="http://schemas.openxmlformats.org/officeDocument/2006/extended-properties" xmlns:vt="http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes">
              <Application>LMSMOB Chat</Application>
            </Properties>
            """.trimIndent(),
        )
        zip.writeTextEntry(
            "docProps/core.xml",
            """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <cp:coreProperties xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
              <dc:creator>LMSMOB Chat</dc:creator>
              <dc:title>Chat table export</dc:title>
              <dcterms:created xsi:type="dcterms:W3CDTF">${Instant.now()}</dcterms:created>
            </cp:coreProperties>
            """.trimIndent(),
        )
        zip.writeTextEntry(
            "xl/worksheets/sheet1.xml",
            buildString {
                appendLine("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
                appendLine("""<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">""")
                appendLine("<sheetData>")
                tableRows.forEachIndexed { rowIndex, row ->
                    val rowNumber = rowIndex + 1
                    append("""<row r="$rowNumber">""")
                    for (column in 0 until columnCount) {
                        val cellRef = "${columnName(column)}$rowNumber"
                        val value = row.getOrElse(column) { "" }
                            .replace(Regex("\\*\\*(.+?)\\*\\*"), "$1")
                            .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
                            .trim()
                        append("""<c r="$cellRef" t="inlineStr"><is><t xml:space="preserve">${value.xmlEscape()}</t></is></c>""")
                    }
                    appendLine("</row>")
                }
                appendLine("</sheetData>")
                appendLine("</worksheet>")
            },
        )
    }
    return output.toByteArray()
}

private fun ZipOutputStream.writeTextEntry(name: String, text: String) {
    putNextEntry(ZipEntry(name))
    write(text.toByteArray(Charsets.UTF_8))
    closeEntry()
}

private fun columnName(index: Int): String {
    var value = index + 1
    val name = StringBuilder()
    while (value > 0) {
        val remainder = (value - 1) % 26
        name.insert(0, ('A'.code + remainder).toChar())
        value = (value - 1) / 26
    }
    return name.toString()
}

private fun String.xmlEscape(): String =
    replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;")

private fun String.safeFileBaseName(): String =
    replace(Regex("[^A-Za-z0-9._-]+"), "-")
        .trim('-', '.', '_')
        .take(48)
        .ifBlank { "lmstudio-image" }

private enum class MessageBlockType {
    Text,
    Tools,
    Sources,
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
    val sourceLines = mutableListOf<String>()
    var collectingSources = false
    content.lines().forEach { line ->
        val trimmed = line.trim()
        when {
            trimmed.startsWith("Tools used:", ignoreCase = true) -> {
                collectingSources = false
                blocks += MessageBlock(MessageBlockType.Tools, trimmed.substringAfter(":").trim())
            }
            trimmed.startsWith("Tool errors:", ignoreCase = true) -> {
                collectingSources = false
                blocks += MessageBlock(MessageBlockType.Error, trimmed.substringAfter(":").trim())
            }
            trimmed.startsWith("Sources:", ignoreCase = true) -> {
                collectingSources = true
                trimmed.substringAfter(":").trim().takeIf { it.isNotBlank() }?.let { sourceLines += it }
            }
            collectingSources && trimmed.isBlank() -> {
                collectingSources = false
            }
            collectingSources -> {
                sourceLines += line
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
    if (sourceLines.isNotEmpty()) {
        blocks += MessageBlock(MessageBlockType.Sources, sourceLines.joinToString("\n").trim())
    }

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

private fun String.toVoiceOutputChunks(targetChars: Int = VoiceOutputDefaultChunkChars): List<String> {
    val normalizedTargetChars = targetChars.normalizedVoiceOutputChunkSize()
    val normalized = replace("\r\n", "\n")
        .replace(Regex("[ \\t]+"), " ")
        .replace(Regex("\\n{3,}"), "\n\n")
        .trim()
    if (normalized.isBlank()) return emptyList()

    val segments = normalized
        .split(Regex("(?<=[.!?…])\\s+|\\n+"))
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .flatMap { it.splitLongVoiceOutputSegment(normalizedTargetChars) }

    val chunks = mutableListOf<String>()
    val current = StringBuilder()
    fun flush() {
        val chunk = current.toString().trim()
        if (chunk.isNotBlank()) chunks += chunk
        current.clear()
    }

    segments.forEach { segment ->
        if (current.isEmpty()) {
            current.append(segment)
        } else if (current.length + 1 + segment.length <= normalizedTargetChars) {
            current.append(' ').append(segment)
        } else {
            flush()
            current.append(segment)
        }
        if (current.length >= normalizedTargetChars) {
            flush()
        }
    }
    flush()

    return chunks
}

private fun String.splitLongVoiceOutputSegment(maxChars: Int): List<String> {
    if (length <= maxChars) return listOf(this)
    val chunks = mutableListOf<String>()
    val current = StringBuilder()
    split(Regex("\\s+")).forEach { word ->
        if (word.length > maxChars) {
            if (current.isNotBlank()) {
                chunks += current.toString().trim()
                current.clear()
            }
            word.chunked(maxChars).forEach { chunks += it }
        } else if (current.isEmpty()) {
            current.append(word)
        } else if (current.length + 1 + word.length <= maxChars) {
            current.append(' ').append(word)
        } else {
            chunks += current.toString().trim()
            current.clear()
            current.append(word)
        }
    }
    if (current.isNotBlank()) chunks += current.toString().trim()
    return chunks
}

private fun String.toTtsChunkPreview(): String =
    replace(Regex("\\s+"), " ")
        .trim()
        .let { value -> if (value.length <= 80) value else "${value.take(77)}..." }

private fun TtsChunkId.toUtteranceId(): String =
    listOf(messageId, runId.toString(), index.toString()).joinToString(TtsChunkUtteranceSeparator)

private fun TtsChunkId.toSynthesisUtteranceId(attempt: Int = 0): String =
    listOf(
        TtsSynthesisUtterancePrefix,
        messageId,
        runId.toString(),
        index.toString(),
        attempt.toString(),
    ).joinToString(TtsChunkUtteranceSeparator)

private fun String.toTtsChunkId(): TtsChunkId? {
    val parts = split(TtsChunkUtteranceSeparator)
    if (parts.size != 3) return null
    val runId = parts[1].toLongOrNull() ?: return null
    val index = parts[2].toIntOrNull() ?: return null
    return TtsChunkId(messageId = parts[0], runId = runId, index = index)
}

private fun String.toSynthesisTtsChunkId(): TtsChunkId? {
    val parts = split(TtsChunkUtteranceSeparator)
    if (parts.size !in 4..5 || parts[0] != TtsSynthesisUtterancePrefix) return null
    val runId = parts[2].toLongOrNull() ?: return null
    val index = parts[3].toIntOrNull() ?: return null
    return TtsChunkId(messageId = parts[1], runId = runId, index = index)
}

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
                MessageBlockType.Sources -> SourcesBlock(block.text)
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

private const val UrlAnnotationTag = "URL"
private val BoldMarkdownRegex = Regex("\\*\\*(.+?)\\*\\*")

private fun markdownAnnotatedString(text: String): AnnotatedString {
    val normalized = text.normalizeInlineRichText()
    return buildAnnotatedString {
        appendMarkdownLinksAndText(normalized)
    }
}

private fun AnnotatedString.Builder.appendMarkdownLinksAndText(text: String) {
    var index = 0
    MarkdownLinkRegex.findAll(text).forEach { match ->
        appendPlainLinksAndBold(text.substring(index, match.range.first))
        val title = match.groupValues.getOrNull(1).orEmpty().ifBlank { match.groupValues.getOrNull(2).orEmpty() }
        val url = match.groupValues.getOrNull(2).orEmpty().cleanSourceUrl()
        if (url.startsWith("http")) {
            pushStringAnnotation(UrlAnnotationTag, url)
            withStyle(
                SpanStyle(
                    color = Color(0xFF4FC3F7),
                    textDecoration = TextDecoration.Underline,
                ),
            ) {
                append(title)
            }
            pop()
        } else {
            append(match.value)
        }
        index = match.range.last + 1
    }
    appendPlainLinksAndBold(text.substring(index))
}

private fun AnnotatedString.Builder.appendPlainLinksAndBold(text: String) {
    var index = 0
    UrlRegex.findAll(text).forEach { match ->
        appendBoldMarkdown(text.substring(index, match.range.first))
        val rawUrl = match.value
        val url = rawUrl.cleanSourceUrl()
        val trailing = rawUrl.removePrefix(url)
        if (url.startsWith("http")) {
            pushStringAnnotation(UrlAnnotationTag, url)
            withStyle(
                SpanStyle(
                    color = Color(0xFF4FC3F7),
                    textDecoration = TextDecoration.Underline,
                ),
            ) {
                append(url)
            }
            pop()
            append(trailing)
        } else {
            append(rawUrl)
        }
        index = match.range.last + 1
    }
    appendBoldMarkdown(text.substring(index))
}

private fun AnnotatedString.Builder.appendBoldMarkdown(text: String) {
    var index = 0
    BoldMarkdownRegex.findAll(text).forEach { match ->
        append(text.substring(index, match.range.first))
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(match.groupValues[1])
        }
        index = match.range.last + 1
    }
    append(text.substring(index))
}

@Composable
private fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onSurface,
    fontWeight: FontWeight? = null,
) {
    val context = LocalContext.current
    val annotatedText = remember(text) { markdownAnnotatedString(text) }
    ClickableText(
        text = annotatedText,
        modifier = modifier,
        style = style.copy(
            color = color,
            fontWeight = fontWeight ?: style.fontWeight,
        ),
        onClick = { offset ->
            annotatedText.getStringAnnotations(UrlAnnotationTag, offset, offset)
                .firstOrNull()
                ?.let { annotation -> context.openUrl(annotation.item) }
        },
    )
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
                RichBlockType.Heading -> MarkdownText(
                    text = block.text,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                )
                RichBlockType.Paragraph -> MarkdownText(
                    text = block.text,
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
                    MarkdownText(
                        text = block.text,
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
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
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
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
        OutlinedButton(
            onClick = {
                scope.launch {
                    val result = withContext(Dispatchers.IO) {
                        context.saveMarkdownTableToDownloads(table)
                    }
                    result
                        .onSuccess {
                            Toast.makeText(context, "Table saved to Downloads", Toast.LENGTH_SHORT).show()
                        }
                        .onFailure { throwable ->
                            Toast.makeText(context, throwable.friendlyMessage(), Toast.LENGTH_LONG).show()
                        }
                }
            },
            modifier = Modifier.align(Alignment.End),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Download,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "XLSX",
                style = MaterialTheme.typography.labelMedium,
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
            MarkdownText(
                text = cells.getOrElse(index) { "" },
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
private fun SourcesBlock(text: String) {
    val context = LocalContext.current
    val sources = remember(text) {
        text.extractSourceLinks().ifEmpty {
            text.lines().mapNotNull { line ->
                val url = UrlRegex.find(line)?.value?.cleanSourceUrl() ?: return@mapNotNull null
                SourceLink(
                    title = line.replace(UrlRegex, "").trim('-', ' ', '[', ']').ifBlank { url.toSourceTitle() },
                    url = url,
                )
            }
        }.distinctBy { it.url }
    }
    if (sources.isEmpty()) return

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "Sources",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(sources) { source ->
                AssistChip(
                    onClick = { context.openUrl(source.url) },
                    label = {
                        Text(
                            text = source.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                )
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
    isSpeechPaused: Boolean,
    onCopy: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSpeak: () -> Unit,
    onStopSpeaking: () -> Unit,
    onPauseSpeaking: () -> Unit,
    onResumeSpeaking: () -> Unit,
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
            if (isSpeaking) {
                IconButton(
                    onClick = if (isSpeechPaused) onResumeSpeaking else onPauseSpeaking,
                    modifier = Modifier.size(34.dp),
                ) {
                    Icon(
                        imageVector = if (isSpeechPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                        contentDescription = if (isSpeechPaused) "Resume speaking" else "Pause speaking",
                        modifier = Modifier.size(17.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(
                    onClick = onStopSpeaking,
                    modifier = Modifier.size(34.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Stop,
                        contentDescription = "Stop speaking",
                        modifier = Modifier.size(17.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                IconButton(
                    onClick = onSpeak,
                    modifier = Modifier.size(34.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Read aloud",
                        modifier = Modifier.size(17.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
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
    onPreview: (ChatImageAttachment) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f),
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.clickable { onPreview(attachment) },
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
private fun PresetDrawer(
    presets: List<ChatAppendPreset>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onPresetClick: (String) -> Unit,
) {
    if (presets.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(presets.size, expanded) {
                detectVerticalDragGestures { _, dragAmount ->
                    when {
                        dragAmount < -6f -> onExpandedChange(true)
                        dragAmount > 6f -> onExpandedChange(false)
                    }
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        AnimatedVisibility(visible = expanded) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(end = 4.dp),
            ) {
                items(presets) { preset ->
                    FilterChip(
                        selected = false,
                        onClick = { onPresetClick(preset.id) },
                        label = {
                            Text(
                                text = preset.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                    )
                }
            }
        }
        if (!expanded) {
            Box(
                modifier = Modifier
                    .width(96.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onExpandedChange(true) },
            )
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
    onChatAppendPresetApply: (String) -> Unit,
    onDismissError: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val canSend = (input.isNotBlank() || attachedImages.isNotEmpty() || attachedDocumentText != null) && !isSending
    var previewAttachment by remember { mutableStateOf<ChatImageAttachment?>(null) }
    var presetsExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(state.chatAppendPresets.isEmpty()) {
        if (state.chatAppendPresets.isEmpty()) {
            presetsExpanded = false
        }
    }

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
            verticalArrangement = Arrangement.spacedBy(6.dp),
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
                    if (attachedImages.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(end = 4.dp),
                        ) {
                            items(attachedImages) { attachment ->
                                AttachedImageChip(
                                    attachment = attachment,
                                    onClear = onClearImage,
                                    onPreview = { previewAttachment = it },
                                )
                            }
                        }
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

            PresetDrawer(
                presets = state.chatAppendPresets,
                expanded = presetsExpanded,
                onExpandedChange = { presetsExpanded = it },
                onPresetClick = { presetId ->
                    onChatAppendPresetApply(presetId)
                    presetsExpanded = false
                },
            )

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

    previewAttachment?.let { attachment ->
        ImagePreviewDialog(
            attachment = attachment,
            onDismiss = { previewAttachment = null },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WatchJobsSheet(
    state: ChatUiState,
    onDismiss: () -> Unit,
    selectedUtilityTab: UtilityTab,
    onUtilityTabSelected: (UtilityTab) -> Unit,
    onCreateWatchJob: (String, String, String, String, String, String, String, String, String, String, String) -> Unit,
    onUpdateWatchJob: (String, String, String, String, String, String, String, String, String, String, String, String, Boolean) -> Unit,
    onToggleWatchJob: (String, Boolean) -> Unit,
    onDeleteWatchJob: (String) -> Unit,
) {
    val scrollState = rememberScrollState()
    var title by remember { mutableStateOf("") }
    var trigger by remember { mutableStateOf("notification") }
    var appQuery by remember { mutableStateOf("") }
    var senderQuery by remember { mutableStateOf("") }
    var textQuery by remember { mutableStateOf("") }
    var matchMode by remember { mutableStateOf("filter") }
    var aiInstruction by remember { mutableStateOf("") }
    var scheduleMinutes by remember { mutableStateOf("15") }
    var alertMessage by remember { mutableStateOf("") }
    var alertMode by remember { mutableStateOf("normal") }
    var lifetime by remember { mutableStateOf("noend") }
    var editingJobId by remember { mutableStateOf<String?>(null) }
    var editingEnabled by remember { mutableStateOf(true) }
    var localError by remember { mutableStateOf<String?>(null) }
    var pendingDelete by remember { mutableStateOf<WatchJob?>(null) }

    fun clearWatchJobForm() {
        title = ""
        trigger = "notification"
        appQuery = ""
        senderQuery = ""
        textQuery = ""
        matchMode = "filter"
        aiInstruction = ""
        scheduleMinutes = "15"
        alertMessage = ""
        alertMode = "normal"
        lifetime = "noend"
        editingJobId = null
        editingEnabled = true
        localError = null
    }

    fun editWatchJob(job: WatchJob) {
        title = job.title
        trigger = job.trigger
        appQuery = job.appQuery
        senderQuery = job.senderQuery
        textQuery = job.textQuery
        matchMode = job.matchMode.normalizedWatchMatchMode()
        aiInstruction = job.aiInstruction
        scheduleMinutes = job.scheduleMinutes.takeIf { it > 0 }?.toString() ?: "15"
        alertMessage = job.alertMessage
        alertMode = job.alertMode.normalizedWatchAlertMode()
        lifetime = job.lifetime.normalizedWatchLifetime()
        editingJobId = job.id
        editingEnabled = job.enabled
        localError = null
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 22.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            UtilityTabs(
                selected = selectedUtilityTab,
                onSelected = onUtilityTabSelected,
            )
            Text(
                text = "Watch jobs",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = if (editingJobId == null) {
                    "Create local monitors for notifications, notification history, or scheduled LM Studio prompt checks."
                } else {
                    "Editing a watch job. Save to update the stored filters and schedule."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (!state.watchJobToolEnabled) {
                Text(
                    text = "Enable Watch jobs in Phone tools if you want the model to create these after confirmation.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Job title") },
                placeholder = { Text("Mom on Telegram") },
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = trigger == "notification",
                    onClick = {
                        trigger = "notification"
                        if (matchMode == "prompt") matchMode = "ai"
                    },
                    label = { Text("On notification") },
                )
                FilterChip(
                    selected = trigger == "schedule",
                    onClick = { trigger = "schedule" },
                    label = { Text("On schedule") },
                )
            }
            Text(
                text = "Match mode",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = matchMode == "filter",
                    onClick = { matchMode = "filter" },
                    label = { Text("Filters") },
                )
                FilterChip(
                    selected = matchMode == "ai",
                    onClick = { matchMode = "ai" },
                    label = { Text("AI") },
                )
                FilterChip(
                    selected = matchMode == "prompt",
                    onClick = {
                        matchMode = "prompt"
                        trigger = "schedule"
                    },
                    label = { Text("Prompt") },
                )
            }
            Text(
                text = when (matchMode) {
                    "ai" -> "AI mode sends matching notifications to LM Studio and expects a strict ALERT or IGNORE decision."
                    "prompt" -> "Prompt mode runs this scheduled task through LM Studio. Enable Server tools when the task needs web search or fetching."
                    else -> "Filter mode alerts when the local app, sender, and text filters match."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            AnimatedVisibility(visible = matchMode != "prompt") {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    OutlinedTextField(
                        value = appQuery,
                        onValueChange = { appQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text(if (matchMode == "ai") "App pre-filter (optional)" else "App filter") },
                        placeholder = { Text("Telegram") },
                    )
                    OutlinedTextField(
                        value = senderQuery,
                        onValueChange = { senderQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text(if (matchMode == "ai") "Sender pre-filter (optional)" else "Sender filter") },
                        placeholder = { Text("Mom") },
                    )
                    OutlinedTextField(
                        value = textQuery,
                        onValueChange = { textQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text(if (matchMode == "ai") "Text pre-filter (optional)" else "Text contains") },
                        placeholder = { Text("urgent") },
                    )
                }
            }
            AnimatedVisibility(visible = matchMode == "ai" || matchMode == "prompt") {
                OutlinedTextField(
                    value = aiInstruction,
                    onValueChange = { aiInstruction = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6,
                    label = { Text(if (matchMode == "prompt") "Scheduled prompt" else "AI instruction") },
                    placeholder = {
                        Text(
                            if (matchMode == "prompt") {
                                "Search the web for new Lithuanian AI policy news. Alert only when a credible new item appears and include source URLs."
                            } else {
                                "Alert me if this notification mentions an offer from company Mindylab."
                            },
                        )
                    },
                    supportingText = {
                        Text(
                            if (matchMode == "prompt") {
                                "The model returns ALERT or IGNORE plus notification title/detail. Server tools are used when enabled."
                            } else {
                                "Keep it specific. The model returns only ALERT or IGNORE."
                            },
                        )
                    },
                )
            }
            AnimatedVisibility(visible = trigger == "schedule") {
                GenerationNumberField(
                    value = scheduleMinutes,
                    onValueChange = { scheduleMinutes = it },
                    label = "Check every minutes",
                    modifier = Modifier.fillMaxWidth(),
                    decimal = false,
                )
            }
            OutlinedTextField(
                value = alertMessage,
                onValueChange = { alertMessage = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Alert message") },
                placeholder = { Text("Mom wrote on Telegram") },
            )
            Text(
                text = "Alert mode",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = alertMode == "normal",
                    onClick = { alertMode = "normal" },
                    label = { Text("Normal") },
                )
                FilterChip(
                    selected = alertMode == "alarm",
                    onClick = { alertMode = "alarm" },
                    label = { Text("Alarm") },
                )
            }
            Text(
                text = if (alertMode == "alarm") {
                    "Alarm opens a full-screen alert and repeats sound/vibration until stopped."
                } else {
                    "Normal sends a high-priority notification."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Lifetime",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = lifetime == "once",
                    onClick = { lifetime = "once" },
                    label = { Text("Once") },
                )
                FilterChip(
                    selected = lifetime == "today",
                    onClick = { lifetime = "today" },
                    label = { Text("Today") },
                )
                FilterChip(
                    selected = lifetime == "noend",
                    onClick = { lifetime = "noend" },
                    label = { Text("No end") },
                )
            }
            AnimatedVisibility(visible = editingJobId != null) {
                ToolToggleRow(
                    title = "Enabled",
                    description = "Keep this watch job active after saving.",
                    checked = editingEnabled,
                    onCheckedChange = { editingEnabled = it },
                )
            }
            localError?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (editingJobId != null) {
                    OutlinedButton(
                        onClick = { clearWatchJobForm() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text("Cancel")
                    }
                }
                Button(
                    onClick = {
                        if ((matchMode == "ai" || matchMode == "prompt") && aiInstruction.isBlank()) {
                            localError = if (matchMode == "prompt") {
                                "Add a scheduled prompt."
                            } else {
                                "Add AI instructions for AI mode."
                            }
                            return@Button
                        }
                        if (matchMode == "filter" && listOf(appQuery, senderQuery, textQuery).all { it.isBlank() }) {
                            localError = "Add at least one filter so the job does not alert on every notification."
                            return@Button
                        }
                        localError = null
                        val jobId = editingJobId
                        if (jobId == null) {
                            onCreateWatchJob(
                                title,
                                trigger,
                                appQuery,
                                senderQuery,
                                textQuery,
                                matchMode,
                                aiInstruction,
                                scheduleMinutes,
                                alertMessage,
                                alertMode,
                                lifetime,
                            )
                        } else {
                            onUpdateWatchJob(
                                jobId,
                                title,
                                trigger,
                                appQuery,
                                senderQuery,
                                textQuery,
                                matchMode,
                                aiInstruction,
                                scheduleMinutes,
                                alertMessage,
                                alertMode,
                                lifetime,
                                editingEnabled,
                            )
                        }
                        clearWatchJobForm()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(if (editingJobId == null) "Create watch job" else "Save changes")
                }
            }

            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.45f))

            Text(
                text = "Active jobs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            if (state.watchJobs.isEmpty()) {
                Text(
                    text = "No watch jobs yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    state.watchJobs.forEach { job ->
                        WatchJobCard(
                            job = job,
                            onToggle = { enabled -> onToggleWatchJob(job.id, enabled) },
                            onEdit = { editWatchJob(job) },
                            onDelete = { pendingDelete = job },
                        )
                    }
                }
            }
        }
    }

    val jobToDelete = pendingDelete
    if (jobToDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete watch job?") },
            text = { Text("This will remove \"${jobToDelete.title}\" from this device.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteWatchJob(jobToDelete.id)
                        pendingDelete = null
                    },
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun WatchJobCard(
    job: WatchJob,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = if (job.trigger == "schedule") {
                            if (job.matchMode.normalizedWatchMatchMode() == "prompt") {
                                "Prompt every ${job.scheduleMinutes} min"
                            } else {
                                "Every ${job.scheduleMinutes} min"
                            }
                        } else {
                            "On matching notification"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "${job.alertMode.watchAlertModeLabel()} alert - ${job.lifetime.watchLifetimeLabel()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "${job.matchMode.watchMatchModeLabel()} match",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(checked = job.enabled, onCheckedChange = onToggle)
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit watch job",
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete watch job",
                    )
                }
            }
            Text(
                text = if (job.matchMode.normalizedWatchMatchMode() == "prompt") {
                    "Prompt: ${job.aiInstruction}"
                } else {
                    buildList {
                        if (job.appQuery.isNotBlank()) add("App: ${job.appQuery}")
                        if (job.senderQuery.isNotBlank()) add("Sender: ${job.senderQuery}")
                        if (job.textQuery.isNotBlank()) add("Text: ${job.textQuery}")
                    }.joinToString("  |  ").ifBlank { "No filters" }
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (job.matchMode.normalizedWatchMatchMode() == "prompt") 2 else Int.MAX_VALUE,
                overflow = TextOverflow.Ellipsis,
            )
            if (job.matchMode.normalizedWatchMatchMode() == "ai") {
                Text(
                    text = "AI: ${job.aiInstruction}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = "Matches: ${job.matchCount}  |  Last checked: ${job.lastCheckedAt.toWatchJobTime()}  |  Last match: ${job.lastMatchedAt.toWatchJobTime()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun WatchJobAlarmScreen(
    title: String,
    detail: String,
    jobId: String,
    onStop: () -> Unit,
) {
    val context = LocalContext.current
    val handler = remember { Handler(Looper.getMainLooper()) }
    DisposableEffect(jobId) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val ringtone = context.watchJobAlarmRingtone()
        val playRunnable = object : Runnable {
            override fun run() {
                runCatching {
                    if (ringtone?.isPlaying != true) {
                        ringtone?.play()
                    }
                }
                handler.postDelayed(this, 3500L)
            }
        }
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 700, 350, 900),
                        0,
                    ),
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 700, 350, 900), 0)
            }
        }
        handler.post(playRunnable)
        onDispose {
            handler.removeCallbacks(playRunnable)
            runCatching { ringtone?.stop() }
            runCatching { vibrator.cancel() }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AppMark(modifier = Modifier.size(82.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(14.dp))
            if (detail.isNotBlank()) {
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(28.dp))
            }
            Button(
                onClick = onStop,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                ),
            ) {
                Text("Stop alert")
            }
        }
    }
}

private fun Context.watchJobAlarmRingtone(): Ringtone? {
    val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    return runCatching {
        RingtoneManager.getRingtone(applicationContext, uri)?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            }
        }
    }.getOrNull()
}

private fun Long.toWatchJobTime(): String {
    if (this <= 0L) return "Never"
    val formatter = DateTimeFormatter.ofPattern("MMM d HH:mm", Locale.getDefault())
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}

private fun String.normalizedWatchAlertMode(): String =
    when (trim().lowercase(Locale.getDefault()).replace("_", "-")) {
        "alarm", "fullscreen", "full-screen", "strong" -> "alarm"
        else -> "normal"
    }

private fun String.normalizedWatchLifetime(): String =
    when (trim().lowercase(Locale.getDefault()).replace("_", "-")) {
        "once", "one-time", "one time", "single" -> "once"
        "today", "only-today", "only today" -> "today"
        else -> "noend"
    }

private fun String.normalizedWatchMatchMode(): String =
    when (trim().lowercase(Locale.getDefault()).replace("_", "-").replace(" ", "-")) {
        "ai", "model", "llm", "smart" -> "ai"
        "prompt", "scheduled-prompt", "schedule-prompt", "scheduled-task", "task", "web", "internet", "news" -> "prompt"
        else -> "filter"
    }

private fun String.watchAlertModeLabel(): String =
    if (normalizedWatchAlertMode() == "alarm") "Alarm" else "Normal"

private fun String.watchLifetimeLabel(): String =
    when (normalizedWatchLifetime()) {
        "once" -> "once"
        "today" -> "today only"
        else -> "no end"
    }

private fun String.watchMatchModeLabel(): String =
    when (normalizedWatchMatchMode()) {
        "ai" -> "AI"
        "prompt" -> "Prompt"
        else -> "Filters"
    }

private fun WatchJob.watchJobFiltersText(): String =
    buildList {
        if (appQuery.isNotBlank()) add("app=$appQuery")
        if (senderQuery.isNotBlank()) add("sender=$senderQuery")
        if (textQuery.isNotBlank()) add("text=$textQuery")
    }.joinToString(", ").ifBlank { "none" }

private fun WatchJob.watchJobToolDescription(): String =
    buildString {
        appendLine("Title: $title")
        appendLine("ID: $id")
        appendLine("Enabled: $enabled")
        appendLine("Trigger: $trigger${if (trigger == "schedule") " every $scheduleMinutes minutes" else ""}")
        appendLine("Match mode: ${matchMode.watchMatchModeLabel()}")
        appendLine("Filters: ${watchJobFiltersText()}")
        when (matchMode.normalizedWatchMatchMode()) {
            "ai" -> appendLine("AI instruction: $aiInstruction")
            "prompt" -> appendLine("Scheduled prompt: $aiInstruction")
        }
        appendLine("Alert mode: ${alertMode.watchAlertModeLabel()}")
        appendLine("Lifetime: ${lifetime.watchLifetimeLabel()}")
        appendLine("Matches: $matchCount")
    }.trim()

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
    selectedUtilityTab: UtilityTab,
    onUtilityTabSelected: (UtilityTab) -> Unit,
    onApiUrlChange: (String) -> Unit,
    onModelChange: (String) -> Unit,
    onApiTokenChange: (String) -> Unit,
    onServerToolsEnabledChange: (Boolean) -> Unit,
    onServerIntegrationsChange: (String) -> Unit,
    onAllowedToolsChange: (String) -> Unit,
    onNativeIntentToolEnabledChange: (Boolean) -> Unit,
    onCalendarToolEnabledChange: (Boolean) -> Unit,
    onAlarmToolEnabledChange: (Boolean) -> Unit,
    onContactsToolEnabledChange: (Boolean) -> Unit,
    onNotificationDigestToolEnabledChange: (Boolean) -> Unit,
    onLocalFileSearchToolEnabledChange: (Boolean) -> Unit,
    onPickLocalSearchFolder: () -> Unit,
    onDeviceStatusToolEnabledChange: (Boolean) -> Unit,
    onWatchJobToolEnabledChange: (Boolean) -> Unit,
    onVoiceInputEnabledChange: (Boolean) -> Unit,
    onVoiceOutputEnabledChange: (Boolean) -> Unit,
    onVoiceOutputEngineChange: (String) -> Unit,
    onVoiceOutputLanguageChange: (String) -> Unit,
    onVoiceOutputVoiceChange: (String) -> Unit,
    onVoiceOutputSpeedChange: (Float) -> Unit,
    onVoiceOutputQualityChange: (Int) -> Unit,
    onVoiceOutputChunkSizeChange: (Int) -> Unit,
    onVoiceOutputStartBufferChange: (Int) -> Unit,
    voiceOutputVoiceOptions: List<VoiceOutputVoiceOption>,
    onAutoReadAnswersEnabledChange: (Boolean) -> Unit,
    onAppendCapabilityGuideToSystemPromptChange: (Boolean) -> Unit,
    onAppendDateTimeToSystemPromptChange: (Boolean) -> Unit,
    onShareDefaultSelectionEnabledChange: (Boolean) -> Unit,
    onSystemProfileSelect: (String) -> Unit,
    onSystemProfileNameChange: (String) -> Unit,
    onSystemPromptChange: (String) -> Unit,
    onChatAppendPresetSelect: (String) -> Unit,
    onChatAppendPresetNameChange: (String) -> Unit,
    onChatAppendPresetTextChange: (String) -> Unit,
    onChatAppendPresetSave: () -> Unit,
    onChatAppendPresetCreate: () -> Unit,
    onChatAppendPresetDelete: (String) -> Unit,
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
    val context = LocalContext.current
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
            UtilityTabs(
                selected = selectedUtilityTab,
                onSelected = onUtilityTabSelected,
            )
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
                title = "Append capability guide",
                description = "Adds a live map of model, document, LM Studio MCP/server tools, and Android phone actions to each request so the model chooses the correct side and explains what must be enabled first.",
                checked = state.appendCapabilityGuideToSystemPrompt,
                onCheckedChange = onAppendCapabilityGuideToSystemPromptChange,
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
                text = "Chat text presets",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(end = 10.dp),
            ) {
                items(state.chatAppendPresets) { preset ->
                    FilterChip(
                        selected = state.activeChatAppendPresetId == preset.id,
                        onClick = { onChatAppendPresetSelect(preset.id) },
                        label = {
                            Text(
                                text = preset.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        leadingIcon = if (state.activeChatAppendPresetId == preset.id) {
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
                value = state.chatAppendPresetNameDraft,
                onValueChange = onChatAppendPresetNameChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Preset name") },
                placeholder = { Text(ChatAppendPresetSampleName) },
            )
            OutlinedTextField(
                value = state.chatAppendPresetTextDraft,
                onValueChange = onChatAppendPresetTextChange,
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 5,
                label = { Text("Append text") },
                placeholder = { Text(ChatAppendPresetSampleText) },
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = onChatAppendPresetCreate,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("New")
                }
                OutlinedButton(
                    onClick = { onChatAppendPresetDelete(state.activeChatAppendPresetId) },
                    modifier = Modifier.weight(1f),
                    enabled = state.activeChatAppendPresetId.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Delete")
                }
                Button(
                    onClick = onChatAppendPresetSave,
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
                title = "Alarms",
                description = "Read the next alarm, create alarm drafts, or open Clock to list/edit/delete alarms.",
                checked = state.alarmToolEnabled,
                onCheckedChange = onAlarmToolEnabledChange,
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
            ToolToggleRow(
                title = "Watch jobs",
                description = "Let the model create local notification or scheduled watch jobs after confirmation.",
                checked = state.watchJobToolEnabled,
                onCheckedChange = onWatchJobToolEnabledChange,
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    val selectedVoiceEngine = state.voiceOutputEngine.normalizedVoiceOutputEngine()
                    val selectedVoiceLanguage = state.voiceOutputLanguage.normalizedVoiceOutputLanguage()
                    val filteredVoiceOptions = voiceOutputVoiceOptions.filteredForLanguage(selectedVoiceLanguage)
                    val supertonicInstalled = context.isPackageInstalled(SupertonicTtsEnginePackage)
                    var languageMenuExpanded by remember { mutableStateOf(false) }
                    var voiceMenuExpanded by remember { mutableStateOf(false) }

                    Text(
                        text = "Voice output engine",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(end = 10.dp),
                    ) {
                        item {
                            FilterChip(
                                selected = selectedVoiceEngine == VoiceOutputEngineSystem,
                                onClick = { onVoiceOutputEngineChange(VoiceOutputEngineSystem) },
                                label = { Text("System") },
                                leadingIcon = if (selectedVoiceEngine == VoiceOutputEngineSystem) {
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
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedVoiceEngine == VoiceOutputEngineSupertonic,
                                onClick = { onVoiceOutputEngineChange(VoiceOutputEngineSupertonic) },
                                label = { Text("Supertonic") },
                                leadingIcon = if (selectedVoiceEngine == VoiceOutputEngineSupertonic) {
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
                            )
                        }
                    }
                    Text(
                        text = when (selectedVoiceEngine) {
                            VoiceOutputEngineSupertonic -> if (supertonicInstalled) {
                                "Uses the installed Supertonic Android TTS engine for read-aloud playback."
                            } else {
                                "Supertonic TTS engine is not installed on this phone yet."
                            }
                            else -> "Uses the Android system text-to-speech engine selected on this phone."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Voice output language",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { languageMenuExpanded = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = selectedVoiceLanguage.voiceOutputLanguageLabel(),
                                modifier = Modifier.weight(1f),
                            )
                        }
                        DropdownMenu(
                            expanded = languageMenuExpanded,
                            onDismissRequest = { languageMenuExpanded = false },
                            modifier = Modifier.heightIn(max = 320.dp),
                        ) {
                            VoiceOutputLanguageOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    leadingIcon = if (selectedVoiceLanguage == option.tag) {
                                        {
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = null,
                                            )
                                        }
                                    } else {
                                        null
                                    },
                                    onClick = {
                                        onVoiceOutputLanguageChange(option.tag)
                                        languageMenuExpanded = false
                                    },
                                )
                            }
                        }
                    }
                    Text(
                        text = selectedVoiceLanguage.voiceOutputLanguageDescription(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Speaker voice",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { voiceMenuExpanded = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = voiceOutputVoiceOptions.isNotEmpty(),
                        ) {
                            Text(
                                text = state.voiceOutputVoiceName.voiceOutputVoiceLabel(voiceOutputVoiceOptions),
                                modifier = Modifier.weight(1f),
                            )
                        }
                        DropdownMenu(
                            expanded = voiceMenuExpanded,
                            onDismissRequest = { voiceMenuExpanded = false },
                            modifier = Modifier.heightIn(max = 320.dp),
                        ) {
                            DropdownMenuItem(
                                text = { Text("Engine default voice") },
                                leadingIcon = if (state.voiceOutputVoiceName.isBlank()) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                        )
                                    }
                                } else {
                                    null
                                },
                                onClick = {
                                    onVoiceOutputVoiceChange("")
                                    voiceMenuExpanded = false
                                },
                            )
                            filteredVoiceOptions.forEach { voice ->
                                DropdownMenuItem(
                                    text = { Text(voice.label) },
                                    leadingIcon = if (state.voiceOutputVoiceName == voice.name) {
                                        {
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = null,
                                            )
                                        }
                                    } else {
                                        null
                                    },
                                    onClick = {
                                        onVoiceOutputVoiceChange(voice.name)
                                        voiceMenuExpanded = false
                                    },
                                )
                            }
                        }
                    }
                    Text(
                        text = if (voiceOutputVoiceOptions.isEmpty()) {
                            "The selected TTS engine has not exposed speaker voices yet."
                        } else {
                            "Shows voices exposed by the selected Android TTS engine."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "Speed",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = state.voiceOutputSpeed.voiceOutputSpeedLabel(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Slider(
                        value = state.voiceOutputSpeed.normalizedVoiceOutputSpeed(),
                        onValueChange = onVoiceOutputSpeedChange,
                        valueRange = 0.5f..2.0f,
                        steps = 5,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "Quality",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = state.voiceOutputQuality.voiceOutputQualityLabel(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Slider(
                        value = state.voiceOutputQuality.normalizedVoiceOutputQuality().toFloat(),
                        onValueChange = { onVoiceOutputQualityChange(it.roundToInt()) },
                        valueRange = 1f..10f,
                        steps = 8,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "Chunk size",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = state.voiceOutputChunkSize.voiceOutputChunkSizeLabel(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Slider(
                        value = state.voiceOutputChunkSize.normalizedVoiceOutputChunkSize().toFloat(),
                        onValueChange = { onVoiceOutputChunkSizeChange(it.roundToInt()) },
                        valueRange = VoiceOutputMinChunkChars.toFloat()..VoiceOutputMaxChunkChars.toFloat(),
                        steps = ((VoiceOutputMaxChunkChars - VoiceOutputMinChunkChars) / VoiceOutputChunkStepChars) - 1,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "Start buffer",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = state.voiceOutputStartBuffer.voiceOutputStartBufferLabel(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Slider(
                        value = state.voiceOutputStartBuffer.normalizedVoiceOutputStartBuffer().toFloat(),
                        onValueChange = { onVoiceOutputStartBufferChange(it.roundToInt()) },
                        valueRange = VoiceOutputMinStartBufferChunks.toFloat()..VoiceOutputMaxStartBufferChunks.toFloat(),
                        steps = (VoiceOutputMaxStartBufferChunks - VoiceOutputMinStartBufferChunks) - 1,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedButton(
                            onClick = { context.openAndroidTtsSettings() },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("TTS settings")
                        }
                        if (selectedVoiceEngine == VoiceOutputEngineSupertonic && !supertonicInstalled) {
                            OutlinedButton(
                                onClick = { context.openUrl(SupertonicTtsInstallUrl) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f),
                            ) {
                                Text("Install")
                            }
                        }
                    }
                    ToolToggleRow(
                        title = "Auto-read answers",
                        description = "Read new assistant replies aloud after generation finishes.",
                        checked = state.autoReadAnswersEnabled,
                        onCheckedChange = onAutoReadAnswersEnabledChange,
                    )
                }
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
            ToolToggleRow(
                title = "Default share destination",
                description = if (state.shareDefaultDestination.isBlank()) {
                    "Not set. Use Set default selection in the share popup to choose one."
                } else {
                    "Currently sends shared content to ${state.shareDefaultDestination.shareDefaultDestinationLabel(state.sessions)}. Turn off to ask every time."
                },
                checked = state.shareDefaultDestination.isNotBlank(),
                onCheckedChange = onShareDefaultSelectionEnabledChange,
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
private fun InfoSheet(
    onDismiss: () -> Unit,
    selectedUtilityTab: UtilityTab,
    onUtilityTabSelected: (UtilityTab) -> Unit,
) {
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
            UtilityTabs(
                selected = selectedUtilityTab,
                onSelected = onUtilityTabSelected,
            )
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
