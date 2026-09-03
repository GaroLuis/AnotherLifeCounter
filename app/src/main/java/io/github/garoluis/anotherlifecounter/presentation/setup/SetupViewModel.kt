package io.github.garoluis.anotherlifecounter.presentation.setup

import androidx.lifecycle.ViewModel
import io.github.garoluis.anotherlifecounter.domain.model.Player
import io.github.garoluis.anotherlifecounter.domain.usecase.GameUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SetupUiState(
    val playerCount: Int = 2,
    val commanderNames: List<String> = List(4) { "" },
    val players: List<Player> = emptyList()
)

class SetupViewModel(
    private val gameUseCases: GameUseCases = GameUseCases()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    fun setPlayerCount(count: Int) {
        _uiState.value = _uiState.value.copy(playerCount = count)
    }

    fun updateCommanderName(index: Int, name: String) {
        val currentNames = _uiState.value.commanderNames.toMutableList()
        if (index in currentNames.indices) {
            currentNames[index] = name
            _uiState.value = _uiState.value.copy(commanderNames = currentNames)
        }
    }

    fun startGame(): List<Player> {
        val state = _uiState.value
        val names = state.commanderNames.take(state.playerCount)
        val players = gameUseCases.createPlayers(state.playerCount, names)
        _uiState.value = state.copy(players = players)
        return players
    }
}