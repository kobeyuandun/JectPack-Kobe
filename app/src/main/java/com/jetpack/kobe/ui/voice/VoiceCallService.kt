package com.jetpack.kobe.ui.voice

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat

/**
 * 语音通话前台服务
 * 用于保持通话连接，防止系统杀死应用
 */
class VoiceCallService : Service() {

    private val binder = LocalBinder()
    private var isRunning = false
    private var currentChannelName = ""
    private var currentUserName = ""

    inner class LocalBinder : Binder() {
        fun getService(): VoiceCallService = this@VoiceCallService
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_CALL -> {
                val channelName = intent.getStringExtra(EXTRA_CHANNEL_NAME) ?: ""
                val userName = intent.getStringExtra(EXTRA_USER_NAME) ?: "AI 助手"
                startForegroundService(channelName, userName)
            }
            ACTION_UPDATE_CALL -> {
                val userName = intent.getStringExtra(EXTRA_USER_NAME) ?: "AI 助手"
                updateNotification(userName)
            }
            ACTION_END_CALL -> {
                stopForegroundService()
            }
        }
        return START_STICKY
    }

    /**
     * 启动前台服务并显示通知
     */
    private fun startForegroundService(channelName: String, userName: String) {
        currentChannelName = channelName
        currentUserName = userName
        isRunning = true

        val notification = VoiceCallNotificationManager.showOngoingCallNotification(
            this,
            userName
        )

        startForeground(VoiceCallNotificationManager.NOTIFICATION_ID_ONGOING, notification)
    }

    /**
     * 更新通话通知
     */
    private fun updateNotification(userName: String) {
        currentUserName = userName
        val notification = VoiceCallNotificationManager.showOngoingCallNotification(
            this,
            userName
        )
        val notificationManager = getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify(VoiceCallNotificationManager.NOTIFICATION_ID_ONGOING, notification)
    }

    /**
     * 停止前台服务
     */
    private fun stopForegroundService() {
        isRunning = false
        VoiceCallNotificationManager.cancelOngoingCallNotification(this)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    /**
     * 是否正在通话
     */
    fun isInCall(): Boolean = isRunning

    /**
     * 获取当前通话信息
     */
    fun getCallInfo(): Pair<String, String> = Pair(currentChannelName, currentUserName)

    companion object {
        const val ACTION_START_CALL = "com.jetpack.kobe.ACTION_START_CALL"
        const val ACTION_UPDATE_CALL = "com.jetpack.kobe.ACTION_UPDATE_CALL"
        const val ACTION_END_CALL = "com.jetpack.kobe.ACTION_END_CALL"

        const val EXTRA_CHANNEL_NAME = "channel_name"
        const val EXTRA_USER_NAME = "user_name"

        /**
         * 启动通话服务
         */
        fun start(context: Context, channelName: String, userName: String) {
            val intent = Intent(context, VoiceCallService::class.java).apply {
                action = ACTION_START_CALL
                putExtra(EXTRA_CHANNEL_NAME, channelName)
                putExtra(EXTRA_USER_NAME, userName)
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        /**
         * 更新通话服务
         */
        fun update(context: Context, userName: String) {
            val intent = Intent(context, VoiceCallService::class.java).apply {
                action = ACTION_UPDATE_CALL
                putExtra(EXTRA_USER_NAME, userName)
            }
            context.startService(intent)
        }

        /**
         * 停止通话服务
         */
        fun stop(context: Context) {
            val intent = Intent(context, VoiceCallService::class.java).apply {
                action = ACTION_END_CALL
            }
            context.startService(intent)
        }
    }
}
