package com.jetpack.kobe.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.animation.core.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import com.jetpack.kobe.bean.MsgBean
import com.jetpack.kobe.ui.voice.DoubaoVoiceCallActivity
import kotlinx.coroutines.delay

/**
 * Jetpack Compose 版本的聊天 Fragment
 * 支持真实网络请求的 SSE 流式输出
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

    /**
     * 启动语音通话
     */
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
    val streamingText by viewModel.streamingText.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val listState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }

    // 计算总消息数（包括历史消息和正在流式输出的消息）
    val totalMessageCount = messages.size + if (streamingText.isNotEmpty()) 1 else 0

    // 自动滚动到最新消息
    LaunchedEffect(totalMessageCount, streamingText) {
        if (totalMessageCount > 0) {
            try {
                listState.animateScrollToItem(
                    index = (totalMessageCount - 1).coerceAtLeast(0)
                )
            } catch (e: Exception) {
                // 忽略滚动异常
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
                onPlusClick = {
                    // 扩展功能菜单
                }
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
                // 历史消息
                items(
                    items = messages,
                    key = { it.id }
                ) { messageState ->
                    val playAnimation = messageState.isTyping && messageState.msgBean.type == MsgBean.TYPE_RECEIVED
                    MessageBubbleContent(
                        messageId = messageState.id,
                        content = messageState.msgBean.content,
                        isSentByMe = messageState.msgBean.type == MsgBean.TYPE_SENT,
                        playAnimation = playAnimation,
                        isStreaming = false
                    )
                }

                // 正在流式输出的消息
                if (streamingText.isNotEmpty()) {
                    item(key = "streaming") {
                        MessageBubbleContent(
                            messageId = "streaming",
                            content = streamingText,
                            isSentByMe = false,
                            playAnimation = false,
                            isStreaming = true
                        )
                    }
                }
            }
        }
    }
}

/**
 * 流式消息气泡（入口函数）
 */
@Composable
private fun StreamingMessageBubble(
    messageId: String,
    content: String,
    isSentByMe: Boolean,
    playAnimation: Boolean = false,
    isStreaming: Boolean = false
) {
    MessageBubbleContent(
        messageId = messageId,
        content = content,
        isSentByMe = isSentByMe,
        playAnimation = playAnimation,
        isStreaming = isStreaming
    )
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
            // 清空历史按钮
            IconButton(onClick = onClearClick) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "清空",
                    tint = Color(0xFF666666)
                )
            }
            // 语音通话按钮
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
 * 消息气泡内容组件（真正的实现）
 */
@Composable
private fun MessageBubbleContent(
    messageId: String,
    content: String,
    isSentByMe: Boolean,
    playAnimation: Boolean,
    isStreaming: Boolean
) {
    // 发送的消息直接显示完整内容，不需要动画状态
    if (isSentByMe || isStreaming) {
        SimpleMessageBubble(
            content = content,
            isSentByMe = isSentByMe,
            isStreaming = isStreaming
        )
        return
    }

    // 接收消息：根据 playAnimation 决定是否显示动画
    if (playAnimation) {
        // 需要播放动画的消息，使用 AnimatingMessageBubble
        AnimatingMessageBubble(
            messageId = messageId,
            content = content
        )
    } else {
        // 不需要动画，直接显示完整内容
        SimpleMessageBubble(
            content = content,
            isSentByMe = false,
            isStreaming = false
        )
    }
}

/**
 * 带打字机动画的消息气泡
 */
@Composable
private fun AnimatingMessageBubble(
    messageId: String,
    content: String
) {
    // 状态只与 messageId 绑定，每个 messageId 只执行一次动画
    var displayedText by remember(messageId) { mutableStateOf("") }
    var isAnimating by remember(messageId) { mutableStateOf(true) }

    // 只在首次创建时启动动画
    LaunchedEffect(messageId) {
        if (displayedText.isEmpty()) {
            val chars = content.toList()
            chars.forEachIndexed { index, _ ->
                delay(20)
                displayedText = content.take(index + 1)
            }
            isAnimating = false
        }
    }

    val showCursor = isAnimating

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        // 对方头像
        Avatar(
            name = "助",
            backgroundColor = Color(0xFF6366F1),
            modifier = Modifier.size(38.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.widthIn(max = 260.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 16.dp,
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
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = displayedText,
                        color = Color(0xFF1A1A1A),
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )

                    // 光标效果
                    if (showCursor) {
                        Spacer(modifier = Modifier.width(2.dp))

                        // 闪烁光标动画
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
                text = getCurrentTime(),
                fontSize = 11.sp,
                color = Color(0xFF9CA3AF),
                modifier = Modifier.padding(start = 6.dp, top = 4.dp)
            )
        }
    }
}

/**
 * 不带动画的简单消息气泡
 */
@Composable
private fun SimpleMessageBubble(
    content: String,
    isSentByMe: Boolean,
    isStreaming: Boolean
) {
    val showCursor = isStreaming

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
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = content,
                        color = if (isSentByMe) Color.White else Color(0xFF1A1A1A),
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )

                    // 光标效果（仅流式输出时）
                    if (showCursor && !isSentByMe) {
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
                text = getCurrentTime(),
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
 * 消息气泡（保留原有接口用于兼容）
 */
@Composable
fun MessageBubble(
    message: String,
    isSentByMe: Boolean
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
                Text(
                    text = message,
                    color = if (isSentByMe) Color.White else Color(0xFF1A1A1A),
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }

            Text(
                text = getCurrentTime(),
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
    // 发送后自动重新获取焦点，保持键盘打开
    LaunchedEffect(messageText) {
        if (messageText.isEmpty()) {
            // 延迟请求焦点，确保输入框完成重组
            kotlinx.coroutines.delay(50)
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
            // 语音按钮
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

            // 输入框
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
                // 移除 enabled = !isLoading，避免失去焦点导致键盘隐藏
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = { onSendClick() }
                )
            )

            // 表情按钮
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

            // 加号按钮
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

            // 发送按钮 / 停止按钮
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isLoading -> Color(0xFFEF4444) // 红色停止按钮
                            messageText.isNotBlank() -> Color(0xFF22C55E)
                            else -> Color(0xFFE5E7EB)
                        }
                    )
                    .clickable(
                        onClick = onSendClick
                    ),
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

// ============ 预览 ============

@Preview(showBackground = true, heightDp = 800)
@Composable
fun ChatScreenPreview() {
    MaterialTheme {
        val viewModel = ChatViewModel()
        ChatScreen(viewModel = viewModel, onBackClick = {}, onVoiceCallClick = {})
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun MessageBubblePreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
    ) {
        StreamingMessageBubble(
            messageId = "msg1",
            content = "你好！很高兴认识你",
            isSentByMe = false,
            playAnimation = true,
            isStreaming = false
        )
        Spacer(modifier = Modifier.height(16.dp))
        StreamingMessageBubble(
            messageId = "msg2",
            content = "我也很高兴认识你！这是用 Jetpack Compose 构建的聊天界面",
            isSentByMe = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        StreamingMessageBubble(
            messageId = "msg3",
            content = "这是正在流式输出的消息，你会看到文字逐字逐句地出现",
            isSentByMe = false,
            isStreaming = true
        )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 80)
@Composable
fun ChatInputBarPreview() {
    MaterialTheme {
        val focusRequester = remember { FocusRequester() }
        ChatInputBar(
            messageText = "输入消息",
            isLoading = false,
            focusRequester = focusRequester,
            onMessageChange = {},
            onSendClick = {},
            onPlusClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 60)
@Composable
fun ChatTopBarPreview() {
    ChatTopBar(
        onBackClick = {},
        onVoiceCallClick = {},
        userName = "智能助手",
        userStatus = "在线"
    )
}
