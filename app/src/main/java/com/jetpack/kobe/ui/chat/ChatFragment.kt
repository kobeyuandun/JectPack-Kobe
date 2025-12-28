package com.jetpack.kobe.ui.chat

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jetpack.jplib.base.LazyFragment
import com.jetpack.kobe.R
import com.jetpack.kobe.bean.MsgBean
import com.jetpack.kobe.databinding.FragmentChatBinding

class ChatFragment : LazyFragment<FragmentChatBinding>() {

    private val msgList = ArrayList<MsgBean>()
    private lateinit var adapter: ChatAdapter
    private val handler = Handler(Looper.getMainLooper())

    override fun getLayoutId(): Int = R.layout.fragment_chat

    override fun initViewModel() {
        // ViewModel 初始化
    }

    override fun lazyInit() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // 初始化 RecyclerView
        binding.rvChatList.layoutManager = LinearLayoutManager(context)
        adapter = ChatAdapter(msgList)
        binding.rvChatList.adapter = adapter

        // 添加一些默认消息
        msgList.add(MsgBean("你好！", MsgBean.TYPE_RECEIVED))
        adapter.notifyDataSetChanged()

        // 发送按钮点击事件
        binding.llInputBar.getChildAt(3).setOnClickListener { // 发送按钮是第4个子view
            val content = binding.etInput.text.toString()
            if (!TextUtils.isEmpty(content)) {
                sendMsg(content)
                binding.etInput.setText("")
            }
        }
    }

    private fun sendMsg(content: String) {
        // 1. 添加发送的消息到列表
        msgList.add(MsgBean(content, MsgBean.TYPE_SENT))
        adapter.notifyItemInserted(msgList.size - 1)
        binding.rvChatList.scrollToPosition(msgList.size - 1)

        // 2. 模拟接收回复
        handler.postDelayed({
            receiveMsg("我收到了你的消息：$content")
        }, 1000)
    }

    private fun receiveMsg(content: String) {
        msgList.add(MsgBean(content, MsgBean.TYPE_RECEIVED))
        adapter.notifyItemInserted(msgList.size - 1)
        binding.rvChatList.scrollToPosition(msgList.size - 1)
    }

    override fun onClick() {
        // 其他点击事件
    }
}