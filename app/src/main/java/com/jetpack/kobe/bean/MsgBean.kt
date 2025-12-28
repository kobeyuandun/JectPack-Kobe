package com.jetpack.kobe.bean

import com.chad.library.adapter.base.entity.MultiItemEntity // 1. 引入接口

data class MsgBean(
    val content: String,
    val type: Int // 0: 接收 (左边), 1: 发送 (右边)
) : MultiItemEntity { // 2. 实现接口

    companion object {
        const val TYPE_RECEIVED = 0
        const val TYPE_SENT = 1
    }

    // 3. 重写 getItemType 方法，返回布局类型
    override fun getItemType(): Int {
        return type
    }
}
