package io.github.garoluis.anotherlifecounter.presentation.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.garoluis.anotherlifecounter.R
import io.github.garoluis.anotherlifecounter.ui.theme.Player1Accent
import io.github.garoluis.anotherlifecounter.ui.theme.Player2Accent
import io.github.garoluis.anotherlifecounter.ui.theme.Player3Accent
import io.github.garoluis.anotherlifecounter.ui.theme.Player4Accent

private val playerColors = listOf(Player1Accent, Player2Accent, Player3Accent, Player4Accent)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.title_statistics),
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground
            )
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.winrate_no_data),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else if (uiState.totalFinishedGames == 0) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.winrate_no_data),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = stringResource(R.string.winrate_overall),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OverallWinrateChart(
                    winrates = uiState.overallWinrates,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                )

                Text(
                    text = stringResource(R.string.winrate_head_to_head),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                HeadToHeadSelectors(
                    allPlayerNames = uiState.allCommanderNames,
                    selectedPlayerA = uiState.selectedCommanderA,
                    selectedPlayerB = uiState.selectedCommanderB,
                    onSelectPlayerA = { viewModel.selectPlayerA(it) },
                    onSelectPlayerB = { viewModel.selectPlayerB(it) },
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val headToHead = uiState.headToHead
                if (headToHead != null) {
                    HeadToHeadChart(
                        result = headToHead,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.winrate_no_head_to_head),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OverallWinrateChart(
    winrates: List<CommanderWinrate>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.heightIn(max = 300.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            winrates.forEachIndexed { index, winrate ->
                val color = playerColors[index % playerColors.size]
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (index < winrates.lastIndex) 12.dp else 0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = winrate.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(
                                R.string.winrate_format_with_games,
                                winrate.winPercentage,
                                winrate.wins,
                                winrate.totalGames
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .height(28.dp)
                    ) {
                        val barHeight = size.height
                        val barWidth = size.width

                        drawRoundRect(
                            color = Color.Gray.copy(alpha = 0.2f),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(6f, 6f)
                        )

                        if (winrate.totalGames > 0) {
                            val winWidth = barWidth * (winrate.wins.toFloat() / winrate.totalGames)
                            drawRoundRect(
                                color = color,
                                size = Size(winWidth, barHeight),
                                cornerRadius = CornerRadius(6f, 6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeadToHeadSelectors(
    allPlayerNames: List<String>,
    selectedPlayerA: String?,
    selectedPlayerB: String?,
    onSelectPlayerA: (String?) -> Unit,
    onSelectPlayerB: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        var expandedA by remember { mutableStateOf(false) }
        var expandedB by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expandedA,
            onExpandedChange = { expandedA = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = selectedPlayerA ?: "",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedA)
                },
                singleLine = true,
            )
            ExposedDropdownMenu(
                expanded = expandedA,
                onDismissRequest = { expandedA = false }
            ) {
                allPlayerNames.forEachIndexed { index, name ->
                    if (index > 0) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 5.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                        )
                    }

                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            onSelectPlayerA(name)
                            expandedA = false
                        }
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = expandedB,
            onExpandedChange = { expandedB = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = selectedPlayerB ?: "",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedB)
                },
                singleLine = true,
            )
            ExposedDropdownMenu(
                expanded = expandedB,
                onDismissRequest = { expandedB = false }
            ) {
                allPlayerNames.forEachIndexed { index, name ->
                    if (index > 0) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 5.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                        )
                    }

                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            onSelectPlayerB(name)
                            expandedB = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeadToHeadChart(
    result: HeadToHeadResult,
    modifier: Modifier = Modifier
) {
    val winRateA = if (result.totalGames > 0) result.winsA.toFloat() / result.totalGames else 0f
    val winRateB = if (result.totalGames > 0) result.winsB.toFloat() / result.totalGames else 0f

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = result.commanderA,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Player1Accent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 15.dp)
                ) {
                    Text(
                        text = "vs",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${result.totalGames} game${if (result.totalGames != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = result.commanderB,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Player2Accent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${result.winsA}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Player1Accent
                    )
                    Canvas(
                        modifier = Modifier
                            .width(60.dp)
                            .height(140.dp)
                            .padding(top = 8.dp)
                    ) {
                        val barHeight = size.height * winRateA
                        val barTop = size.height - barHeight

                        drawRoundRect(
                            color = Color.Gray.copy(alpha = 0.2f),
                            size = Size(size.width, size.height),
                            cornerRadius = CornerRadius(8f, 8f)
                        )

                        drawRoundRect(
                            color = Player1Accent,
                            topLeft = Offset(0f, barTop),
                            size = Size(size.width, barHeight),
                            cornerRadius = CornerRadius(8f, 8f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.winrate_format, winRateA * 100f),
                        style = MaterialTheme.typography.bodySmall,
                        color = Player1Accent
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${result.winsB}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Player2Accent
                    )
                    Canvas(
                        modifier = Modifier
                            .width(60.dp)
                            .height(140.dp)
                            .padding(top = 8.dp)
                    ) {
                        val barHeight = size.height * winRateB
                        val barTop = size.height - barHeight

                        drawRoundRect(
                            color = Color.Gray.copy(alpha = 0.2f),
                            size = Size(size.width, size.height),
                            cornerRadius = CornerRadius(8f, 8f)
                        )

                        drawRoundRect(
                            color = Player2Accent,
                            topLeft = Offset(0f, barTop),
                            size = Size(size.width, barHeight),
                            cornerRadius = CornerRadius(8f, 8f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.winrate_format, winRateB * 100f),
                        style = MaterialTheme.typography.bodySmall,
                        color = Player2Accent
                    )
                }
            }
        }
    }
}
