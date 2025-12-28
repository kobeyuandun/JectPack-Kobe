package com.restful.retrofit

import android.util.Log
import com.restful.HiInterceptor
import kotlin.math.log

/**
 * @author yuandunbin
 * @date 2022/11/5
 */
class BizInterceptor :HiInterceptor {
    override fun intercept(chain: HiInterceptor.Chain): Boolean {

        if (chain.isRequestPeroid) {
            val request = chain.request()
            request.addHeader("auth-token", "11111")
        } else if (chain.response() != null) {
            Log.d("BizInterceptor", chain.request().endPointUrl())
            Log.d("BizInterceptor", chain.response()!!.rawData)
        }
        return false
    }
}