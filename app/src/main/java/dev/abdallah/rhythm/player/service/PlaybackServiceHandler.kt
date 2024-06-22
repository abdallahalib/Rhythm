package dev.abdallah.rhythm.player.service

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "PlaybackServiceHandler"

class PlaybackServiceHandler @Inject constructor(
    private val exoPlayer: ExoPlayer,
) : Player.Listener {

    private val _playbackState: MutableStateFlow<PlaybackState> =
        MutableStateFlow(PlaybackState.Initial)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    init {
        exoPlayer.addListener(this)
    }

    fun isRunning(): Boolean {
        return exoPlayer.playbackState != Player.STATE_IDLE
    }

    fun getCurrentMediaItemIndex() = exoPlayer.currentMediaItemIndex

    fun setMediaItemList(mediaItems: List<MediaItem>, index: Int) {
        exoPlayer.setMediaItems(
            mediaItems,
            index,
            0,
        )
        exoPlayer.prepare()
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        Log.d(TAG, "Playback state: $playbackState")
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        Log.d(TAG, "isPlaying: $isPlaying")
        if (isPlaying) {
            onPositionChanged()
            _playbackState.update {
                PlaybackState.Playing
            }
        } else if (exoPlayer.playbackState != Player.STATE_BUFFERING) {
            _playbackState.update {
                PlaybackState.Paused
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun onPositionChanged() {
        GlobalScope.launch(Dispatchers.Main) {
            while (exoPlayer.isPlaying) {
                delay(500)
                Log.d(
                    TAG,
                    (exoPlayer.currentPosition.toFloat() / exoPlayer.duration.toFloat()).toString()
                )
                _playbackState.update {
                    PlaybackState.Progress(exoPlayer.currentPosition)
                }
            }
        }
    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)
        exoPlayer.currentMediaItem?.let { mediaItem ->
            _playbackState.update {
                PlaybackState.NowPlaying(
                    index = exoPlayer.currentMediaItemIndex,
                )
            }
        }
    }

    fun onPlayerEvent(event: PlayerEvent) {
        when (event) {
            is PlayerEvent.PlayPause -> {
                if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
            }

            is PlayerEvent.Change -> {
                exoPlayer.shuffleModeEnabled = false
                exoPlayer.seekToDefaultPosition(event.position)
                if (!exoPlayer.isPlaying) exoPlayer.play()
            }

            is PlayerEvent.Next -> {
                if (exoPlayer.hasNextMediaItem()) {
                    exoPlayer.seekToNextMediaItem()
                } else {
                    exoPlayer.seekToDefaultPosition(0)
                }
            }

            is PlayerEvent.Previous -> {
                if (exoPlayer.hasPreviousMediaItem()) {
                    exoPlayer.seekToPreviousMediaItem()
                } else {
                    exoPlayer.seekToDefaultPosition(exoPlayer.mediaItemCount - 1)
                }
            }

            is PlayerEvent.Seek -> {
                exoPlayer.seekTo(
                    (event.position * exoPlayer.duration).toLong()
                )
            }
        }
    }

    fun isPlaying() = exoPlayer.isPlaying
}

sealed class PlayerEvent {
    data object PlayPause : PlayerEvent()
    data class Change(val position: Int) : PlayerEvent()
    data object Next : PlayerEvent()
    data object Previous : PlayerEvent()
    data class Seek(val position: Float) : PlayerEvent()
}

sealed class PlaybackState {
    data object Initial : PlaybackState()
    data object Playing : PlaybackState()
    data object Paused : PlaybackState()
    data class Progress(val position: Long) : PlaybackState()
    data class NowPlaying(val index: Int) : PlaybackState()
    data class Error(val message: String) : PlaybackState()
}





