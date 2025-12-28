package com.restful.retrofit

import com.restful.HiResponse
import java.lang.reflect.Type

/**
 * @author yuandunbin
 * @date 2022/11/5
 */
interface HiConvert {
    fun <T> convert(rawData: String?, dataType: Type): HiResponse<T>
}