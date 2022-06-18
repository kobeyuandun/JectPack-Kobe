package com.jetpack.kobe

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.jetpack.kobe.base.BaseLoadingActivity
import com.jetpack.kobe.ui.MainFragment

/**
 * @author yuandunbin
 * @date 2022/4/21
 */
class MainActivity: BaseLoadingActivity() {
    override fun init2(savedInstanceState: Bundle?) {
    }

    override fun getLayoutId() = R.layout.activity_main

    override fun onBackPressed() {
        //获取hostFragment
        val mMainNavFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.host_fragment)
        //获取当前所在的fragment
        val fragment =
            mMainNavFragment?.childFragmentManager?.primaryNavigationFragment
        //如果当前处于根fragment即HostFragment
        if (fragment is MainFragment) {
            //Activity退出但不销毁
            moveTaskToBack(false)
        } else {
            super.onBackPressed()
        }
    }

}