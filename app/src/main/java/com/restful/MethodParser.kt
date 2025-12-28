package com.restful

import com.restful.annotation.*
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author yuandunbin
 * @date 2022/10/30
 */
class MethodParser(val baseUrl: String, method: Method, args: Array<Any>) {
    private var returnType: Type? = null
    private var relativeUrl: String? = null
    private var domainUrl: String? = null
    private var httpMethod: Int = 0
    private var fromPost: Boolean = true
    private var headers: MutableMap<String, String> = mutableMapOf()
    private var parameters: MutableMap<String, String> = mutableMapOf()

    init {
        parseMethodAnnotations(method)
        parseMethodParameters(method, args)
        parseMethodReturnType(method)
    }

    private fun parseMethodReturnType(method: Method) {
        if (method.returnType != HiCall::class.java) {
            throw IllegalStateException(
                String.format(
                    "method %s must be type of HiCall.class",
                    method.name
                )
            )
        }
        val genericReturnType = method.genericReturnType
        if (genericReturnType is ParameterizedType) {
            val actualTypeArguments = genericReturnType.actualTypeArguments
            require(actualTypeArguments.size == 1) { "method %s can only has one generic return type" }
            returnType = actualTypeArguments[0]
        } else {
            throw IllegalStateException(
                String.format(
                    "method %s can only has one generic return type",
                    method.name
                )
            )
        }
    }

    private fun isPrimitive(value: Any) :Boolean {
        if (value.javaClass == String::class.java) {
            return true
        }
        try {
            val field = value.javaClass.getField("TYPE")
            val clazz = field[null] as Class<*>
            if (clazz.isPrimitive) {
                return true
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * parse method parameters such as path, filed
     */
    private fun parseMethodParameters(method: Method, args: Array<Any>) {
        val parameterAnnotations = method.parameterAnnotations
        val equals = parameterAnnotations.size == args.size
        require(equals) {
           String.format("arguments annotations count %s don`t match expect count %s", parameterAnnotations.size, args.size)
        }
        for (index in args.indices) {
            val annotations = parameterAnnotations[index]
            require(annotations.size <= 1) {
                "filed can only has one annotation : index =$index"
            }
            val value = args[index]
            require(isPrimitive(value)) {
                "8 basic types are supported for now, index = $index"
            }
            val annotation = annotations[0]
            if (annotation is Filed) {
                val key = annotation.value
                val value = args[index]
                parameters[key] = value.toString()
            } else if (annotation is Path) {
                val replaceName = annotation.value
                val replacement = value.toString()
                if (replaceName != null && replacement != null) {
                    val newRelativeUrl = relativeUrl?.replace("$replaceName", replacement)
                    relativeUrl = newRelativeUrl
                }
            } else {
                throw IllegalStateException("cannot handle parameters annotation:" + annotation.javaClass.toString())
            }
        }

    }

    /**
     * parse method annotations such as get, headers, post, baseurl
     */
    private fun parseMethodAnnotations(method: Method) {
        val annotations = method.annotations
        for (annotation in annotations) {
//            when(annotation) {
//                is GET -> {
//                    relativeUrl = annotation.value
//                    httpMethod = HiRequest.METHOD.GET
//                }
//
//                is POST -> {
//                    relativeUrl = annotation.value
//                    httpMethod = HiRequest.METHOD.POST
//                    fromPost = annotation.formPost
//                }
//                is Headers -> {
//                    val headersArray = annotation.value
//                    for (header in headersArray) {
//                        val colon = header.indexOf(":")
//                        check(!(colon == 0 || colon == -1)) {
//                            String.format("@headers value must be in the from [name : value], but found %s", header)
//                        }
//                        val name = header.substring(0, colon)
//                        val value = header.substring(colon + 1).trim()
//                        headers[name] = value
//                    }
//                }
//                is BaseUrl -> {
//                    domainUrl = annotation.value
//                }
//                else -> {
//                    throw IllegalStateException("cannot handle method annotation:" + annotation.javaClass.toString())
//                }
//            }
            if (annotation is GET) {
                relativeUrl = annotation.value
                httpMethod = HiRequest.METHOD.GET
            } else if (annotation is POST) {
                relativeUrl = annotation.value
                httpMethod = HiRequest.METHOD.POST
                fromPost = annotation.formPost
            } else if (annotation is Headers) {
                val headersArray = annotation.value
                for (header in headersArray) {
                    val colon = header.indexOf(":")
                    check(!(colon == 0 || colon == -1)) {
                        String.format("@headers value must be in the from [name : value], but found %s", header)
                    }
                    val name = header.substring(0, colon)
                    val value = header.substring(colon + 1).trim()
                    headers[name] = value
                }
            } else if (annotation is BaseUrl) {
                domainUrl = annotation.value
            } else {
                throw IllegalStateException("cannot handle method annotation:" + annotation.javaClass.toString())
            }

        }
        require(httpMethod == HiRequest.METHOD.GET || httpMethod == HiRequest.METHOD.POST) {
            String.format("method must has one of GET, POST", method.name)
        }
        if (domainUrl == null)
            domainUrl = baseUrl
    }

    fun newRequest(): HiRequest{
        var request = HiRequest()
        request.domainUrl = domainUrl
        request.httpMethod = httpMethod
        request.headers = headers
        request.parameters = parameters
        request.returnType = returnType
        request.relativeUrl = relativeUrl
        request.formPost = fromPost
        return request
    }

    companion object {
        fun parser(baseUrl: String, method: Method, args: Array<Any>): MethodParser {
            return MethodParser(baseUrl, method, args)
        }
    }
}