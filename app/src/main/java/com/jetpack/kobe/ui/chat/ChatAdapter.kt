package com.jetpack.kobe.ui.chat

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jetpack.kobe.R
import com.jetpack.kobe.bean.MsgBean

class ChatAdapter(data: MutableList<MsgBean>) : BaseMultiItemQuickAdapter<MsgBean, BaseViewHolder>(data) {

    init {
        addItemType(MsgBean.TYPE_RECEIVED, R.layout.item_chat_msg_left)
        addItemType(MsgBean.TYPE_SENT, R.layout.item_chat_msg_right)
    }

    override fun convert(helper: BaseViewHolder, item: MsgBean) {
        when (helper.itemViewType) {
            MsgBean.TYPE_RECEIVED -> {
                helper.setText(R.id.tv_content, item.content)
            }
            MsgBean.TYPE_SENT -> {
                helper.setText(R.id.tv_content, item.content)
            }
        }
    }
}