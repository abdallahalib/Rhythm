package dev.abdallah.rhythm.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.abdallah.rhythm.data.SongRepository
import dev.abdallah.rhythm.data.db.Playlist
import dev.abdallah.rhythm.data.db.Song
import dev.abdallah.rhythm.data.local.model.Album
import dev.abdallah.rhythm.data.local.model.Artist
import dev.abdallah.rhythm.data.local.model.Folder
import dev.abdallah.rhythm.player.service.PlaybackServiceHandler
import dev.abdallah.rhythm.player.service.PlaybackState
import dev.abdallah.rhythm.player.service.PlayerEvent
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "SongViewModel"

@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class SongViewModel @Inject constructor(
    private val playbackServiceHandler: PlaybackServiceHandler,
    private val repository: SongRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var progress by savedStateHandle.saveable { mutableFloatStateOf(0f) }
    var isPlaying by savedStateHandle.saveable { mutableStateOf(false) }
    var nowPlaying by savedStateHandle.saveable { mutableStateOf(Song.NONE) }
    var songs by savedStateHandle.saveable { mutableStateOf(listOf<Song>()) }
    var playlists by savedStateHandle.saveable { mutableStateOf(listOf<Playlist>()) }
    var folderList by savedStateHandle.saveable { mutableStateOf(listOf<Folder>()) }
    var artistList by savedStateHandle.saveable { mutableStateOf(listOf<Artist>()) }
    var albumList by savedStateHandle.saveable { mutableStateOf(listOf<Album>()) }
    var selectedFolder by savedStateHandle.saveable { mutableStateOf(Folder.ROOT) }
    var selectedAlbum by savedStateHandle.saveable { mutableStateOf(Album.NONE) }
    var selectedArtist by savedStateHandle.saveable { mutableStateOf(Artist.NONE) }
    var selectedPlaylist by savedStateHandle.saveable { mutableStateOf(Playlist.NONE) }
    var playlistSongList by savedStateHandle.saveable { mutableStateOf(listOf<Song>()) }
    var favorites by savedStateHandle.saveable { mutableStateOf(listOf<Song>()) }

    init {
        viewModelScope.launch {
            playbackServiceHandler.playbackState.collect { mediaState ->
                when (mediaState) {
                    is PlaybackState.Initial -> {
                    }

                    is PlaybackState.Buffering -> calculateProgressValue(mediaState.progress)
                    is PlaybackState.Playing -> isPlaying = mediaState.isPlaying
                    is PlaybackState.Progress -> calculateProgressValue(mediaState.progress)
                    is PlaybackState.NowPlaying -> {
                        mediaState.id.toLongOrNull()?.let {
                            nowPlaying = repository.getSong(it)
                        }
                    }

                    is PlaybackState.Ready -> {
                        isPlaying = mediaState.isPlaying
                    }
                }
            }
        }
        viewModelScope.launch {
            loadAudioData()
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            repository.refreshData()
        }
    }

    fun selectFolder(folder: Folder) {
        selectedFolder = folder
    }

    fun selectAlbum(album: Album) {
        selectedAlbum = album
    }

    fun selectArtist(artist: Artist) {
        selectedArtist = artist
    }

    private fun loadAudioData() {
        viewModelScope.launch {
            repository.getSongs().collect {
                songs = it
                playbackServiceHandler.onResume()
            }
        }
        viewModelScope.launch {
            repository.getArtists().collect {
                artistList = it
            }
        }
        viewModelScope.launch {
            repository.getAlbums().collect {
                albumList = it
            }
        }
        viewModelScope.launch {
            repository.getFolders().collect {
                folderList = it
            }
        }
        viewModelScope.launch {
            repository.getPlaylists().collect {
                playlists = it
            }
        }
        viewModelScope.launch {
            repository.getPlaylistSongs(1).collect {
                favorites = it
                Log.d(TAG, "Favorites: $it")
            }
        }
    }

    fun onFavorite(song: Song) {
        viewModelScope.launch {
            repository.onFavorite(song)
            repository.getPlaylistSongs(1).collect {
                favorites = it
                if (selectedPlaylist.id == 1) {
                    playlistSongList = it
                }
            }
            repository.getPlaylists().collect {
                playlists = it
            }
        }
    }

    fun getFolderSongs(): List<Song> {
        return songs.filter { audio ->
            audio.data.contains(selectedFolder.path)
        }
    }

    fun getAlbumSongs(): List<Song> {
        return songs.filter { audio ->
            audio.albumId == selectedAlbum.id
        }
    }

    fun getArtistSongs(): List<Song> {
        return songs.filter { song ->
            song.artistId == selectedArtist.id
        }
    }

    fun setMediaItemList(songs: List<Song>) {
        viewModelScope.launch {
            songs.map { song ->
                MediaItem.Builder().setUri(song.uri).setMediaId(song.id.toString())
                    .setMediaMetadata(
                        MediaMetadata.Builder().setAlbumArtist(song.artist)
                            .setDisplayTitle(song.title)
                            .setSubtitle(song.displayName).build()
                    ).build()
            }.also {
                playbackServiceHandler.setMediaItemList(it)
            }
        }
    }

    private fun calculateProgressValue(currentProgress: Long) {
        progress =
            if (currentProgress > 0) ((currentProgress.toFloat() / nowPlaying.duration.toFloat()))
            else 0f
    }

    fun onUiEvents(uiEvents: UIEvents) = viewModelScope.launch {
        when (uiEvents) {
            UIEvents.Backward -> playbackServiceHandler.onPlayerEvents(PlayerEvent.Backward)
            UIEvents.Forward -> playbackServiceHandler.onPlayerEvents(PlayerEvent.Forward)
            UIEvents.Next -> playbackServiceHandler.onPlayerEvents(PlayerEvent.Next)
            UIEvents.Previous -> playbackServiceHandler.onPlayerEvents(PlayerEvent.Previous)
            is UIEvents.PlayPause -> {
                playbackServiceHandler.onPlayerEvents(
                    PlayerEvent.PlayPause
                )
            }

            is UIEvents.SeekTo -> {
                playbackServiceHandler.onPlayerEvents(
                    PlayerEvent.SeekTo,
                    seekPosition = ((nowPlaying.duration * uiEvents.position) / 100f).toLong()
                )
            }

            is UIEvents.ChangeSong -> {
                playbackServiceHandler.onPlayerEvents(
                    PlayerEvent.ChangeSong, selectedAudioIndex = uiEvents.index
                )
            }

            is UIEvents.UpdateProgress -> {
                playbackServiceHandler.onPlayerEvents(
                    PlayerEvent.UpdateProgress(
                        uiEvents.newProgress
                    )
                )
                progress = uiEvents.newProgress
            }

            is UIEvents.Shuffle -> {
                playbackServiceHandler.onPlayerEvents(PlayerEvent.Shuffle(uiEvents.start))
            }
        }
    }

    override fun onCleared() {
        viewModelScope.launch {
            playbackServiceHandler.onPlayerEvents(PlayerEvent.Stop)
        }
        super.onCleared()
    }

    fun selectPlaylist(playlist: Playlist) {
        selectedPlaylist = playlist
        viewModelScope.launch {
            repository.getPlaylistSongs(playlist.id).collect {
                playlistSongList = it
            }
        }
    }

    fun getPlaylistSongs(): List<Song> {
        return playlistSongList
    }

    fun newPlaylist(playlistName: String) {
        viewModelScope.launch {
            repository.addPlaylist(playlistName)
            repository.getPlaylists().collect {
                playlists = it
            }
        }
    }
}


sealed class UIEvents {
    data object PlayPause : UIEvents()
    data class ChangeSong(val index: Int) : UIEvents()
    data class SeekTo(val position: Float) : UIEvents()
    data object Next : UIEvents()
    data object Previous : UIEvents()
    data object Backward : UIEvents()
    data object Forward : UIEvents()
    data class UpdateProgress(val newProgress: Float) : UIEvents()
    data class Shuffle(val start: Int) : UIEvents()
}


