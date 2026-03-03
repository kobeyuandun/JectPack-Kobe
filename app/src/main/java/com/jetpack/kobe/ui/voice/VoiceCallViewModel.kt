package com.jetpack.kobe.ui.voice

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.agora.rtc2.IRtcEngineEventHandler

/**
 * 语音通话 ViewModel
 */
class VoiceCallViewModel(application: Application) : AndroidViewModel(application) {

    private val voiceCallManager = VoiceCallManager.getInstance()

    // 通话状态
    private val _callState = MutableLiveData<CallState>(CallState.Idle)
    val callState: LiveData<CallState> = _callState

    // 连接状态
    private val _connectionState = MutableLiveData<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: LiveData<ConnectionState> = _connectionState

    // 静音状态
    private val _isMuted = MutableLiveData<Boolean>(false)
    val isMuted: LiveData<Boolean> = _isMuted

    // 扬声器状态
    private val _isSpeakerOn = MutableLiveData<Boolean>(true)
    val isSpeakerOn: LiveData<Boolean> = _isSpeakerOn

    // 通话时长（秒）
    private val _callDuration = MutableLiveData<Int>(0)
    val callDuration: LiveData<Int> = _callDuration

    // 远程用户信息
    private val _remoteUsers = MutableLiveData<Set<Int>>(emptySet())
    val remoteUsers: LiveData<Set<Int>> = _remoteUsers

    // 音量信息
    private val _audioVolumeInfo = MutableLiveData<Pair<Int, Int>>(Pair(0, 0))
    val audioVolumeInfo: LiveData<Pair<Int, Int>> = _audioVolumeInfo

    // RTC 事件处理器
    private val rtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onError(err: Int) {
            _callState.postValue(CallState.Error("通话错误: $err"))
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            _connectionState.postValue(ConnectionState.Connected)
            _callState.postValue(CallState.InCall)
        }

        override fun onLeaveChannel(stats: IRtcEngineEventHandler.RtcStats?) {
            _connectionState.postValue(ConnectionState.Disconnected)
            _callState.postValue(CallState.Ended)
            _callDuration.postValue(0)
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            val currentUsers = _remoteUsers.value ?: emptySet()
            _remoteUsers.postValue(currentUsers + uid)
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            val currentUsers = _remoteUsers.value ?: emptySet()
            _remoteUsers.postValue(currentUsers - uid)
        }

        override fun onAudioVolumeIndication(speakers: Array<out IRtcEngineEventHandler.AudioVolumeInfo>?, totalVolume: Int) {
            speakers?.firstOrNull()?.let {
                _audioVolumeInfo.postValue(Pair(it.uid, it.volume))
            }
        }
    }

    /**
     * 初始化语音通话
     */
    fun initVoiceCall(): Boolean {
        if (!VoiceCallManager.isAppIdValid()) {
            _callState.value = CallState.Error("请先设置声网 AppId")
            return false
        }
        if (!voiceCallManager.isInitialized()) {
            val result = voiceCallManager.init(getApplication(), rtcEventHandler)
            return result == 0
        }
        return true
    }

    /**
     * 加入频道
     */
    fun joinChannel(channelName: String, uid: Int = 0, token: String? = null) {
        if (!initVoiceCall()) return

        _callState.value = CallState.Connecting
        voiceCallManager.joinChannel(channelName, uid, token)
    }

    /**
     * 离开频道
     */
    fun leaveChannel() {
        voiceCallManager.leaveChannel()
        _remoteUsers.value = emptySet()
    }

    /**
     * 切换静音
     */
    fun toggleMute() {
        val newMuteState = voiceCallManager.toggleMute()
        _isMuted.value = newMuteState
    }

    /**
     * 切换扬声器
     */
    fun toggleSpeaker() {
        val newSpeakerState = voiceCallManager.toggleSpeakerphone()
        _isSpeakerOn.value = newSpeakerState
    }

    /**
     * 更新通话时长
     */
    fun updateCallDuration(seconds: Int) {
        _callDuration.value = seconds
    }

    /**
     * 释放资源
     */
    override fun onCleared() {
        super.onCleared()
        voiceCallManager.removeEventHandler(rtcEventHandler)
    }

    /**
     * 通话状态
     */
    sealed class CallState {
        object Idle : CallState()
        object Connecting : CallState()
        object InCall : CallState()
        object Ended : CallState()
        data class Error(val message: String) : CallState()
    }

    /**
     * 连接状态
     */
    sealed class ConnectionState {
        object Disconnected : ConnectionState()
        object Connecting : ConnectionState()
        object Connected : ConnectionState()
    }
}
