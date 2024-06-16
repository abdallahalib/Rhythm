package dev.abdallah.rhythm.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Playlist(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String
)