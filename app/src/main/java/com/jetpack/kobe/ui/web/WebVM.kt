package com.jetpack.kobe.ui.web

import androidx.databinding.ObservableField
import com.jetpack.jplib.base.BaseViewModel

/**
 * @author yuandunbin
 * @date 2022/5/25
 */
class WebVM : BaseViewModel() {
    /**
     * webView 进度
     */
    val progress = ObservableField<Int>()


    /**
     * 最大 进度
     */
    val maxProgress = ObservableField<Int>()

    /**
     * progress是否隐藏
     */
    val isVisible = ObservableField<Boolean>()
}