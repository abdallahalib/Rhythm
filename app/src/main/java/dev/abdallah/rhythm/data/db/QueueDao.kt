package dev.abdallah.rhythm.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface QueueDao {
    @Query("SELECT * FROM queue")
    fun get(): List<Queue>

    @Upsert
    fun upsert(vararg queue: Queue)

    @Delete
    fun delete(vararg queue: Queue)

    @Query("DELETE FROM queue")
    fun delete()
}