package io.github.garoluis.anotherlifecounter.presentation.game

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min

private const val REFERENCE_HEIGHT_DP = 700
private const val MIN_SCALE = 0.7f
private const val MAX_SCALE = 1.0f

@Composable
fun rememberScreenScale(): Float {
    val context = LocalContext.current
    val density = LocalDensity.current
    return remember {
        val activity = context as? Activity ?: return@remember 1.0f
        val windowMetrics = activity.windowManager.currentWindowMetrics
        val heightDp = with(density) { windowMetrics.bounds.height().toDp().value }
        min(MAX_SCALE, max(MIN_SCALE, heightDp / REFERENCE_HEIGHT_DP))
    }
}

fun Dp.scaled(scale: Float): Dp = this * scale

fun TextUnit.scaled(scale: Float): TextUnit = (this.value * scale).sp
