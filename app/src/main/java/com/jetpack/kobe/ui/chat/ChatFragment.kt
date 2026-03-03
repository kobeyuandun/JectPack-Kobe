package com.jetpack.kobe.ui.chat

import android.animation.ValueAnimator
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jetpack.jplib.base.LazyFragment
import com.jetpack.kobe.R
import com.jetpack.kobe.bean.MsgBean
import com.jetpack.kobe.databinding.FragmentChatBinding
import com.jetpack.kobe.ui.voice.DoubaoVoiceCallActivity

class ChatFragment : LazyFragment<FragmentChatBinding>() {

    private val msgList = ArrayList<MsgBean>()
    private lateinit var adapter: ChatAdapter
    private val handler = Handler(Looper.getMainLooper())

    // 回复消息库
    private val replyMessages = listOf(
        "我收到了你的消息：%s",
        "很有趣的想法！✨",
        "让我想想... 🤔",
        "你说得对！👍",
        "继续说，我听着呢 👂",
        "这个话题很有意思！💡",
        "原来如此！😊",
        "我完全同意你的看法",
        "这确实是个好问题"
    )

    override fun getLayoutId(): Int = R.layout.fragment_chat

    override fun initViewModel() {
        // ViewModel 初始化
    }

    override fun lazyInit() {
        setupToolbar()
        setupRecyclerView()
        setupInputBar()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        // 先初始化 adapter
        adapter = ChatAdapter(msgList)

        binding.rvChatList.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = false
            }
            adapter = this@ChatFragment.adapter

            // 添加滚动动画
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        recyclerView.post {
                            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                            val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                            if (lastVisiblePosition == adapter?.itemCount?.minus(1)) {
                                // 已滚动到底部
                            }
                        }
                    }
                }
            })
        }

        // 添加欢迎消息（带动画）
        addMessageWithAnimation(MsgBean("你好！很高兴认识你 👋", MsgBean.TYPE_RECEIVED))
    }

    private fun setupInputBar() {
        // 发送按钮点击事件
        binding.flSend.setOnClickListener {
            val content = binding.etInput.text.toString()
            if (!TextUtils.isEmpty(content.trim())) {
                sendMessage(content.trim())
                binding.etInput.setText("")
            }
        }

        // 语音按钮点击事件 - 启动语音通话
        binding.flVoice.setOnClickListener {
            startVoiceCall()
        }

        // 表情按钮点击事件
        binding.flEmoji.setOnClickListener {
            // TODO: 实现表情面板
        }

        // 更多按钮点击事件
        binding.flMore.setOnClickListener {
            // TODO: 实现更多功能菜单
        }

        // 监听输入框内容变化，动态调整发送按钮状态
        binding.etInput.afterTextChanged {
            updateSendButtonState(!it.isNullOrEmpty())
        }
    }

    private fun sendMessage(content: String) {
        // 添加发送的消息（带动画）
        addMessageWithAnimation(MsgBean(content, MsgBean.TYPE_SENT))

        // 模拟接收回复（随机延迟）
        val delay = (800..1500).random()
        handler.postDelayed({
            val randomReply = replyMessages.random()
            val reply = String.format(randomReply, content)
            addMessageWithAnimation(MsgBean(reply, MsgBean.TYPE_RECEIVED))
        }, delay.toLong())
    }

    private fun addMessageWithAnimation(msg: MsgBean) {
        val position = msgList.size
        msgList.add(msg)
        adapter.notifyItemInserted(position)

        // 平滑滚动到最新消息
        binding.rvChatList.post {
            binding.rvChatList.smoothScrollToPosition(msgList.size - 1)
        }

        // 新消息淡入动画
        binding.rvChatList.post {
            val viewHolder = binding.rvChatList.findViewHolderForAdapterPosition(position)
            viewHolder?.itemView?.apply {
                alpha = 0f
                animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }
        }
    }

    private fun updateSendButtonState(canSend: Boolean) {
        binding.flSend.alpha = if (canSend) 1.0f else 0.5f
        binding.flSend.isEnabled = canSend
    }

    /**
     * 启动语音通话（豆包风格）
     */
    private fun startVoiceCall() {
        // 使用时间戳作为频道名，保证每次通话都是新频道
        val channelName = "voice_call_${System.currentTimeMillis()}"
        DoubaoVoiceCallActivity.start(requireContext(), channelName, "AI 助手")
    }

    override fun onClick() {
        // 其他点击事件
    }
}

// EditText 扩展函数 - 监听文本变化
private fun android.widget.EditText.afterTextChanged(afterTextChanged: (String?) -> Unit) {
    this.addTextChangedListener(object : android.text.TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(editable: android.text.Editable?) {
            afterTextChanged.invoke(editable?.toString())
        }
    })
}
