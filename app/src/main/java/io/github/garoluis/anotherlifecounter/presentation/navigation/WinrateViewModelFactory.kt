package io.github.garoluis.anotherlifecounter.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.garoluis.anotherlifecounter.data.local.GameHistoryRepository
import io.github.garoluis.anotherlifecounter.presentation.stats.StatsViewModel

class WinrateViewModelFactory(
    private val repository: GameHistoryRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
            return StatsViewModel(repository = repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
