package com.msdc.baobuzz.features.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.msdc.baobuzz.features.onboarding.components.LiveScoreIllustration
import com.msdc.baobuzz.features.onboarding.components.OnboardingDotIndicator
import com.msdc.baobuzz.features.onboarding.components.OnboardingFeaturePage
import com.msdc.baobuzz.features.onboarding.components.OnboardingLeaguePage
import com.msdc.baobuzz.features.onboarding.components.OnboardingWelcomePage
import com.msdc.baobuzz.features.onboarding.components.StandingsIllustration
import com.msdc.baobuzz.ui.theme.BaoBuzzColors
import com.msdc.baobuzz.ui.theme.pulsingGlow
import kotlinx.coroutines.launch

private const val TOTAL_PAGES = 4
private const val LAST_INTRO_PAGE = 2
private const val LEAGUE_PAGE = 3

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingPagerScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { TOTAL_PAGES })
    val coroutineScope = rememberCoroutineScope()

    // Handle back button - scroll to previous page or exit
    BackHandler(enabled = pagerState.currentPage > 0) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BaoBuzzColors.BackgroundDark,
                        BaoBuzzColors.PrimaryBlue.copy(alpha = 0.25f),
                        BaoBuzzColors.BackgroundDark
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> OnboardingWelcomePage(isActive = pagerState.currentPage == 0)
                    1 -> OnboardingFeaturePage(
                        title = "Live Scores",
                        subtitle = "Real-time match updates with minute-by-minute coverage across top leagues",
                        isActive = pagerState.currentPage == 1,
                        illustration = { LiveScoreIllustration(modifier = Modifier.fillMaxWidth()) }
                    )
                    2 -> OnboardingFeaturePage(
                        title = "Stats & Standings",
                        subtitle = "League tables, player stats, and comprehensive team analytics at your fingertips",
                        isActive = pagerState.currentPage == 2,
                        illustration = { StandingsIllustration(modifier = Modifier.fillMaxWidth()) }
                    )
                    3 -> OnboardingLeaguePage(
                        leagues = uiState.availableLeagues,
                        selectedIds = uiState.selectedLeagueIds,
                        onToggle = { viewModel.toggleLeagueSelection(it) },
                        isActive = pagerState.currentPage == 3
                    )
                }
            }

            // Bottom section
            OnboardingBottomSection(
                currentPage = pagerState.currentPage,
                canFinish = uiState.canContinue,
                selectedCount = uiState.selectedLeagueIds.size,
                onNext = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                onSkip = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(LEAGUE_PAGE)
                    }
                },
                onFinish = {
                    viewModel.completeOnboarding { onComplete() }
                }
            )
        }
    }
}

@Composable
private fun OnboardingBottomSection(
    currentPage: Int,
    canFinish: Boolean,
    selectedCount: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onFinish: () -> Unit
) {
    val isLeaguePage = currentPage == LEAGUE_PAGE

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dot indicator
        OnboardingDotIndicator(
            totalDots = TOTAL_PAGES,
            selectedIndex = currentPage
        )

        Spacer(modifier = Modifier.height(28.dp))

        if (isLeaguePage) {
            // "Get Started" button
            GetStartedButton(
                enabled = canFinish,
                selectedCount = selectedCount,
                onClick = onFinish
            )
        } else {
            // Next + Skip row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skip button
                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onSkip
                    )
                )

                // Next button
                NextButton(onClick = onNext)
            }
        }
    }
}

@Composable
private fun NextButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        BaoBuzzColors.PrimaryBlue,
                        BaoBuzzColors.PrimaryBlueLight
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 32.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Next",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = Color.White
        )
    }
}

@Composable
private fun GetStartedButton(
    enabled: Boolean,
    selectedCount: Int,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (enabled) BaoBuzzColors.PrimaryBlue else Color.White.copy(alpha = 0.08f),
        animationSpec = tween(300),
        label = "btn_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (enabled) Color.White else Color.White.copy(alpha = 0.3f),
        animationSpec = tween(300),
        label = "btn_text"
    )

    val glowModifier = if (enabled) {
        Modifier.pulsingGlow(glowColor = BaoBuzzColors.PrimaryBlueLight, maxAlpha = 0.25f)
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .then(glowModifier)
            .background(bgColor)
            .border(
                width = 1.dp,
                color = if (enabled) BaoBuzzColors.PrimaryBlueLight.copy(alpha = 0.3f)
                else Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(16.dp)
            )
            .then(
                if (enabled) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (selectedCount > 0) "Get Started ($selectedCount)" else "Select a league",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp
            ),
            color = textColor
        )
    }
}
