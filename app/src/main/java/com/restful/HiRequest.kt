package com.restful

import androidx.annotation.IntDef
import java.lang.reflect.Type

/**
 * @author yuandunbin
 * @date 2022/10/30
 */
class HiRequest {
    @METHOD
    var httpMethod: Int = 0
    var headers: MutableMap<String, String>? = null
    var parameters: MutableMap<String, String>? = null
    var domainUrl: String? = null
    var relativeUrl: String? = null
    var returnType: Type? = null
    var formPost: Boolean = true

    @IntDef(value = [METHOD.GET, METHOD.POST])
    annotation class METHOD {
        companion object {
            const val GET = 0
            const val POST = 1
        }
    }

    fun endPointUrl(): String {
        if (relativeUrl == null) {
            throw IllegalStateException("relativeUrl must be not null")
        }

        if (!relativeUrl!!.startsWith("/")) {
            return domainUrl + relativeUrl
        }
        val index = domainUrl!!.indexOf("/")
        return domainUrl!!.substring(0, index) + relativeUrl
    }

    fun addHeader(name: String, value: String) {
        if (headers == null) {
            headers = mutableMapOf()
        }
        headers!![name] = value

    }
}