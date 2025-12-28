package com.restful

/**
 * @author yuandunbin
 * @date 2022/11/2
 */
open class Scheduler(private val callFactory: HiCall.Factory, val interceptors: MutableList<HiInterceptor>) {
//    var interceptors1 :MutableList<HiInterceptor> = mutableListOf()
    fun newCall(newRequest: HiRequest) :HiCall<*>{
        val newCall = callFactory.newCall(newRequest)
//        interceptors1  = interceptors
        return ProxyCall(newCall, newRequest, interceptors)

    }

    internal class ProxyCall<T>(val delegate: HiCall<T>, val request: HiRequest, val interceptors: MutableList<HiInterceptor>) :HiCall<T> {
        override fun excute(): HiResponse<T> {
            dispatchInterceptor(request, null)
            val response = delegate.excute()
            dispatchInterceptor(request, response)
            return response
        }

        override fun enqueue(callback: HiCallback<T>) {
            dispatchInterceptor(request, null)
            delegate.enqueue(object :HiCallback<T> {
                override fun onSuccess(response: HiResponse<T>) {
                    dispatchInterceptor(request, response)
                    if (callback != null) {
                        callback.onSuccess(response)
                    }
                }

                override fun onFailed(throwable: Throwable) {
                    if (callback != null) {
                        callback.onFailed(throwable)
                    }
                }

            })
        }


        private fun dispatchInterceptor(request: HiRequest, response: HiResponse<T>?) {
            InterceptorChain(request, response).dispatch()
        }

        internal inner class InterceptorChain(val request: HiRequest, val response: HiResponse<T>?) : HiInterceptor.Chain {
            var callIndex = 0
            override val isRequestPeroid: Boolean
                get() = response == null
            override fun request(): HiRequest {
                return request
            }

            override fun response(): HiResponse<*>? {
                return response
            }

            fun dispatch() {
                //todo 先引用它，后期有bug再说
//                val interceptors :MutableList<HiInterceptor> = mutableListOf()
                val interceptor = interceptors[callIndex]
                val intercept = interceptor.intercept(this)
                if (!intercept && callIndex < interceptors.size) {
                    dispatch()
                }
            }

        }
    }
}