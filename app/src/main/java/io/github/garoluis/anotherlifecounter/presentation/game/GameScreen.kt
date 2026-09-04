package io.github.garoluis.anotherlifecounter.presentation.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.garoluis.anotherlifecounter.domain.model.Player

fun getPlayerRotation(playerIndex: Int, totalPlayers: Int): Float {
    return when (totalPlayers) {
        2 -> when (playerIndex) {
            0 -> 180f
            1 -> 0f
            else -> 0f
        }
        3 -> when (playerIndex) {
            0 -> 270f
            1 -> 0f
            2 -> 90f
            else -> 0f
        }
        4 -> when (playerIndex) {
            0, 1 -> 270f
            2, 3 -> 90f
            else -> 0f
        }
        else -> 0f
    }
}

private fun Modifier.rotatedLayout(rotationZ: Float): Modifier {
    if (rotationZ % 180f == 0f) return this
    return layout { measurable, constraints ->
        val swapped = measurable.measure(
            Constraints(
                minWidth = constraints.maxHeight,
                maxWidth = constraints.maxHeight,
                minHeight = constraints.maxWidth,
                maxHeight = constraints.maxWidth
            )
        )
        layout(constraints.maxWidth, constraints.maxHeight) {
            swapped.placeRelative(
                x = (constraints.maxWidth - swapped.width) / 2,
                y = (constraints.maxHeight - swapped.height) / 2
            )
        }
    }
}

@Composable
private fun HorizontalDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outline)
    )
}

@Composable
private fun VerticalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.outline)
    )
}

@Composable
fun GameScreen(
    players: List<Player>,
    viewModel: GameViewModel = viewModel()
) {
    LaunchedEffect(players) {
        viewModel.initializePlayers(players)
    }

    val uiState by viewModel.uiState.collectAsState()
    val playerCount = uiState.players.size

    Box(modifier = Modifier.fillMaxSize()) {
        when (playerCount) {
            2 -> TwoPlayerLayout(uiState.players, viewModel)
            3 -> ThreePlayerLayout(uiState.players, viewModel)
            4 -> FourPlayerLayout(uiState.players, viewModel)
        }

        SmallFloatingActionButton(
            onClick = { viewModel.saveGame() },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = "Save Game",
                modifier = Modifier.height(18.dp)
            )
        }
    }
}

@Composable
private fun TwoPlayerLayout(
    players: List<Player>,
    viewModel: GameViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        PlayerPanel(
            player = players[0],
            players = players,
            rotationZ = getPlayerRotation(0, 2),
            onLifeChange = { delta -> viewModel.updateLife(players[0].id, delta) },
            onDamageChange = { opponentId, delta ->
                viewModel.updateCommanderDamage(players[0].id, opponentId, delta)
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        HorizontalDivider()
        PlayerPanel(
            player = players[1],
            players = players,
            rotationZ = getPlayerRotation(1, 2),
            onLifeChange = { delta -> viewModel.updateLife(players[1].id, delta) },
            onDamageChange = { opponentId, delta ->
                viewModel.updateCommanderDamage(players[1].id, opponentId, delta)
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Composable
private fun ThreePlayerLayout(
    players: List<Player>,
    viewModel: GameViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            PlayerPanel(
                player = players[2],
                players = players,
                rotationZ = getPlayerRotation(2, 3),
                onLifeChange = { delta -> viewModel.updateLife(players[2].id, delta) },
                onDamageChange = { opponentId, delta ->
                    viewModel.updateCommanderDamage(players[2].id, opponentId, delta)
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .rotatedLayout(getPlayerRotation(2, 3))
            )
            VerticalDivider()
            PlayerPanel(
                player = players[0],
                players = players,
                rotationZ = getPlayerRotation(0, 3),
                onLifeChange = { delta -> viewModel.updateLife(players[0].id, delta) },
                onDamageChange = { opponentId, delta ->
                    viewModel.updateCommanderDamage(players[0].id, opponentId, delta)
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .rotatedLayout(getPlayerRotation(0, 3))
            )
        }
        HorizontalDivider()
        PlayerPanel(
            player = players[1],
            players = players,
            rotationZ = getPlayerRotation(1, 3),
            onLifeChange = { delta -> viewModel.updateLife(players[1].id, delta) },
            onDamageChange = { opponentId, delta ->
                viewModel.updateCommanderDamage(players[1].id, opponentId, delta)
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Composable
private fun FourPlayerLayout(
    players: List<Player>,
    viewModel: GameViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            PlayerPanel(
                player = players[2],
                players = players,
                rotationZ = getPlayerRotation(2, 4),
                onLifeChange = { delta -> viewModel.updateLife(players[2].id, delta) },
                onDamageChange = { opponentId, delta ->
                    viewModel.updateCommanderDamage(players[2].id, opponentId, delta)
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .rotatedLayout(getPlayerRotation(2, 4))
            )
            VerticalDivider()
            PlayerPanel(
                player = players[0],
                players = players,
                rotationZ = getPlayerRotation(0, 4),
                onLifeChange = { delta -> viewModel.updateLife(players[0].id, delta) },
                onDamageChange = { opponentId, delta ->
                    viewModel.updateCommanderDamage(players[0].id, opponentId, delta)
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .rotatedLayout(getPlayerRotation(0, 4))
            )
        }
        HorizontalDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            PlayerPanel(
                player = players[3],
                players = players,
                rotationZ = getPlayerRotation(3, 4),
                onLifeChange = { delta -> viewModel.updateLife(players[3].id, delta) },
                onDamageChange = { opponentId, delta ->
                    viewModel.updateCommanderDamage(players[3].id, opponentId, delta)
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .rotatedLayout(getPlayerRotation(3, 4))
            )
            VerticalDivider()
            PlayerPanel(
                player = players[1],
                players = players,
                rotationZ = getPlayerRotation(1, 4),
                onLifeChange = { delta -> viewModel.updateLife(players[1].id, delta) },
                onDamageChange = { opponentId, delta ->
                    viewModel.updateCommanderDamage(players[1].id, opponentId, delta)
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .rotatedLayout(getPlayerRotation(1, 4))
            )
        }
    }
}
