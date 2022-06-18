package com.jetpack.kobe.base

import android.os.Bundle
import android.widget.FrameLayout
import com.jetpack.jplib.base.BaseActivity
import com.jetpack.jplib.common.dip2px
import com.jetpack.jplib.utils.StatusUtils
import com.jetpack.kobe.base.view.LoadingTips

/**
 * @author yuandunbin
 * @date 2022/4/21
 */
abstract class BaseLoadingActivity: BaseActivity() {
    var loadingTip: LoadingTips? = null
    override fun init(savedInstanceState: Bundle?) {
        val decorView = window.decorView as FrameLayout
        val loadMarginTop = StatusUtils.getStatusBarHeight(this) + dip2px(this, 50f)
        val loadMarginBottom =  StatusUtils.getNavigationBarHeight(this)
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.topMargin = loadMarginTop
        layoutParams.bottomMargin = loadMarginBottom
        loadingTip = LoadingTips(this)
        decorView.addView(loadingTip, layoutParams)
        init2(savedInstanceState)
    }
    abstract fun init2(savedInstanceState: Bundle?)
}