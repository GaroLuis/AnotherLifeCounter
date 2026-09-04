package io.github.garoluis.anotherlifecounter.data.local

import io.github.garoluis.anotherlifecounter.domain.model.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GameHistoryRepository(private val dao: GameHistoryDao) {

    fun getAllGames(): Flow<List<GameHistoryEntity>> = dao.getAllGames()

    suspend fun saveGame(players: List<Player>) {
        val playerNames = Json.encodeToString(players.map { it.name })
        val playersJson = Json.encodeToString(players)
        val entity = GameHistoryEntity(
            timestamp = System.currentTimeMillis(),
            playerCount = players.size,
            playerNames = playerNames,
            playersJson = playersJson
        )
        dao.insertGame(entity)
    }

    suspend fun deleteGame(id: Long) = dao.deleteGame(id)

    suspend fun deleteGames(ids: List<Long>) = dao.deleteGames(ids)

    suspend fun getGameById(id: Long): List<Player>? {
        return dao.getGameById(id)?.playersJson?.let { json ->
            Json.decodeFromString<List<Player>>(json)
        }
    }
}
