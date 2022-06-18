package com.jetpack.jplib.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpack.jplib.common.toast
import com.jetpack.jplib.http.ApiException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * @author yuandunbin
 * @date 2022/4/17
 */

typealias vmError = (e: ApiException) -> Unit

open class BaseViewModel : ViewModel() {
    /**
     * 错误信息livedata
     */
    val errorLiveData = MutableLiveData<ApiException>()

    /**
     * 无更多数据
     */
    val footLiveData = MutableLiveData<Any>()

    /**
     *  无数据
     */
    val emptyLiveData = MutableLiveData<Any>()

    protected fun <T> launch(block: () -> T, error: vmError? = null) {
        viewModelScope.launch {
            runCatching {
                block()
            }.onFailure {
                it.printStackTrace()
                getApiException(it).apply {
                    withContext(Dispatchers.Main) {
                        error?.invoke(this@apply)
                        toast(errorMessage)
                    }
                }
            }
        }
    }

    protected fun <T> launch(block: suspend () -> T) {
        viewModelScope.launch {
            runCatching {
                block()
            }.onFailure {
                it.printStackTrace()
                getApiException(it).apply {
                    //todo 测试环境打开日志
//                    if (BuildConfig.DEBUG) {
//                        it.printStackTrace()
//                        return@onFailure
//                    }
                    withContext(Dispatchers.Main) {
                        toast(errorMessage)
                        errorLiveData.value = this@apply
                    }
                }
            }
        }
    }
    private fun getApiException(e: Throwable): ApiException {
        return when (e) {
            is UnknownHostException -> {
                ApiException("网络异常", -100)
            }
            is JSONException -> {
                ApiException("数据异常", -100)
            }
            is SocketTimeoutException -> {
                ApiException("连接超时", -100)
            }
            is ConnectException -> {
                ApiException("连接错误", -100)
            }
            is HttpException -> {
                ApiException("http code ${e.code()}", -100)
            }
            is ApiException -> {
                e
            }
            /**
             * 如果协程还在运行，个别机型退出当前界面时，viewModel会通过抛出CancellationException，
             * 强行结束协程，与java中InterruptException类似，所以不必理会,只需将toast隐藏即可
             */
            is CancellationException -> {
                ApiException("", -10)
            }
            else -> {
                ApiException("未知错误", -100)
            }
        }

    }

    /**
     * 处理列表是否有更多数据
     */
    protected fun<T> handleList(listLiveData: LiveData<MutableList<T>>, pageSize:Int = 20){
        val listSize = listLiveData.value?.size?:0
        if (listSize % pageSize != 0){
            footLiveData.value = 1
        }
    }
}