package dev.abdallah.rhythm.data.local.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Artist(
    val name: String,
    val id: String,
    val artworkSmall: String,
    val artworkLarge: String,
): Parcelable {
    companion object {
        val NONE = Artist("", "", "", "")
    }
}