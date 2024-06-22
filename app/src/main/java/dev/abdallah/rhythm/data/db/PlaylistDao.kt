package dev.abdallah.rhythm.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Query("SELECT * FROM playlist")
    fun getAll(): Flow<List<Playlist>>

    @Upsert
    fun upsert(playlist: Playlist)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(playlist: Playlist)

    @Query("DELETE FROM playlist WHERE id = :id")
    fun delete(id: Long)

    @Transaction
    suspend fun addSong(playlist: Playlist, song: Song) {
        val songs = playlist.songs.toMutableList()
        songs.add(song)
        songs.distinct().apply {
            upsert(playlist.copy(songs = this))
        }
    }

    @Transaction
    suspend fun removeSong(playlist: Playlist, song: Song) {
        val songs = playlist.songs.toMutableList()
        songs.remove(song)
        upsert(playlist.copy(songs = songs))
    }
}