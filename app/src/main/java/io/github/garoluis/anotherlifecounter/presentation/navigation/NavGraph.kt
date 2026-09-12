package io.github.garoluis.anotherlifecounter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.github.garoluis.anotherlifecounter.data.local.AppDatabase
import io.github.garoluis.anotherlifecounter.data.local.GameHistoryRepository
import io.github.garoluis.anotherlifecounter.domain.model.Player
import io.github.garoluis.anotherlifecounter.presentation.game.GameScreen
import io.github.garoluis.anotherlifecounter.presentation.game.GameViewModel
import io.github.garoluis.anotherlifecounter.presentation.history.HistoryScreen
import io.github.garoluis.anotherlifecounter.presentation.history.HistoryViewModel
import io.github.garoluis.anotherlifecounter.presentation.setup.SetupScreen
import io.github.garoluis.anotherlifecounter.presentation.setup.SetupViewModel
import io.github.garoluis.anotherlifecounter.presentation.stats.StatsScreen
import io.github.garoluis.anotherlifecounter.presentation.stats.StatsViewModel
import kotlinx.serialization.json.Json

object Routes {
    const val SETUP = "setup"
    const val GAME = "game/{players}"
    const val HISTORY = "history"
    const val STATS = "stats"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)
    val repository = GameHistoryRepository(database.gameHistoryDao())

    NavHost(
        navController = navController,
        startDestination = Routes.SETUP
    ) {
        composable(Routes.SETUP) {
            val setupViewModel: SetupViewModel = viewModel(
                factory = SetupViewModelFactory(repository)
            )

            SetupScreen(
                onStartGame = { players ->
                    val playersJson = Json.encodeToString(players)
                    navController.navigate("game/$playersJson")
                },
                onShowHistory = {
                    navController.navigate(Routes.HISTORY)
                },
                onShowStats = {
                    navController.navigate(Routes.STATS)
                },
                viewModel = setupViewModel
            )
        }

        composable(Routes.GAME) { backStackEntry ->
            val playersJson = backStackEntry.arguments?.getString("players") ?: "[]"
            val players = Json.decodeFromString<List<Player>>(playersJson)
            val gameViewModel: GameViewModel = viewModel(
                factory = GameViewModelFactory(repository)
            )
            GameScreen(
                players = players,
                viewModel = gameViewModel
            )
        }

        composable(Routes.HISTORY) {
            val historyViewModel: HistoryViewModel = viewModel(
                factory = HistoryViewModelFactory(repository)
            )
            HistoryScreen(
                onRestoreGame = { players ->
                    val playersJson = Json.encodeToString(players)
                    navController.navigate("game/$playersJson")
                },
                viewModel = historyViewModel
            )
        }

        composable(Routes.STATS) {
            val winrateViewModel: StatsViewModel = viewModel(
                factory = WinrateViewModelFactory(repository)
            )
            StatsScreen(
                viewModel = winrateViewModel
            )
        }
    }
}
