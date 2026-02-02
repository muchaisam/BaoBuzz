package com.msdc.baobuzz.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

data class FavoriteItem(
        val id: Int,
        val name: String,
        val subtitle: String,
        val type: FavoriteType,
        val imageUrl: String? = null,
        val additionalInfo: String? = null
)

enum class FavoriteType {
    TEAM,
    PLAYER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
        onBackPressed: () -> Unit,
        onNavigateToTeam: (Int) -> Unit = {},
        onNavigateToPlayer: (Int) -> Unit = {},
        modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Teams", "Players")

    // Sample favorite data - in real app, this would come from ViewModel/Repository
    var favoriteTeams by remember {
        mutableStateOf(
                listOf(
                        FavoriteItem(
                                1,
                                "Manchester United",
                                "Premier League",
                                FavoriteType.TEAM,
                                additionalInfo = "1st in league"
                        ),
                        FavoriteItem(
                                2,
                                "Barcelona",
                                "La Liga",
                                FavoriteType.TEAM,
                                additionalInfo = "2nd in league"
                        ),
                        FavoriteItem(
                                3,
                                "Bayern Munich",
                                "Bundesliga",
                                FavoriteType.TEAM,
                                additionalInfo = "1st in league"
                        ),
                        FavoriteItem(
                                4,
                                "Paris Saint-Germain",
                                "Ligue 1",
                                FavoriteType.TEAM,
                                additionalInfo = "1st in league"
                        )
                )
        )
    }

    var favoritePlayers by remember {
        mutableStateOf(
                listOf(
                        FavoriteItem(
                                1,
                                "Cristiano Ronaldo",
                                "Al Nassr",
                                FavoriteType.PLAYER,
                                additionalInfo = "15 goals this season"
                        ),
                        FavoriteItem(
                                2,
                                "Lionel Messi",
                                "Inter Miami",
                                FavoriteType.PLAYER,
                                additionalInfo = "12 goals, 8 assists"
                        ),
                        FavoriteItem(
                                3,
                                "Erling Haaland",
                                "Manchester City",
                                FavoriteType.PLAYER,
                                additionalInfo = "28 goals this season"
                        ),
                        FavoriteItem(
                                4,
                                "Kylian Mbappé",
                                "Real Madrid",
                                FavoriteType.PLAYER,
                                additionalInfo = "22 goals, 6 assists"
                        )
                )
        )
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = {
                            Text(
                                    text = "Favorites",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackPressed) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        },
                        colors =
                                TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        titleContentColor = MaterialTheme.colorScheme.onSurface
                                )
                )
            }
    ) { paddingValues ->
        Column(modifier = modifier.fillMaxSize().padding(paddingValues)) {
            // Tab Row
            TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                            imageVector =
                                                    if (index == 0) Icons.Default.Sports
                                                    else Icons.Default.Person,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                            text = title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight =
                                                    if (selectedTab == index) FontWeight.Bold
                                                    else FontWeight.Normal
                                    )
                                }
                            }
                    )
                }
            }

            // Content
            LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        if (favoriteTeams.isEmpty()) {
                            item {
                                EmptyFavoritesState(
                                        type = "teams",
                                        message = "No favorite teams yet",
                                        description =
                                                "Start following your favorite teams to see them here"
                                )
                            }
                        } else {
                            items(favoriteTeams, key = { it.id }) { team ->
                                FavoriteTeamItem(
                                        item = team,
                                        onClick = { onNavigateToTeam(team.id) },
                                        onRemove = {
                                            favoriteTeams =
                                                    favoriteTeams.filter { it.id != team.id }
                                        }
                                )
                            }
                        }
                    }
                    1 -> {
                        if (favoritePlayers.isEmpty()) {
                            item {
                                EmptyFavoritesState(
                                        type = "players",
                                        message = "No favorite players yet",
                                        description =
                                                "Start following your favorite players to see them here"
                                )
                            }
                        } else {
                            items(favoritePlayers, key = { it.id }) { player ->
                                FavoritePlayerItem(
                                        item = player,
                                        onClick = { onNavigateToPlayer(player.id) },
                                        onRemove = {
                                            favoritePlayers =
                                                    favoritePlayers.filter { it.id != player.id }
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteTeamItem(item: FavoriteItem, onClick: () -> Unit, onRemove: () -> Unit) {
    var showRemoveDialog by remember { mutableStateOf(false) }

    Card(
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Team Avatar
            Box(
                    modifier =
                            Modifier.size(56.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
            ) {
                Text(
                        text = item.name.split(" ").map { it.first() }.joinToString("").take(3),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                )
            }

            // Team Info
            Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                )
                Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                item.additionalInfo?.let { info ->
                    Card(
                            shape = RoundedCornerShape(6.dp),
                            colors =
                                    CardDefaults.cardColors(
                                            containerColor =
                                                    MaterialTheme.colorScheme.primaryContainer
                                    )
                    ) {
                        Text(
                                text = info,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Favorite Button
            IconButton(onClick = { showRemoveDialog = true }) {
                Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Remove from favorites",
                        tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    // Remove Confirmation Dialog
    if (showRemoveDialog) {
        AlertDialog(
                onDismissRequest = { showRemoveDialog = false },
                title = { Text("Remove from Favorites") },
                text = {
                    Text("Are you sure you want to remove ${item.name} from your favorites?")
                },
                confirmButton = {
                    TextButton(
                            onClick = {
                                onRemove()
                                showRemoveDialog = false
                            }
                    ) { Text("Remove") }
                },
                dismissButton = {
                    TextButton(onClick = { showRemoveDialog = false }) { Text("Cancel") }
                }
        )
    }
}

@Composable
private fun FavoritePlayerItem(item: FavoriteItem, onClick: () -> Unit, onRemove: () -> Unit) {
    var showRemoveDialog by remember { mutableStateOf(false) }

    Card(
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Player Avatar
            Box(
                    modifier =
                            Modifier.size(56.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondary),
                    contentAlignment = Alignment.Center
            ) {
                Text(
                        text = item.name.split(" ").map { it.first() }.joinToString(""),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold
                )
            }

            // Player Info
            Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                )
                Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                item.additionalInfo?.let { info ->
                    Card(
                            shape = RoundedCornerShape(6.dp),
                            colors =
                                    CardDefaults.cardColors(
                                            containerColor =
                                                    MaterialTheme.colorScheme.secondaryContainer
                                    )
                    ) {
                        Text(
                                text = info,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Favorite Button
            IconButton(onClick = { showRemoveDialog = true }) {
                Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Remove from favorites",
                        tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    // Remove Confirmation Dialog
    if (showRemoveDialog) {
        AlertDialog(
                onDismissRequest = { showRemoveDialog = false },
                title = { Text("Remove from Favorites") },
                text = {
                    Text("Are you sure you want to remove ${item.name} from your favorites?")
                },
                confirmButton = {
                    TextButton(
                            onClick = {
                                onRemove()
                                showRemoveDialog = false
                            }
                    ) { Text("Remove") }
                },
                dismissButton = {
                    TextButton(onClick = { showRemoveDialog = false }) { Text("Cancel") }
                }
        )
    }
}

@Composable
private fun EmptyFavoritesState(type: String, message: String, description: String) {
    Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(64.dp)
        )

        Text(
                text = message,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
        )

        Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
        )
    }
}
