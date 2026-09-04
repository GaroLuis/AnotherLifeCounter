package io.github.garoluis.anotherlifecounter.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.garoluis.anotherlifecounter.data.local.GameHistoryEntity
import io.github.garoluis.anotherlifecounter.data.local.GameHistoryRepository
import io.github.garoluis.anotherlifecounter.domain.model.Player
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: GameHistoryRepository
) : ViewModel() {

    val games: StateFlow<List<GameHistoryEntity>> = repository.getAllGames()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteGame(id: Long) {
        viewModelScope.launch {
            repository.deleteGame(id)
        }
    }

    suspend fun getGameById(id: Long): List<Player>? = repository.getGameById(id)
}
