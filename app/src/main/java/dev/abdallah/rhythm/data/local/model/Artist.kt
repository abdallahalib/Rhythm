package dev.abdallah.rhythm.data.local.model

import android.os.Parcelable
import dev.abdallah.rhythm.data.db.Song
import kotlinx.parcelize.Parcelize

@Parcelize
data class Artist(
    val name: String,
    val id: String,
    val artworkSmall: String,
    val artworkLarge: String,
    val songs: List<Song>,
): Parcelable {
    companion object {
        val NONE = Artist("", "", "", "", emptyList())
    }
}