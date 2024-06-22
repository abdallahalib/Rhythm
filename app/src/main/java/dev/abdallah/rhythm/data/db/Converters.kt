package dev.abdallah.rhythm.data.db

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun fromSongList(value: List<Song>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toSongList(value: String): List<Song> {
        val listType = object : TypeToken<List<Song>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
