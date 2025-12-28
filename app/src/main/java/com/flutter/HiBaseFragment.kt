package com.flutter

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @author yuandunbin
 * @date 2022/12/16
 */
abstract class HiBaseFragment : Fragment() {
    protected var layoutView: View? = null

    @LayoutRes
    abstract fun getLayoutId(): Int

    @Nullable
    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        layoutView = inflater.inflate(getLayoutId(), container, false)
        return layoutView
    }

    //检测 宿主 是否还存活
    fun isAlive(): Boolean {
        return if (isRemoving() || isDetached() || getActivity() == null) {
            false
        } else true
    }
}
