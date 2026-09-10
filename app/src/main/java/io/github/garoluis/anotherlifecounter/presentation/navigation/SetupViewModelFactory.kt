package io.github.garoluis.anotherlifecounter.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.garoluis.anotherlifecounter.data.local.GameHistoryRepository
import io.github.garoluis.anotherlifecounter.domain.usecase.GameUseCases
import io.github.garoluis.anotherlifecounter.presentation.setup.SetupViewModel

class SetupViewModelFactory(
    private val repository: GameHistoryRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SetupViewModel::class.java)) {
            return SetupViewModel(
                repository = repository,
                gameUseCases = GameUseCases()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
