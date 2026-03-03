package com.jetpack.kobe.ui.voice

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.jetpack.kobe.R
import com.jetpack.kobe.databinding.ActivityVoiceCallDoubaoBinding

/**
 * 豆包风格语音通话界面
 *
 * 动效特点：波纹扩散效果（Ripple Wave Effect）
 * - 多层圆环从中心向外连续扩散
 * - 扩散的同时渐隐消失
 * - 循环产生新的波纹
 */
class DoubaoVoiceCallActivity : AppCompatActivity() {

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
            val intent = Intent(context, DoubaoVoiceCallActivity::class.java).apply {
                putExtra(EXTRA_CHANNEL_NAME, channelName)
                putExtra(EXTRA_USER_NAME, userName)
                putExtra(EXTRA_UID, uid)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityVoiceCallDoubaoBinding
    private val viewModel: VoiceCallViewModel by viewModels()

    private var channelName: String = ""
    private var userName: String = ""
    private var uid: Int = 0

    // 动画相关
    private var isAnimating = false
    private val rippleAnimators = mutableListOf<AnimatorSet>()
    private val animationHandler = android.os.Handler(android.os.Looper.getMainLooper())

    // 权限请求
    private val recordAudioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            joinChannel()
        } else {
            updateHintText("需要麦克风权限")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVoiceCallDoubaoBinding.inflate(layoutInflater)
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
    }

    private fun setupUI() {
        setupStatusBar()

        binding.flMic.setOnClickListener { toggleMute() }
        binding.flUpload.setOnClickListener { updateHintText("上传功能开发中") }
        binding.flVideo.setOnClickListener { updateHintText("视频通话功能开发中") }
        binding.flClose.setOnClickListener { endCall() }
        binding.ivMore.setOnClickListener { }
        binding.tvTextMode.setOnClickListener { }

        // 开始豆包风格波纹扩散动画
        startRippleWaveAnimation()
    }

    private fun setupStatusBar() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = android.graphics.Color.TRANSPARENT
        }
    }

    private fun observeViewModel() {
        viewModel.callState.observe(this) { state ->
            when (state) {
                is VoiceCallViewModel.CallState.Idle -> {
                    updateHintText("准备连接")
                }
                is VoiceCallViewModel.CallState.Connecting -> {
                    updateHintText("正在连接...")
                }
                is VoiceCallViewModel.CallState.InCall -> {
                    updateHintText("你可以开始说话")
                    startRippleWaveAnimation()
                }
                is VoiceCallViewModel.CallState.Ended -> {
                    updateHintText("通话结束")
                    stopRippleAnimation()
                    finishDelayed()
                }
                is VoiceCallViewModel.CallState.Error -> {
                    updateHintText(state.message)
                    stopRippleAnimation()
                }
            }
        }

        viewModel.remoteUsers.observe(this) { users ->
            if (users.isNotEmpty()) {
                updateHintText("对方已加入")
            }
        }
    }

    private fun checkAndRequestPermissions() {
        if (hasRecordAudioPermission()) {
            joinChannel()
        } else {
            requestRecordAudioPermission()
        }
    }

    private fun hasRecordAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestRecordAudioPermission() {
        recordAudioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
    }

    private fun joinChannel() {
        if (VoiceCallManager.isAppIdValid()) {
            viewModel.joinChannel(channelName, uid)
        } else {
            updateHintText("请先设置声网 AppId")
        }
    }

    private fun endCall() {
        viewModel.leaveChannel()
        stopRippleAnimation()
        finish()
    }

    private fun finishDelayed() {
        binding.root.postDelayed({ finish() }, 1500)
    }

    private fun toggleMute() {
        viewModel.toggleMute()
        val isMuted = viewModel.isMuted.value == true
        updateMicUI(isMuted)
    }

    private fun updateMicUI(isMuted: Boolean) {
        binding.flMic.alpha = if (isMuted) 0.5f else 1.0f
        updateHintText(if (isMuted) "麦克风已关闭" else "你可以开始说话")
    }

    private fun updateHintText(text: String) {
        binding.tvHintText.text = text
    }

    /**
     * 豆包风格波纹扩散动画（Ripple Wave Effect）
     *
     * 动画原理：
     * - 每层波纹独立向外扩散（scale: 1.0 → 目标值）
     * - 扩散过程中透明度逐渐降低（alpha: 1.0 → 0）
     * - 波纹消失后重新从中心开始
     * - 多层波纹错开时间启动，形成连续波浪效果
     */
    private fun startRippleWaveAnimation() {
        if (isAnimating) return
        isAnimating = true

        // 波纹配置：从外到内，每层有不同的扩散速度和延迟
        val ripples = listOf(
            RippleConfig(
                view = binding.rippleLayer1,
                targetScale = 1.5f,
                duration = 2500L,
                startDelay = 0L
            ),
            RippleConfig(
                view = binding.rippleLayer2,
                targetScale = 1.5f,
                duration = 2500L,
                startDelay = 600L
            ),
            RippleConfig(
                view = binding.rippleLayer3,
                targetScale = 1.5f,
                duration = 2500L,
                startDelay = 1200L
            ),
            RippleConfig(
                view = binding.rippleLayer4,
                targetScale = 1.5f,
                duration = 2500L,
                startDelay = 1800L
            )
        )

        ripples.forEach { config ->
            startContinuousRipple(config)
        }

        // 核心圆的律动效果
        startCorePulseAnimation()
    }

    /**
     * 启动单个波纹的连续扩散动画
     */
    private fun startContinuousRipple(config: RippleConfig) {
        // 使用可空变量，在 run() 内部引用
        var runnable: Runnable? = null
        runnable = object : Runnable {
            override fun run() {
                if (!isAnimating) return

                val animator = createRippleAnimator(config)
                rippleAnimators.add(animator)

                animator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        rippleAnimators.remove(animator)
                        // 动画结束后，延迟一段时间再开始下一轮
                        if (isAnimating) {
                            runnable?.let { animationHandler.postDelayed(it, 100) }
                        }
                    }
                })

                animator.start()
            }
        }

        // 首次启动
        animationHandler.postDelayed(runnable!!, config.startDelay)
    }

    /**
     * 创建单次波纹扩散动画
     */
    private fun createRippleAnimator(config: RippleConfig): AnimatorSet {
        // 缩放动画：从中心向外扩散
        val scaleX = ObjectAnimator.ofFloat(
            config.view,
            "scaleX",
            1.0f,
            config.targetScale
        ).apply {
            duration = config.duration
            interpolator = AccelerateDecelerateInterpolator()
        }

        val scaleY = ObjectAnimator.ofFloat(
            config.view,
            "scaleY",
            1.0f,
            config.targetScale
        ).apply {
            duration = config.duration
            interpolator = AccelerateDecelerateInterpolator()
        }

        // 透明度动画：逐渐消失
        val alpha = ObjectAnimator.ofFloat(
            config.view,
            "alpha",
            config.view.alpha,
            0.0f
        ).apply {
            duration = config.duration
            interpolator = AccelerateDecelerateInterpolator()
        }

        return AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
        }
    }

    /**
     * 核心圆的三色流动动画
     * 通过双层反向旋转+缩放+透明度变化实现颜色混合流动效果
     */
    private fun startCorePulseAnimation() {
        // 底层顺时针旋转
        val rotation = ObjectAnimator.ofFloat(
            binding.coreCircle,
            "rotation",
            0f,
            360f
        ).apply {
            duration = 6000L
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
        }

        // 叠加层逆时针旋转，产生颜色混合流动效果
        val rotationOverlay = ObjectAnimator.ofFloat(
            binding.coreCircleOverlay,
            "rotation",
            360f,
            0f
        ).apply {
            duration = 4000L
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
        }

        // 缩放律动
        val scaleX = ObjectAnimator.ofFloat(
            binding.coreCircle,
            "scaleX",
            1.0f,
            1.12f,
            1.0f
        ).apply {
            duration = 2500L
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }

        val scaleY = ObjectAnimator.ofFloat(
            binding.coreCircle,
            "scaleY",
            1.0f,
            1.12f,
            1.0f
        ).apply {
            duration = 2500L
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }

        // 叠加层也同步缩放
        val scaleXOverlay = ObjectAnimator.ofFloat(
            binding.coreCircleOverlay,
            "scaleX",
            1.0f,
            1.12f,
            1.0f
        ).apply {
            duration = 2500L
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }

        val scaleYOverlay = ObjectAnimator.ofFloat(
            binding.coreCircleOverlay,
            "scaleY",
            1.0f,
            1.12f,
            1.0f
        ).apply {
            duration = 2500L
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }

        // 透明度脉动 - 叠加层
        val alphaOverlay = ObjectAnimator.ofFloat(
            binding.coreCircleOverlay,
            "alpha",
            0.5f,
            0.7f,
            0.5f
        ).apply {
            duration = 2000L
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }

        val coreAnimator = AnimatorSet().apply {
            playTogether(rotation, rotationOverlay, scaleX, scaleY, scaleXOverlay, scaleYOverlay, alphaOverlay)
        }

        rippleAnimators.add(coreAnimator)
        coreAnimator.start()
    }

    /**
     * 停止波纹动画
     */
    private fun stopRippleAnimation() {
        if (!isAnimating) return
        isAnimating = false

        animationHandler.removeCallbacksAndMessages(null)

        rippleAnimators.forEach { it.cancel() }
        rippleAnimators.clear()

        // 重置所有视图状态
        binding.rippleLayer1.apply {
            scaleX = 1f
            scaleY = 1f
            alpha = 1f
        }
        binding.rippleLayer2.apply {
            scaleX = 1f
            scaleY = 1f
            alpha = 1f
        }
        binding.rippleLayer3.apply {
            scaleX = 1f
            scaleY = 1f
            alpha = 1f
        }
        binding.rippleLayer4.apply {
            scaleX = 1f
            scaleY = 1f
            alpha = 1f
        }
        binding.coreCircle.apply {
            scaleX = 1f
            scaleY = 1f
            rotation = 0f
            alpha = 1f
        }
        binding.coreCircleOverlay.apply {
            scaleX = 1f
            scaleY = 1f
            rotation = 0f
            alpha = 0.6f
        }
    }

    /**
     * 波纹配置数据类
     */
    private data class RippleConfig(
        val view: View,
        val targetScale: Float,
        val duration: Long,
        val startDelay: Long
    )

    override fun onDestroy() {
        super.onDestroy()
        stopRippleAnimation()
        animationHandler.removeCallbacksAndMessages(null)
    }
}
