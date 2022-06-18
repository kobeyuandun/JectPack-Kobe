package com.jetpack.jplib.utils

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @author yuandunbin
 * @date 2022/4/14
 */
@Target(AnnotationTarget.FIELD)
@Retention(RetentionPolicy.RUNTIME)
annotation class Param(val value: String = "")
