package com.example.newsapp.DB

import androidx.room.TypeConverter
import com.example.newsapp.Models.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source?): String? {
        return source?.name
    }

    @TypeConverter
    fun toSource(name: String?): Source? {
        return name?.let { Source(it, it) }
    }
}
