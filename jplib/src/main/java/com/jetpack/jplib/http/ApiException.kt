package com.jetpack.jplib.http

/**
 * @author yuandunbin
 * @date 2022/4/17
 */
class ApiException(val errorMessage : String, val errorCode: Int):Throwable() {
}