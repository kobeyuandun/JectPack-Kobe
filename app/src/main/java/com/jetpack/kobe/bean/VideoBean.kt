package com.jetpack.kobe.bean

data class VideoBean(
    val title: String,
    val description: String,
    val videoUrl: String,  // 视频流 URL
    val coverUrl: String = "",  // 可选的封面图 URL
    val author: String = "",  // 作者
    var isLiked: Boolean = false,  // 是否点赞
    var likeCount: String = "0",  // 点赞数
    var commentCount: String = "0"  // 评论数
)