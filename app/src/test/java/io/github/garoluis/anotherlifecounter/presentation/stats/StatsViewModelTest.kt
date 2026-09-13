package io.github.garoluis.anotherlifecounter.presentation.stats

import io.github.garoluis.anotherlifecounter.domain.model.Player
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StatsViewModelTest {

    // determineWinner tests

    @Test
    fun `determineWinner with empty list returns null`() {
        val result = StatsViewModel.determineWinner(emptyList())
        assertNull(result)
    }

    @Test
    fun `determineWinner with single player alive returns that player`() {
        val player = Player(id = 0, name = "Alice", life = 40)
        val result = StatsViewModel.determineWinner(listOf(player))
        assertEquals(player, result)
    }

    @Test
    fun `determineWinner with single player at zero life returns null`() {
        val player = Player(id = 0, name = "Alice", life = 0)
        val result = StatsViewModel.determineWinner(listOf(player))
        assertNull(result)
    }

    @Test
    fun `determineWinner with single player at negative life returns null`() {
        val player = Player(id = 0, name = "Alice", life = -5)
        val result = StatsViewModel.determineWinner(listOf(player))
        assertNull(result)
    }

    @Test
    fun `determineWinner with two players one at zero life returns the alive one`() {
        val alive = Player(id = 0, name = "Alice", life = 30)
        val dead = Player(id = 1, name = "Bob", life = 0)
        val result = StatsViewModel.determineWinner(listOf(alive, dead))
        assertEquals(alive, result)
    }

    @Test
    fun `determineWinner with two players both alive returns null`() {
        val p1 = Player(id = 0, name = "Alice", life = 30)
        val p2 = Player(id = 1, name = "Bob", life = 25)
        val result = StatsViewModel.determineWinner(listOf(p1, p2))
        assertNull(result)
    }

    @Test
    fun `determineWinner with two players both dead returns null`() {
        val p1 = Player(id = 0, name = "Alice", life = 0)
        val p2 = Player(id = 1, name = "Bob", life = 0)
        val result = StatsViewModel.determineWinner(listOf(p1, p2))
        assertNull(result)
    }

    @Test
    fun `determineWinner with commander damage at exactly 21 marks player as loser`() {
        val winner = Player(id = 0, name = "Alice", life = 30)
        val loser = Player(
            id = 1,
            name = "Bob",
            life = 35,
            commanderDamage = mapOf(0 to 21)
        )
        val result = StatsViewModel.determineWinner(listOf(winner, loser))
        assertEquals(winner, result)
    }

    @Test
    fun `determineWinner with commander damage above 21 marks player as loser`() {
        val winner = Player(id = 0, name = "Alice", life = 30)
        val loser = Player(
            id = 1,
            name = "Bob",
            life = 35,
            commanderDamage = mapOf(0 to 25)
        )
        val result = StatsViewModel.determineWinner(listOf(winner, loser))
        assertEquals(winner, result)
    }

    @Test
    fun `determineWinner with commander damage below 21 does not eliminate player`() {
        val p1 = Player(id = 0, name = "Alice", life = 30, commanderDamage = mapOf(1 to 20))
        val p2 = Player(id = 1, name = "Bob", life = 25, commanderDamage = mapOf(0 to 20))
        val result = StatsViewModel.determineWinner(listOf(p1, p2))
        assertNull(result)
    }

    @Test
    fun `determineWinner with three players one loser returns two winners as null`() {
        val p1 = Player(id = 0, name = "Alice", life = 30)
        val p2 = Player(id = 1, name = "Bob", life = 25)
        val p3 = Player(id = 2, name = "Charlie", life = 0)
        val result = StatsViewModel.determineWinner(listOf(p1, p2, p3))
        assertNull(result)
    }

    @Test
    fun `determineWinner with three players two losers returns the survivor`() {
        val winner = Player(id = 0, name = "Alice", life = 30)
        val loser1 = Player(id = 1, name = "Bob", life = 0)
        val loser2 = Player(id = 2, name = "Charlie", life = -5)
        val result = StatsViewModel.determineWinner(listOf(winner, loser1, loser2))
        assertEquals(winner, result)
    }

    @Test
    fun `determineWinner with four players all dead returns null`() {
        val players = (0..3).map { Player(id = it, name = "P$it", life = 0) }
        val result = StatsViewModel.determineWinner(players)
        assertNull(result)
    }

    @Test
    fun `determineWinner with four players three dead returns the survivor`() {
        val winner = Player(id = 0, name = "Alice", life = 40)
        val losers = (1..3).map { Player(id = it, name = "P$it", life = 0) }
        val result = StatsViewModel.determineWinner(listOf(winner) + losers)
        assertEquals(winner, result)
    }

    // CommanderWinrate tests

    @Test
    fun `CommanderWinrate winPercentage with zero games returns 0`() {
        val winrate = CommanderWinrate(name = "Alice", wins = 0, losses = 0, totalGames = 0)
        assertEquals(0f, winrate.winPercentage)
    }

    @Test
    fun `CommanderWinrate winPercentage with all wins returns 100`() {
        val winrate = CommanderWinrate(name = "Alice", wins = 5, losses = 0, totalGames = 5)
        assertEquals(100f, winrate.winPercentage)
    }

    @Test
    fun `CommanderWinrate winPercentage with half wins returns 50`() {
        val winrate = CommanderWinrate(name = "Alice", wins = 3, losses = 3, totalGames = 6)
        assertEquals(50f, winrate.winPercentage)
    }

    @Test
    fun `CommanderWinrate winPercentage with one win out of three returns 33_33`() {
        val winrate = CommanderWinrate(name = "Alice", wins = 1, losses = 2, totalGames = 3)
        assertEquals(33.33f, winrate.winPercentage, 0.01f)
    }

    @Test
    fun `CommanderWinrate winPercentage with zero wins returns 0`() {
        val winrate = CommanderWinrate(name = "Alice", wins = 0, losses = 5, totalGames = 5)
        assertEquals(0f, winrate.winPercentage)
    }

    // HeadToHeadResult tests

    @Test
    fun `HeadToHeadResult stores correct values`() {
        val result = HeadToHeadResult(
            commanderA = "Alice",
            commanderB = "Bob",
            winsA = 3,
            winsB = 2,
            totalGames = 5
        )
        assertEquals("Alice", result.commanderA)
        assertEquals("Bob", result.commanderB)
        assertEquals(3, result.winsA)
        assertEquals(2, result.winsB)
        assertEquals(5, result.totalGames)
    }
}
