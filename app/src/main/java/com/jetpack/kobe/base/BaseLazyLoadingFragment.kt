package com.jetpack.kobe.base

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.jetpack.jplib.base.BaseFragment
import com.jetpack.jplib.base.LazyFragment
import com.jetpack.kobe.base.view.LoadingTips

/**
 * @author yuandunbin
 * @date 2022/4/21
 */
abstract class BaseLazyLoadingFragment<BD : ViewDataBinding> : LazyFragment<BD>() {
    private var loadingTips: LoadingTips? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseLoadingActivity) {
            loadingTips = context.loadingTip
        }
    }

    /**
     * 设置loadingView上下变局
     */
//    protected fun setLoadingMargin(topMargin: Int, bottomMargin: Int) {
//        val loadMarginTop = StatusUtils.getStatusBarHeight(mActivity) +topMargin
//        val loadMarginBottom =  StatusUtils.getNavigationBarHeight(mActivity) + bottomMargin
//        val params = loadingTips?.layoutParams as ViewGroup.MarginLayoutParams
//        params.topMargin = loadMarginTop
//        params.bottomMargin = loadMarginBottom
//        loadingTips?.layoutParams = params
//    }
}