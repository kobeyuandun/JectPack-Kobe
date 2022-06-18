package com.jetpack.jplib.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding

/**
 * @author yuandunbin
 * @date 2022/4/16
 */
abstract class LazyFragment<BD: ViewDataBinding> : BaseFragment<BD>() {
    var isLazy = false
    override fun onResume() {
        super.onResume()
        //增加fragment是否可见的判断
        if (!isLazy && !isHidden) {
            lazyInit()
            isLazy = true
        }
    }

    abstract fun lazyInit()

    override fun init(savedInstanceState: Bundle?) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        isLazy = false
    }
}