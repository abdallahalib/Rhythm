package dev.abdallah.rhythm.data.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Song(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val artistId: String,
    val album: String,
    val albumId: Long,
    val duration: Long,
    val displayName: String,
    val data: String,
    val uri: String,
    val folder: String,
    val artworkSmall: String,
    val artworkLarge: String,
) : Parcelable {
    companion object {
        val NONE = Song(
            id = -1,
            title = "",
            artist = "",
            artistId = "",
            album = "",
            albumId = 1,
            duration = 1,
            displayName = "",
            data = "",
            uri = "",
            folder = "",
            artworkSmall = "",
            artworkLarge = "",
        )
    }
}