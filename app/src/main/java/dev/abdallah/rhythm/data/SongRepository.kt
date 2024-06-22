package dev.abdallah.rhythm.data

import android.util.Log
import dev.abdallah.rhythm.data.db.Playlist
import dev.abdallah.rhythm.data.db.PlaylistDao
import dev.abdallah.rhythm.data.db.Queue
import dev.abdallah.rhythm.data.db.QueueDao
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
    private val queueDao: QueueDao,
    private val playlistDao: PlaylistDao,
) {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            refreshData()
            playlistDao.insert(Playlist(name = "Favorites", id = 1))
        }
    }

    suspend fun addPlaylist(name: String) = withContext(Dispatchers.IO) {
        playlistDao.upsert(Playlist(name = name))
    }

    suspend fun deletePlaylist(playlistId: Long) = withContext(Dispatchers.IO) {
        playlistDao.delete(playlistId)
    }

    suspend fun getPlaylists(): Flow<List<Playlist>> = withContext(Dispatchers.IO) {
        return@withContext playlistDao.getAll()
    }

    suspend fun setQueue(songs: List<Song>) = withContext(Dispatchers.IO) {
        val queue = mutableListOf<Queue>()
        for (index in songs.indices) {
            queue += Queue(id = songs[index].id, index = index)
        }
        queueDao.delete()
        queueDao.upsert(*queue.toTypedArray())
    }

    suspend fun getQueue(): List<Song> = withContext(Dispatchers.IO) {
        val queue = queueDao.get()
        return@withContext songDao.getSongs(*queue.map { it.id }.toLongArray()).first()
    }

    suspend fun getSong(id: Long): Song = withContext(Dispatchers.IO) {
        return@withContext songDao.getSong(id)
    }

    suspend fun getSongs(): Flow<List<Song>> = withContext(Dispatchers.IO) {
        songDao.getAll().map { it }
    }


    fun getArtists(songs: List<Song>): List<Artist> {
        return songs.groupBy { it.artistId }.map { (id, list) ->
            val song = songs.first()
            Artist(
                name = song.artist,
                id = id,
                artworkSmall = song.artworkSmall,
                artworkLarge = song.artworkLarge,
                songs = list
            )
        }
    }

    fun getAlbums(songs: List<Song>): List<Album> {
        return songs.groupBy { it.albumId }.map { (id, list) ->
            val song = list.first()
            Album(
                name = song.album,
                id = id,
                artworkSmall = song.artworkSmall,
                artworkLarge = song.artworkLarge,
                songs = list
            )
        }
    }

    fun getFolders(songs: List<Song>): List<Folder> {
        return songs.groupBy { it.data.substringBeforeLast('/') }
            .map { (path, songsInFolder) ->
                val name = path.substringAfterLast('/')
                Folder(
                    path = path,
                    name = name,
                    songs = songsInFolder
                )
            }
    }

    private suspend fun refreshData() = withContext(Dispatchers.IO) {
        val songs = contentResolver.queryAudio()
        val newSet = songs.map { it.id }.toSet()
        val existingSet = songDao.getAll().first().map { it.id }.toSet()

        val itemsToDelete = songDao.getAll().first().filter { it.id !in newSet }
        val itemsToAdd = songs.filter { it.id !in existingSet }

        songDao.deleteAll(itemsToDelete)
        songDao.upsert(*itemsToAdd.toTypedArray())
    }

    suspend fun addSong(playlist: Playlist, song: Song) = withContext(Dispatchers.IO) {
        playlistDao.addSong(playlist, song)
    }

    suspend fun removeSong(playlist: Playlist, song: Song) = withContext(Dispatchers.IO) {
        playlistDao.removeSong(playlist, song)
    }
}