package dev.abdallah.rhythm.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlist")
    fun getAll(): Flow<List<Playlist>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(playlist: Playlist)

    @Query("DELETE FROM playlist WHERE id = :id")
    fun delete(id: Long)
}