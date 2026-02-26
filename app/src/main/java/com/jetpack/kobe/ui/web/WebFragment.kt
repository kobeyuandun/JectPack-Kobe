package com.jetpack.kobe.ui.web

import android.os.Bundle
import android.text.Html
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import com.jetpack.jplib.base.BaseFragment
import com.jetpack.jplib.common.clickNoRepeat
import com.jetpack.jplib.utils.Param
import com.jetpack.kobe.R
import com.jetpack.kobe.databinding.FragmentWebBinding

/**
 * @author yuandunbin
 * @date 2022/5/25
 */
class WebFragment : BaseFragment<FragmentWebBinding>() {
    /**
     * 通过注解接收参数
     * url
     */
    @Param
    private var loadUrl: String? = null

    /**
     * 文章标题
     */
    @Param
    private var title: String? = null

    private var webVM: WebVM? = null

    override fun initViewModel() {
        webVM = getActivityViewModel(WebVM::class.java)
    }

    override fun init(savedInstanceState: Bundle?) {
        binding.vm = webVM
        initView()
    }

    override fun initView() {
        binding.tvTitle.text = Html.fromHtml(title)
        binding.ivBack.clickNoRepeat {
            nav().navigateUp()
        }
        initWebView()
    }

    private fun initWebView() {
        val settings = binding.webView.settings
        settings.javaScriptEnabled = true
        //自适应屏幕
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        settings.loadWithOverviewMode = true
        //如果不设置WebViewClient，请求会跳转系统浏览器
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                //返回false，意味着请求过程里，不管有多少次的跳转请求（即新的请求地址）
                //均交给webView自己处理，这也是此方法的默认处理
                return false
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                //返回false，意味着请求过程里，不管有多少次的跳转请求（即新的请求地址）
                //均交给webView自己处理，这也是此方法的默认处理
                return false
            }
        }
        loadUrl?.let { binding.webView.loadUrl(it) }
        webVM?.maxProgress?.set(100)
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                //进度小于100，显示进度条
                if (newProgress < 100) {
                    webVM?.isVisible?.set(true)
                }
                //等于100隐藏
                else if (newProgress == 100) {
                    webVM?.isVisible?.set(false)
                }
                //改变进度
                webVM?.progress?.set(newProgress)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    nav().navigateUp()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun getLayoutId() = R.layout.fragment_web
}