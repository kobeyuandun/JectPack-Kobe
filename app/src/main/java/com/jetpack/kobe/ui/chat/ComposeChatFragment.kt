package com.jetpack.kobe.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import com.jetpack.kobe.ui.voice.DoubaoVoiceCallActivity
import kotlinx.coroutines.delay

/**
 * Jetpack Compose 版本的聊天 Fragment
 * 重构版：模仿豆包的一问一答体验
 *
 * 核心设计：
 * 1. 统一的消息列表，streaming 状态消息也在列表中
 * 2. 只有最后一条接收消息可以是 streaming 状态
 * 3. streaming 状态的消息实时显示内容 + 光标
 * 4. 已完成的消息保持最终状态，滚动不复播动画
 */
class ComposeChatFragment : Fragment() {

    private val viewModel: ChatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ChatScreen(
                    viewModel = viewModel,
                    onBackClick = { findNavController().navigateUp() },
                    onVoiceCallClick = { startVoiceCall() }
                )
            }
        }
    }

    private fun startVoiceCall() {
        val channelName = "voice_call_${System.currentTimeMillis()}"
        DoubaoVoiceCallActivity.start(requireContext(), channelName, "AI 助手")
    }
}

/**
 * 聊天界面主屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onBackClick: () -> Unit = {},
    onVoiceCallClick: () -> Unit = {}
) {
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val listState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }

    // 自动滚动到最新消息
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            try {
                listState.animateScrollToItem(messages.size - 1)
            } catch (e: Exception) {
                // 忽略滚动异常
            }
        }
    }

    // 跟随 streaming 内容滚动（监听最后一条消息的 content 变化）
    val lastMessage = messages.lastOrNull()
    LaunchedEffect(lastMessage?.content, lastMessage?.isStreaming) {
        if (lastMessage != null && lastMessage.isStreaming && lastMessage.content.isNotEmpty()) {
            try {
                // 使用 scrollToItem 而不是 animateScrollToItem，避免频繁动画
                listState.scrollToItem(messages.size - 1)
            } catch (e: Exception) {
                // 忽略
            }
        }
    }

    Scaffold(
        topBar = {
            ChatTopBar(
                onBackClick = onBackClick,
                onVoiceCallClick = onVoiceCallClick,
                onClearClick = { viewModel.clearHistory() },
                userName = "智能助手",
                userStatus = if (isLoading) "正在输入..." else "在线"
            )
        },
        bottomBar = {
            ChatInputBar(
                messageText = messageText,
                isLoading = isLoading,
                focusRequester = focusRequester,
                onMessageChange = { messageText = it },
                onSendClick = {
                    if (messageText.isNotBlank() && !isLoading) {
                        val sentMessage = messageText
                        messageText = ""
                        viewModel.sendMessage(sentMessage)
                    }
                },
                onPlusClick = { }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAFC)),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = messages,
                    key = { it.id }
                ) { message ->
                    ChatMessageItem(
                        content = message.content,
                        isSentByMe = message.isSentByMe,
                        isStreaming = message.isStreaming,
                        timestamp = message.timestamp
                    )
                }
            }
        }
    }
}

/**
 * 单条消息组件
 * 核心逻辑：
 * - isSentByMe: 发送者（我/AI）
 * - isStreaming: 是否正在流式输出（只有最后一条AI消息可能为true）
 * - content: 当前内容（流式输出时实时更新）
 */
@Composable
private fun ChatMessageItem(
    content: String,
    isSentByMe: Boolean,
    isStreaming: Boolean,
    timestamp: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isSentByMe) Arrangement.End else Arrangement.Start
    ) {
        if (!isSentByMe) {
            Avatar(
                name = "助",
                backgroundColor = Color(0xFF6366F1),
                modifier = Modifier.size(38.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 260.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = if (isSentByMe) Color(0xFF22C55E) else Color.White,
                        shape = RoundedCornerShape(
                            topStart = if (isSentByMe) 16.dp else 4.dp,
                            topEnd = if (isSentByMe) 4.dp else 16.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        )
                    )
                    .padding(
                        start = 14.dp,
                        end = 14.dp,
                        top = 10.dp,
                        bottom = 10.dp
                    )
            ) {
                // 流式输出时显示闪烁光标
                val showCursor = !isSentByMe && isStreaming

                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = content,
                        color = if (isSentByMe) Color.White else Color(0xFF1A1A1A),
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )

                    // 光标效果
                    if (showCursor) {
                        Spacer(modifier = Modifier.width(2.dp))

                        val infiniteTransition = rememberInfiniteTransition(label = "cursor")
                        val alpha by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 0f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(500),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "cursor_alpha"
                        )

                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(18.dp)
                                .background(Color.Black.copy(alpha = alpha))
                        )
                    }
                }
            }

            // 时间戳
            Text(
                text = timestamp,
                fontSize = 11.sp,
                color = Color(0xFF9CA3AF),
                modifier = if (isSentByMe) {
                    Modifier.padding(top = 4.dp)
                } else {
                    Modifier.padding(start = 6.dp, top = 4.dp)
                }
            )
        }

        if (isSentByMe) {
            Spacer(modifier = Modifier.width(8.dp))
            Avatar(
                name = "我",
                backgroundColor = Color(0xFFF59E0B),
                modifier = Modifier.size(38.dp)
            )
        }
    }
}

/**
 * 聊天顶部栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    onBackClick: () -> Unit,
    onVoiceCallClick: () -> Unit,
    onClearClick: () -> Unit = {},
    userName: String,
    userStatus: String
) {
    CenterAlignedTopAppBar(
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = userName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = userStatus,
                    fontSize = 11.sp,
                    color = if (userStatus == "正在输入...") Color(0xFF6366F1) else Color(0xFF07C160)
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    tint = Color(0xFF333333)
                )
            }
        },
        actions = {
            IconButton(onClick = onClearClick) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "清空",
                    tint = Color(0xFF666666)
                )
            }
            IconButton(onClick = onVoiceCallClick) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "语音通话",
                    tint = Color(0xFF22C55E)
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.VideoCall,
                    contentDescription = "视频通话",
                    tint = Color(0xFF666666)
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "更多",
                    tint = Color(0xFF666666)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color(0xFF1A1A1A)
        )
    )
}

/**
 * 头像组件
 */
@Composable
fun Avatar(
    name: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * 聊天输入栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputBar(
    messageText: String,
    isLoading: Boolean = false,
    focusRequester: FocusRequester,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onPlusClick: () -> Unit
) {
    LaunchedEffect(messageText) {
        if (messageText.isEmpty()) {
            delay(50)
            focusRequester.requestFocus()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding(),
        shadowElevation = 4.dp,
        tonalElevation = 2.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "语音",
                    tint = Color(0xFF666666),
                    modifier = Modifier.size(22.dp)
                )
            }

            OutlinedTextField(
                value = messageText,
                onValueChange = onMessageChange,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 40.dp)
                    .focusRequester(focusRequester),
                placeholder = {
                    Text(
                        text = "输入消息...",
                        color = Color(0xFF9CA3AF),
                        fontSize = 15.sp
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color(0xFFF5F5F5),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color(0xFF22C55E)
                ),
                shape = RoundedCornerShape(20.dp),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = { onSendClick() }
                )
            )

            IconButton(
                onClick = { },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SentimentSatisfied,
                    contentDescription = "表情",
                    tint = Color(0xFF666666),
                    modifier = Modifier.size(22.dp)
                )
            }

            IconButton(
                onClick = onPlusClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "更多",
                    tint = Color(0xFF666666),
                    modifier = Modifier.size(24.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isLoading -> Color(0xFFEF4444)
                            messageText.isNotBlank() -> Color(0xFF22C55E)
                            else -> Color(0xFFE5E7EB)
                        }
                    )
                    .clickable(onClick = onSendClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isLoading) Icons.Default.Close else Icons.Default.Send,
                    contentDescription = if (isLoading) "停止" else "发送",
                    tint = if (messageText.isNotBlank() || isLoading) Color.White else Color(0xFF9CA3AF),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

/**
 * 获取当前时间
 */
fun getCurrentTime(): String {
    val now = java.util.Date()
    val formatter = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    return formatter.format(now)
}
