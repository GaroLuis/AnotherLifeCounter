package io.github.garoluis.anotherlifecounter.presentation.game

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CounterDisplay(
    value: Int,
    label: String?,
    modifier: Modifier = Modifier,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    isLarge: Boolean = false,
    incrementColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    decrementColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    var originalValue by remember { mutableIntStateOf(value) }
    val scale = rememberScreenScale()

    LaunchedEffect(value) {
        delay(5000.milliseconds)
        originalValue = value
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TextButton(
            onClick = {},
            modifier = Modifier
                .size((if (isLarge) 56.dp else 36.dp).scaled(scale).coerceAtLeast(32.dp))
                .repeatingClickable(
                    onClick = onDecrement,
                ),
            contentPadding = if (isLarge) ButtonDefaults.TextButtonContentPadding else PaddingValues(
                0.dp
            ),
            colors = ButtonDefaults.textButtonColors(
                contentColor = decrementColor
            )
        ) {
            Text(
                text = "−",
                fontSize = (if (isLarge) 26.sp else 16.sp).scaled(scale),
                fontWeight = FontWeight.Bold
            )
        }

        if (label != null && !isLarge) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp.scaled(scale))
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp.scaled(scale)),
        ) {

            androidx.compose.animation.AnimatedVisibility(
                visible = isLarge && value != originalValue,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = if (originalValue < value) {
                        "+${value - originalValue}"
                    } else {
                        "${value - originalValue}"
                    },
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp.scaled(scale)
                    ),
                    textAlign = TextAlign.Right,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(0.75f)
                )
            }

            Text(
                text = "$value",
                style = if (isLarge) {
                    MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 60.sp.scaled(scale)
                    )
                } else {
                    MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                },
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
        }

        TextButton(
            onClick = {},
            modifier = Modifier
                .size((if (isLarge) 56.dp else 36.dp).scaled(scale).coerceAtLeast(32.dp))
                .repeatingClickable(
                    onClick = onIncrement,
                ),
            contentPadding = if (isLarge) ButtonDefaults.TextButtonContentPadding else PaddingValues(
                0.dp
            ),
            colors = ButtonDefaults.textButtonColors(
                contentColor = incrementColor
            )
        ) {
            Text(
                text = "+",
                fontSize = (if (isLarge) 26.sp else 16.sp).scaled(scale),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun Modifier.repeatingClickable(
    maxDelayMillis: Long = 750,
    minDelayMillis: Long = 100,
    delayDecayFactor: Float = .20f,
    onClick: () -> Unit,
): Modifier = composed {
    val currentClickListener by rememberUpdatedState(onClick)

    pointerInput(Unit) {
        coroutineScope {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)

                val job = launch {
                    var currentDelayMillis = maxDelayMillis

                    while (down.pressed) {
                        currentClickListener()
                        delay(currentDelayMillis.milliseconds)
                        val nextMillis =
                            currentDelayMillis - (currentDelayMillis * delayDecayFactor)
                        currentDelayMillis = nextMillis.toLong().coerceAtLeast(minDelayMillis)
                    }
                }

                waitForUpOrCancellation()
                job.cancel()
            }
        }
    }
}
