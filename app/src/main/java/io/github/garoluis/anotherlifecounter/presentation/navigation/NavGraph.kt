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
import io.github.garoluis.anotherlifecounter.presentation.splash.SplashScreen
import kotlinx.serialization.json.Json

object Routes {
    const val SPLASH = "splash"
    const val SETUP = "setup"
    const val GAME = "game/{players}"
    const val HISTORY = "history"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)
    val repository = GameHistoryRepository(database.gameHistoryDao())

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.SETUP) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.SETUP) {
            SetupScreen(
                onStartGame = { players ->
                    val playersJson = Json.encodeToString(players)
                    navController.navigate("game/$playersJson")
                },
                onShowHistory = {
                    navController.navigate(Routes.HISTORY)
                }
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
                onBack = { navController.popBackStack() },
                onRestoreGame = { players ->
                    val playersJson = Json.encodeToString(players)
                    navController.navigate("game/$playersJson") {
                        popUpTo(Routes.SETUP)
                    }
                },
                viewModel = historyViewModel
            )
        }
    }
}
