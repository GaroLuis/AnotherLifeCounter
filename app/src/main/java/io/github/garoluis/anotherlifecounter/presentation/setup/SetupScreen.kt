package io.github.garoluis.anotherlifecounter.presentation.setup

import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.garoluis.anotherlifecounter.R
import io.github.garoluis.anotherlifecounter.domain.model.Player
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    onStartGame: (List<Player>) -> Unit,
    onShowHistory: () -> Unit = {},
    onShowStats: () -> Unit = {},
    viewModel: SetupViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val keyboardController = LocalSoftwareKeyboardController.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { viewModel.exportGames(context, it) }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.importGames(context, it) }
    }

    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(250.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Bottom

                ) {
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.option_stats)) },
                        selected = false,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = stringResource(R.string.content_description_navigation_stats)
                            )
                        },
                        onClick = {
                            scope.launch {
                                drawerState.snapTo(DrawerValue.Closed)
                                onShowStats()
                            }
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.option_game_history)) },
                        selected = false,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = stringResource(R.string.content_description_navigation_history)
                            )
                        },
                        onClick = {
                            scope.launch {
                                drawerState.snapTo(DrawerValue.Closed)
                                onShowHistory()
                            }
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.option_export_data)) },
                        selected = false,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = stringResource(R.string.content_description_navigation_export_data)
                            )
                        },
                        onClick = {
                            scope.launch {
                                drawerState.snapTo(DrawerValue.Closed)
                                val dateTime = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"))
                                exportLauncher.launch("game_history_backup_$dateTime.json")
                            }
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.option_import_data)) },
                        selected = false,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Upload,
                                contentDescription = stringResource(R.string.content_description_navigation_import_data)
                            )
                        },
                        onClick = {
                            scope.launch {
                                drawerState.snapTo(DrawerValue.Closed)
                                importLauncher.launch(arrayOf("application/json"))
                            }
                        }
                    )
                }
            }
        },
        drawerState = drawerState,
        scrimColor = Color.Black.copy(alpha = 0.75f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.head_line),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = stringResource(R.string.subhead_line),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) {
                                        drawerState.snapTo(DrawerValue.Open)
                                    } else {
                                        drawerState.snapTo(DrawerValue.Closed)
                                    }
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(R.string.content_description_menu),
                                modifier = Modifier.height(22.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.label_number_of_players),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(bottom = 28.dp)
                ) {
                    for (count in 2..4) {
                        val isSelected = uiState.playerCount == count
                        Button(
                            onClick = { viewModel.setPlayerCount(count) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(
                                text = "$count",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.label_commander_names),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    uiState.commanderNames.take(uiState.playerCount)
                        .mapIndexed { index, name -> index to name }
                        .chunked(2)
                        .forEach { row ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                row.forEach { (index, name) ->
                                    val suggestionsObj = uiState.commanderSuggestions[index]


                                    val suggestions = suggestionsObj?.data ?: emptyList()
                                    var expanded by remember { mutableStateOf(false) }

                                    LaunchedEffect(suggestions) {
                                        expanded = suggestions.isNotEmpty()
                                    }

                                    Card(
                                        modifier = Modifier.weight(1f),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        shape = MaterialTheme.shapes.medium
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp)
                                        ) {
                                            Row(
                                                Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.Top,
                                            ) {
                                                Text(
                                                    text = stringResource(
                                                        R.string.default_commander_name,
                                                        index + 1
                                                    ),
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(bottom = 6.dp)
                                                )

                                                if (suggestionsObj?.isLoading == true) {
                                                    Box(
                                                        modifier = Modifier.size(15.dp)
                                                    ) {
                                                        CircularProgressIndicator(
                                                            strokeWidth = 2.5.dp
                                                        )
                                                    }
                                                }
                                            }

                                            ExposedDropdownMenuBox(
                                                expanded = expanded,
                                                onExpandedChange = { expanded = it }
                                            ) {
                                                OutlinedTextField(
                                                    value = name,
                                                    onValueChange = {
                                                        viewModel.updateCommanderName(
                                                            index,
                                                            it
                                                        )
                                                    },
                                                    singleLine = true,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                                        focusedLabelColor = MaterialTheme.colorScheme.primary
                                                    ),
                                                    trailingIcon = {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            if (name.isNotEmpty()) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Close,
                                                                    contentDescription = stringResource(
                                                                        R.string.content_description_clear_select
                                                                    ),
                                                                    modifier = Modifier
                                                                        .size(20.dp)
                                                                        .clickable {
                                                                            viewModel.updateCommanderName(
                                                                                index,
                                                                                ""
                                                                            )
                                                                            viewModel.dismissSuggestions(
                                                                                index
                                                                            )
                                                                        },
                                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                                )
                                                            }
                                                        }
                                                    }
                                                )
                                                ExposedDropdownMenu(
                                                    expanded = expanded && suggestions.isNotEmpty(),
                                                    onDismissRequest = {
                                                        expanded = false
                                                        viewModel.dismissSuggestions(index)
                                                    }
                                                ) {

                                                    suggestions.forEachIndexed { suggestionIndex, suggestion ->
                                                        if (suggestionIndex > 0) {
                                                            HorizontalDivider(
                                                                modifier = Modifier.padding(horizontal = 5.dp),
                                                                thickness = 0.5.dp,
                                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                                                            )
                                                        }

                                                        DropdownMenuItem(
                                                            text = { Text(suggestion) },
                                                            onClick = {
                                                                viewModel.selectCommanderName(
                                                                    index,
                                                                    suggestion
                                                                )
                                                                expanded = false
                                                                viewModel.dismissSuggestions(index)
                                                                keyboardController?.hide()
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                // Fill remaining space if odd number of players
                                if (row.size < 2) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                }
            }

            Button(
                onClick = { onStartGame(viewModel.startGame()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = stringResource(R.string.button_start),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
