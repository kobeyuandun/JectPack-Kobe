package com.jetpack.kobe.http

/**
 * @author yuandunbin
 * @date 2022/4/24
 */
class RetrofitManager {
    companion object {
        // 存储 apiservice
        private val map = mutableMapOf<Class<*>, Any>()
        private val factory = RetrofitFactory.factory()

        //动态指定域名
        fun <T : Any> getApiService(clazz: Class<T>): T {
            return getAService(clazz)
        }

        //单例
        private fun <T : Any> getAService(apiClass: Class<T>): T {
            return if (map[apiClass] == null) {
                synchronized(RetrofitManager::class.java) {
                    val t = factory.create(apiClass)
                    if (map[apiClass] == null) {
                        map[apiClass] = t
                    }
                    t
                }
            } else {
                map[apiClass] as T
            }
        }
    }
}