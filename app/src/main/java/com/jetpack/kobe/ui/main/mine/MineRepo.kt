package com.jetpack.kobe.ui.main.mine

import com.jetpack.jplib.base.BaseRepository
import com.jetpack.kobe.http.ApiService
import com.jetpack.kobe.http.RetrofitManager


/**
 * des 我的
 * @date 2020/7/10
 * @author zs
 */
class MineRepo: BaseRepository() {

    suspend fun getInternal() = withIO {
        RetrofitManager.getApiService(ApiService::class.java)
            .getIntegral()
            .data()
    }

}