package dev.abdallah.rhythm.data.local.model

import android.os.Parcelable
import dev.abdallah.rhythm.data.db.Song
import kotlinx.parcelize.Parcelize

@Parcelize
data class Folder(
    val path: String,
    val name: String,
    val songs: List<Song>
) : Parcelable {
    companion object {
        val NONE = Folder("", "Root", emptyList())
    }
}