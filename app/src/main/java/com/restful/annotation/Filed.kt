package com.restful.annotation

/**
 * @author yuandunbin
 * @date 2022/10/30
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Filed(val value : String)
