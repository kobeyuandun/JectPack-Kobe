package com.jetpack.jplib.http

import android.util.Log
import com.jetpack.jplib.http.OkLogger

object OkLogger {
    private var isLogEnable = true
    private var tag = "OkHttp"
    fun debug(isEnable: Boolean) {
        debug(tag, isEnable)
    }

    fun debug(logTag: String, isEnable: Boolean) {
        tag = logTag
        isLogEnable = isEnable
    }

    fun v(msg: String?) {
        v(tag, msg)
    }

    fun v(tag: String?, msg: String?) {
        if (isLogEnable && msg != null) Log.v(tag ?: "OkHttp", msg)
    }

    fun d(msg: String?) {
        d(tag, msg)
    }

    fun d(tag: String?, msg: String?) {
        if (isLogEnable && msg != null) Log.d(tag ?: "OkHttp", msg)
    }

    fun i(msg: String?) {
        i(tag, msg)
    }

    fun i(tag: String?, msg: String?) {
        if (isLogEnable && msg != null) Log.i(tag ?: "OkHttp", msg)
    }

    fun w(msg: String?) {
        w(tag, msg)
    }

    fun w(tag: String?, msg: String?) {
        if (isLogEnable && msg != null) Log.w(tag ?: "OkHttp", msg)
    }

    fun e(msg: String?) {
        e(tag, msg)
    }

    fun e(tag: String?, msg: String?) {
        if (isLogEnable && msg != null) Log.e(tag ?: "OkHttp", msg)
    }

    fun printStackTrace(t: Throwable?) {
        if (isLogEnable && t != null) t.printStackTrace()
    }
}