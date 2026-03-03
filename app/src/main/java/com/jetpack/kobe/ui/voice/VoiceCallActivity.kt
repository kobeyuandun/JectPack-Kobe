package com.jetpack.kobe.ui.voice

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.jetpack.kobe.R
import com.jetpack.kobe.databinding.ActivityVoiceCallBinding
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * 语音通话界面
 *
 * 使用方法：
 * ```
 * // 启动语音通话
 * VoiceCallActivity.start(context, channelName = "test_channel")
 * ```
 */
class VoiceCallActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_CHANNEL_NAME = "channel_name"
        private const val EXTRA_USER_NAME = "user_name"
        private const val EXTRA_UID = "uid"

        fun start(
            context: Context,
            channelName: String,
            userName: String = "AI 助手",
            uid: Int = 0
        ) {
            val intent = Intent(context, VoiceCallActivity::class.java).apply {
                putExtra(EXTRA_CHANNEL_NAME, channelName)
                putExtra(EXTRA_USER_NAME, userName)
                putExtra(EXTRA_UID, uid)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityVoiceCallBinding
    private val viewModel: VoiceCallViewModel by viewModels()

    private var channelName: String = ""
    private var userName: String = ""
    private var uid: Int = 0

    // 通话计时
    private val executor = Executors.newSingleThreadScheduledExecutor()
    private var callSeconds = 0
    private var isTiming = false

    // 波纹动画
    private var outerWaveAnimator: Animator? = null
    private var middleWaveAnimator: Animator? = null

    // 权限请求
    private val recordAudioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            joinChannel()
        } else {
            binding.tvCallStatus.text = "需要麦克风权限"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVoiceCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getExtras()
        setupUI()
        observeViewModel()
        checkAndRequestPermissions()
    }

    private fun getExtras() {
        channelName = intent.getStringExtra(EXTRA_CHANNEL_NAME) ?: "default_channel"
        userName = intent.getStringExtra(EXTRA_USER_NAME) ?: "AI 助手"
        uid = intent.getIntExtra(EXTRA_UID, 0)

        binding.tvUserName.text = userName
    }

    private fun setupUI() {
        // 设置状态栏
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // 静音按钮
        binding.flMute.setOnClickListener {
            viewModel.toggleMute()
            updateMuteUI()
        }

        // 挂断按钮
        binding.flHangup.setOnClickListener {
            endCall()
        }

        // 扬声器按钮
        binding.flSpeaker.setOnClickListener {
            viewModel.toggleSpeaker()
            updateSpeakerUI()
        }

        // 初始化 UI 状态
        updateMuteUI()
        updateSpeakerUI()
    }

    private fun observeViewModel() {
        // 监听通话状态
        viewModel.callState.observe(this) { state ->
            when (state) {
                is VoiceCallViewModel.CallState.Idle -> {
                    binding.tvCallStatus.text = "准备就绪"
                }
                is VoiceCallViewModel.CallState.Connecting -> {
                    binding.tvCallStatus.text = "正在连接..."
                }
                is VoiceCallViewModel.CallState.InCall -> {
                    binding.tvCallStatus.text = "通话中"
                    startCallTimer()
                    startWaveAnimation()
                }
                is VoiceCallViewModel.CallState.Ended -> {
                    binding.tvCallStatus.text = "通话结束"
                    stopCallTimer()
                    stopWaveAnimation()
                    finishDelayed()
                }
                is VoiceCallViewModel.CallState.Error -> {
                    binding.tvCallStatus.text = state.message
                    stopCallTimer()
                    stopWaveAnimation()
                }
            }
        }

        // 监听静音状态
        viewModel.isMuted.observe(this) { isMuted ->
            updateMuteUI(isMuted)
        }

        // 监听扬声器状态
        viewModel.isSpeakerOn.observe(this) { isOn ->
            updateSpeakerUI(isOn)
        }

        // 监听通话时长
        viewModel.callDuration.observe(this) { seconds ->
            binding.tvCallDuration.text = formatDuration(seconds)
        }

        // 监听远程用户
        viewModel.remoteUsers.observe(this) { users ->
            binding.tvUserStatus.text = if (users.isNotEmpty()) {
                "对方已加入"
            } else {
                ""
            }
        }
    }

    /**
     * 检查并请求权限
     */
    private fun checkAndRequestPermissions() {
        if (hasRecordAudioPermission()) {
            joinChannel()
        } else {
            requestRecordAudioPermission()
        }
    }

    /**
     * 是否有录音权限
     */
    private fun hasRecordAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 请求录音权限
     */
    private fun requestRecordAudioPermission() {
        recordAudioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
    }

    /**
     * 加入频道
     */
    private fun joinChannel() {
        if (VoiceCallManager.isAppIdValid()) {
            viewModel.joinChannel(channelName, uid)
        } else {
            binding.tvCallStatus.text = "请先设置声网 AppId"
        }
    }

    /**
     * 挂断通话
     */
    private fun endCall() {
        viewModel.leaveChannel()
        stopCallTimer()
        stopWaveAnimation()
        finish()
    }

    /**
     * 延迟结束
     */
    private fun finishDelayed() {
        binding.root.postDelayed({
            finish()
        }, 1500)
    }

    /**
     * 开始通话计时
     */
    private fun startCallTimer() {
        if (isTiming) return
        isTiming = true
        callSeconds = 0

        executor.scheduleAtFixedRate({
            callSeconds++
            viewModel.updateCallDuration(callSeconds)
        }, 1, 1, TimeUnit.SECONDS)
    }

    /**
     * 停止通话计时
     */
    private fun stopCallTimer() {
        isTiming = false
    }

    /**
     * 格式化时长
     */
    private fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", minutes, secs)
    }

    /**
     * 更新静音 UI
     */
    private fun updateMuteUI(isMuted: Boolean = viewModel.isMuted.value == true) {
        val iconRes = if (isMuted) {
            R.drawable.ic_voice_mute
        } else {
            R.drawable.ic_voice_unmute
        }
        binding.ivMute.setImageResource(iconRes)

        val bgRes = if (isMuted) {
            R.drawable.voice_call_control_button_selected_bg
        } else {
            R.drawable.voice_call_control_button_bg
        }
        binding.flMute.setBackgroundResource(bgRes)
    }

    /**
     * 更新扬声器 UI
     */
    private fun updateSpeakerUI(isOn: Boolean = viewModel.isSpeakerOn.value == true) {
        val iconRes = if (isOn) {
            R.drawable.ic_voice_speaker_on
        } else {
            R.drawable.ic_voice_speaker_off
        }
        binding.ivSpeaker.setImageResource(iconRes)

        val bgRes = if (isOn) {
            R.drawable.voice_call_control_button_selected_bg
        } else {
            R.drawable.voice_call_control_button_bg
        }
        binding.flSpeaker.setBackgroundResource(bgRes)
    }

    /**
     * 开始波纹动画
     */
    private fun startWaveAnimation() {
        val outerWave = binding.viewWaveOuter
        val middleWave = binding.viewWaveMiddle

        outerWaveAnimator = createWaveAnimator(outerWave, 1000).apply {
            startDelay = 0
            start()
        }

        middleWaveAnimator = createWaveAnimator(middleWave, 1000).apply {
            startDelay = 500
            start()
        }
    }

    /**
     * 创建波纹动画
     */
    private fun createWaveAnimator(view: View, duration: Long): Animator {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.5f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.5f)
        val alpha = ObjectAnimator.ofFloat(view, "alpha", 0.5f, 0f)

        return AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            setDuration(duration)
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    view.scaleX = 1f
                    view.scaleY = 1f
                    view.alpha = 0.5f
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (isTiming) {
                        start()
                    }
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
    }

    /**
     * 停止波纹动画
     */
    private fun stopWaveAnimation() {
        outerWaveAnimator?.cancel()
        middleWaveAnimator?.cancel()
        outerWaveAnimator = null
        middleWaveAnimator = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCallTimer()
        stopWaveAnimation()
        executor.shutdown()
    }
}
