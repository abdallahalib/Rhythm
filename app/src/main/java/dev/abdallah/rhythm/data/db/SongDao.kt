package dev.abdallah.rhythm.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM song")
    fun getAll(): Flow<List<Song>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(songs: List<Song>)

    @Delete
    fun delete(song: Song)

    @Delete
    fun deleteAll(songs: List<Song>)
    @Query("SELECT * FROM song WHERE id = :id")
    fun getSong(id: Long): Song
}