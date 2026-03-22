package com.jetpack.kobe.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpack.kobe.ui.chat.model.ChatMessage
import com.jetpack.kobe.ui.chat.network.MockStreamingChatApi
import com.jetpack.kobe.ui.chat.network.StreamingChatApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 聊天界面 ViewModel - 重构版
 *
 * 核心设计原则：
 * 1. 统一的消息列表，包括正在流式输出的消息
 * 2. 只有最后一条 AI 回复消息可以是 streaming 状态
 * 3. streaming 状态的消息内容实时更新
 * 4. 新消息到来时，自动将之前的 streaming 消息标记为完成
 */
class ChatViewModel(
    private val chatApi: StreamingChatApi = MockStreamingChatApi()
) : ViewModel() {

    /**
     * 消息数据类
     * @param content 消息内容
     * @param isSentByMe 是否为发送者（我）发送的消息
     * @param isStreaming 是否正在流式输出（只有最后一条AI消息可能为true）
     * @param timestamp 时间戳
     * @param id 唯一标识
     */
    data class ChatMessageItem(
        val content: String,
        val isSentByMe: Boolean,
        val isStreaming: Boolean = false,
        val timestamp: String = getCurrentTime(),
        val id: String = UUID.randomUUID().toString()
    )

    // 消息列表（包括正在流式输出的消息）
    private val _messages = MutableStateFlow<List<ChatMessageItem>>(emptyList())
    val messages: StateFlow<List<ChatMessageItem>> = _messages.asStateFlow()

    // 是否正在发送/接收
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 当前会话的历史消息（用于 API 上下文）
    private val conversationHistory = mutableListOf<ChatMessage>()

    init {
        // 初始化欢迎消息
        _messages.value = listOf(
            ChatMessageItem(
                content = "你好！很高兴认识你 👋\n这是使用 Jetpack Compose 构建的聊天界面",
                isSentByMe = false,
                isStreaming = false
            )
        )
        conversationHistory.add(ChatMessage.assistant(_messages.value.first().content))
    }

    /**
     * 发送消息
     */
    fun sendMessage(content: String) {
        if (content.isBlank() || _isLoading.value) return

        viewModelScope.launch {
            // 1. 先将之前的 streaming 消息标记为完成
            completeStreamingMessage()

            // 2. 添加用户消息
            addUserMessage(content)
            conversationHistory.add(ChatMessage.user(content))

            // 3. 创建一条空的 AI 消息，标记为 streaming
            val streamingMessage = ChatMessageItem(
                content = "",
                isSentByMe = false,
                isStreaming = true
            )
            _messages.update { it + streamingMessage }

            // 4. 开始流式接收
            _isLoading.value = true

            try {
                chatApi.streamChat(
                    com.jetpack.kobe.ui.chat.model.ChatRequest(
                        model = "gpt-3.5-turbo",
                        messages = conversationHistory.toList(),
                        stream = true
                    )
                ).collect { chunk ->
                    when (chunk) {
                        "\u0000" -> {
                            // 流式结束
                            completeStreamingMessage()
                            _isLoading.value = false
                        }
                        else -> {
                            // 实时更新 streaming 消息的内容
                            _messages.update { messages ->
                                messages.map { msg ->
                                    if (msg.isStreaming) {
                                        msg.copy(content = msg.content + chunk)
                                    } else {
                                        msg
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // 错误处理
                _messages.update { messages ->
                    messages.map { msg ->
                        if (msg.isStreaming) {
                            msg.copy(content = "请求失败: ${e.message}", isStreaming = false)
                        } else {
                            msg
                        }
                    }
                }
                delay(2000)
                _isLoading.value = false
            }
        }
    }

    /**
     * 将当前 streaming 消息标记为完成
     */
    private fun completeStreamingMessage() {
        _messages.update { messages ->
            val updated = messages.map { msg ->
                if (msg.isStreaming) {
                    msg.copy(isStreaming = false).also {
                        // 将完成的消息添加到对话历史
                        if (it.content.isNotEmpty()) {
                            conversationHistory.add(ChatMessage.assistant(it.content))
                        }
                    }
                } else {
                    msg
                }
            }
            updated
        }
    }

    /**
     * 添加用户消息
     */
    private fun addUserMessage(content: String) {
        val userMessage = ChatMessageItem(
            content = content,
            isSentByMe = true,
            isStreaming = false
        )
        _messages.update { it + userMessage }
    }

    /**
     * 清空对话历史
     */
    fun clearHistory() {
        conversationHistory.clear()
        _isLoading.value = false
        _messages.value = listOf(
            ChatMessageItem(
                content = "你好！很高兴认识你 👋\n对话已重置",
                isSentByMe = false,
                isStreaming = false
            )
        )
        conversationHistory.add(ChatMessage.assistant(_messages.value.first().content))
    }

    /**
     * 获取当前时间
     */
    private fun getCurrentTime(): String {
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        return formatter.format(Date())
    }
}
