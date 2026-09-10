package io.github.garoluis.anotherlifecounter.presentation.setup

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.garoluis.anotherlifecounter.data.ScryfallApi
import io.github.garoluis.anotherlifecounter.data.local.GameHistoryEntity
import io.github.garoluis.anotherlifecounter.data.local.GameHistoryRepository
import io.github.garoluis.anotherlifecounter.domain.model.Player
import io.github.garoluis.anotherlifecounter.domain.usecase.GameUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.milliseconds

data class Suggestions(
    val data: List<String>,
    val isLoading: Boolean,
)

data class SetupUiState(
    val playerCount: Int = 2,
    val commanderNames: List<String> = List(4) { "" },
    val commanderSuggestions: Map<Int, Suggestions> = emptyMap(),
    val players: List<Player> = emptyList()
)

class SetupViewModel(
    private val repository: GameHistoryRepository,
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

    fun selectCommanderName(index: Int, name: String) {
        searchJobs[index]?.cancel()
        val currentNames = _uiState.value.commanderNames.toMutableList()
        if (index in currentNames.indices) {
            currentNames[index] = name
            _uiState.value = _uiState.value.copy(commanderNames = currentNames)
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

        _uiState.value = _uiState.value.copy(
            commanderSuggestions = _uiState.value.commanderSuggestions +
                    (index to Suggestions(data = emptyList(), isLoading = true))
        )

        searchJobs[index] = viewModelScope.launch {
            delay(300.milliseconds)
            val results = ScryfallApi.searchCommanders(query)
            _uiState.value = _uiState.value.copy(
                commanderSuggestions = _uiState.value.commanderSuggestions + (index to Suggestions(
                    data = results,
                    isLoading = false
                ))
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

    fun exportGames(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val games = repository.exportAllGames()
                val json = Json.encodeToString(games)
                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(json.toByteArray())
                }
                Toast.makeText(context, "Export complete", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun importGames(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val json = context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.bufferedReader().readText()
                } ?: throw Exception("Could not read file")
                val games = Json.decodeFromString<List<GameHistoryEntity>>(json)
                val gamesWithResetIds = games.map { it.copy(id = 0) }
                repository.importGames(gamesWithResetIds)
                Toast.makeText(context, "Import complete", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
