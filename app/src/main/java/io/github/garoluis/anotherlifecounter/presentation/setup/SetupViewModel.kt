package io.github.garoluis.anotherlifecounter.presentation.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.garoluis.anotherlifecounter.data.ScryfallApi
import io.github.garoluis.anotherlifecounter.domain.model.Player
import io.github.garoluis.anotherlifecounter.domain.usecase.GameUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class SetupUiState(
    val playerCount: Int = 2,
    val commanderNames: List<String> = List(4) { "" },
    val commanderSuggestions: Map<Int, List<String>> = emptyMap(),
    val players: List<Player> = emptyList()
)

class SetupViewModel(
    private val gameUseCases: GameUseCases = GameUseCases()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    private val searchJobs = mutableMapOf<Int, Job>()

    fun setPlayerCount(count: Int) {
        _uiState.value = _uiState.value.copy(playerCount = count)
    }

    fun updateCommanderName(index: Int, name: String) {
        val currentNames = _uiState.value.commanderNames.toMutableList()
        if (index in currentNames.indices) {
            currentNames[index] = name
            _uiState.value = _uiState.value.copy(commanderNames = currentNames)
            searchCommanders(index, name)
        }
    }

    private fun searchCommanders(index: Int, query: String) {
        searchJobs[index]?.cancel()
        if (query.length < 3) {
            _uiState.value = _uiState.value.copy(
                commanderSuggestions = _uiState.value.commanderSuggestions - index
            )
            return
        }
        searchJobs[index] = viewModelScope.launch {
            delay(300.milliseconds)
            val results = ScryfallApi.searchCommanders(query)
            _uiState.value = _uiState.value.copy(
                commanderSuggestions = _uiState.value.commanderSuggestions + (index to results)
            )
        }
    }

    fun dismissSuggestions(index: Int) {
        _uiState.value = _uiState.value.copy(
            commanderSuggestions = _uiState.value.commanderSuggestions - index
        )
    }

    fun startGame(): List<Player> {
        val state = _uiState.value
        val names = state.commanderNames.take(state.playerCount)
        val players = gameUseCases.createPlayers(state.playerCount, names)
        _uiState.value = state.copy(players = players)
        return players
    }
}
