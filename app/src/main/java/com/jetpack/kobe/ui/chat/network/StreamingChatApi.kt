package com.jetpack.kobe.ui.chat.network

import com.jetpack.kobe.ui.chat.model.ChatConfig
import com.jetpack.kobe.ui.chat.model.ChatRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException

/**
 * SSE 流式聊天 API 客户端
 * 使用 OkHttp 直接处理 SSE 流式响应
 */
open class StreamingChatApi(
    protected val config: ChatConfig
) {
    private val client = OkHttpClient.Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * 发起流式聊天请求
     * @param request 聊天请求
     * @return Flow<String> 每次emit一个字符/词片段
     */
    open fun streamChat(request: ChatRequest): Flow<String> = flow {
        val jsonBody = buildRequestJson(request)

        val httpRequest = Request.Builder()
            .url("${config.baseUrl}/chat/completions")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer ${config.apiKey}")
            .addHeader("Accept", "text/event-stream")
            .addHeader("Cache-Control", "no-cache")
            .post(jsonBody.toRequestBody("application/json".toMediaType()))
            .build()

        try {
            val response = client.newCall(httpRequest).execute()

            if (!response.isSuccessful) {
                throw IOException("HTTP ${response.code}: ${response.message}")
            }

            response.body?.source()?.use { bufferedSource ->
                while (!bufferedSource.exhausted()) {
                    val line = bufferedSource.readUtf8Line() ?: break

                    // SSE 格式: "data: {...}"
                    if (line.startsWith("data: ")) {
                        val data = line.substring(6).trim()

                        when {
                            data == "[DONE]" -> {
                                emit("\u0000") // 发送结束标记
                                return@flow
                            }
                            data.isNotEmpty() -> {
                                // 解析 JSON 获取 delta.content
                                val content = parseStreamChunk(data)
                                if (content != null) {
                                    emit(content)
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw IOException("Stream chat failed: ${e.message}", e)
        }
    }

    /**
     * 构建请求 JSON
     */
    private fun buildRequestJson(request: ChatRequest): String {
        val messagesArray = JSONArray()
        request.messages.forEach { msg ->
            messagesArray.put(JSONObject().apply {
                put("role", msg.role)
                put("content", msg.content)
            })
        }

        return JSONObject().apply {
            put("model", request.model)
            put("messages", messagesArray)
            put("stream", request.stream)
            put("temperature", request.temperature)
        }.toString()
    }

    /**
     * 解析 SSE 流式数据块
     * 示例格式:
     * {
     *   "id": "chat-123",
     *   "object": "chat.completion.chunk",
     *   "created": 1694268190,
     *   "model": "gpt-3.5-turbo",
     *   "choices": [{
     *     "index": 0,
     *     "delta": {"content": "你"},
     *     "finish_reason": null
     *   }]
     * }
     */
    private fun parseStreamChunk(json: String): String? {
        return try {
            val jsonObject = JSONObject(json)
            val choices = jsonObject.optJSONArray("choices") ?: return null
            if (choices.length() == 0) return null

            val choice = choices.getJSONObject(0)
            val delta = choice.optJSONObject("delta") ?: return null

            if (delta.has("content")) delta.getString("content") else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 取消正在进行的请求
     */
    fun cancel() {
        client.dispatcher.cancelAll()
    }
}

/**
 * 模拟 SSE API（用于测试）
 */
class MockStreamingChatApi : StreamingChatApi(
    ChatConfig(baseUrl = "", apiKey = "", model = "")
) {
    private val mockResponses = listOf(
        "我收到了你的消息，",
        "让我思考一下如何回复你...\n\n",
        "这是一个很有趣的话题！",
        "根据我的理解，",
        "这涉及到几个方面：\n",
        "1. 技术实现\n",
        "2. 用户体验\n",
        "3. 性能优化\n\n",
        "希望能帮到你！"
    )

    override fun streamChat(request: ChatRequest): Flow<String> = flow {
        // 模拟网络延迟
        kotlinx.coroutines.delay(500)

        mockResponses.forEach { chunk ->
            kotlinx.coroutines.delay((50..150).random().toLong())
            emit(chunk)
        }

        emit("\u0000") // 结束标记
    }
}
