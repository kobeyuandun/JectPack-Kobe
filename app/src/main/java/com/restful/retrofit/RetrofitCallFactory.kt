package com.restful.retrofit

import com.restful.HiCall
import com.restful.HiCallback
import com.restful.HiRequest
import com.restful.HiResponse
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

/**
 * @author yuandunbin
 * @date 2022/11/4
 */
class RetrofitCallFactory(val baseUrl: String) : HiCall.Factory {

    private var apiService: ApiService

    private val gsonConvert: HiConvert

    init {
        val retrofit = Retrofit.Builder().baseUrl(baseUrl).build()
        apiService = retrofit.create(ApiService::class.java)
        gsonConvert = GsonConvert()
    }

    override fun newCall(request: HiRequest): HiCall<Any> {
        return RetrofitCall(request)

    }

    internal inner class RetrofitCall<T>(val request: HiRequest) : HiCall<T> {
        override fun excute(): HiResponse<T> {
            val realCall = createRealCall(request)
            val response = realCall.execute()
            return parseResponse(response)
        }

        private fun parseResponse(response: Response<ResponseBody>): HiResponse<T> {
            var rawData: String? = null
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    rawData = body.toString()
                }
            } else {
                val body = response.errorBody()
                if (body != null) {
                    rawData = body.toString()
                }
            }
            return gsonConvert.convert(rawData, request.returnType!!)
        }

        override fun enqueue(callback: HiCallback<T>) {
            val realCall = createRealCall(request)
            realCall.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val response = parseResponse(response)
                    callback.onSuccess(response)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                     callback.onFailed(t)
                }
            })
        }

        private fun createRealCall(request: HiRequest): Call<ResponseBody> {
            if (request.httpMethod == HiRequest.METHOD.GET) {
                return apiService.get(request.headers, request.endPointUrl(), request.parameters)
            } else if (request.httpMethod == HiRequest.METHOD.POST) {
                val parameters = request.parameters
                val builder = FormBody.Builder()
                var requestBody: RequestBody? = null
                var jsonObject = JSONObject()
                for ((key, value) in parameters!!) {
                    if (request.formPost) {
                        builder.add(key, value)
                    } else {
                        jsonObject.put(key, value)
                    }
                }
                if (request.formPost) {
                    requestBody = builder.build()
                } else {
                    requestBody = RequestBody.create(
                        "application/json;utf-8".toMediaTypeOrNull(),
                        jsonObject.toString()
                    )
                }
                return apiService.post(request.headers, request.endPointUrl(), requestBody)
            } else {
                throw IllegalStateException("hirestful only support GET POST for now, url=" + request.endPointUrl())
            }
        }
    }

    interface ApiService {
        @retrofit2.http.GET
        fun get(
            @HeaderMap headers: MutableMap<String, String>?, @Url url: String,
            @QueryMap(encoded = true) params: MutableMap<String, String>?
        ): Call<ResponseBody>

        @POST
        fun post(
            @HeaderMap headers: MutableMap<String, String>?, @Url url: String,
            @Body body: RequestBody?
        ): Call<ResponseBody>
    }
}