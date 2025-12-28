package com.restful.retrofit

import com.restful.HiRestful

/**
 * @author yuandunbin
 * @date 2022/11/5
 */
object ApiFactory {
    private val baseUrl = "https://api.devio.org/as/"
    private val hiRestful = HiRestful(baseUrl, RetrofitCallFactory(baseUrl))
    init {
        hiRestful.addInterceptor(BizInterceptor())
    }
    fun <T> create(service: Class<T>) :T {
        return hiRestful.create(service)
    }
}