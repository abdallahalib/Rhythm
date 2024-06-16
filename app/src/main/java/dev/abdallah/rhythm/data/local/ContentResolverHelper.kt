package dev.abdallah.rhythm.data.local

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.abdallah.rhythm.data.db.Song
import dev.abdallah.rhythm.util.THUMBNAIL_SMALL_SIZE
import dev.abdallah.rhythm.util.getScreenWidthInPx
import dev.abdallah.rhythm.util.toPx
import java.io.File
import javax.inject.Inject

class ContentResolverHelper @Inject constructor(@ApplicationContext val context: Context) {
    private var mCursor: Cursor? = null

    private val projection: Array<String> = arrayOf(
        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
        MediaStore.Audio.AudioColumns._ID,
        MediaStore.Audio.AudioColumns.ARTIST,
        MediaStore.Audio.AudioColumns.DATA,
        MediaStore.Audio.AudioColumns.DURATION,
        MediaStore.Audio.AudioColumns.TITLE,
        MediaStore.Audio.AudioColumns.ALBUM,
        MediaStore.Audio.AudioColumns.ARTIST_ID,
        MediaStore.Audio.AudioColumns.ALBUM_ID,
    )
    private val sortOrder = "${MediaStore.Audio.AudioColumns.DISPLAY_NAME} ASC"

    private var thumbnailLargeSize = getScreenWidthInPx()

    @WorkerThread
    fun queryAudio(): List<Song> {
        return getCursorData()
    }

    private fun getCursorData(): MutableList<Song> {
        val songList = mutableListOf<Song>()
        mCursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder
        )

        mCursor?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
            val artistIdColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST_ID)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)
            cursor.apply {
                if (count == 0) {
                    Log.e("Cursor", "getCursorData: Cursor is Empty")
                } else {
                    while (cursor.moveToNext()) {
                        val displayName = getString(displayNameColumn)
                        val id = getLong(idColumn)
                        val artist = getString(artistColumn)
                        val artistId = getString(artistIdColumn)
                        val duration = getInt(durationColumn)
                        val title = getString(titleColumn)
                        val uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
                        )
                        val data = getString(dataColumn)
                        val album = getString(albumColumn)
                        val albumId = getLong(albumIdColumn)
                        val artworkLarge = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            getAlbumArtworkPathLarge(albumId, uri)
                        } else {
                            getAlbumArtworkPathLegacy(albumId)
                        }
                        val artworkSmall = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            getAlbumArtworkPathSmall(albumId, uri)
                        } else {
                            artworkLarge
                        }
                        songList += Song(
                            uri = uri.toString(),
                            title = title,
                            artist = artist,
                            artistId = artistId,
                            duration = duration,
                            id = id,
                            displayName = displayName,
                            data = data,
                            album = album,
                            artworkSmall = artworkSmall,
                            artworkLarge = artworkLarge,
                            albumId = albumId
                        )


                    }

                }
            }


        }

        return songList
    }

    private fun getAlbumArtworkPathLegacy(albumId: Long): String {
        val collection = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART
        )
        val selection = "${MediaStore.Audio.Albums._ID} = ?"
        val selectionArgs = arrayOf(
            "$albumId"
        )
        val sortOrder = null
        var path = ""
        context.contentResolver.query(
            collection, projection, selection, selectionArgs, sortOrder
        )?.use { cursor ->
            val albumArtColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)
            while (cursor.moveToNext()) {
                path = cursor.getString(albumArtColumn)
            }
        }
        return path
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAlbumArtworkPathSmall(albumId: Long, uri: Uri): String {
        val file = File(context.filesDir, "${albumId}_small")
        if (file.exists()) {
            return file.path
        } else {
            try {
                val size = THUMBNAIL_SMALL_SIZE.toPx(context).toInt()
                val bitmap: Bitmap = context.contentResolver.loadThumbnail(
                    uri, Size(size, size), null
                )
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, file.outputStream())
                return file.path
            } catch (e: Exception) {
                return ""
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAlbumArtworkPathLarge(albumId: Long, uri: Uri): String {
        val file = File(context.filesDir, "${albumId}_large")
        if (file.exists()) {
            return file.path
        } else {
            try {
                val bitmap: Bitmap = context.contentResolver.loadThumbnail(
                    uri, Size(thumbnailLargeSize, thumbnailLargeSize), null
                )
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, file.outputStream())
                return file.path
            } catch (e: Exception) {
                return ""
            }
        }
    }
}