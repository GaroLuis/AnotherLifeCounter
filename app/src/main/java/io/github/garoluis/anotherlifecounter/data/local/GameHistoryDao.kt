package io.github.garoluis.anotherlifecounter.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameHistoryDao {
    @Insert
    suspend fun insertGame(entity: GameHistoryEntity): Long

    @Query("SELECT * FROM game_history ORDER BY timestamp DESC")
    fun getAllGames(): Flow<List<GameHistoryEntity>>

    @Query("SELECT * FROM game_history WHERE id = :id")
    suspend fun getGameById(id: Long): GameHistoryEntity?

    @Query("DELETE FROM game_history WHERE id = :id")
    suspend fun deleteGame(id: Long)

    @Query("DELETE FROM game_history WHERE id IN (:ids)")
    suspend fun deleteGames(ids: List<Long>)
}
