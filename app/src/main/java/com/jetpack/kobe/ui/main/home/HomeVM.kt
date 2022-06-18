package com.jetpack.kobe.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jetpack.jplib.base.BaseViewModel
import com.jetpack.kobe.bean.ArticleListBean
import kotlinx.coroutines.async

/**
 * @author yuandunbin
 * @date 2022/4/24
 */
class HomeVM : BaseViewModel() {
    private val repo by lazy { HomeRepo() }
    val articleList = MutableLiveData<MutableList<ArticleListBean>>()

    /**
     * banner
     */
    private val _banner = MutableLiveData<MutableList<BannerBean>>()

    /**
     * 对外部提供只读的LiveData
     */
    val banner: LiveData<MutableList<BannerBean>> = _banner

    /**
     * 获取banner
     */
    fun getBanner() {
        launch {
            _banner.value = repo.getBanner()
        }
    }

    fun getArticle() {
        launch {
            val list = mutableListOf<ArticleListBean>()
            val articles = viewModelScope.async {
                repo.getArticles()
            }
            val articlesTop = viewModelScope.async {
                repo.getTopArticles()
            }
            list.addAll(articlesTop.await())
            list.addAll(articles.await())
            articleList.value = list
        }
    }


    /**
     * 加载更多
     */
//    fun loadMoreArticle() {
//        launch {
//            val list = articleList.value
//            list?.addAll(repo.loadMoreArticles())
//            articleList.value = list
//            handleList(_articleList)
//        }
//    }
}