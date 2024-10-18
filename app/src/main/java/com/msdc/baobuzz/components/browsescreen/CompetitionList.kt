package com.msdc.baobuzz.components.browsescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.msdc.baobuzz.R
import com.msdc.baobuzz.models.LeagueInfo
import com.msdc.baobuzz.models.LeagueInfoProvider
import com.msdc.baobuzz.ux.Typography

@Composable
fun CompetitionList(leagueInfoProvider: LeagueInfoProvider, searchQuery: String) {
    val allLeagues = leagueInfoProvider.getAllLeagueInfo()
    val filteredLeagues = allLeagues.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.country.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn {
        item {
            Text(
                "TOP COMPETITIONS",
                modifier = Modifier.padding(16.dp),
                color = Color.Gray,
                style = Typography.body2
            )
        }
        items(filteredLeagues) { league ->
            CompetitionItem(league)
        }
    }
}

@Composable
fun CompetitionItem(league: LeagueInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = league.flagUrl).apply(block = fun ImageRequest.Builder.() {
                    crossfade(true)
                    placeholder(R.drawable.manunited)
                }).build()
            ),
            contentDescription = "${league.name} logo",
            modifier = Modifier.size(24.dp),
            contentScale = ContentScale.Fit
        )
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(league.country, color = Color.Gray, style = Typography.body2)
            Text(league.name, fontWeight = FontWeight.Bold, style = Typography.body1)
        }
    }
}
