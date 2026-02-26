package com.jetpack.kobe.ui.chat

import android.text.format.DateFormat
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jetpack.kobe.R
import com.jetpack.kobe.bean.MsgBean
import java.util.*

class ChatAdapter(data: MutableList<MsgBean>) : BaseMultiItemQuickAdapter<MsgBean, BaseViewHolder>(data) {

    init {
        addItemType(MsgBean.TYPE_RECEIVED, R.layout.item_chat_msg_left_enhanced)
        addItemType(MsgBean.TYPE_SENT, R.layout.item_chat_msg_right_enhanced)
    }

    override fun convert(helper: BaseViewHolder, item: MsgBean) {
        when (helper.itemViewType) {
            MsgBean.TYPE_RECEIVED -> {
                helper.setText(R.id.tv_content, item.content)
                helper.setText(R.id.tv_time, getCurrentTime())
                helper.setText(R.id.tv_avatar, "助")
            }
            MsgBean.TYPE_SENT -> {
                helper.setText(R.id.tv_content, item.content)
                helper.setText(R.id.tv_time, getCurrentTime())
                helper.setText(R.id.tv_avatar, "我")
            }
        }
    }

    private fun getCurrentTime(): String {
        val now = Calendar.getInstance()
        val hours = now.get(Calendar.HOUR_OF_DAY)
        val minutes = now.get(Calendar.MINUTE)
        return String.format("%02d:%02d", hours, minutes)
    }
}
