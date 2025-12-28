package com.jetpack.kobe.ui.video

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jetpack.kobe.R
import com.jetpack.kobe.bean.VideoBean

class VideoFeedAdapter(data: MutableList<VideoBean>) :
    BaseQuickAdapter<VideoBean, BaseViewHolder>(R.layout.item_video_feed, data) {

    override fun convert(helper: BaseViewHolder, item: VideoBean) {
        helper.setText(R.id.tv_title, item.title)
        helper.setText(R.id.tv_desc, item.description)
        
        // 模拟视频背景颜色
        helper.getView<android.view.View>(R.id.v_video_bg).setBackgroundColor(item.color)
    }
}