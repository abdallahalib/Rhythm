package dev.abdallah.rhythm.data.local.model

import android.os.Parcelable
import dev.abdallah.rhythm.data.db.Song
import kotlinx.parcelize.Parcelize

@Parcelize
data class Album(
    val name: String,
    val artworkSmall: String,
    val artworkLarge: String,
    val id: Long,
    val songs: List<Song>
) : Parcelable {
    companion object {
        val NONE = Album("", "", "",-1, emptyList())
    }
}