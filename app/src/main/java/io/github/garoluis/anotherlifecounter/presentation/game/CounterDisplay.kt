package io.github.garoluis.anotherlifecounter.presentation.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CounterDisplay(
    value: Int,
    label: String?,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    isLarge: Boolean = false,
    incrementColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    decrementColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    val scale = rememberScreenScale()

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TextButton(
            onClick = onDecrement,
            modifier = Modifier.size((if (isLarge) 56.dp else 36.dp).scaled(scale).coerceAtLeast(32.dp)),
            contentPadding = if (isLarge) ButtonDefaults.TextButtonContentPadding else PaddingValues(0.dp),
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
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp.scaled(scale))
        )

        TextButton(
            onClick = onIncrement,
            modifier = Modifier.size((if (isLarge) 56.dp else 36.dp).scaled(scale).coerceAtLeast(32.dp)),
            contentPadding = if (isLarge) ButtonDefaults.TextButtonContentPadding else PaddingValues(0.dp),
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
