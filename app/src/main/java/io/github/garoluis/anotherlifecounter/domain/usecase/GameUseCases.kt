package io.github.garoluis.anotherlifecounter.domain.usecase

import io.github.garoluis.anotherlifecounter.domain.model.Player

data class GameState(
    val players: List<Player> = emptyList(),
    val isGameStarted: Boolean = false
)

class GameUseCases {

    fun createPlayers(count: Int, names: List<String>): List<Player> {
        return (0 until count).map { index ->
            Player(
                id = index,
                name = names.getOrElse(index) { "Commander ${index + 1}" }
            )
        }
    }

    fun updateLife(player: Player, delta: Int): Player {
        return player.copy(life = player.life + delta)
    }

    fun updateCommanderDamage(
        player: Player,
        opponentId: Int,
        delta: Int
    ): Player {
        val currentDamage = player.commanderDamage[opponentId] ?: Player.DEFAULT_DAMAGE
        val newDamage = currentDamage + delta
        return player.copy(
            commanderDamage = player.commanderDamage + (opponentId to newDamage)
        )
    }
}