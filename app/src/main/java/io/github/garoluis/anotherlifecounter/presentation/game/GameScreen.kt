package io.github.garoluis.anotherlifecounter.presentation.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.garoluis.anotherlifecounter.R
import io.github.garoluis.anotherlifecounter.domain.model.Player
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

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

private fun Modifier.landscapeRotation(isLandscape: Boolean): Modifier {
    if (!isLandscape) return this
    return graphicsLayer(rotationZ = 270f)
        .layout { measurable, constraints ->
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
    LaunchedEffect(Unit) {
        viewModel.initializePlayers(players)
    }

    val uiState by viewModel.uiState.collectAsState()
    val playerCount = uiState.players.size
    var randomNumber by remember { mutableIntStateOf(0) }
    var diceNumber by remember { mutableIntStateOf(0) }
    var isDiceHolding by remember { mutableStateOf(false) }

    LaunchedEffect(isDiceHolding) {
        if (isDiceHolding) {
            delay(1000.milliseconds)
            var count = 2
            diceNumber = count
            while (isDiceHolding) {
                delay(1000.milliseconds)
                count++
                diceNumber = count
            }
        }
    }

    LaunchedEffect(randomNumber) {
        if (randomNumber != 0) {
            delay(5000.milliseconds)
            randomNumber = 0
            diceNumber = 0
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (uiState.isSaved) 1.2f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "saveScale"
    )

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val pressedPointers = event.changes.filter { it.pressed }

                        if (pressedPointers.size >= 2 && randomNumber == 0) {
                            val positions = pressedPointers.map { it.position }
                            val distance = (positions[0] - positions[1]).getDistance()
                            val maxProximity = 500f

                            if (distance <= maxProximity) {
                                isDiceHolding = true

                                while (true) {
                                    val nextEvent = awaitPointerEvent()
                                    val held = nextEvent.changes.filter { it.pressed }
                                    if (held.size < 2) break
                                }

                                isDiceHolding = false
                                if (diceNumber > 0) {
                                    randomNumber = (1..diceNumber).random()
                                }
                            }
                        }
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .landscapeRotation(isLandscape)
        ) {
            when (playerCount) {
                2 -> TwoPlayerLayout(uiState.players, viewModel)
                3 -> ThreePlayerLayout(uiState.players, viewModel)
                4 -> FourPlayerLayout(uiState.players, viewModel)
            }
        }

        SmallFloatingActionButton(
            onClick = { viewModel.saveGame() },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp),
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            AnimatedContent(
                targetState = uiState.isSaved,
                transitionSpec = { fadeIn(tween(100)) togetherWith fadeOut(tween(100)) },
            ) { saved ->
                Icon(
                    imageVector = if (saved) Icons.Default.Check else Icons.Default.Save,
                    contentDescription = if (saved) stringResource(R.string.content_description_saved) else stringResource(
                        R.string.content_description_save_game
                    ),
                    modifier = Modifier.height(if (saved) 22.dp else 18.dp)
                )
            }
        }
        if (diceNumber > 0 && randomNumber == 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary.copy(0.45f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$diceNumber",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = (if (isLandscape) 250.sp else 250.sp).scaled(scale),
                )
            }
        }


        if (randomNumber != 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary.copy(0.45f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$randomNumber/$diceNumber",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = (if (isLandscape) 250.sp else 125.sp).scaled(scale),
                )
            }
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
            onDoubleTap = {id -> viewModel.updateStartingPlayer(id)},
            onDamageChange = { opponentId, delta ->
                viewModel.updateCommanderDamage(players[0].id, opponentId, delta)
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            damageWeight = 0.3f
        )
        HorizontalDivider()
        PlayerPanel(
            player = players[1],
            players = players,
            rotationZ = getPlayerRotation(1, 2),
            onLifeChange = { delta -> viewModel.updateLife(players[1].id, delta) },
            onDoubleTap = {id -> viewModel.updateStartingPlayer(id)},
            onDamageChange = { opponentId, delta ->
                viewModel.updateCommanderDamage(players[1].id, opponentId, delta)
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            damageWeight = 0.3f
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
                onDoubleTap = {id -> viewModel.updateStartingPlayer(id)},
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
                onDoubleTap = {id -> viewModel.updateStartingPlayer(id)},
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
            onDoubleTap = {id -> viewModel.updateStartingPlayer(id)},
            onDamageChange = { opponentId, delta ->
                viewModel.updateCommanderDamage(players[1].id, opponentId, delta)
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            damageWeight = 0.3f
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
                onDoubleTap = {id -> viewModel.updateStartingPlayer(id)},
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
                onDoubleTap = {id -> viewModel.updateStartingPlayer(id)},
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
                onDoubleTap = {id -> viewModel.updateStartingPlayer(id)},
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
                onDoubleTap = {id -> viewModel.updateStartingPlayer(id)},
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
