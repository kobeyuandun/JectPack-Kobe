package com.jetpack.kobe.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpack.kobe.bean.MsgBean
import com.jetpack.kobe.ui.chat.model.ChatMessage
import com.jetpack.kobe.ui.chat.network.MockStreamingChatApi
import com.jetpack.kobe.ui.chat.network.StreamingChatApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * 聊天界面 ViewModel
 * 处理消息发送、流式响应接收
 */
class ChatViewModel(
    private val chatApi: StreamingChatApi = MockStreamingChatApi()
) : ViewModel() {

    // 消息列表
    private val _messages = MutableStateFlow<List<MessageState>>(emptyList())
    val messages: StateFlow<List<MessageState>> = _messages.asStateFlow()

    // 正在流式输出的文本
    private val _streamingText = MutableStateFlow("")
    val streamingText: StateFlow<String> = _streamingText.asStateFlow()

    // 是否正在发送/接收
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 当前会话的历史消息（用于 API 上下文）
    private val conversationHistory = mutableListOf<ChatMessage>()

    init {
        // 初始化欢迎消息
        addMessage(
            content = "你好！很高兴认识你 👋\n这是使用 Jetpack Compose 构建的聊天界面",
            isSent = false,
            isTyping = true
        )
    }

    /**
     * 发送消息
     */
    fun sendMessage(content: String) {
        if (content.isBlank() || _isLoading.value) return

        viewModelScope.launch {
            // 1. 添加用户消息
            addMessage(content, isSent = true, isTyping = false)
            conversationHistory.add(ChatMessage.user(content))

            // 2. 开始流式接收
            _isLoading.value = true
            _streamingText.value = ""

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
                            // 流式结束，添加完整消息到列表
                            finishStreaming()
                        }
                        else -> {
                            // 追加文本
                            _streamingText.value += chunk
                        }
                    }
                }
            } catch (e: Exception) {
                // 错误处理
                _streamingText.value = "请求失败: ${e.message}"
                kotlinx.coroutines.delay(2000)
                finishStreaming()
            }
        }
    }

    /**
     * 完成流式输出，将内容添加到消息列表
     */
    private fun finishStreaming() {
        val fullText = _streamingText.value
        if (fullText.isNotEmpty()) {
            // 只将最后一条接收消息的 isTyping 设为 false，不影响其他消息
            _messages.update { messages ->
                val updated = messages.mapIndexed { index, msg ->
                    // 只将最后一条接收消息的动画关闭
                    if (index == messages.lastIndex &&
                        msg.msgBean.type == MsgBean.TYPE_RECEIVED) {
                        msg.copy(isTyping = false)
                    } else {
                        msg
                    }
                }
                updated + MessageState(
                    msgBean = MsgBean(fullText, MsgBean.TYPE_RECEIVED),
                    isTyping = true,  // 只有新消息播放动画
                    id = UUID.randomUUID().toString()
                )
            }
            conversationHistory.add(ChatMessage.assistant(fullText))
        }
        _streamingText.value = ""
        _isLoading.value = false
    }

    /**
     * 添加消息到列表
     */
    private fun addMessage(content: String, isSent: Boolean, isTyping: Boolean) {
        _messages.update { currentMessages ->
            // 如果是接收消息且需要播放动画，只关闭最后一条接收消息的动画
            val updated = if (!isSent && isTyping && currentMessages.isNotEmpty()) {
                currentMessages.mapIndexed { index, msg ->
                    if (index == currentMessages.lastIndex &&
                        msg.msgBean.type == MsgBean.TYPE_RECEIVED) {
                        msg.copy(isTyping = false)
                    } else {
                        msg
                    }
                }
            } else {
                currentMessages
            }
            updated + MessageState(
                msgBean = MsgBean(content, if (isSent) MsgBean.TYPE_SENT else MsgBean.TYPE_RECEIVED),
                isTyping = isTyping,
                id = UUID.randomUUID().toString()
            )
        }
    }

    /**
     * 清空对话历史
     */
    fun clearHistory() {
        conversationHistory.clear()
        _messages.value = emptyList()
        _streamingText.value = ""
        // 重新添加欢迎消息
        addMessage(
            content = "你好！很高兴认识你 👋\n对话已重置",
            isSent = false,
            isTyping = true
        )
    }

    /**
     * 重试当前消息
     */
    fun retry() {
        // 移除最后一条助手消息，重新请求
        val lastMessage = conversationHistory.lastOrNull()
        if (lastMessage?.role == ChatMessage.ROLE_ASSISTANT) {
            conversationHistory.removeLast()
            _messages.update { it.dropLast(1) }
        }

        // 重新发送用户消息（不添加到列表，直接重用）
        val _lastUserMessage = conversationHistory.lastOrNull {
            it.role == ChatMessage.ROLE_USER
        } ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _streamingText.value = ""

            try {
                chatApi.streamChat(
                    com.jetpack.kobe.ui.chat.model.ChatRequest(
                        model = "gpt-3.5-turbo",
                        messages = conversationHistory.toList(),
                        stream = true
                    )
                ).collect { chunk ->
                    when (chunk) {
                        "\u0000" -> finishStreaming()
                        else -> _streamingText.value += chunk
                    }
                }
            } catch (e: Exception) {
                _streamingText.value = "请求失败: ${e.message}"
                kotlinx.coroutines.delay(2000)
                finishStreaming()
            }
        }
    }
}

/**
 * 消息状态数据类
 * @param msgBean 原始消息数据
 * @param isTyping 是否正在打字（用于接收消息的打字机效果）
 * @param id 唯一标识符
 */
data class MessageState(
    val msgBean: MsgBean,
    val isTyping: Boolean = false,
    val id: String = UUID.randomUUID().toString()
)
