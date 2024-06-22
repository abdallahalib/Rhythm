package dev.abdallah.rhythm.ui.viewmodel

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.abdallah.rhythm.Screen
import dev.abdallah.rhythm.data.SongRepository
import dev.abdallah.rhythm.data.db.Playlist
import dev.abdallah.rhythm.data.db.Song
import dev.abdallah.rhythm.data.local.model.Album
import dev.abdallah.rhythm.data.local.model.Artist
import dev.abdallah.rhythm.data.local.model.Folder
import dev.abdallah.rhythm.player.service.PlaybackServiceHandler
import dev.abdallah.rhythm.player.service.PlaybackState
import dev.abdallah.rhythm.player.service.PlayerEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "SongViewModel"

@HiltViewModel
class SongViewModel @Inject constructor(
    private val playbackServiceHandler: PlaybackServiceHandler,
    private val repository: SongRepository,
) : ViewModel() {

    private val _songState = MutableStateFlow(SongState())
    private val _songs = MutableStateFlow(emptyList<Song>())
    val state = combine(_songState, _songs) { state, songs ->
        state.copy(
            songs = songs,
            folders = repository.getFolders(songs),
            artists = repository.getArtists(songs),
            albums = repository.getAlbums(songs),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), SongState())

    init {
        onResume()
        getSongs()
        getPlaylists()
        viewModelScope.launch {
            playbackServiceHandler.playbackState.collect { playbackState ->
                when (playbackState) {
                    is PlaybackState.Initial -> {
                    }

                    is PlaybackState.Playing -> {
                        _songState.update {
                            it.copy(isPlaying = true)
                        }
                    }

                    is PlaybackState.Paused -> {
                        _songState.update {
                            it.copy(isPlaying = false)
                        }
                    }

                    is PlaybackState.Progress -> {
                        _songState.update {
                            it.copy(position = playbackState.position)
                        }
                    }

                    is PlaybackState.NowPlaying -> {
                        _songState.update { songState ->
                            songState.copy(index = playbackState.index)
                        }
                    }

                    is PlaybackState.Error -> {


                    }
                }
            }
        }
    }

    private fun getPlaylists() {
        viewModelScope.launch {
            repository.getPlaylists().collect { playlists ->
                _songState.update {
                    it.copy(
                        playlists = playlists
                    )
                }
            }
        }
    }

    private fun getSongs() {
        viewModelScope.launch {
            repository.getSongs().collect { songs ->
                _songs.update {
                    songs
                }
            }
        }
    }

    private fun onResume() {
        if (playbackServiceHandler.isRunning()) {
            val index = playbackServiceHandler.getCurrentMediaItemIndex()
            val isPlaying = playbackServiceHandler.isPlaying()
            viewModelScope.launch {
                _songState.update {
                    it.copy(
                        isPlaying = isPlaying, index = index, queue = repository.getQueue()
                    )
                }
            }
        }
    }

    fun onEvent(event: SongEvent) {
        when (event) {
            is SongEvent.PlayPause -> {
                viewModelScope.launch {
                    playbackServiceHandler.onPlayerEvent(PlayerEvent.PlayPause)
                }
            }

            is SongEvent.Seek -> {
                viewModelScope.launch {
                    playbackServiceHandler.onPlayerEvent(PlayerEvent.Seek(event.position))
                }
            }

            is SongEvent.Change -> {
                if (event.songs.isNotEmpty()) {
                    viewModelScope.launch {
                        if (event.songs != _songState.value.queue) {
                            setQueue(event.songs, event.index)
                        }
                        playbackServiceHandler.onPlayerEvent(PlayerEvent.Change(event.index))
                    }
                }
            }

            is SongEvent.Next -> {
                viewModelScope.launch {
                    playbackServiceHandler.onPlayerEvent(PlayerEvent.Next)
                }
            }

            is SongEvent.Previous -> {
                viewModelScope.launch {
                    playbackServiceHandler.onPlayerEvent(PlayerEvent.Previous)
                }
            }

            is SongEvent.Shuffle -> {
                if (event.songs.isNotEmpty()) {
                    viewModelScope.launch {
                        setQueue(event.songs.shuffled(), 0)
                        playbackServiceHandler.onPlayerEvent(PlayerEvent.Change(0))
                    }
                }
            }

            is SongEvent.Share -> {

            }

            is SongEvent.Edit -> {

            }

            is SongEvent.AddTo -> {

            }

            is SongEvent.DeleteFrom -> {

            }

            is SongEvent.Filter -> {
                _songState.update {
                    it.copy(
                        filter = event.filter,
                    )
                }
            }

            is SongEvent.DeletePlaylist -> {

            }

            is SongEvent.RemoveSong -> {

            }

            is SongEvent.Navigate -> {
                _songState.update {
                    it.copy(
                        screen = event.screen
                    )
                }
            }

            is SongEvent.Favorite -> {
                viewModelScope.launch {
                    if (state.value.playlists.first().songs.any { it.id == event.song.id }) {
                        repository.removeSong(state.value.playlists.first(), event.song)
                    } else {
                        repository.addSong(state.value.playlists.first(), event.song)
                    }
                    getPlaylists()
                }
            }

            SongEvent.ShowNewPlaylistDialog -> {
                _songState.update {
                    it.copy(
                        showNewPlaylistDialog = true
                    )
                }
            }

            SongEvent.HideNewPlaylistDialog -> {
                _songState.update {
                    it.copy(
                        showNewPlaylistDialog = false
                    )
                }
            }

            is SongEvent.NewPlaylist -> {
                _songState.update {
                    it.copy(
                        showNewPlaylistDialog = false
                    )
                }
                viewModelScope.launch {
                    repository.addPlaylist(event.name)
                    getPlaylists()
                }
            }
        }
    }

    private fun setQueue(songs: List<Song>, index: Int) {
        val mediaItems = songs.map { song ->
            MediaItem.Builder().setUri(song.uri).setMediaId(song.id.toString()).setMediaMetadata(
                MediaMetadata.Builder().setAlbumArtist(song.artist).setTitle(song.title)
                    .setArtworkUri(song.artwork.toUri()).build()
            ).build()
        }
        playbackServiceHandler.setMediaItemList(mediaItems, index)
        viewModelScope.launch {
            _songState.update {
                it.copy(
                    index = index, queue = songs
                )
            }
            repository.setQueue(songs)
        }
    }
}

sealed interface SongEvent {
    data object PlayPause : SongEvent
    data class Seek(val position: Float) : SongEvent
    data class Change(val songs: List<Song>, val index: Int) : SongEvent
    data object Next : SongEvent
    data object Previous : SongEvent
    data class Shuffle(val songs: List<Song>) : SongEvent
    data class Share(val song: Song) : SongEvent
    data class Edit(val song: Song) : SongEvent
    data class AddTo(val playlist: Playlist, val song: Song) : SongEvent
    data class DeleteFrom(val playlist: Playlist, val song: Song) : SongEvent
    data class Filter(val filter: SongFilter) : SongEvent
    data class DeletePlaylist(val playlistId: Long) : SongEvent
    data class RemoveSong(val songId: Long, val playlistId: Long) : SongEvent
    data class Navigate(val screen: Screen) : SongEvent
    data class Favorite(val song: Song) : SongEvent
    data object ShowNewPlaylistDialog : SongEvent
    data object HideNewPlaylistDialog : SongEvent
    data class NewPlaylist(val name: String) : SongEvent
}

data class SongState(
    val songs: List<Song> = emptyList(),
    val index: Int = 0,
    val isPlaying: Boolean = false,
    val position: Long = 0,
    val playlists: List<Playlist> = emptyList(),
    val folders: List<Folder> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val albums: List<Album> = emptyList(),
    val filter: SongFilter = SongFilter.All,
    val screen: Screen = Screen.HOME,
    val queue: List<Song> = listOf(Song.NONE),
    val showNewPlaylistDialog : Boolean = false,
)

sealed class SongFilter {
    data object All : SongFilter()
    data class Folder(val folder: dev.abdallah.rhythm.data.local.model.Folder) : SongFilter()
    data class Album(val album: dev.abdallah.rhythm.data.local.model.Album) : SongFilter()
    data class Artist(val artist: dev.abdallah.rhythm.data.local.model.Artist) : SongFilter()
    data class Playlist(val playlist: dev.abdallah.rhythm.data.db.Playlist) : SongFilter()
}