package com.jetpack.kobe.ui.voice

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.jetpack.kobe.R

/**
 * 全屏来电界面
 */
class IncomingCallActivity : AppCompatActivity() {

    private var channelName: String = ""
    private var userName: String = ""
    private var uid: Int = 0

    private var ringtone: Ringtone? = null
    private var isVibrating = false
    private var callTimer: CountDownTimer? = null

    // 动画相关
    private val avatarAnimators = mutableListOf<Animator>()
    private var isAnimating = false

    // 视图引用
    private lateinit var ivAvatar: View
    private lateinit var tvCallerName: View
    private lateinit var tvCallStatus: View
    private lateinit var btnAccept: View
    private lateinit var btnReject: View
    private lateinit var rippleLayer1: View
    private lateinit var rippleLayer2: View
    private lateinit var rippleLayer3: View

    companion object {
        const val EXTRA_CHANNEL_NAME = "channel_name"
        const val EXTRA_USER_NAME = "user_name"
        const val EXTRA_UID = "uid"
        private const val CALL_TIMEOUT = 30_000L // 30秒未接听自动挂断

        fun start(
            context: Context,
            channelName: String,
            userName: String = "AI 助手",
            uid: Int = 0
        ) {
            val intent = Intent(context, IncomingCallActivity::class.java).apply {
                putExtra(EXTRA_CHANNEL_NAME, channelName)
                putExtra(EXTRA_USER_NAME, userName)
                putExtra(EXTRA_UID, uid)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or
                        Intent.FLAG_ACTIVITY_NO_USER_ACTION
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_call)
        setupFullScreen()
        bindViews()
        getExtras()
        setupUI()
        startIncomingCallEffects()
        startCallTimer()
    }

    private fun setupFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.BLACK
        }
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
    }

    private fun bindViews() {
        ivAvatar = findViewById(R.id.iv_avatar)
        tvCallerName = findViewById(R.id.tv_caller_name)
        tvCallStatus = findViewById(R.id.tv_call_status)
        btnAccept = findViewById(R.id.btn_accept)
        btnReject = findViewById(R.id.btn_reject)
        rippleLayer1 = findViewById(R.id.ripple_layer1)
        rippleLayer2 = findViewById(R.id.ripple_layer2)
        rippleLayer3 = findViewById(R.id.ripple_layer3)
    }

    private fun getExtras() {
        channelName = intent.getStringExtra(EXTRA_CHANNEL_NAME) ?: "default_channel"
        userName = intent.getStringExtra(EXTRA_USER_NAME) ?: "AI 助手"
        uid = intent.getIntExtra(EXTRA_UID, 0)
    }

    private fun setupUI() {
        // 设置来电者信息
        (tvCallerName as android.widget.TextView).text = userName
        (tvCallStatus as android.widget.TextView).text = "语音通话"

        // 设置接听按钮
        btnAccept.setOnClickListener {
            acceptCall()
        }

        // 设置挂断按钮
        btnReject.setOnClickListener {
            rejectCall()
        }
    }

    /**
     * 开始来电效果：铃声 + 震动 + 动画
     */
    private fun startIncomingCallEffects() {
        startRinging()
        startVibrating()
        startAvatarAnimation()
        startRippleAnimation()
    }

    /**
     * 播放铃声
     */
    private fun startRinging() {
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ringtone = RingtoneManager.getRingtone(applicationContext, notification)
            ringtone?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 开始振动
     */
    private fun startVibrating() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pattern = longArrayOf(0, 1000, 1000)
            val effect = VibrationEffect.createWaveform(pattern, 0)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            val pattern = longArrayOf(0, 1000, 1000)
            vibrator.vibrate(pattern, 0)
        }
        isVibrating = true
    }

    /**
     * 停止铃声和振动
     */
    private fun stopCallEffects() {
        ringtone?.stop()
        ringtone = null

        if (isVibrating) {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            vibrator.cancel()
            isVibrating = false
        }

        stopAnimations()
    }

    /**
     * 开始头像呼吸动画
     */
    private fun startAvatarAnimation() {
        isAnimating = true

        // 缩放动画
        val scaleX = ObjectAnimator.ofFloat(ivAvatar, "scaleX", 1f, 1.08f, 1f).apply {
            duration = 2000L
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = AccelerateDecelerateInterpolator()
        }

        val scaleY = ObjectAnimator.ofFloat(ivAvatar, "scaleY", 1f, 1.08f, 1f).apply {
            duration = 2000L
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = AccelerateDecelerateInterpolator()
        }

        avatarAnimators.add(scaleX)
        avatarAnimators.add(scaleY)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.start()
    }

    /**
     * 开始波纹动画
     */
    private fun startRippleAnimation() {
        // 三层波纹
        val ripples = listOf(rippleLayer1, rippleLayer2, rippleLayer3)

        ripples.forEachIndexed { index, view ->
            val delay = index * 600L

            view.postDelayed({
                if (isAnimating) {
                    startSingleRipple(view, delay)
                }
            }, delay)
        }
    }

    /**
     * 单个波纹的动画
     */
    private fun startSingleRipple(view: View, delay: Long) {
        if (!isAnimating) return

        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.5f).apply {
            duration = 2000L
            interpolator = AccelerateDecelerateInterpolator()
        }

        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.5f).apply {
            duration = 2000L
            interpolator = AccelerateDecelerateInterpolator()
        }

        val alpha = ObjectAnimator.ofFloat(view, "alpha", 0.6f, 0f).apply {
            duration = 2000L
            interpolator = AccelerateDecelerateInterpolator()
        }

        val animatorSet = AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator) {
                    if (isAnimating) {
                        view.postDelayed({
                            startSingleRipple(view, delay)
                        }, 100)
                    }
                }

                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }

        avatarAnimators.add(animatorSet)
        animatorSet.start()
    }

    /**
     * 停止所有动画
     */
    private fun stopAnimations() {
        isAnimating = false
        avatarAnimators.forEach { it.cancel() }
        avatarAnimators.clear()

        // 重置波纹视图
        resetView(rippleLayer1)
        resetView(rippleLayer2)
        resetView(rippleLayer3)
    }

    /**
     * 重置视图的变换和透明度
     */
    private fun resetView(view: View) {
        view.scaleX = 1f
        view.scaleY = 1f
        view.alpha = 0.6f
    }

    /**
     * 开始超时计时器
     */
    private fun startCallTimer() {
        callTimer = object : CountDownTimer(CALL_TIMEOUT, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                // 可选：显示剩余时间
            }

            override fun onFinish() {
                // 超时自动挂断
                rejectCall()
            }
        }.start()
    }

    /**
     * 接听电话
     */
    private fun acceptCall() {
        stopCallEffects()
        callTimer?.cancel()

        // 取消来电通知
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1001)

        // 启动语音通话界面
        DoubaoVoiceCallActivity.start(this, channelName, userName, uid)
        finish()
    }

    /**
     * 拒绝电话
     */
    private fun rejectCall() {
        stopCallEffects()
        callTimer?.cancel()

        // 取消来电通知
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1001)

        // 通知 VoiceCallManager 拒绝通话
        VoiceCallManager.getInstance().rejectCall(channelName)

        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCallEffects()
        callTimer?.cancel()
    }

    override fun onBackPressed() {
        // 来电界面不允许通过返回键退出，只能选择接听或拒绝
    }
}
