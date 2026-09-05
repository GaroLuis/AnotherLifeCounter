package io.github.garoluis.anotherlifecounter.presentation.game

import io.github.garoluis.anotherlifecounter.domain.model.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: GameViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = GameViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createPlayers(count: Int = 2): List<Player> {
        return (0 until count).map { Player(id = it, name = "Player $it") }
    }

    @Test
    fun `initializePlayers sets players in state`() = runTest {
        viewModel.initializePlayers(createPlayers(3))
        advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.players.size)
    }

    @Test
    fun `initializePlayers assigns starting player to exactly one player`() = runTest {
        viewModel.initializePlayers(createPlayers(4))
        advanceUntilIdle()

        val startingCount = viewModel.uiState.value.players.count { it.isStartingPlayer }
        assertEquals(1, startingCount)
    }

    @Test
    fun `initializePlayers with empty list sets empty state`() = runTest {
        viewModel.initializePlayers(emptyList())
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.players.isEmpty())
    }

    @Test
    fun `updateLife updates correct player`() = runTest {
        viewModel.initializePlayers(createPlayers(3))
        advanceUntilIdle()

        viewModel.updateLife(playerId = 1, delta = -5)
        advanceUntilIdle()

        val players = viewModel.uiState.value.players
        assertEquals(35, players[1].life)
        assertEquals(40, players[0].life)
        assertEquals(40, players[2].life)
    }

    @Test
    fun `updateLife with positive delta increases life`() = runTest {
        viewModel.initializePlayers(createPlayers(2))
        advanceUntilIdle()

        viewModel.updateLife(playerId = 0, delta = 10)
        advanceUntilIdle()

        assertEquals(50, viewModel.uiState.value.players[0].life)
    }

    @Test
    fun `updateCommanderDamage updates correct player`() = runTest {
        viewModel.initializePlayers(createPlayers(3))
        advanceUntilIdle()

        viewModel.updateCommanderDamage(playerId = 2, opponentId = 0, delta = 4)
        advanceUntilIdle()

        val player2 = viewModel.uiState.value.players[2]
        assertEquals(4, player2.commanderDamage[0])
    }

    @Test
    fun `updateCommanderDamage does not affect other players`() = runTest {
        viewModel.initializePlayers(createPlayers(3))
        advanceUntilIdle()

        viewModel.updateCommanderDamage(playerId = 0, opponentId = 1, delta = 7)
        advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.players[1].commanderDamage.size)
    }

    @Test
    fun `updateCommanderDamage clamps at 0`() = runTest {
        viewModel.initializePlayers(createPlayers(2))
        advanceUntilIdle()

        viewModel.updateCommanderDamage(playerId = 0, opponentId = 1, delta = -3)
        advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.players[0].commanderDamage[1])
    }

    @Test
    fun `saveGame with null repository does not crash`() = runTest {
        viewModel.initializePlayers(createPlayers(2))
        advanceUntilIdle()

        viewModel.saveGame()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSaved)
    }
}
