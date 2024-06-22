package dev.abdallah.rhythm.data.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Playlist(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val songs: List<Song> = emptyList()
) : Parcelable {
    companion object {
        val NONE = Playlist(0, "")
    }
}