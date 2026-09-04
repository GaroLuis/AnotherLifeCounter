package io.github.garoluis.anotherlifecounter.presentation.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.garoluis.anotherlifecounter.domain.model.Player
import io.github.garoluis.anotherlifecounter.ui.theme.Player1Accent
import io.github.garoluis.anotherlifecounter.ui.theme.Player2Accent
import io.github.garoluis.anotherlifecounter.ui.theme.Player3Accent
import io.github.garoluis.anotherlifecounter.ui.theme.Player4Accent

private val playerAccents = listOf(Player1Accent, Player2Accent, Player3Accent, Player4Accent)

@Composable
fun PlayerPanel(
    player: Player,
    players: List<Player>,
    rotationZ: Float,
    onLifeChange: (Int) -> Unit,
    onDamageChange: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val playerIndex = players.indexOfFirst { it.id == player.id }
    val accentColor = playerAccents.getOrElse(playerIndex) { Player1Accent }

    Column(
        modifier = modifier
            .graphicsLayer(rotationZ = rotationZ)
            .background(MaterialTheme.colorScheme.surface)
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = player.name,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = accentColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.padding(top = 18.dp, bottom = 2.dp)
        )

        CounterDisplay(
            value = player.life,
            label = null,
            onIncrement = { onLifeChange(1) },
            onDecrement = { onLifeChange(-1) },
            isLarge = true,
            incrementColor = Player1Accent,
            decrementColor = Player1Accent,
            modifier = Modifier.weight(0.4f)
        )

        Column(
            modifier = Modifier.weight(0.6f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            players
                .filter { it.id != player.id }
                .forEach { opponent ->
                    val opponentIndex = players.indexOfFirst { it.id == opponent.id }
                    val opponentAccent = playerAccents.getOrElse(opponentIndex) { Player2Accent }
                    val damage = player.commanderDamage[opponent.id] ?: 0
                    CounterDisplay(
                        value = damage,
                        label = opponent.name,
                        onIncrement = { onDamageChange(opponent.id, 1) },
                        onDecrement = { onDamageChange(opponent.id, -1) },
                        isLarge = false,
                        incrementColor = opponentAccent,
                        decrementColor = opponentAccent,
                        modifier = Modifier.weight(1f)
                    )
                }
        }
    }
}
