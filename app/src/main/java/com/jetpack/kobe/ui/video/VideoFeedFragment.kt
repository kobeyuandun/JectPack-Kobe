package com.jetpack.kobe.ui.video

import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.jetpack.jplib.base.LazyFragment
import com.jetpack.kobe.bean.VideoBean
import com.jetpack.kobe.databinding.FragmentVideoFeedBinding

/**
 * 视频流页面 - 使用真实视频流播放
 * 垂直滑动切换视频，自动播放当前可见视频
 */
class VideoFeedFragment : LazyFragment<FragmentVideoFeedBinding>() {

    private val videoList = ArrayList<VideoBean>()
    private lateinit var adapter: VideoFeedAdapter

    // 用于检测页面滚动，控制视频播放
    private val viewPagerPageCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            // 设置当前播放位置
            adapter.setCurrentPlayingPosition(position)
            adapter.playCurrentPlayer()
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            // 滚动时暂停播放
            if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                adapter.pauseCurrentPlayer()
            } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                // 滚动结束，恢复播放
                adapter.playCurrentPlayer()
            }
        }
    }

    override fun getLayoutId(): Int = com.jetpack.kobe.R.layout.fragment_video_feed

    override fun initViewModel() {
        // ViewModel 初始化
    }

    override fun lazyInit() {
        // 返回按钮
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // 加载视频数据
        loadVideoData()

        // 初始化 Adapter
        adapter = VideoFeedAdapter(videoList, object : VideoFeedAdapter.OnItemListener {
            override fun onLikeClick(position: Int, video: VideoBean) {
                // 点赞事件处理
                showShortToast("点赞: ${video.title}")
            }

            override fun onCommentClick(position: Int, video: VideoBean) {
                // 评论事件处理
                showShortToast("评论: ${video.title}")
            }

            override fun onShareClick(position: Int, video: VideoBean) {
                // 分享事件处理
                showShortToast("分享: ${video.title}")
            }

            override fun onFollowClick(position: Int, video: VideoBean) {
                // 关注事件处理
                showShortToast("关注: ${video.title}")
            }

            override fun onPlayError(position: Int, errorMsg: String?) {
                showShortToast("播放失败: $errorMsg")
            }
        })

        binding.vpVideo.adapter = adapter

        // 设置 ViewPager2 为垂直方向
        binding.vpVideo.orientation = ViewPager2.ORIENTATION_VERTICAL

        // 注册页面变化监听
        binding.vpVideo.registerOnPageChangeCallback(viewPagerPageCallback)

        // 设置第一个页面为初始播放位置
        adapter.setCurrentPlayingPosition(0)
    }

    /**
     * 加载视频数据
     * 使用一些公开的测试视频 URL
     */
    private fun loadVideoData() {
        // 示例视频 URL - 使用公开的测试视频源
        // 你可以替换成自己的视频服务器地址
        videoList.add(
            VideoBean(
                title = "绝美风景 - 西藏之旅",
                description = "这是我上次去西藏拍的，景色太美了！高原的蓝天白云，雪山草地，每一帧都是壁纸。",
                videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
                author = "旅行达人",
                likeCount = "12500",
                commentCount = "328"
            )
        )

        videoList.add(
            VideoBean(
                title = "可爱猫咪日常",
                description = "家里新来的小猫，超级粘人，每天都要抱抱～看看它的可爱瞬间吧！",
                videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                author = "萌宠之家",
                likeCount = "8900",
                commentCount = "156"
            )
        )

        videoList.add(
            VideoBean(
                title = "美食制作 - 牛排教程",
                description = "这家店的牛排真的绝绝子，推荐大家来尝尝。今天教大家如何在家煎出完美牛排！",
                videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                author = "美食小当家",
                likeCount = "23400",
                commentCount = "567"
            )
        )

        videoList.add(
            VideoBean(
                title = "Android Jetpack 架构实战",
                description = "Jetpack 架构组件详解，包括 ViewModel、LiveData、Navigation 等，帮你快速掌握现代 Android 开发。",
                videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                author = "码农日记",
                likeCount = "15600",
                commentCount = "423"
            )
        )

        videoList.add(
            VideoBean(
                title = "生活 Vlog - 平凡的一天",
                description = "记录生活中的小确幸，即使是平凡的一天也有它的美好。早起晨跑、工作、咖啡时光...",
                videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
                author = "生活记录者",
                likeCount = "6700",
                commentCount = "89"
            )
        )

        videoList.add(
            VideoBean(
                title = "搞笑合集 - 今日份快乐",
                description = "哈哈哈哈笑死我了，收集了一波搞笑视频，不开心的时候来看看吧！",
                videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
                author = "快乐源泉",
                likeCount = "34500",
                commentCount = "890"
            )
        )
    }

    /**
     * 页面可见时恢复播放
     */
    override fun onResume() {
        super.onResume()
        adapter.playCurrentPlayer()
    }

    /**
     * 页面不可见时暂停播放
     */
    override fun onPause() {
        super.onPause()
        adapter.pauseCurrentPlayer()
    }

    /**
     * 释放资源
     */
    override fun onDestroyView() {
        super.onDestroyView()
        binding.vpVideo.unregisterOnPageChangeCallback(viewPagerPageCallback)
        adapter.releaseAllPlayers()
    }

    private fun showShortToast(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }
}
