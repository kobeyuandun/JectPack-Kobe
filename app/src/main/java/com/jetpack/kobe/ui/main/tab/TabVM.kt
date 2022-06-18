package com.zs.zs_jetpack.ui.main.tab

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jetpack.jplib.base.BaseViewModel
import com.jetpack.kobe.ui.main.tab.TabBean

/**
 * des tab
 * @date 2020/7/7
 * @author zs
 */
class TabVM : BaseViewModel() {

    private val repo by lazy { TabRepo() }

    /**
     * tab
     */
    private val _tabLiveData = MutableLiveData<MutableList<TabBean>>()
    val tabLiveData: LiveData<MutableList<TabBean>> = _tabLiveData

    fun getTab(type: Int) {
        launch {
            _tabLiveData.value = repo.getTab(type)
        }
    }
}