package com.restful

import java.io.IOException

/**
 * @author yuandunbin
 * @date 2022/10/30
 */
interface HiCall<T> {
    @Throws(IOException::class)
    fun excute():HiResponse<T>

    fun enqueue(callback: HiCallback<T>)

    interface Factory {
        fun newCall(request: HiRequest) :HiCall<*>
    }
}