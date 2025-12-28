package com.restful.annotation

/**
 * @author yuandunbin
 * @date 2022/10/30
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class GET(val value : String)
