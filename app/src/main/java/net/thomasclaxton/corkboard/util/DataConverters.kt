package net.thomasclaxton.corkboard.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.thomasclaxton.corkboard.models.NoteListItem

class DataConverters {

    @TypeConverter
    fun toGson(list: List<NoteListItem>): String {
        val gson = Gson()
        val type = object : TypeToken<List<NoteListItem>>() {}.type
        return gson.toJson(list, type)
    }

    @TypeConverter
    fun toList(json: String): List<NoteListItem> {
        val gson = Gson()
        val type = object : TypeToken<List<NoteListItem>>() {}.type
        return gson.fromJson(json, type)
    }
}