package com.jetpack.kobe.ui.video

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.jetpack.kobe.R
import com.jetpack.kobe.bean.VideoBean

/**
 * 视频流适配器，使用 ViewPager2 实现垂直滑动视频播放
 * 使用 ExoPlayer 实现真实的视频流播放
 */
class VideoFeedAdapter(
    private val videos: List<VideoBean>,
    private val onItemListener: OnItemListener? = null
) : RecyclerView.Adapter<VideoFeedAdapter.VideoViewHolder>() {

    private var currentPlayingPosition = -1
    private val players = mutableMapOf<Int, ExoPlayer>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video_feed, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videos[position], position)
    }

    override fun getItemCount(): Int = videos.size

    override fun onViewRecycled(holder: VideoViewHolder) {
        super.onViewRecycled(holder)
        // 释放播放器资源
        holder.releasePlayer()
    }

    /**
     * 释放所有播放器资源
     */
    fun releaseAllPlayers() {
        players.values.forEach { it.release() }
        players.clear()
    }

    /**
     * 暂停当前播放的视频
     */
    fun pauseCurrentPlayer() {
        players[currentPlayingPosition]?.playWhenReady = false
    }

    /**
     * 恢复当前视频播放
     */
    fun playCurrentPlayer() {
        players[currentPlayingPosition]?.playWhenReady = true
    }

    /**
     * 设置当前播放位置
     */
    fun setCurrentPlayingPosition(position: Int) {
        // 暂停之前的视频
        if (currentPlayingPosition != position && currentPlayingPosition >= 0) {
            players[currentPlayingPosition]?.playWhenReady = false
        }
        currentPlayingPosition = position
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val playerView: PlayerView = itemView.findViewById(R.id.player_view)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvDesc: TextView = itemView.findViewById(R.id.tv_desc)
        private val tvAuthor: TextView = itemView.findViewById(R.id.tv_author)
        private val tvLikeCount: TextView = itemView.findViewById(R.id.tv_like_count)
        private val tvCommentCount: TextView = itemView.findViewById(R.id.tv_comment_count)
        private val ivLike: android.widget.ImageView = itemView.findViewById(R.id.iv_like)
        private val llLike: View = itemView.findViewById(R.id.ll_like)
        private val llComment: View = itemView.findViewById(R.id.ll_comment)
        private val llShare: View = itemView.findViewById(R.id.ll_share)
        private val ivFollow: android.widget.ImageView = itemView.findViewById(R.id.iv_follow)

        private var player: ExoPlayer? = null
        private var currentPosition = -1

        fun bind(video: VideoBean, position: Int) {
            currentPosition = position

            // 设置文本信息
            tvTitle.text = video.title
            tvDesc.text = video.description
            tvLikeCount.text = formatCount(video.likeCount)
            tvCommentCount.text = formatCount(video.commentCount)

            // 作者信息
            if (video.author.isNotEmpty()) {
                tvAuthor.text = video.author
                tvAuthor.visibility = View.VISIBLE
            } else {
                tvAuthor.visibility = View.GONE
            }

            // 点赞状态
            updateLikeIcon(video.isLiked)

            // 初始化播放器
            initPlayer(video)

            // 点击事件
            llLike.setOnClickListener {
                video.isLiked = !video.isLiked
                video.likeCount = if (video.isLiked) {
                    (video.likeCount.toIntOrNull() ?: 0) + 1
                } else {
                    (video.likeCount.toIntOrNull() ?: 1) - 1
                }.toString()
                updateLikeIcon(video.isLiked)
                tvLikeCount.text = formatCount(video.likeCount)
                onItemListener?.onLikeClick(adapterPosition, video)
            }

            llComment.setOnClickListener {
                onItemListener?.onCommentClick(adapterPosition, video)
            }

            llShare.setOnClickListener {
                onItemListener?.onShareClick(adapterPosition, video)
            }

            ivFollow.setOnClickListener {
                ivFollow.visibility = View.GONE
                onItemListener?.onFollowClick(adapterPosition, video)
            }

            // 点击视频区域暂停/播放
            playerView.setOnClickListener {
                player?.let { exoPlayer ->
                    if (exoPlayer.isPlaying) {
                        exoPlayer.playWhenReady = false
                    } else {
                        exoPlayer.playWhenReady = true
                    }
                }
            }
        }

        private fun initPlayer(video: VideoBean) {
            val context = itemView.context

            // 创建 ExoPlayer
            player = ExoPlayer.Builder(context).build().also { exoPlayer ->
                players[currentPosition] = exoPlayer
                playerView.player = exoPlayer

                // 创建媒体项
                val mediaItem = MediaItem.fromUri(video.videoUrl)
                exoPlayer.setMediaItem(mediaItem)

                // 设置监听器
                exoPlayer.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_IDLE -> {
                                progressBar.visibility = View.GONE
                            }
                            Player.STATE_BUFFERING -> {
                                progressBar.visibility = View.VISIBLE
                            }
                            Player.STATE_READY -> {
                                progressBar.visibility = View.GONE
                                // 如果是当前可见项，自动播放
                                if (currentPosition == currentPlayingPosition) {
                                    exoPlayer.playWhenReady = true
                                }
                            }
                            Player.STATE_ENDED -> {
                                // 播放结束，自动重播
                                exoPlayer.seekToDefaultPosition()
                                exoPlayer.playWhenReady = true
                            }
                        }
                    }

                    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                        // 播放状态变化时可以更新 UI
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        progressBar.visibility = View.GONE
                        // 播放错误处理
                        onItemListener?.onPlayError(currentPosition, error.message)
                    }
                })

                // 准备播放
                exoPlayer.prepare()
            }
        }

        private fun updateLikeIcon(isLiked: Boolean) {
            if (isLiked) {
                ivLike.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                ivLike.setImageResource(android.R.drawable.btn_star_big_off)
            }
        }

        private fun formatCount(count: String): String {
            val num = count.toIntOrNull() ?: return count
            return when {
                num >= 10000 -> "${num / 10000}.${num % 10000 / 1000}w"
                num >= 1000 -> "${num / 1000}.${num % 1000 / 100}k"
                else -> count
            }
        }

        fun releasePlayer() {
            player?.run {
                playWhenReady = false
                stop()
                release()
            }
            player = null
            players.remove(currentPosition)
        }
    }

    interface OnItemListener {
        fun onLikeClick(position: Int, video: VideoBean) {}
        fun onCommentClick(position: Int, video: VideoBean) {}
        fun onShareClick(position: Int, video: VideoBean) {}
        fun onFollowClick(position: Int, video: VideoBean) {}
        fun onPlayError(position: Int, errorMsg: String?) {}
    }
}
