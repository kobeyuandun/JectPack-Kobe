package com.jetpack.kobe.http

import com.jetpack.jplib.http.ApiException
import java.io.Serializable

/**
 * @author yuandunbin
 * @date 2022/5/18
 */
class ApiResponse<T> : Serializable {
    private var data: T? = null
    private var errorMsg = ""
    private var errorCode = 0

    fun data(): T {
        when(errorCode) {
            //请求成功
            0, 200 -> {
                return data!!
            }
            //未登陆请求需要用户信息的接口
            -1001 -> {
                throw ApiException(errorMsg, errorCode)
            }
            //登录失败
            -1 -> {
                throw ApiException(errorMsg, errorCode)
            }
        }
        throw ApiException(errorMsg, errorCode)
    }

    fun data(clazz: Class<T>) :T {
        when(errorCode) {
            //请求成功
            0, 200 -> {
                //避免业务层做null判断,通过反射将null替换为T类型空对象
                if (clazz == null) {
                    data = clazz.newInstance()
                }
                return data!!
            }
            //未登陆请求需要用户信息的接口
            -1001 -> {
                throw ApiException(errorMsg, errorCode)
            }
            //登录失败
            -1 -> {
                throw ApiException(errorMsg, errorCode)
            }
        }
        throw ApiException(errorMsg, errorCode)
    }
}