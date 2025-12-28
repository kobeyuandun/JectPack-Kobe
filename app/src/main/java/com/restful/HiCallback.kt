package com.restful

/**
 * @author yuandunbin
 * @date 2022/10/30
 */
interface HiCallback<T> {
    fun onSuccess(response : HiResponse<T>)
    fun onFailed(throwable: Throwable)
}