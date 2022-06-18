package com.jetpack.kobe.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.jetpack.jplib.base.BaseFragment
import com.jetpack.jplib.common.doSelected
import com.jetpack.jplib.common.initFragment
import com.jetpack.kobe.R
import com.jetpack.kobe.constants.Constants
import com.jetpack.kobe.databinding.FragmentMainBinding
import com.jetpack.kobe.ui.main.home.HomeFragment
import com.jetpack.kobe.ui.main.mine.MineFragment
import com.jetpack.kobe.ui.main.tab.TabFragment

/**
 * @author yuandunbin
 * @date 2022/4/21
 */
class MainFragment: BaseFragment<FragmentMainBinding>() {
    private val fragmentList = arrayListOf<Fragment>()

    private val homeFragment by lazy { HomeFragment() }
    /**
     * 项目
     */
    private val projectFragment by lazy {
        TabFragment().apply {
            arguments = Bundle().apply {
                putInt("type", Constants.PROJECT_TYPE)
            }
        }
    }

    /**
     * 广场
     */
    private val squareFragment by lazy { HomeFragment() }

    /**
     * 公众号
     */
    private val publicNumberFragment by lazy {
        TabFragment().apply {
            arguments = Bundle().apply {
                putInt("type",Constants.ACCOUNT_TYPE)
            }
        }
    }

    /**
     * 我的
     */
    private val mineFragment by lazy { MineFragment() }

    init {
        fragmentList.apply {
            add(homeFragment)
            add(projectFragment)
            add(squareFragment)
            add(publicNumberFragment)
            add(mineFragment)
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        binding.vpHome.initFragment(childFragmentManager, fragmentList).run {
            //全部缓存,避免切换导致重新加载
            offscreenPageLimit = fragmentList.size
        }

        binding.vpHome.doSelected {
            binding.btnNav.menu.getItem(it).isChecked = true
        }

        //初始化底部导航栏
        binding.btnNav.run {
            setOnNavigationItemSelectedListener {  item ->
                when(item.itemId){
                    R.id.menu_home -> {
                        binding.vpHome.setCurrentItem(0, false)
                    }
                    R.id.menu_project -> binding.vpHome.setCurrentItem(1, false)
                    R.id.menu_square -> binding.vpHome.setCurrentItem(2, false)
                    R.id.menu_official_account -> binding.vpHome.setCurrentItem(3, false)
                    R.id.menu_mine -> binding.vpHome.setCurrentItem(4, false)
                }
                // 这里注意返回true,否则点击失效
                true
            }
        }
    }

    override fun getLayoutId() = R.layout.fragment_main
}