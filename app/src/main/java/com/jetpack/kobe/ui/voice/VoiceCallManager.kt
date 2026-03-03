package com.jetpack.kobe.ui.voice

import android.content.Context
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig

/**
 * 声网语音通话管理器
 *
 * 使用说明：
 * 1. 需要申请声网 AppId 并在调用前设置
 * 2. 调用 joinChannel 加入频道
 * 3. 调用 leaveChannel 离开频道
 *
 * 获取 AppId: https://console.agora.io/
 */
class VoiceCallManager private constructor() {

    private var rtcEngine: RtcEngine? = null
    private var config: RtcEngineConfig? = null
    private var isMuted = false // 本地跟踪静音状态

    companion object {
        private var instance: VoiceCallManager? = null
        private val eventHandlers = mutableListOf<IRtcEngineEventHandler>()

        // TODO: 请替换为你的声网 AppId
        // 获取地址: https://console.agora.io/
        private const val AGORA_APP_ID = "YOUR_AGORA_APP_ID"

        @JvmStatic
        fun getInstance(): VoiceCallManager {
            return instance ?: synchronized(this) {
                instance ?: VoiceCallManager().also { instance = it }
            }
        }

        /**
         * 检查是否已设置 AppId
         */
        fun isAppIdValid(): Boolean {
            return AGORA_APP_ID != "YOUR_AGORA_APP_ID" && AGORA_APP_ID.isNotEmpty()
        }
    }

    /**
     * 初始化 RtcEngine
     */
    fun init(context: Context, eventHandler: IRtcEngineEventHandler): Int {
        if (rtcEngine != null) {
            return 0 // 已经初始化
        }

        // 添加事件处理器
        addEventHandler(eventHandler)

        try {
            config = RtcEngineConfig().apply {
                mContext = context.applicationContext
                mAppId = AGORA_APP_ID
                mEventHandler = object : IRtcEngineEventHandler() {
                    override fun onError(err: Int) {
                        eventHandlers.forEach { it.onError(err) }
                    }

                    override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                        eventHandlers.forEach { it.onJoinChannelSuccess(channel, uid, elapsed) }
                    }

                    override fun onLeaveChannel(stats: io.agora.rtc2.IRtcEngineEventHandler.RtcStats?) {
                        eventHandlers.forEach { it.onLeaveChannel(stats) }
                    }

                    override fun onUserJoined(uid: Int, elapsed: Int) {
                        eventHandlers.forEach { it.onUserJoined(uid, elapsed) }
                    }

                    override fun onUserOffline(uid: Int, reason: Int) {
                        eventHandlers.forEach { it.onUserOffline(uid, reason) }
                    }

                    override fun onRemoteAudioStats(stats: io.agora.rtc2.IRtcEngineEventHandler.RemoteAudioStats?) {
                        eventHandlers.forEach { it.onRemoteAudioStats(stats) }
                    }

                    override fun onAudioVolumeIndication(speakers: Array<out io.agora.rtc2.IRtcEngineEventHandler.AudioVolumeInfo>?, totalVolume: Int) {
                        eventHandlers.forEach { it.onAudioVolumeIndication(speakers, totalVolume) }
                    }
                }
            }

            rtcEngine = RtcEngine.create(config)
            rtcEngine?.apply {
                // 设置音频配置
                setAudioProfile(Constants.AUDIO_PROFILE_MUSIC_HIGH_QUALITY)
                // 启用音频音量指示
                enableAudioVolumeIndication(200, 3, true)
                // 设置扬声器
                setEnableSpeakerphone(true)
            }

            return 0
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }
    }

    /**
     * 加入频道
     *
     * @param channelName 频道名称
     * @param uid 用户ID，0 表示自动分配
     * @param token Token，如启用了 App Certificate 则需要
     */
    fun joinChannel(channelName: String, uid: Int = 0, token: String? = null): Int {
        return rtcEngine?.joinChannel(
            token,
            channelName,
            "Extra Optional Data",
            uid
        ) ?: -1
    }

    /**
     * 离开频道
     */
    fun leaveChannel(): Int {
        return rtcEngine?.leaveChannel() ?: -1
    }

    /**
     * 静音/取消静音
     */
    fun muteLocalAudioStream(muted: Boolean): Int {
        isMuted = muted
        return rtcEngine?.muteLocalAudioStream(muted) ?: -1
    }

    /**
     * 设置扬声器模式
     */
    fun setEnableSpeakerphone(enabled: Boolean): Int {
        return rtcEngine?.setEnableSpeakerphone(enabled) ?: -1
    }

    /**
     * 切换静音状态
     */
    fun toggleMute(): Boolean {
        isMuted = !isMuted
        muteLocalAudioStream(isMuted)
        return isMuted
    }

    /**
     * 是否已静音
     */
    fun isMuted(): Boolean {
        return isMuted
    }

    /**
     * 切换扬声器
     */
    fun toggleSpeakerphone(): Boolean {
        val currentEnabled = rtcEngine?.isSpeakerphoneEnabled() ?: false
        setEnableSpeakerphone(!currentEnabled)
        return !currentEnabled
    }

    /**
     * 是否开启扬声器
     */
    fun isSpeakerphoneEnabled(): Boolean {
        return rtcEngine?.isSpeakerphoneEnabled() ?: false
    }

    /**
     * 添加事件处理器
     */
    private fun addEventHandler(handler: IRtcEngineEventHandler) {
        if (!eventHandlers.contains(handler)) {
            eventHandlers.add(handler)
        }
    }

    /**
     * 移除事件处理器
     */
    fun removeEventHandler(handler: IRtcEngineEventHandler) {
        eventHandlers.remove(handler)
    }

    /**
     * 释放资源
     */
    fun release() {
        rtcEngine?.leaveChannel()
        rtcEngine?.let { RtcEngine.destroy() }
        rtcEngine = null
        config = null
        eventHandlers.clear()
        isMuted = false
    }

    /**
     * 是否已初始化
     */
    fun isInitialized(): Boolean {
        return rtcEngine != null
    }
}
