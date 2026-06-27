package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_posters")
data class FavoritePoster(
    @PrimaryKey val id: String, // unique poster ID
    val title: String,
    val category: String,
    val dateStr: String, // e.g. "2026-11-05" or "Every Day"
    val drawableResName: String, // Resource filename
    val timestamp: Long = System.currentTimeMillis()
)
