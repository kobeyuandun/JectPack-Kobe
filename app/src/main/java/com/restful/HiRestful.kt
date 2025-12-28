package com.restful

import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

/**
 * @author yuandunbin
 * @date 2022/10/30
 */
class HiRestful constructor(val baseUrl: String,val callFactory: HiCall.Factory) {
    private var interceptors: MutableList<HiInterceptor> = mutableListOf()
    private var methodService: ConcurrentHashMap<Method, MethodParser> = ConcurrentHashMap()
    private var scheduler :Scheduler
    fun addInterceptor(interceptor: HiInterceptor) {
        interceptors.add(interceptor)
    }

    init {
        scheduler = Scheduler(callFactory, interceptors)
    }
    fun<T> create(service: Class<T>):T {
        return Proxy.newProxyInstance(service.classLoader, arrayOf<Class<*>>(service)) {
            proxy ,method, args ->
            var methodParser = methodService[method]
            if (methodParser == null) {
                methodParser = MethodParser.parser(baseUrl, method, args)
                methodService[method] = methodParser
            }
            val newRequest = methodParser.newRequest()
            callFactory.newCall(newRequest)
            scheduler.newCall(newRequest)
        } as T
    }
}