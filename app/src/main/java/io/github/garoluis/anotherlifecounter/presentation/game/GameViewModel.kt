package io.github.garoluis.anotherlifecounter.presentation.game

import androidx.lifecycle.ViewModel
import io.github.garoluis.anotherlifecounter.domain.model.Player
import io.github.garoluis.anotherlifecounter.domain.usecase.GameUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class GameUiState(
    val players: List<Player> = emptyList()
)

class GameViewModel(
    private val gameUseCases: GameUseCases = GameUseCases()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun initializePlayers(players: List<Player>) {
        _uiState.value = GameUiState(players = players)
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
}