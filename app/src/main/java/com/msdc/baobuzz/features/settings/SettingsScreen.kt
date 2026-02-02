package com.msdc.baobuzz.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.msdc.baobuzz.models.League

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
        viewModel: SettingsViewModel = hiltViewModel(),
        onNavigateToOnboarding: () -> Unit = {},
        onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    Column(
            modifier =
                    Modifier.fillMaxSize()
                            .background(
                                    Brush.verticalGradient(
                                            colors =
                                                    listOf(
                                                            MaterialTheme.colorScheme.background,
                                                            MaterialTheme.colorScheme.surfaceVariant
                                                                    .copy(alpha = 0.3f)
                                                    )
                                    )
                            )
                            .padding(16.dp)
    ) {
        // Enhanced Header Section
        Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                        CardDefaults.cardColors(
                                containerColor =
                                        MaterialTheme.colorScheme.primaryContainer.copy(
                                                alpha = 0.1f
                                        )
                        ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                                text = "Settings",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                                text = "Customize Your Experience",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Card(
                        colors =
                                CardDefaults.cardColors(
                                        containerColor =
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                ),
                        shape = RoundedCornerShape(12.dp)
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (val state = uiState) {
            is SettingsUiState.Loading -> {
                LoadingState()
            }
            is SettingsUiState.Error -> {
                ErrorState(message = state.message, onRetry = { viewModel.retry() })
            }
            is SettingsUiState.DataCleared -> {
                DataClearedState(onNavigateToOnboarding = onNavigateToOnboarding)
            }
            is SettingsUiState.Loaded -> {
                LoadedContent(
                        state = state,
                        viewModel = viewModel,
                        availableLeagues = viewModel.availableLeagues,
                        onShowClearDataDialog = { showClearDataDialog = true },
                        onShowLanguageDialog = { showLanguageDialog = true }
                )
            }
        }
    }

    // Clear Data Confirmation Dialog
    if (showClearDataDialog) {
        AlertDialog(
                onDismissRequest = { showClearDataDialog = false },
                title = { Text("Clear All Data?") },
                text = {
                    Text(
                            "This will remove all your preferences and you'll need to set up the app again. This action cannot be undone."
                    )
                },
                confirmButton = {
                    TextButton(
                            onClick = {
                                viewModel.clearAllData()
                                showClearDataDialog = false
                            }
                    ) { Text("Clear Data") }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDataDialog = false }) { Text("Cancel") }
                }
        )
    }

    // Language Selection Dialog
    if (showLanguageDialog) {
        LanguageSelectionDialog(
                currentLanguage = (uiState as? SettingsUiState.Loaded)?.preferredLanguage ?: "en",
                onLanguageSelected = { languageCode: String ->
                    viewModel.changeLanguage(languageCode)
                    showLanguageDialog = false
                },
                onDismiss = { showLanguageDialog = false }
        )
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                    text = "Loading settings...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                    )
    ) {
        Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                    text = "Error loading settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) { Text("Try Again") }
        }
    }
}

@Composable
private fun DataClearedState(onNavigateToOnboarding: () -> Unit) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
    ) {
        Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                    text = "Data Cleared Successfully",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                    text =
                            "All your preferences have been cleared. You can now set up the app again.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNavigateToOnboarding, modifier = Modifier.fillMaxWidth()) {
                Text("Start Setup")
            }
        }
    }
}

@Composable
private fun LoadedContent(
        state: SettingsUiState.Loaded,
        viewModel: SettingsViewModel,
        availableLeagues: List<League>,
        onShowClearDataDialog: () -> Unit,
        onShowLanguageDialog: () -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // League Management Section
        item {
            LeagueManagementSection(
                    selectedLeagues = state.selectedLeagues,
                    availableLeagues = availableLeagues,
                    onAddLeague = viewModel::addLeague,
                    onRemoveLeague = viewModel::removeLeague
            )
        }

        // Notifications Section
        item {
            NotificationSection(
                    notificationsEnabled = state.notificationsEnabled,
                    onToggleNotifications = viewModel::toggleNotifications
            )
        }

        // App Preferences Section
        item {
            AppPreferencesSection(
                    preferredLanguage = state.preferredLanguage,
                    onShowLanguageDialog = onShowLanguageDialog
            )
        }

        // Data Management Section
        item { DataManagementSection(onShowClearDataDialog = onShowClearDataDialog) }

        // About Section
        item { AboutSection() }
    }
}

@Composable
private fun LeagueManagementSection(
        selectedLeagues: List<League>,
        availableLeagues: List<League>,
        onAddLeague: (League) -> Unit,
        onRemoveLeague: (League) -> Unit
) {
    SettingsSectionCard(title = "League Preferences", icon = Icons.Default.Sports) {
        Text(
                text = "Selected Leagues (${selectedLeagues.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
        )

        if (selectedLeagues.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // ✅ ADDED KEY - Performance optimization
                items(
                    items = selectedLeagues,
                    key = { it.id }
                ) { league ->
                    SelectedLeagueChip(league = league, onRemove = { onRemoveLeague(league) })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
                text = "Available Leagues",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        val unselectedLeagues =
                availableLeagues.filter { available ->
                    selectedLeagues.none { selected -> selected.id == available.id }
                }

        if (unselectedLeagues.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // ✅ ADDED KEY - Performance optimization
                items(
                    items = unselectedLeagues,
                    key = { it.id }
                ) { league ->
                    AvailableLeagueChip(league = league, onAdd = { onAddLeague(league) })
                }
            }
        } else {
            Text(
                    text = "All available leagues are selected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun NotificationSection(
        notificationsEnabled: Boolean,
        onToggleNotifications: (Boolean) -> Unit
) {
    SettingsSectionCard(title = "Notifications", icon = Icons.Default.Notifications) {
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Match Notifications", style = MaterialTheme.typography.bodyLarge)
                Text(
                        text = "Get notified about live scores and match events",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(checked = notificationsEnabled, onCheckedChange = onToggleNotifications)
        }
    }
}

@Composable
private fun AppPreferencesSection(preferredLanguage: String, onShowLanguageDialog: () -> Unit) {
    SettingsSectionCard(title = "App Preferences", icon = Icons.Default.Settings) {
        // Language Setting
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Language", style = MaterialTheme.typography.bodyLarge)
                Text(
                        text =
                                SupportedLanguage.entries
                                        .find { it.code == preferredLanguage }
                                        ?.displayName
                                        ?: "English",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextButton(onClick = onShowLanguageDialog) { Text("Change") }
        }
    }
}

@Composable
private fun DataManagementSection(onShowClearDataDialog: () -> Unit) {
    SettingsSectionCard(title = "Data Management", icon = Icons.Default.Storage) {
        OutlinedButton(
                onClick = onShowClearDataDialog,
                modifier = Modifier.fillMaxWidth(),
                colors =
                        ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                        )
        ) {
            Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Clear All Data")
        }

        Text(
                text =
                        "This will remove all your preferences and require you to set up the app again.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun AboutSection() {
    SettingsSectionCard(title = "About BaoBuzz", icon = Icons.Default.Info) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoRow(label = "Version", value = "1.0.0")
            InfoRow(label = "Data Source", value = "API-Football")
            InfoRow(label = "Developer", value = "Your Name")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
                text =
                        "BaoBuzz provides real-time football scores, standings, and statistics for your favorite leagues.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsSectionCard(
        title: String,
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        content: @Composable ColumnScope.() -> Unit
) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = androidx.compose.ui.graphics.Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(24.dp)
    ) {
        Box(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(
                                        Brush.verticalGradient(
                                                colors =
                                                        listOf(
                                                                androidx.compose.ui.graphics.Color.White.copy(alpha = 0.05f),
                                                                androidx.compose.ui.graphics.Color.White.copy(alpha = 0.02f)
                                                        )
                                        )
                                )
                                .border(
                                    width = 1.dp,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            androidx.compose.ui.graphics.Color.White.copy(alpha = 0.15f),
                                            androidx.compose.ui.graphics.Color.White.copy(alpha = 0.05f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .padding(20.dp)
        ) {
            Column {
                // Enhanced section header with glassmorphism
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                            text = title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                    )
                }

                content()
            }
        }
    }
}

@Composable
private fun SelectedLeagueChip(league: League, onRemove: () -> Unit) {
    AssistChip(
            onClick = onRemove,
            label = { Text(league.name) },
            leadingIcon = {
                AsyncImage(
                        model = league.logo,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                )
            },
            trailingIcon = {
                Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        modifier = Modifier.size(16.dp)
                )
            }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AvailableLeagueChip(league: League, onAdd: () -> Unit) {
    FilterChip(
            onClick = onAdd,
            label = { Text(league.name) },
            selected = false,
            leadingIcon = {
                AsyncImage(
                        model = league.logo,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                )
            }
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun LanguageSelectionDialog(
        currentLanguage: String,
        onLanguageSelected: (String) -> Unit,
        onDismiss: () -> Unit
) {
    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Select Language") },
            text = {
                Column {
                    SupportedLanguage.entries.forEach { language ->
                        Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                    selected = language.code == currentLanguage,
                                    onClick = { onLanguageSelected(language.code) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = language.displayName)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}
