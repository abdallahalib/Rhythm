package dev.abdallah.rhythm.data

import dev.abdallah.rhythm.data.db.Song
import dev.abdallah.rhythm.data.db.SongDao
import dev.abdallah.rhythm.data.local.ContentResolverHelper
import dev.abdallah.rhythm.data.local.model.Album
import dev.abdallah.rhythm.data.local.model.Artist
import dev.abdallah.rhythm.data.local.model.Folder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class SongRepository @Inject constructor(
    private val contentResolver: ContentResolverHelper,
    private val songDao: SongDao,
) {

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
}