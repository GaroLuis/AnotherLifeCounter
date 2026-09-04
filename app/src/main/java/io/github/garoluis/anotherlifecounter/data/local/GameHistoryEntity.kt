package io.github.garoluis.anotherlifecounter.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_history")
data class GameHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val playerCount: Int,
    val playerNames: String,
    val playersJson: String
)
