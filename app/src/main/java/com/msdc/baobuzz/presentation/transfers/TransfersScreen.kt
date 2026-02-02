package com.msdc.baobuzz.presentation.transfers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.msdc.baobuzz.core.models.TransferDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransfersScreen(
        teamId: Int,
        viewModel: TransfersViewModel = hiltViewModel(),
        navController: NavHostController? = null
) {
    val transfers by viewModel.transfers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(teamId) { viewModel.getTransfers(teamId) }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Team Transfers") },
                        navigationIcon = {
                            if (navController != null) {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                            imageVector = Icons.Default.ArrowBack,
                                            contentDescription = "Back"
                                    )
                                }
                            }
                        }
                )
            }
    ) { paddingValues ->
        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .background(
                                        Brush.verticalGradient(
                                                colors =
                                                        listOf(
                                                                MaterialTheme.colorScheme
                                                                        .background,
                                                                MaterialTheme.colorScheme
                                                                        .surfaceVariant.copy(
                                                                        alpha = 0.3f
                                                                )
                                                        )
                                        )
                                )
                                .padding(paddingValues)
                                .padding(16.dp)
        ) {
            if (isLoading) {
                Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                            modifier = Modifier.fillMaxWidth().padding(48.dp),
                            contentAlignment = Alignment.Center
                    ) {
                        Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                    modifier = Modifier.size(48.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 4.dp
                            )
                            Text(
                                    text = "Loading Transfers...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                    text = "Fetching latest transfer data",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else if (transfers.isEmpty()) {
                Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                            modifier =
                                    Modifier.fillMaxWidth()
                                            .background(
                                                    Brush.verticalGradient(
                                                            colors =
                                                                    listOf(
                                                                            MaterialTheme
                                                                                    .colorScheme
                                                                                    .primary.copy(
                                                                                    alpha = 0.05f
                                                                            ),
                                                                            MaterialTheme
                                                                                    .colorScheme
                                                                                    .primaryContainer
                                                                                    .copy(
                                                                                            alpha =
                                                                                                    0.1f
                                                                                    )
                                                                    )
                                                    )
                                            )
                    ) {
                        Column(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Card(
                                    modifier = Modifier.size(80.dp),
                                    colors =
                                            CardDefaults.cardColors(
                                                    containerColor =
                                                            MaterialTheme.colorScheme.primary.copy(
                                                                    alpha = 0.1f
                                                            )
                                            ),
                                    shape = RoundedCornerShape(20.dp)
                            ) {
                                Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                            imageVector = Icons.Filled.ArrowForward,
                                            contentDescription = null,
                                            modifier = Modifier.size(40.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Text(
                                    text = "No Transfers Found",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                    text =
                                            "This team doesn't have any recorded transfers in our database.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    // ✅ ADDED KEY - Performance optimization
                    items(
                        items = transfers,
                        key = { it.id }
                    ) { transfer ->
                        EnhancedTransferItem(transfer = transfer)
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedTransferItem(transfer: TransferDetails) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp)
    ) {
        Box(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(
                                        Brush.horizontalGradient(
                                                colors =
                                                        listOf(
                                                                MaterialTheme.colorScheme
                                                                        .primaryContainer.copy(
                                                                        alpha = 0.1f
                                                                ),
                                                                MaterialTheme.colorScheme
                                                                        .secondaryContainer.copy(
                                                                        alpha = 0.05f
                                                                )
                                                        )
                                        )
                                )
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                // Player name and position
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                            text = transfer.player.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                    )

                    Card(
                            colors =
                                    CardDefaults.cardColors(
                                            containerColor =
                                                    MaterialTheme.colorScheme.primary.copy(
                                                            alpha = 0.1f
                                                    )
                                    ),
                            shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                                text = transfer.type,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Transfer route
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    // From team
                    Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                    ) {
                        AsyncImage(
                                model = transfer.teamOut.logo,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                text = transfer.teamOut.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    // Arrow
                    Card(
                            colors =
                                    CardDefaults.cardColors(
                                            containerColor =
                                                    MaterialTheme.colorScheme.secondary.copy(
                                                            alpha = 0.1f
                                                    )
                                    ),
                            shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                                imageVector = Icons.Filled.ArrowForward,
                                contentDescription = "transferred to",
                                modifier = Modifier.size(40.dp).padding(8.dp),
                                tint = MaterialTheme.colorScheme.secondary
                        )
                    }

                    // To team
                    Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                    ) {
                        AsyncImage(
                                model = transfer.teamIn.logo,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                text = transfer.teamIn.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Transfer date
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                            text = "Transfer Date: ${transfer.date}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// Keep the old TransferItem for backward compatibility
@Composable
fun TransferItem(transfer: TransferDetails) {
    EnhancedTransferItem(transfer = transfer)
}
