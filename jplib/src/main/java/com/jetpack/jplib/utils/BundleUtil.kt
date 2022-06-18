package com.jetpack.jplib.utils

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment

/**
 * @author yuandunbin
 * @date 2022/4/13
 */
object BundleUtil {
    fun initBundle(fragment: Fragment) {
        fragment?.arguments?.apply {
           setBundle(fragment, this)
        }
    }

    private fun setBundle(obj: Any, intent: Bundle) {
        val javaClass = obj.javaClass
        val declaredFields = javaClass.declaredFields
        for (item in declaredFields) {
            if (item.isAnnotationPresent(Param::class.java)) {
                item.getAnnotation(Param::class.java)?.let {
                    val key: String = if (TextUtils.isEmpty(it.value)) item.name else it.value
                    if (intent.containsKey(key)) {
                        val type = item.type
                        when (type) {
                            Boolean::class.javaPrimitiveType -> {
                                intent.getBoolean(key, false)
                            }
                            Int::class.javaPrimitiveType -> {
                                intent.getInt(key, 0)
                            }
                            Long::class.javaPrimitiveType -> {
                                intent.getLong(key, 0L)
                            }
                            String::class.java -> {
                                intent.getString(key)
                            }
                            Double::class.javaPrimitiveType -> {
                                intent.getDouble(key, 0.0)
                            }
                            Byte::class.javaPrimitiveType -> {
                                intent.getByte(key, "".toByte())
                            }
                            Char::class.javaPrimitiveType -> {
                                intent.getChar(key, '\u0000')
                            }
                            Float::class.javaPrimitiveType -> {
                                intent.getFloat(key, 0f)
                            }
                            else -> {
                                intent.getSerializable(key)
                            }
                        }?.apply {
                            item.isAccessible = true
                            try {
                                item[obj] = this
                            } catch (e: IllegalAccessException) {
                                e.printStackTrace()
                            }
                            item.isAccessible = false
                        }
                    }
                }
            }
        }
    }
}