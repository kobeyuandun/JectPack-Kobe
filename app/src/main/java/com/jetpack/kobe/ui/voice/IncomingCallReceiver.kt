package com.jetpack.kobe.ui.voice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log

/**
 * 来电广播接收器
 * 负责接收来电通知的点击事件（接听/挂断）
 * 在应用后台时被唤起
 */
class IncomingCallReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "IncomingCallReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val channelName = intent.getStringExtra(VoiceCallNotificationManager.EXTRA_CHANNEL_NAME) ?: ""
        val userName = intent.getStringExtra(VoiceCallNotificationManager.EXTRA_USER_NAME) ?: "AI 助手"
        val uid = intent.getIntExtra(VoiceCallNotificationManager.EXTRA_UID, 0)

        Log.d(TAG, "onReceive: action=$action")

        when (action) {
            VoiceCallNotificationManager.ACTION_ACCEPT -> {
                // 接听电话 - 拉起通话界面
                Log.d(TAG, "Accept call: channel=$channelName")
                VoiceCallNotificationManager.cancelIncomingCallNotification(context)
                startVoiceCallActivity(context, channelName, userName, uid, true)
            }
            VoiceCallNotificationManager.ACTION_REJECT -> {
                // 拒绝电话
                Log.d(TAG, "Reject call")
                VoiceCallNotificationManager.cancelIncomingCallNotification(context)
                VoiceCallManager.getInstance().rejectCall(channelName)
            }
            VoiceCallNotificationManager.ACTION_END_CALL -> {
                // 挂断电话
                Log.d(TAG, "End call")
                VoiceCallNotificationManager.cancelOngoingCallNotification(context)
                VoiceCallManager.getInstance().leaveChannel()
                VoiceCallService.stop(context)
            }
        }
    }

    /**
     * 启动语音通话界面
     * @param answered 是否已接听（接听=true，主动拨打=false）
     */
    private fun startVoiceCallActivity(
        context: Context,
        channelName: String,
        userName: String,
        uid: Int,
        answered: Boolean
    ) {
        // 唤醒屏幕
        wakeUpScreen(context)

        val intent = Intent(context, DoubaoVoiceCallActivity::class.java).apply {
            putExtra(VoiceCallNotificationManager.EXTRA_CHANNEL_NAME, channelName)
            putExtra(VoiceCallNotificationManager.EXTRA_USER_NAME, userName)
            putExtra(VoiceCallNotificationManager.EXTRA_UID, uid)
            putExtra("answered", answered)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or
                    Intent.FLAG_ACTIVITY_NO_USER_ACTION
        }
        context.startActivity(intent)
    }

    /**
     * 唤醒屏幕
     */
    private fun wakeUpScreen(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenOn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            powerManager.isInteractive
        } else {
            @Suppress("DEPRECATION")
            powerManager.isScreenOn
        }

        if (!isScreenOn) {
            // 屏幕关闭时，使用 WakeLock 唤醒
            val wakeLock = powerManager.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "VoiceCall:WakeUpScreen"
            ).apply {
                acquire(10000) // 保持唤醒10秒
            }
            // 使用后释放
            @Suppress("WAKE_LOCK_PERMISSION")
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    wakeLock.release(10)
                } else {
                    wakeLock.release()
                }
            } catch (e: Exception) {
                // 忽略
            }
        }
    }
}
