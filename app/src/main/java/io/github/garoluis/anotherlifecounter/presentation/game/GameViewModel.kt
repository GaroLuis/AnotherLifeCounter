package io.github.garoluis.anotherlifecounter.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.garoluis.anotherlifecounter.data.local.GameHistoryRepository
import io.github.garoluis.anotherlifecounter.domain.model.Player
import io.github.garoluis.anotherlifecounter.domain.usecase.GameUseCases
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class GameUiState(
    val players: List<Player> = emptyList(),
    val isSaved: Boolean = false
)

class GameViewModel(
    private val gameUseCases: GameUseCases = GameUseCases(),
    private val repository: GameHistoryRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun initializePlayers(players: List<Player>) {
        val startingId = if (players.isNotEmpty()) players.random().id else null
        val updatedPlayers = players.map { it.copy(isStartingPlayer = it.id == startingId) }
        _uiState.value = GameUiState(players = updatedPlayers)
    }

    fun updateLife(playerId: Int, delta: Int) {
        _uiState.update { state ->
            val updatedPlayers = state.players.map { player ->
                if (player.id == playerId) {
                    gameUseCases.updateLife(player, delta)
                } else {
                    player
                }
            }
            state.copy(players = updatedPlayers)
        }
    }

    fun updateCommanderDamage(playerId: Int, opponentId: Int, delta: Int) {
        _uiState.update { state ->
            val updatedPlayers = state.players.map { player ->
                if (player.id == playerId) {
                    gameUseCases.updateCommanderDamage(player, opponentId, delta)
                } else {
                    player
                }
            }
            state.copy(players = updatedPlayers)
        }
    }

    fun saveGame() {
        val repo = repository ?: return
        viewModelScope.launch {
            repo.saveGame(_uiState.value.players)
            _uiState.update { it.copy(isSaved = true) }
            delay(1500.milliseconds)
            _uiState.update { it.copy(isSaved = false) }
        }
    }
}
