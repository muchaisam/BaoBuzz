package com.msdc.baobuzz.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

data class SearchResult(
    val id: Int,
    val title: String,
    val subtitle: String,
    val type: SearchResultType,
    val imageUrl: String? = null
)

enum class SearchResultType {
    TEAM,
    PLAYER,
    MATCH,
    LEAGUE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackPressed: () -> Unit,
    onNavigateToTeam: (Int) -> Unit = {},
    onNavigateToPlayer: (Int) -> Unit = {},
    onNavigateToMatch: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<SearchResult>>(emptyList()) }
    var recentSearches by remember {
        mutableStateOf(
            listOf(
                "Manchester United",
                "Premier League",
                "Cristiano Ronaldo",
                "Champions League"
            )
        )
    }
    var trendingSearches by remember {
        mutableStateOf(
            listOf("Liverpool vs Arsenal", "Erling Haaland", "Barcelona", "World Cup 2024")
        )
    }

    // Simulate search delay and results
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            isSearching = true
            // Simulate API delay
            kotlinx.coroutines.delay(500)
            searchResults = performSearch(searchQuery)
            isSearching = false
        } else {
            searchResults = emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                "Search teams, players, matches...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint =
                                            MaterialTheme.colorScheme
                                                .onSurfaceVariant
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor =
                                    MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor =
                                    MaterialTheme.colorScheme.outline
                            ),
                        modifier = Modifier.fillMaxWidth()
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
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                searchQuery.isEmpty() -> {
                    // Show recent and trending searches when no query
                    item {
                        SearchSuggestions(
                            recentSearches = recentSearches,
                            trendingSearches = trendingSearches,
                            onSearchClick = { query -> searchQuery = query },
                            onClearRecentSearches = { recentSearches = emptyList() }
                        )
                    }
                }

                isSearching -> {
                    // Show loading state
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }
                }

                searchResults.isNotEmpty() -> {
                    // Show search results
                    items(searchResults) { result ->
                        SearchResultItem(
                            result = result,
                            onClick = {
                                when (result.type) {
                                    SearchResultType.TEAM -> onNavigateToTeam(result.id)
                                    SearchResultType.PLAYER -> onNavigateToPlayer(result.id)
                                    SearchResultType.MATCH -> onNavigateToMatch(result.id)
                                    SearchResultType.LEAGUE -> {
                                        // Handle league navigation if needed
                                    }
                                }
                            }
                        )
                    }
                }

                else -> {
                    // Show no results
                    item { NoResultsFound(query = searchQuery) }
                }
            }
        }
    }
}

@Composable
private fun SearchSuggestions(
    recentSearches: List<String>,
    trendingSearches: List<String>,
    onSearchClick: (String) -> Unit,
    onClearRecentSearches: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // Recent Searches
        if (recentSearches.isNotEmpty()) {
            SearchSection(
                title = "Recent Searches",
                icon = Icons.Default.History,
                items = recentSearches,
                onItemClick = onSearchClick,
                onClearAll = onClearRecentSearches
            )
        }

        // Trending Searches
        SearchSection(
            title = "Trending",
            icon = Icons.Default.TrendingUp,
            items = trendingSearches,
            onItemClick = onSearchClick
        )
    }
}

@Composable
private fun SearchSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    items: List<String>,
    onItemClick: (String) -> Unit,
    onClearAll: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (onClearAll != null) {
                    TextButton(onClick = onClearAll) {
                        Text(
                            "Clear All",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            items.forEach { item ->
                SearchSuggestionItem(text = item, onClick = { onItemClick(item) })
            }
        }
    }
}

@Composable
private fun SearchSuggestionItem(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SearchResultItem(result: SearchResult, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Result Type Indicator/Avatar
            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(getTypeColor(result.type)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getTypeIcon(result.type),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = result.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = result.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Type Badge
            Card(
                shape = RoundedCornerShape(6.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = getTypeColor(result.type).copy(alpha = 0.1f)
                    )
            ) {
                Text(
                    text = result.type.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall,
                    color = getTypeColor(result.type),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun NoResultsFound(query: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(64.dp)
        )

        Text(
            text = "No results found",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text =
                "We couldn't find anything for \"$query\".\nTry searching for teams, players, or matches.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun getTypeColor(type: SearchResultType): Color {
    return when (type) {
        SearchResultType.TEAM -> MaterialTheme.colorScheme.primary
        SearchResultType.PLAYER -> MaterialTheme.colorScheme.secondary
        SearchResultType.MATCH -> MaterialTheme.colorScheme.tertiary
        SearchResultType.LEAGUE -> MaterialTheme.colorScheme.error
    }
}

private fun getTypeIcon(type: SearchResultType): String {
    return when (type) {
        SearchResultType.TEAM -> "T"
        SearchResultType.PLAYER -> "P"
        SearchResultType.MATCH -> "M"
        SearchResultType.LEAGUE -> "L"
    }
}

private fun performSearch(query: String): List<SearchResult> {
    // Simulate search results - in real app, this would call API
    val allResults =
        listOf(
            SearchResult(
                1,
                "Manchester United",
                "Premier League • England",
                SearchResultType.TEAM
            ),
            SearchResult(
                2,
                "Manchester City",
                "Premier League • England",
                SearchResultType.TEAM
            ),
            SearchResult(
                3,
                "Cristiano Ronaldo",
                "Al Nassr • Portugal",
                SearchResultType.PLAYER
            ),
            SearchResult(
                4,
                "Lionel Messi",
                "Inter Miami • Argentina",
                SearchResultType.PLAYER
            ),
            SearchResult(
                5,
                "Man United vs Liverpool",
                "Premier League • Today 15:30",
                SearchResultType.MATCH
            ),
            SearchResult(
                6,
                "Chelsea vs Arsenal",
                "Premier League • Tomorrow 17:45",
                SearchResultType.MATCH
            ),
            SearchResult(
                7,
                "Premier League",
                "England • 20 teams",
                SearchResultType.LEAGUE
            ),
            SearchResult(8, "Champions League", "UEFA • 32 teams", SearchResultType.LEAGUE)
        )

    return allResults.filter {
        it.title.contains(query, ignoreCase = true) ||
                it.subtitle.contains(query, ignoreCase = true)
    }
}
