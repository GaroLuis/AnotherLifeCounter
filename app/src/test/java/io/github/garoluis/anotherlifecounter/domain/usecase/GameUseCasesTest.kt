package io.github.garoluis.anotherlifecounter.domain.usecase

import io.github.garoluis.anotherlifecounter.domain.model.Player
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GameUseCasesTest {

    private lateinit var useCases: GameUseCases

    @Before
    fun setUp() {
        useCases = GameUseCases()
    }

    // createPlayers tests

    @Test
    fun `createPlayers creates correct number of players`() {
        val players = useCases.createPlayers(3, listOf("Alice", "Bob", "Charlie"))
        assertEquals(3, players.size)
    }

    @Test
    fun `createPlayers assigns correct names`() {
        val players = useCases.createPlayers(2, listOf("Alice", "Bob"))
        assertEquals("Alice", players[0].name)
        assertEquals("Bob", players[1].name)
    }

    @Test
    fun `createPlayers assigns sequential ids`() {
        val players = useCases.createPlayers(4, listOf("A", "B", "C", "D"))
        assertEquals(0, players[0].id)
        assertEquals(1, players[1].id)
        assertEquals(2, players[2].id)
        assertEquals(3, players[3].id)
    }

    @Test
    fun `createPlayers uses default name when name is blank`() {
        val players = useCases.createPlayers(2, listOf("", "Bob"))
        assertEquals("Commander 1", players[0].name)
        assertEquals("Bob", players[1].name)
    }

    @Test
    fun `createPlayers uses default name when name list is empty`() {
        val players = useCases.createPlayers(2, emptyList())
        assertEquals("Commander 1", players[0].name)
        assertEquals("Commander 2", players[1].name)
    }

    @Test
    fun `createPlayers uses default name when index out of bounds`() {
        val players = useCases.createPlayers(3, listOf("Alice"))
        assertEquals("Alice", players[0].name)
        assertEquals("Commander 2", players[1].name)
        assertEquals("Commander 3", players[2].name)
    }

    @Test
    fun `createPlayers uses default life of 40`() {
        val players = useCases.createPlayers(1, listOf("Test"))
        assertEquals(Player.DEFAULT_LIFE, players[0].life)
    }

    // updateLife tests

    @Test
    fun `updateLife increases life with positive delta`() {
        val player = Player(id = 0, name = "Test")
        val updated = useCases.updateLife(player, 5)
        assertEquals(45, updated.life)
    }

    @Test
    fun `updateLife decreases life with negative delta`() {
        val player = Player(id = 0, name = "Test")
        val updated = useCases.updateLife(player, -10)
        assertEquals(30, updated.life)
    }

    @Test
    fun `updateLife allows negative life`() {
        val player = Player(id = 0, name = "Test", life = 5)
        val updated = useCases.updateLife(player, -10)
        assertEquals(-5, updated.life)
    }

    // updateCommanderDamage tests

    @Test
    fun `updateCommanderDamage increases damage`() {
        val player = Player(id = 0, name = "Test")
        val updated = useCases.updateCommanderDamage(player, opponentId = 1, delta = 3)
        assertEquals(3, updated.commanderDamage[1])
    }

    @Test
    fun `updateCommanderDamage decreases damage`() {
        val player = Player(id = 0, name = "Test", commanderDamage = mapOf(1 to 5))
        val updated = useCases.updateCommanderDamage(player, opponentId = 1, delta = -2)
        assertEquals(3, updated.commanderDamage[1])
    }

    @Test
    fun `updateCommanderDamage does not go below 0`() {
        val player = Player(id = 0, name = "Test")
        val updated = useCases.updateCommanderDamage(player, opponentId = 1, delta = -1)
        assertEquals(0, updated.commanderDamage[1])
    }

    @Test
    fun `updateCommanderDamage does not go below 0 from existing value`() {
        val player = Player(id = 0, name = "Test", commanderDamage = mapOf(1 to 2))
        val updated = useCases.updateCommanderDamage(player, opponentId = 1, delta = -5)
        assertEquals(0, updated.commanderDamage[1])
    }

    @Test
    fun `updateCommanderDamage subtracts delta from life`() {
        val player = Player(id = 0, name = "Test")
        val updated = useCases.updateCommanderDamage(player, opponentId = 1, delta = 3)
        assertEquals(37, updated.life)
    }

    @Test
    fun `updateCommanderDamage does not change life when clamped at 0`() {
        val player = Player(id = 0, name = "Test")
        val updated = useCases.updateCommanderDamage(player, opponentId = 1, delta = -1)
        assertEquals(40, updated.life)
    }

    @Test
    fun `updateCommanderDamage partially adjusts life when clamped`() {
        val player = Player(id = 0, name = "Test", commanderDamage = mapOf(1 to 1))
        val updated = useCases.updateCommanderDamage(player, opponentId = 1, delta = -3)
        assertEquals(0, updated.commanderDamage[1])
        assertEquals(41, updated.life)
    }
}
