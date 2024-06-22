package dev.abdallah.rhythm.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM song")
    fun getAll(): Flow<List<Song>>

    @Upsert
    fun upsert(vararg song: Song)

    @Delete
    fun delete(song: Song)

    @Delete
    fun deleteAll(songs: List<Song>)
    @Query("SELECT * FROM song WHERE id = :id")
    fun getSong(id: Long): Song

    @Query("SELECT * FROM song WHERE id IN (:id)")
    fun getSongs(vararg id: Long): Flow<List<Song>>

    @Query("SELECT * FROM song WHERE artistId = :artistId")
    fun getArtistSongs(artistId: String): Flow<List<Song>>

    @Query("SELECT * FROM song WHERE albumId = :albumId")
    fun getAlbumSongs(albumId: Long): Flow<List<Song>>

    @Query("SELECT * FROM song WHERE folder = :folder")
    fun getFolderSongs(folder: String): Flow<List<Song>>
}