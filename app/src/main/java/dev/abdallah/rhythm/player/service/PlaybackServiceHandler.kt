package dev.abdallah.rhythm.player.service

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlaybackServiceHandler @Inject constructor(
    private val exoPlayer: ExoPlayer,
) : Player.Listener {
    private val _playbackState: MutableStateFlow<PlaybackState> =
        MutableStateFlow(PlaybackState.Initial)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var job: Job? = null

    init {
        exoPlayer.addListener(this)
    }

    fun setMediaItemList(mediaItems: List<MediaItem>) {
        exoPlayer.setMediaItems(mediaItems)
        exoPlayer.prepare()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun onResume() {
        if (exoPlayer.mediaItemCount > 0) {
            _playbackState.value =
                PlaybackState.NowPlaying(exoPlayer.currentMediaItem?.mediaId ?: "")
            _playbackState.value =
                PlaybackState.Playing(isPlaying = exoPlayer.isPlaying)
            GlobalScope.launch(Dispatchers.Main) {
                startProgressUpdate()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onEvents(player: Player, events: Player.Events) {
        Log.d("PlaybackServiceHandler", "Start events")
        for (i in 0 until events.size()) {
            Log.d("PlaybackServiceHandler", "onEvents: ${events.get(i)}")
        }
        Log.d("PlaybackServiceHandler", "End events")
        when {
            events.contains(Player.EVENT_MEDIA_METADATA_CHANGED) -> {
                _playbackState.value =
                    PlaybackState.NowPlaying(exoPlayer.currentMediaItem?.mediaId ?: "")
            }

            events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED) -> {
                onPlaybackStateChanged(exoPlayer.playbackState)
            }

            events.contains(Player.EVENT_PLAY_WHEN_READY_CHANGED) -> {
                val isPlaying = exoPlayer.isPlaying
                _playbackState.value =
                    PlaybackState.Playing(
                        isPlaying = isPlaying,
                    )
                if (isPlaying) {
                    GlobalScope.launch(Dispatchers.Main) {
                        startProgressUpdate()
                    }
                } else {
                    stopProgressUpdate()
                }
            }
        }
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    suspend fun onPlayerEvents(
        playerEvent: PlayerEvent,
        selectedAudioIndex: Int = -1,
        seekPosition: Long = 0,
    ) {
        when (playerEvent) {
            PlayerEvent.Backward -> exoPlayer.seekBack()
            PlayerEvent.Forward -> exoPlayer.seekForward()
            PlayerEvent.Next -> {
                if (exoPlayer.hasNextMediaItem()) {
                    exoPlayer.seekToNext()
                } else {
                    exoPlayer.seekTo(0, 0)
                }
            }

            PlayerEvent.Previous -> {
                if (exoPlayer.hasPreviousMediaItem()) {
                    exoPlayer.seekToPrevious()
                } else {
                    exoPlayer.seekTo(exoPlayer.mediaItemCount - 1, 0)
                }
            }

            PlayerEvent.PlayPause -> {
                playOrPause()
            }

            PlayerEvent.SeekTo -> exoPlayer.seekTo(seekPosition)
            PlayerEvent.ChangeSong -> {
                exoPlayer.shuffleModeEnabled = false
                when {
                    selectedAudioIndex == exoPlayer.currentMediaItemIndex -> {
                        playOrPause()
                    }
                    selectedAudioIndex < exoPlayer.mediaItemCount -> {
                        exoPlayer.seekTo(selectedAudioIndex, 0)
                        _playbackState.value = PlaybackState.Playing(
                            isPlaying = true,
                        )
                        exoPlayer.playWhenReady = true
                        startProgressUpdate()
                    }
                }
            }

            PlayerEvent.Stop -> stopProgressUpdate()
            is PlayerEvent.UpdateProgress -> {
                exoPlayer.seekTo(
                    (exoPlayer.duration * playerEvent.newProgress).toLong()
                )
            }

            is PlayerEvent.Shuffle -> {
                exoPlayer.seekToDefaultPosition(playerEvent.start)
                exoPlayer.shuffleModeEnabled = true
            }
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_BUFFERING -> _playbackState.value =
                PlaybackState.Buffering(exoPlayer.currentPosition)

            ExoPlayer.STATE_READY -> _playbackState.value =
                PlaybackState.Ready(
                    isPlaying = exoPlayer.isPlaying,
                    id = exoPlayer.currentMediaItem?.mediaId ?: "",
                    mediaItemCount = exoPlayer.mediaItemCount
                )

            Player.STATE_ENDED -> {
                exoPlayer.seekTo(0, 0)
            }

            Player.STATE_IDLE -> {
            }

        }
    }

    private suspend fun playOrPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            stopProgressUpdate()
        } else {
            exoPlayer.play()
            _playbackState.value =
                PlaybackState.NowPlaying(exoPlayer.currentMediaItem?.mediaId ?: "")
            _playbackState.value = PlaybackState.Playing(
                isPlaying = true,
            )
            startProgressUpdate()
        }
    }


    private suspend fun startProgressUpdate() = job.run {
        while (true) {
            delay(500)
            _playbackState.value = PlaybackState.Progress(exoPlayer.currentPosition)
        }
    }

    private fun stopProgressUpdate() {
        job?.cancel()
        _playbackState.value = PlaybackState.Playing(
            isPlaying = false,
        )
    }


}

sealed class PlayerEvent {
    data object PlayPause : PlayerEvent()
    data object ChangeSong : PlayerEvent()
    data object Backward : PlayerEvent()
    data object Next : PlayerEvent()
    data object Previous : PlayerEvent()
    data object Forward : PlayerEvent()
    data object SeekTo : PlayerEvent()
    data object Stop : PlayerEvent()
    data class UpdateProgress(val newProgress: Float) : PlayerEvent()
    data class Shuffle(val start: Int) : PlayerEvent()
}

sealed class PlaybackState {
    data object Initial : PlaybackState()
    data class Ready(val id: String, val isPlaying: Boolean, val mediaItemCount: Int) :
        PlaybackState()

    data class Progress(val progress: Long) : PlaybackState()
    data class Buffering(val progress: Long) : PlaybackState()
    data class Playing(val isPlaying: Boolean) : PlaybackState()
    data class NowPlaying(val id: String) : PlaybackState()
}





