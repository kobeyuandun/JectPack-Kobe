package com.jetpack.kobe.bean

data class VideoBean(
    val title: String,
    val description: String,
    val color: Int // 暂时用颜色代替视频画面，实际可以换成图片URL或视频URL
)