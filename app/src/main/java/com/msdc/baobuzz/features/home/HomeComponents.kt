package com.msdc.baobuzz.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.msdc.baobuzz.core.models.LeagueStanding
import com.msdc.baobuzz.core.models.LiveMatch
import com.msdc.baobuzz.core.models.Team
import com.msdc.baobuzz.core.models.Transfer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchCard(
    match: LiveMatch,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(300.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (match.status.contains("Live") || match.status.contains("'")) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Match status with live indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (match.status.contains("Live") || match.status.contains("'")) {
                        // Live indicator dot
                        Surface(
                            modifier = Modifier.size(8.dp),
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.error
                        ) {}
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = match.status,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = if (match.status.contains("Live") || match.status.contains("'")) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Teams and score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home team
                EnhancedTeamInfo(
                    team = match.homeTeam,
                    score = match.homeScore,
                    modifier = Modifier.weight(1f)
                )

                // Score or VS
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = if (match.homeScore != null && match.awayScore != null) {
                            "${match.homeScore} - ${match.awayScore}"
                        } else {
                            "VS"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                // Away team
                EnhancedTeamInfo(
                    team = match.awayTeam,
                    score = match.awayScore,
                    modifier = Modifier.weight(1f),
                    alignment = Alignment.End
                )
            }

            // Match minute and additional info if live
            if (match.minute != null || match.status.contains("Live")) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    match.minute?.let { minute ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondary
                        ) {
                            Text(
                                text = "${minute}'",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Additional match info could go here (cards, etc.)
                }
            }
        }
    }
}

@Composable
private fun EnhancedTeamInfo(
    team: Team,
    score: Int?,
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.Start
) {
    Column(
        modifier = modifier,
        horizontalAlignment = alignment
    ) {
        AsyncImage(
            model = team.logo,
            contentDescription = "${team.name} logo",
            modifier = Modifier.size(40.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = team.name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = if (alignment == Alignment.End) TextAlign.End else TextAlign.Start
        )

        score?.let {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "($it)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferCard(
    transfer: Transfer,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player photo with border
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                AsyncImage(
                    model = transfer.player.photo,
                    contentDescription = "${transfer.player.name} photo",
                    modifier = Modifier
                        .size(44.dp)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Transfer info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transfer.player.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    transfer.fromTeam?.let { fromTeam ->
                        // From team with small logo
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = fromTeam.logo,
                                contentDescription = "${fromTeam.name} logo",
                                modifier = Modifier.size(16.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = fromTeam.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Transfer to",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // To team with small logo
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = transfer.toTeam.logo,
                            contentDescription = "${transfer.toTeam.name} logo",
                            modifier = Modifier.size(16.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = transfer.toTeam.name,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Transfer fee if available
                transfer.fee?.let { fee ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = fee,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Transfer type badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = when (transfer.transferType.lowercase()) {
                    "free" -> MaterialTheme.colorScheme.secondaryContainer
                    "loan" -> MaterialTheme.colorScheme.tertiaryContainer
                    else -> MaterialTheme.colorScheme.primaryContainer
                }
            ) {
                Text(
                    text = transfer.transferType,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    color = when (transfer.transferType.lowercase()) {
                        "free" -> MaterialTheme.colorScheme.onSecondaryContainer
                        "loan" -> MaterialTheme.colorScheme.onTertiaryContainer
                        else -> MaterialTheme.colorScheme.onPrimaryContainer
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueStandingsCard(
    standings: LeagueStanding,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(220.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // League header with enhanced design
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    AsyncImage(
                        model = standings.leagueLogo,
                        contentDescription = "${standings.leagueName} logo",
                        modifier = Modifier
                            .size(32.dp)
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = standings.leagueName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Table",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Table header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pos",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.width(28.dp)
                )
                Text(
                    text = "Team",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Pts",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Divider
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Top teams with enhanced styling
            standings.topTeams.take(5).forEachIndexed { index, team ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Position with color coding for top 4/relegation
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = when (team.position) {
                            1 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            in 2..4 -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        modifier = Modifier.width(24.dp)
                    ) {
                        Text(
                            text = "${team.position}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 2.dp),
                            color = when (team.position) {
                                1 -> MaterialTheme.colorScheme.primary
                                in 2..4 -> MaterialTheme.colorScheme.secondary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    AsyncImage(
                        model = team.team.logo,
                        contentDescription = "${team.team.name} logo",
                        modifier = Modifier.size(20.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = team.team.name,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (index < 3) FontWeight.Medium else FontWeight.Normal
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "${team.points}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(32.dp)
                    )
                }

                if (index < standings.topTeams.take(5).size - 1) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }
            }

            // View more indicator if there are more teams
            if (standings.topTeams.size > 5) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "View full table",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
