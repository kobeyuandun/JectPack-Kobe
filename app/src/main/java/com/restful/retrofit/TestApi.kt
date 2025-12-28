package com.restful.retrofit

import com.restful.HiCall
import com.restful.HiRequest
import com.restful.annotation.Filed
import com.restful.annotation.GET
import org.json.JSONObject

/**
 * @author yuandunbin
 * @date 2022/11/5
 */
interface TestApi {
    @GET("cities")
    fun listCity(@Filed("name") name: String) :HiCall<JSONObject>
}