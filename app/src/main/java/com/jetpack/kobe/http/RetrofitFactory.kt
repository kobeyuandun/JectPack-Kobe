package com.jetpack.kobe.http

import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.jetpack.jplib.BaseApplication.Companion.getContext
import com.jetpack.jplib.http.HttpLoggingInterceptor
import com.jetpack.kobe.constants.ApiConstants
import com.jetpack.kobe.constants.Constants
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.logging.Level

/**
 * @author yuandunbin
 * @date 2022/4/20
 */
object RetrofitFactory {
    private val okHttpClientBuilder: OkHttpClient.Builder
        get() {
            return OkHttpClient.Builder()
                .readTimeout(Constants.DEFAULT_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .connectTimeout(Constants.DEFAULT_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .addInterceptor(getLogInterceptor())
                .cookieJar(getCookie())
                .cache(getCache())

        }

    fun factory(): Retrofit {
        val okHttpClient = okHttpClientBuilder.build()
        return Retrofit.Builder().client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(ApiConstants.BASE_URL)
            .build()

    }

    private fun getCache(): Cache? {
        return Cache(File(getContext().cacheDir, "cache"), 1024 * 1024 * 100)
    }

    private fun getCookie(): ClearableCookieJar {
        return PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(getContext()))
    }

    private fun getLogInterceptor(): Interceptor {
        return HttpLoggingInterceptor("OkHttp").apply {
            setPrintLevel(HttpLoggingInterceptor.Level.BODY)
            setColorLevel(Level.INFO)
        }
    }
}