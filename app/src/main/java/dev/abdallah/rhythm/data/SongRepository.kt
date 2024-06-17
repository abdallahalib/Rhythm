package dev.abdallah.rhythm.data

import dev.abdallah.rhythm.data.db.Playlist
import dev.abdallah.rhythm.data.db.PlaylistDao
import dev.abdallah.rhythm.data.db.PlaylistSong
import dev.abdallah.rhythm.data.db.PlaylistSongDao
import dev.abdallah.rhythm.data.db.Song
import dev.abdallah.rhythm.data.db.SongDao
import dev.abdallah.rhythm.data.local.ContentResolverHelper
import dev.abdallah.rhythm.data.local.model.Album
import dev.abdallah.rhythm.data.local.model.Artist
import dev.abdallah.rhythm.data.local.model.Folder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class SongRepository @Inject constructor(
    private val contentResolver: ContentResolverHelper,
    private val songDao: SongDao,
    private val playlistDao: PlaylistDao,
    private val playlistSongDao: PlaylistSongDao
) {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            refreshData()
            playlistDao.insert(Playlist(name = "Favorites", id = 1))
        }
    }

    suspend fun addPlaylist(name: String) = withContext(Dispatchers.IO) {
        playlistDao.insert(Playlist(name = name))
    }

    suspend fun getPlaylists(): Flow<List<Playlist>> = withContext(Dispatchers.IO) {
        return@withContext playlistDao.getAll()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getPlaylistSongs(playlistId: Int): Flow<List<Song>> = withContext(Dispatchers.IO) {
        playlistSongDao.get(playlistId).flatMapConcat { playlistSongs ->
            songDao.getSongs(playlistSongs.map { it.songId })
        }
    }

    suspend fun addPlaylistSong(song: Song, playlist: Playlist) = withContext(Dispatchers.IO) {
        playlistSongDao.insert(PlaylistSong(playlistId = playlist.id, songId = song.id))
    }

    suspend fun removePlaylistSong(songId: Long, playlistId: Int) = withContext(Dispatchers.IO) {
        playlistSongDao.delete(PlaylistSong(playlistId = playlistId, songId = songId))
    }

    suspend fun removePlaylist(playlistId: Long) = withContext(Dispatchers.IO) {
        playlistDao.delete(playlistId)
    }

    suspend fun getSong(id: Long): Song = withContext(Dispatchers.IO) {
        return@withContext songDao.getSong(id)
    }

    suspend fun getSongs(): Flow<List<Song>> = withContext(Dispatchers.IO) {
        songDao.getAll().map { it }
    }

    suspend fun getArtists(): Flow<List<Artist>> = withContext(Dispatchers.IO) {
        songDao.getAll().map { songs ->
            songs.distinctBy { it.artistId }.map { song ->
                Artist(
                    name = song.artist,
                    id = song.artistId,
                    artworkSmall = song.artworkSmall,
                    artworkLarge = song.artworkLarge,
                )
            }
        }
    }

    suspend fun getAlbums(): Flow<List<Album>> = withContext(Dispatchers.IO) {
        songDao.getAll().map { songs ->
            songs.distinctBy { it.albumId }.map { song ->
                Album(
                    name = song.album,
                    id = song.albumId,
                    artworkSmall = song.artworkSmall,
                    artworkLarge = song.artworkLarge,
                )
            }
        }
    }

    suspend fun getFolders(): Flow<List<Folder>> = withContext(Dispatchers.IO) {
        val folderSet = mutableSetOf<Folder>()
        songDao.getAll().map { songs ->
            songs.forEach { song ->
                val parentFile = File(song.data).parentFile
                val folder = Folder(parentFile?.path ?: "", parentFile?.name ?: "")
                folderSet.add(folder)
            }
            return@map folderSet.toList()
        }
    }

    suspend fun refreshData() = withContext(Dispatchers.IO) {
        val songs = contentResolver.queryAudio()
        val newSet = songs.map { it.id }.toSet()
        val existingSet = songDao.getAll().first().map { it.id }.toSet()

        val itemsToDelete = songDao.getAll().first().filter { it.id !in newSet }
        val itemsToAdd = songs.filter { it.id !in existingSet }

        songDao.deleteAll(itemsToDelete)
        songDao.insertAll(itemsToAdd)
    }

    suspend fun onFavorite(song: Song) = withContext(Dispatchers.IO) {
        val favourites = playlistSongDao.get(1).first().filter { it.playlistId == 1 }
        if (favourites.any { it.songId == song.id }) {
            playlistSongDao.delete(favourites.first { it.songId == song.id })
        } else {
            playlistSongDao.insert(PlaylistSong(playlistId = 1, songId = song.id))
        }
    }
}