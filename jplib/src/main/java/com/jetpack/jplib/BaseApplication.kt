package com.jetpack.jplib

import android.app.Application
import android.content.Context

/**
 * @author yuandunbin
 * @date 2022/4/17
 */
open class BaseApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        baseApplication = this
    }

    companion object{
        private lateinit var baseApplication: BaseApplication

        fun getContext(): Context {
            return baseApplication
        }
    }
}