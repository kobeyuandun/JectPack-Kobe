package com.zs.zs_jetpack.ui.main.tab

import com.jetpack.jplib.base.BaseRepository
import com.jetpack.kobe.constants.Constants
import com.jetpack.kobe.http.ApiService
import com.jetpack.kobe.http.RetrofitManager


/**
 * des tab
 * @date 2020/7/7
 * @author zs
 */
class TabRepo : BaseRepository() {


    suspend fun getTab(type: Int) = withIO {
        if (type == Constants.PROJECT_TYPE) {
            RetrofitManager.getApiService(ApiService::class.java)
                .getProjectTabList()
                .data()
        } else {
            RetrofitManager.getApiService(ApiService::class.java)
                .getAccountTabList()
                .data()
        }
    }
}
