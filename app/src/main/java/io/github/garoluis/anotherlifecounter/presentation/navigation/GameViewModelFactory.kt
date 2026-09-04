package io.github.garoluis.anotherlifecounter.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.garoluis.anotherlifecounter.data.local.GameHistoryRepository
import io.github.garoluis.anotherlifecounter.presentation.game.GameViewModel

class GameViewModelFactory(
    private val repository: GameHistoryRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(repository = repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
