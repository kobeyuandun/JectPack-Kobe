package com.jetpack.kobe.ui.voice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jetpack.kobe.R

/**
 * 语音通话通知管理器
 * 负责创建和管理来电通知、通话中通知
 */
object VoiceCallNotificationManager {

    private const val CHANNEL_ID_INCOMING = "voice_call_incoming"
    private const val CHANNEL_ID_ONGOING = "voice_call_ongoing"
    const val NOTIFICATION_ID_INCOMING = 1001
    const val NOTIFICATION_ID_ONGOING = 1002

    /**
     * 通知ID常量
     */
    const val EXTRA_CHANNEL_NAME = "channel_name"
    const val EXTRA_USER_NAME = "user_name"
    const val EXTRA_UID = "uid"
    const val ACTION_ACCEPT = "com.jetpack.kobe.ACTION_ACCEPT_CALL"
    const val ACTION_REJECT = "com.jetpack.kobe.ACTION_REJECT_CALL"
    const val ACTION_END_CALL = "com.jetpack.kobe.ACTION_END_CALL"

    /**
     * 初始化通知渠道
     */
    fun initChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 来电通知渠道（高优先级，发出声音和振动）
            val incomingChannel = NotificationChannel(
                CHANNEL_ID_INCOMING,
                "语音通话",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "语音通话来电通知"
                setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI, null)
                enableVibration(true)
                enableLights(true)
            }

            // 通话中通知渠道（低优先级，静音）
            val ongoingChannel = NotificationChannel(
                CHANNEL_ID_ONGOING,
                "通话中",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "语音通话进行中"
                setSound(null, null)
                enableVibration(false)
            }

            notificationManager.createNotificationChannel(incomingChannel)
            notificationManager.createNotificationChannel(ongoingChannel)
        }
    }

    /**
     * 显示来电通知（全屏通知）
     */
    fun showIncomingCallNotification(
        context: Context,
        channelName: String,
        userName: String,
        uid: Int
    ) {
        initChannels(context)

        // 接听意图
        val acceptIntent = Intent(context, IncomingCallReceiver::class.java).apply {
            action = ACTION_ACCEPT
            putExtra(EXTRA_CHANNEL_NAME, channelName)
            putExtra(EXTRA_USER_NAME, userName)
            putExtra(EXTRA_UID, uid)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val acceptPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            acceptIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 拒绝意图
        val rejectIntent = Intent(context, IncomingCallReceiver::class.java).apply {
            action = ACTION_REJECT
            putExtra(EXTRA_CHANNEL_NAME, channelName)
            putExtra(EXTRA_USER_NAME, userName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val rejectPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            rejectIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 全屏意图 - 点击通知时直接拉起接听界面
        val fullScreenIntent = Intent(context, IncomingCallActivity::class.java).apply {
            putExtra(EXTRA_CHANNEL_NAME, channelName)
            putExtra(EXTRA_USER_NAME, userName)
            putExtra(EXTRA_UID, uid)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or
                    Intent.FLAG_ACTIVITY_NO_USER_ACTION
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_INCOMING)
            .setSmallIcon(R.drawable.ic_voice_call)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setAutoCancel(false)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentTitle(userName)
            .setContentText("语音通话")
            .addAction(R.drawable.ic_call, "接听", acceptPendingIntent)
            .addAction(R.drawable.ic_call_end, "挂断", rejectPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_INCOMING, notification)
    }

    /**
     * 显示通话中通知（用于前台服务）
     */
    fun showOngoingCallNotification(
        context: Context,
        userName: String
    ): Notification {
        initChannels(context)

        // 挂断意图
        val endCallIntent = Intent(context, IncomingCallReceiver::class.java).apply {
            action = ACTION_END_CALL
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val endCallPendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            endCallIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ONGOING)
            .setSmallIcon(R.drawable.ic_voice_call)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setContentTitle("正在与 $userName 通话")
            .setContentText("点击挂断")
            .setContentIntent(endCallPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_ONGOING, notification)
        return notification
    }

    /**
     * 取消来电通知
     */
    fun cancelIncomingCallNotification(context: Context) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID_INCOMING)
    }

    /**
     * 取消通话中通知
     */
    fun cancelOngoingCallNotification(context: Context) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID_ONGOING)
    }
}
