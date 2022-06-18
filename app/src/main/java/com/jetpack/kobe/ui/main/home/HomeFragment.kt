package com.jetpack.kobe.ui.main.home

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.SimpleItemAnimator
import cn.bingoogolapple.bgabanner.BGABanner
import com.jetpack.jplib.common.loadUrl
import com.jetpack.jplib.common.smartConfig
import com.jetpack.jplib.common.smartDismiss
import com.jetpack.kobe.R
import com.jetpack.kobe.base.BaseLazyLoadingFragment
import com.jetpack.kobe.databinding.FragmentHomeBinding
import com.jetpack.kobe.ui.adapter.ArticleAdapter

/**
 * @author yuandunbin
 * @date 2022/4/23
 */
class HomeFragment : BaseLazyLoadingFragment<FragmentHomeBinding>(),
    BGABanner.Adapter<ImageView?, String?>,
    BGABanner.Delegate<ImageView?, String?> {
    private var homeVM: HomeVM? = null
    private var bannerList: MutableList<BannerBean>? = null
    private val adapter by lazy { ArticleAdapter(mActivity) }
    override fun initViewModel() {
        homeVM = getActivityViewModel(HomeVM::class.java)
    }
    
    override fun observe() {
        homeVM?.articleList?.observe(this) {
            binding.smartRefresh.smartDismiss()
            adapter.submitList(it)
            binding.loadingTip.dismiss()
        }

        //banner
        homeVM?.banner?.observe(this) {
            bannerList = it
            initBanner()
        }
        //请求错误
        homeVM?.errorLiveData?.observe(this) {
            binding.smartRefresh.smartDismiss()
        }
    }
    override fun lazyInit() {
        initView()
        loadData()
    }

    override fun initView() {
        binding.vm = homeVM
        //关闭更新动画
        (binding.rvHomeList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        binding.smartRefresh.setOnRefreshListener {
            homeVM?.getBanner()
            homeVM?.getArticle()
        }
//        //上拉加载
//        binding.smartRefresh.setOnLoadMoreListener {
//            homeVM?.loa
//        }
        binding.smartRefresh.smartConfig()
        adapter.apply {
            binding.rvHomeList.adapter = this
            setOnItemClickListener { i, view ->
                nav().navigate(R.id.action_main_fragment_to_web_fragment, this@HomeFragment.adapter.getBundle(i))
            }
            setOnItemChildClickListener { i, view ->
                when (view.id) {
                    //收藏
                    R.id.ivCollect -> {
//                        if (CacheUtil.isLogin()) {
//                            this@HomeFragment.adapter.currentList[i].apply {
//                                //已收藏取消收藏
//                                if (collect) {
//                                    homeVM?.unCollect(id)
//                                } else {
//                                    homeVM?.collect(id)
//                                }
//                            }
//                        } else {
//                            nav().navigate(R.id.action_main_fragment_to_login_fragment)
//                        }
                    }
                }
            }
        }
    }

    override fun loadData() {
        homeVM?.getBanner()
        homeVM?.getArticle()
        binding.loadingTip.loading()
    }

    override fun getLayoutId() = R.layout.fragment_home
    override fun fillBannerItem(
        banner: BGABanner?,
        itemView: ImageView?,
        model: String?,
        position: Int
    ) {
        itemView?.apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            loadUrl(mActivity, bannerList?.get(position)?.imagePath!!)
        }
    }

    override fun onBannerItemClick(
        banner: BGABanner?,
        itemView: ImageView?,
        model: String?,
        position: Int
    ) {
        nav().navigate(R.id.action_main_fragment_to_web_fragment, Bundle().apply {
            bannerList?.get(position)?.let {
                putString("loadUrl", it.url)
                putString("title", it.title)
                putInt("id", it.id)
            }
        })
    }

    /**
     * 初始化banner
     */
    private fun initBanner() {
        binding.banner.apply {
            setAutoPlayAble(true)
            val views: MutableList<View> = ArrayList()
            bannerList?.forEach { _ ->
                views.add(ImageView(mActivity).apply {
                    setBackgroundResource(R.drawable.ripple_bg)
                })
            }
            setAdapter(this@HomeFragment)
            setDelegate(this@HomeFragment)
            setData(views)
        }
    }

}