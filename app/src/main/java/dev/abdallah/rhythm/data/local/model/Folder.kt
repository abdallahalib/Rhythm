package dev.abdallah.rhythm.data.local.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Folder(
    val path: String,
    val name: String,
) : Parcelable {
    companion object {
        val ROOT = Folder("/", "Root")
    }
}