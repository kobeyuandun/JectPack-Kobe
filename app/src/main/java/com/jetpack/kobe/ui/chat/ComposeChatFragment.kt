package com.jetpack.kobe.ui.chat

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jetpack.kobe.bean.MsgBean

/**
 * Jetpack Compose 版本的聊天 Fragment
 * 这是一个纯 Compose 实现的聊天界面，可以在项目中使用
 */
class ComposeChatFragment : Fragment() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ChatScreen(
                    onBackClick = { findNavController().navigateUp() }
                )
            }
        }
    }
}

/**
 * 聊天界面主屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBackClick: () -> Unit = {}
) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<MsgBean>() }
    val listState = rememberLazyListState()
    val handler = remember { Handler(Looper.getMainLooper()) }

    // 初始化欢迎消息
    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages.add(MsgBean("你好！很高兴认识你 👋\n这是使用 Jetpack Compose 构建的聊天界面", MsgBean.TYPE_RECEIVED))
        }
    }

    // 自动滚动到最新消息
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            try {
                listState.animateScrollToItem(
                    index = messages.size - 1
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
                userName = "智能助手",
                userStatus = "在线"
            )
        },
        bottomBar = {
            ChatInputBar(
                messageText = messageText,
                onMessageChange = { messageText = it },
                onSendClick = {
                    if (messageText.isNotBlank()) {
                        // 添加发送的消息
                        messages.add(MsgBean(messageText, MsgBean.TYPE_SENT))
                        val sentMessage = messageText
                        messageText = ""

                        // 模拟接收回复
                        handler.postDelayed({
                            val replies = listOf(
                                "我收到了你的消息：$sentMessage",
                                "很有趣的想法！✨",
                                "让我想想... 🤔",
                                "你说得对！👍",
                                "继续说，我听着呢 👂",
                                "这个话题很有意思！💡",
                                "原来如此！😊",
                                "我完全同意你的看法",
                                "这确实是个好问题",
                                "感谢你的分享！"
                            )
                            val randomReply = replies.random()
                            messages.add(MsgBean(randomReply, MsgBean.TYPE_RECEIVED))
                        }, (800..1500).random().toLong())
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
                items(
                    items = messages,
                    key = { messages.indexOf(it).toString() + it.content }
                ) { msg ->
                    MessageBubble(
                        message = msg.content,
                        isSentByMe = msg.type == MsgBean.TYPE_SENT
                    )
                }
            }
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
                    color = Color(0xFF07C160)
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
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "语音通话",
                    tint = Color(0xFF666666)
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
 * 消息气泡
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
            // 对方头像
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
            // 我的头像
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
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onPlusClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
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
                    .heightIn(min = 40.dp),
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

            // 发送按钮
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (messageText.isNotBlank()) Color(0xFF22C55E) else Color(0xFFE5E7EB)
                    )
                    .clickable(
                        enabled = messageText.isNotBlank(),
                        onClick = onSendClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "发送",
                    tint = if (messageText.isNotBlank()) Color.White else Color(0xFF9CA3AF),
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
        ChatScreen(onBackClick = {})
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
        MessageBubble(
            message = "你好！很高兴认识你",
            isSentByMe = false
        )
        Spacer(modifier = Modifier.height(16.dp))
        MessageBubble(
            message = "我也很高兴认识你！这是用 Jetpack Compose 构建的聊天界面",
            isSentByMe = true
        )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 80)
@Composable
fun ChatInputBarPreview() {
    MaterialTheme {
        ChatInputBar(
            messageText = "输入消息",
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
        userName = "智能助手",
        userStatus = "在线"
    )
}
