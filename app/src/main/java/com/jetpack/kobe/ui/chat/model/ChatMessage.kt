package com.jetpack.kobe.ui.chat.model

/**
 * 聊天消息数据模型
 * @param role 角色：system/user/assistant
 * @param content 消息内容
 */
data class ChatMessage(
    val role: String,
    val content: String
) {
    companion object {
        const val ROLE_SYSTEM = "system"
        const val ROLE_USER = "user"
        const val ROLE_ASSISTANT = "assistant"

        /**
         * 创建用户消息
         */
        fun user(content: String) = ChatMessage(ROLE_USER, content)

        /**
         * 创建助手消息
         */
        fun assistant(content: String) = ChatMessage(ROLE_ASSISTANT, content)

        /**
         * 创建系统消息
         */
        fun system(content: String) = ChatMessage(ROLE_SYSTEM, content)
    }
}

/**
 * 聊天请求体
 * @param model 模型名称
 * @param messages 消息列表
 * @param stream 是否流式输出
 * @param temperature 温度参数 (0-1)
 */
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val stream: Boolean = true,
    val temperature: Double = 0.7
)

/**
 * SSE 流式响应的数据块
 * @param id 请求ID
 * @param choices 选择列表
 * @param model 模型名称
 */
data class ChatStreamChunk(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>
) {
    data class Choice(
        val index: Int,
        val delta: Delta,
        val finishReason: String?
    ) {
        data class Delta(
            val role: String? = null,
            val content: String? = null
        )
    }

    /**
     * 获取当前增量内容
     */
    fun getContent(): String? {
        return choices.firstOrNull()?.delta?.content
    }

    /**
     * 是否完成
     */
    fun isDone(): Boolean {
        return choices.firstOrNull()?.finishReason != null
    }
}

/**
 * 聊天配置
 */
data class ChatConfig(
    val baseUrl: String = "https://api.example.com/v1",
    val apiKey: String = "",
    val model: String = "gpt-3.5-turbo"
) {
    companion object {
        /**
         * 豆包配置示例
         */
        fun doubao(apiKey: String) = ChatConfig(
            baseUrl = "https://ark.cn-beijing.volces.com/api/v3",
            apiKey = apiKey,
            model = "ep-2024..."
        )

        /**
         * OpenAI 配置
         */
        fun openai(apiKey: String) = ChatConfig(
            baseUrl = "https://api.openai.com/v1",
            apiKey = apiKey,
            model = "gpt-3.5-turbo"
        )

        /**
         * 通义千问配置
         */
        fun qwen(apiKey: String) = ChatConfig(
            baseUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1",
            apiKey = apiKey,
            model = "qwen-turbo"
        )
    }
}
