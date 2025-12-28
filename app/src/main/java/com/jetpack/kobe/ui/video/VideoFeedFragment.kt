package com.jetpack.kobe.ui.video

import android.graphics.Color
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.jetpack.jplib.base.LazyFragment
import com.jetpack.kobe.R
import com.jetpack.kobe.bean.VideoBean
import com.jetpack.kobe.databinding.FragmentVideoFeedBinding

class VideoFeedFragment : LazyFragment<FragmentVideoFeedBinding>() {

    private val videoList = ArrayList<VideoBean>()
    private lateinit var adapter: VideoFeedAdapter

    override fun getLayoutId(): Int = R.layout.fragment_video_feed

    override fun initViewModel() {
        // ViewModel 初始化
    }

    override fun lazyInit() {
        // 返回按钮
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // 模拟数据
        videoList.add(VideoBean("绝美风景", "这是我上次去西藏拍的，景色太美了！", Color.parseColor("#FF5722")))
        videoList.add(VideoBean("可爱猫咪", "家里新来的小猫，超级粘人。", Color.parseColor("#009688")))
        videoList.add(VideoBean("美食分享", "这家店的牛排真的绝绝子，推荐大家来尝尝。", Color.parseColor("#FFC107")))
        videoList.add(VideoBean("技术分享", "Android Jetpack 架构组件实战讲解。", Color.parseColor("#3F51B5")))
        videoList.add(VideoBean("生活Vlog", "记录平凡的一天。", Color.parseColor("#9C27B0")))
        videoList.add(VideoBean("搞笑视频", "哈哈哈哈笑死我了。", Color.parseColor("#E91E63")))

        // 初始化 Adapter
        adapter = VideoFeedAdapter(videoList)
        binding.vpVideo.adapter = adapter
        
        // 设置 ViewPager2 为垂直方向
        binding.vpVideo.orientation = ViewPager2.ORIENTATION_VERTICAL
    }

    override fun onClick() {
    }
}