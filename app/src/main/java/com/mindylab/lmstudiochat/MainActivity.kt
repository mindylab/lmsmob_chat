package com.mindylab.lmstudiochat

import android.content.Context
import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import java.util.UUID

private const val DefaultApiUrl = "http://10.0.2.2:1234/v1"
private const val DefaultSystemPrompt = "You are a helpful assistant running locally through LM Studio."
private const val DefaultSystemProfileId = "default"
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = ViewModelProvider(
            this,
            ChatViewModel.factory(applicationContext),
        )[ChatViewModel::class.java]

        setContent {
            LmStudioChatTheme {
                val context = LocalContext.current
                val state by viewModel.uiState.collectAsStateWithLifecycle()
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
                            viewModel.attachImage(attachment)
                        }.onFailure { throwable ->
                            viewModel.showError(throwable.friendlyMessage())
                        }
                    }
                }
                val cameraLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.TakePicturePreview(),
                ) { bitmap: Bitmap? ->
                    if (bitmap != null) {
                        runCatching {
                            bitmap.toAttachment("Camera photo")
                        }.onSuccess { attachment ->
                            viewModel.attachImage(attachment)
                        }.onFailure { throwable ->
                            viewModel.showError(throwable.friendlyMessage())
                        }
                    }
                }
                LmStudioApp(
                    state = state,
                    onInputChange = viewModel::updateInput,
                    onSend = viewModel::sendInput,
                    onSuggestion = viewModel::sendPrompt,
                    onNewChat = viewModel::newChat,
                    onSelectChat = viewModel::selectChat,
                    onChatSearchChange = viewModel::updateChatSearchQuery,
                    onEditMessage = viewModel::editMessage,
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
                    onSystemProfileSelect = viewModel::selectSystemProfile,
                    onSystemProfileNameChange = viewModel::updateSystemProfileNameDraft,
                    onSystemPromptChange = viewModel::updateSystemPromptDraft,
                    onSystemProfileSave = viewModel::saveSystemProfile,
                    onSystemProfileCreate = viewModel::createSystemProfile,
                    onSystemProfileDelete = viewModel::deleteSystemProfile,
                    onReasoningEnabledChange = viewModel::updateReasoningEnabled,
                    onPickImage = { imageLauncher.launch("image/*") },
                    onTakePhoto = { cameraLauncher.launch(null) },
                    onClearImage = viewModel::clearAttachedImage,
                    onRefreshModels = { viewModel.refreshModels(silent = false) },
                    onExportChats = { exportLauncher.launch("lm-studio-chat-history.json") },
                    onImportChats = { importLauncher.launch(arrayOf("application/json", "text/*", "*/*")) },
                    onDismissError = viewModel::dismissError,
                )
            }
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
    val attachedImage: ChatImageAttachment? = null,
    val reasoningEnabled: Boolean = true,
    val previousResponseId: String? = null,
    val isSettingsOpen: Boolean = false,
    val isInfoOpen: Boolean = false,
    val error: String? = null,
    val modelLoadError: String? = null,
)

data class ModelInfo(
    val id: String,
    val supportsVision: Boolean = false,
    val reasoningOptions: List<String> = emptyList(),
    val defaultReasoning: String = "",
)

class ChatViewModel(
    private val settingsStore: SettingsStore,
    private val client: LmStudioClient,
) : ViewModel() {
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

    fun sendPrompt(prompt: String) {
        val trimmedPrompt = prompt.trim()
        val state = _uiState.value
        val attachedImage = state.attachedImage
        if ((trimmedPrompt.isBlank() && attachedImage == null) || state.isSending) return

        if (attachedImage != null && !state.selectedModelSupportsVision()) {
            _uiState.update {
                it.copy(error = "The selected model does not report vision support. Pick a vision model, then attach the image again.")
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

        val userPrompt = trimmedPrompt.ifBlank { "Please describe this image." }
        val userMessage = ChatMessage(
            role = MessageRole.User,
            content = userPrompt,
            attachments = listOfNotNull(attachedImage),
        )
        val activeSessionId = state.activeSessionId
        val activeSession = state.sessions.firstOrNull { it.id == activeSessionId } ?: ChatSession(id = activeSessionId)
        val requestMessages = activeSession.messages + userMessage
        val reasoning = state.reasoningRequestValue()
        val useNativeChat = state.serverToolsEnabled || attachedImage != null || reasoning != null
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
        val promptAttachments = listOfNotNull(attachedImage)
        val systemPrompt = state.activeSystemPrompt()

        settingsStore.saveChatSessions(updatedSessions)
        _uiState.update {
            it.copy(
                sessions = updatedSessions,
                messages = visibleMessages,
                input = "",
                attachedImage = null,
                isSending = true,
                error = null,
            )
        }

        viewModelScope.launch {
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
                    prompt = userPrompt,
                    attachments = promptAttachments,
                    systemPrompt = systemPrompt,
                    previousResponseId = previousResponseId,
                    integrations = if (serverToolsEnabled) serverIntegrations else "",
                    allowedTools = allowedTools,
                    reasoning = reasoning,
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
                        removeAssistantWithError(throwable)
                    },
                )
            } else {
                val result = client.sendChatCompletions(
                    apiUrl = apiUrl,
                    model = model,
                    apiToken = apiToken,
                    messages = requestMessages,
                    systemPrompt = systemPrompt,
                )

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

    fun attachImage(attachment: ChatImageAttachment) {
        _uiState.update {
            if (it.selectedModelSupportsVision()) {
                it.copy(attachedImage = attachment, error = null)
            } else {
                it.copy(error = "The selected model does not report vision support. Pick a vision model first.")
            }
        }
    }

    fun clearAttachedImage() {
        _uiState.update { it.copy(attachedImage = null) }
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
                            attachedImage = current.attachedImage.takeIf {
                                modelInfos.firstOrNull { modelInfo -> modelInfo.id == selectedModel }?.supportsVision == true
                            },
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

private fun ChatUiState.activeSystemPrompt(): String =
    systemProfiles.firstOrNull { it.id == activeSystemProfileId }?.prompt
        ?.takeIf { it.isNotBlank() }
        ?: systemPromptDraft.takeIf { it.isNotBlank() }
        ?: DefaultSystemPrompt

private fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("LM Studio message", text))
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

class LmStudioClient(
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(2, TimeUnit.MINUTES)
        .readTimeout(10, TimeUnit.MINUTES)
        .callTimeout(15, TimeUnit.MINUTES)
        .build(),
) {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

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
                .put("temperature", 0.7)
                .put("stream", false)

            val request = Request.Builder()
                .url(apiUrl.normalizedOpenAiBaseUrl() + "/chat/completions")
                .withApiToken(apiToken)
                .post(payload.toString().toRequestBody(jsonMediaType))
                .build()

            httpClient.newCall(request).execute().use { response ->
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
                .put("temperature", 0.7)
                .put("store", true)

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

            httpClient.newCall(request).execute().use { response ->
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
                .put("temperature", 0.7)
                .put("store", true)
                .put("stream", true)

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

            httpClient.newCall(request).execute().use { response ->
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

private fun Request.Builder.withApiToken(apiToken: String): Request.Builder {
    val trimmedToken = apiToken.trim()
    if (trimmedToken.isNotBlank()) {
        header("Authorization", "Bearer $trimmedToken")
    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LmStudioApp(
    state: ChatUiState,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onSuggestion: (String) -> Unit,
    onNewChat: () -> Unit,
    onSelectChat: (String) -> Unit,
    onChatSearchChange: (String) -> Unit,
    onEditMessage: (String) -> Unit,
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
    onSystemProfileSelect: (String) -> Unit,
    onSystemProfileNameChange: (String) -> Unit,
    onSystemPromptChange: (String) -> Unit,
    onSystemProfileSave: () -> Unit,
    onSystemProfileCreate: () -> Unit,
    onSystemProfileDelete: (String) -> Unit,
    onReasoningEnabledChange: (Boolean) -> Unit,
    onPickImage: () -> Unit,
    onTakePhoto: () -> Unit,
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
            onSystemProfileSelect = onSystemProfileSelect,
            onSystemProfileNameChange = onSystemProfileNameChange,
            onSystemPromptChange = onSystemPromptChange,
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
                    input = state.input,
                    isSending = state.isSending,
                    error = state.error,
                    canAttachImage = state.selectedModelSupportsVision(),
                    attachedImage = state.attachedImage,
                    reasoningSupported = state.selectedModelSupportsReasoningToggle(),
                    reasoningEnabled = state.reasoningEnabled,
                    onInputChange = onInputChange,
                    onSend = onSend,
                    onPickImage = onPickImage,
                    onTakePhoto = onTakePhoto,
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
            )
        }
    }
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
) {
    val listState = rememberLazyListState()

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
            )
        }

        if (state.isSending && state.messages.none { it.isStreaming }) {
            item {
                TypingRow()
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
                    onCopy = { context.copyToClipboard(message.content) },
                    onEdit = { onEditMessage(message.id) },
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
                    onCopy = { context.copyToClipboard(message.content) },
                    onEdit = {},
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
                        imageVector = Icons.Filled.AddPhotoAlternate,
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
    val content = rawContent
        .replace("\r\n", "\n")
        .trim()
    if (content.isBlank()) return emptyList()

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
    onCopy: () -> Unit,
    onEdit: () -> Unit,
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
private fun ChatComposer(
    input: String,
    isSending: Boolean,
    error: String?,
    canAttachImage: Boolean,
    attachedImage: ChatImageAttachment?,
    reasoningSupported: Boolean,
    reasoningEnabled: Boolean,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onPickImage: () -> Unit,
    onTakePhoto: () -> Unit,
    onClearImage: () -> Unit,
    onReasoningEnabledChange: (Boolean) -> Unit,
    onDismissError: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val canSend = (input.isNotBlank() || attachedImage != null) && !isSending

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

            AnimatedVisibility(visible = canAttachImage || reasoningSupported || attachedImage != null) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (attachedImage != null) {
                        AttachedImageChip(
                            attachment = attachedImage,
                            onClear = onClearImage,
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (canAttachImage) {
                            ImageAttachButton(
                                onPickImage = onPickImage,
                                onTakePhoto = onTakePhoto,
                            )
                        }
                        if (reasoningSupported) {
                            FilterChip(
                                selected = reasoningEnabled,
                                onClick = { onReasoningEnabledChange(!reasoningEnabled) },
                                label = { Text("Thinking") },
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
                            onSend()
                        },
                        enabled = canSend,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary,
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
        }
    }
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
    onSystemProfileSelect: (String) -> Unit,
    onSystemProfileNameChange: (String) -> Unit,
    onSystemPromptChange: (String) -> Unit,
    onSystemProfileSave: () -> Unit,
    onSystemProfileCreate: () -> Unit,
    onSystemProfileDelete: (String) -> Unit,
    onRefreshModels: () -> Unit,
    onExportChats: () -> Unit,
    onImportChats: () -> Unit,
) {
    val scrollState = rememberScrollState()

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
