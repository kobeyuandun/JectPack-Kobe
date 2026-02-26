package com.jetpack.jplib.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jetpack.jplib.utils.ColorUtils
import com.jetpack.jplib.utils.StatusUtils


/**
 * @author yuandunbin
 * @date 2022/4/13
 */
abstract class BaseActivity : AppCompatActivity() {
    private var mActivityViewModel: ViewModelProvider? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getLayoutId()?.let {
            setContentView(it)
        }
        setStatusColor()
        setSystemInvadeBlack()
        initViewModel()
        observe()
        init(savedInstanceState)
    }

    /**
     * 设置状态栏背景颜色
     */
    open fun setStatusColor() {
        StatusUtils.setUseStatusBarColor(this, ColorUtils.parseColor("#00ffffff"))
    }

    /**
     * 沉浸式状态
     */
    open fun setSystemInvadeBlack() {
        //第二个参数是是否沉浸,第三个参数是状态栏字体是否为黑色。
        StatusUtils.setSystemStatus(this, true, true)
    }

    abstract fun init(savedInstanceState: Bundle?)

    open fun observe() {
    }

    open fun initViewModel() {
    }

    /**
     * 通过 activity 获取viewModel
     */
    protected fun <T : ViewModel> getActivityViewModel(modelClass: Class<T>): T {
        if (mActivityViewModel == null) {
            mActivityViewModel = ViewModelProvider(this)
        }
        return mActivityViewModel!![modelClass]
    }

    /**
     * 获取布局id
     */
    abstract fun getLayoutId(): Int?
}