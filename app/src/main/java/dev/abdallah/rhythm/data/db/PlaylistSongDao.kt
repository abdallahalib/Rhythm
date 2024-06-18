package dev.abdallah.rhythm.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistSongDao {
    @Query("SELECT * FROM playlistsong WHERE playlistId = :playlistId")
    fun get(playlistId: Int): Flow<List<PlaylistSong>>

    @Query("SELECT * FROM playlistsong")
    fun getAll(): Flow<List<PlaylistSong>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(playlistSong: PlaylistSong)

    @Query("DELETE FROM playlistsong WHERE songId = :songId AND playlistId = :playlistId")
    fun delete(songId: Long, playlistId: Int)

    @Query("DELETE FROM playlistsong WHERE playlistId = :playlistId")
    fun deletePlaylist(playlistId: Int)
}