package com.restful

/**
 * @author yuandunbin
 * @date 2022/10/30
 */
interface HiInterceptor {
    fun intercept(chain: Chain): Boolean

    interface Chain {
        val isRequestPeroid :Boolean get() = false
        fun request(): HiRequest
        fun response(): HiResponse<*>?
    }
}