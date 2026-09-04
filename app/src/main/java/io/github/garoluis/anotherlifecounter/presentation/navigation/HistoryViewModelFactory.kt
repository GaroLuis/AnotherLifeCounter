package io.github.garoluis.anotherlifecounter.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.garoluis.anotherlifecounter.data.local.GameHistoryRepository
import io.github.garoluis.anotherlifecounter.presentation.history.HistoryViewModel

class HistoryViewModelFactory(
    private val repository: GameHistoryRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(repository = repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
