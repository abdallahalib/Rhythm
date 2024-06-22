package dev.abdallah.rhythm.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Queue(
    @PrimaryKey val id: Long,
    val index: Int,
)