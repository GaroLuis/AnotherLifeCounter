package io.github.garoluis.anotherlifecounter.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.garoluis.anotherlifecounter.data.local.GameHistoryEntity
import io.github.garoluis.anotherlifecounter.data.local.GameHistoryRepository
import io.github.garoluis.anotherlifecounter.domain.model.Player
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val COMMANDER_DAMAGE_LETHAL = 21

data class CommanderWinrate(
    val name: String,
    val wins: Int,
    val losses: Int,
    val totalGames: Int
) {
    val winPercentage: Float
        get() = if (totalGames > 0) (wins.toFloat() / totalGames) * 100f else 0f
}

data class HeadToHeadResult(
    val commanderA: String,
    val commanderB: String,
    val winsA: Int,
    val winsB: Int,
    val totalGames: Int
)

data class WinrateUiState(
    val isLoading: Boolean = true,
    val overallWinrates: List<CommanderWinrate> = emptyList(),
    val allCommanderNames: List<String> = emptyList(),
    val selectedCommanderA: String? = null,
    val selectedCommanderB: String? = null,
    val headToHead: HeadToHeadResult? = null,
    val totalFinishedGames: Int = 0
)

class StatsViewModel(
    private val repository: GameHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WinrateUiState())
    val uiState: StateFlow<WinrateUiState> = _uiState.asStateFlow()

    private var cachedGames: List<GameHistoryEntity> = emptyList()
    private var headToHeadJob: Job? = null

    init {
        loadWinrates()
    }

    fun loadWinrates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val games = repository.exportAllGames()
            cachedGames = games

            val allPlayerNames = mutableSetOf<String>()
            val winCounts = mutableMapOf<String, Int>()
            val gameCounts = mutableMapOf<String, Int>()

            var totalGamesWithWinner = 0

            for (game in games) {
                val players = Json.decodeFromString<List<Player>>(game.playersJson)
                val winner = determineWinner(players) ?: continue

                totalGamesWithWinner++
                for (player in players) {
                    allPlayerNames.add(player.name)
                    gameCounts[player.name] = (gameCounts[player.name] ?: 0) + 1
                    if (player.name == winner.name) {
                        winCounts[player.name] = (winCounts[player.name] ?: 0) + 1
                    }
                }
            }

            val overallWinrates = allPlayerNames.sorted().map { name ->
                val wins = winCounts[name] ?: 0
                val total = gameCounts[name] ?: 0
                CommanderWinrate(
                    name = name,
                    wins = wins,
                    losses = total - wins,
                    totalGames = total
                )
            }

            val sortedNames = allPlayerNames.sorted()

            _uiState.update { current ->
                val selectedA = current.selectedCommanderA?.takeIf { it in sortedNames }
                val selectedB = current.selectedCommanderB?.takeIf { it in sortedNames }
                WinrateUiState(
                    isLoading = false,
                    overallWinrates = overallWinrates,
                    allCommanderNames = sortedNames,
                    selectedCommanderA = selectedA,
                    selectedCommanderB = selectedB,
                    headToHead = if (selectedA != null && selectedB != null && selectedA != selectedB) {
                        computeHeadToHead(games, selectedA, selectedB)
                    } else null,
                    totalFinishedGames = totalGamesWithWinner
                )
            }
        }
    }

    fun selectPlayerA(name: String?) {
        _uiState.update { it.copy(selectedCommanderA = name) }
        recomputeHeadToHead()
    }

    fun selectPlayerB(name: String?) {
        _uiState.update { it.copy(selectedCommanderB = name) }
        recomputeHeadToHead()
    }

    private fun recomputeHeadToHead() {
        val state = _uiState.value
        val playerA = state.selectedCommanderA
        val playerB = state.selectedCommanderB
        if (playerA == null || playerB == null || playerA == playerB) {
            headToHeadJob?.cancel()
            _uiState.update { it.copy(headToHead = null) }
            return
        }
        headToHeadJob?.cancel()
        headToHeadJob = viewModelScope.launch {
            val headToHead = computeHeadToHead(cachedGames, playerA, playerB)
            _uiState.update { it.copy(headToHead = headToHead) }
        }
    }

    private fun computeHeadToHead(
        games: List<GameHistoryEntity>,
        playerA: String,
        playerB: String
    ): HeadToHeadResult {
        var winsA = 0
        var winsB = 0
        var total = 0

        for (game in games) {
            val players =  Json.decodeFromString<List<Player>>(game.playersJson)
            val names =  players.map { it.name }
            if (playerA !in names || playerB !in names) continue

            val winner = determineWinner(players) ?: continue
            total++
            when (winner.name) {
                playerA -> winsA++
                playerB -> winsB++
            }
        }

        return HeadToHeadResult(
            commanderA = playerA,
            commanderB = playerB,
            winsA = winsA,
            winsB = winsB,
            totalGames = total
        )
    }

    companion object {
        fun determineWinner(players: List<Player>): Player? {
            if (players.isEmpty()) return null

            val losers = mutableSetOf<Int>()

            for (player in players) {
                if (player.life <= 0) {
                    losers.add(player.id)
                }
                for ((_, damage) in player.commanderDamage) {
                    if (damage >= COMMANDER_DAMAGE_LETHAL) {
                        losers.add(player.id)
                        break
                    }
                }
            }

            val winners = players.filter { it.id !in losers }
            if (winners.size == 1) {
                return winners[0]
            }

            return null
        }
    }
}
