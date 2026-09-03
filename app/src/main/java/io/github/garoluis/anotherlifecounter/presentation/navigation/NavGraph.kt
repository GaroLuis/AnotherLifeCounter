package io.github.garoluis.anotherlifecounter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.github.garoluis.anotherlifecounter.domain.model.Player
import io.github.garoluis.anotherlifecounter.presentation.game.GameScreen
import io.github.garoluis.anotherlifecounter.presentation.setup.SetupScreen
import kotlinx.serialization.json.Json

object Routes {
    const val SETUP = "setup"
    const val GAME = "game/{players}"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SETUP
    ) {
        composable(Routes.SETUP) {
            SetupScreen(
                onStartGame = { players ->
                    val playersJson = Json.encodeToString(players)
                    navController.navigate("game/$playersJson")
                }
            )
        }

        composable(Routes.GAME) { backStackEntry ->
            val playersJson = backStackEntry.arguments?.getString("players") ?: "[]"
            val players = Json.decodeFromString<List<Player>>(playersJson)
            GameScreen(players = players)
        }
    }
}