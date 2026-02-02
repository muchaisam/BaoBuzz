package com.msdc.baobuzz.core.models

import androidx.compose.runtime.Immutable

/**
 * Domain models for historical football data
 * Used by Quiz and Facts features
 * All models marked @Immutable for Compose performance
 */

// ================== Historical Match Data ==================

@Immutable
data class HistoricalMatch(
    val id: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int,
    val awayScore: Int,
    val date: String,
    val round: String,
    val season: String,
    val league: String,
    val winner: String? = null
)

@Immutable
data class HistoricalSeason(
    val id: String,
    val league: String,
    val season: String,
    val champion: String,
    val championPoints: Int,
    val runnerUp: String,
    val topScorer: String,
    val topScorerGoals: Int,
    val totalGoals: Int,
    val totalMatches: Int,
    val averageGoalsPerMatch: Float
)

// ================== Quiz Models ==================

@Immutable
data class QuizQuestion(
    val id: String,
    val type: QuestionType,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String,
    val difficulty: QuizDifficulty = QuizDifficulty.MEDIUM,
    val points: Int = 10
)

enum class QuestionType {
    MATCH_RESULT,        // "Who won the 1998 World Cup Final?"
    TOP_SCORER,          // "Who was the Premier League top scorer in 2015/16?"
    TEAM_STATS,          // "Which team had the most goals in La Liga 2010?"
    HEAD_TO_HEAD,        // "How many times did Barcelona beat Real Madrid in 2012?"
    LEAGUE_STANDINGS,    // "Which team finished 3rd in Serie A 2008/09?"
    CHAMPION,            // "Who won the Premier League in 2015/16?"
    SCORE_GUESS,         // "What was the score in Man Utd vs Liverpool 2012-05-15?"
    SEASON_STATS         // "How many goals were scored in total in Premier League 2016/17?"
}

enum class QuizDifficulty {
    EASY,      // Recent seasons, famous teams
    MEDIUM,    // 5-10 years ago, popular leagues
    HARD,      // 10+ years ago, lesser known teams
    EXPERT     // Very old seasons, rare facts
}

@Immutable
data class QuizScore(
    val id: String,
    val date: String,
    val score: Int,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val accuracy: Float,
    val timeSpent: Long,
    val difficulty: QuizDifficulty
)

@Immutable
data class QuizHistory(
    val totalQuizzes: Int,
    val totalScore: Int,
    val averageAccuracy: Float,
    val bestScore: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val recentScores: List<QuizScore>
)

// ================== Facts Models ==================

@Immutable
data class HistoricalFact(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val category: FactCategory,
    val league: String?,
    val season: String?,
    val imageUrl: String? = null,
    val isSpecial: Boolean = false // For extra amazing facts
)

enum class FactCategory {
    RECORD,          // "Most goals in a single season"
    LEGEND,          // "Pelé scored 1,283 goals in his career"
    MOMENT,          // "Leicester City won the league with 5000-1 odds"
    RIVALRY,         // "El Clásico records"
    UNDERDOG,        // "Giant-killing moments"
    ACHIEVEMENT,     // "First team to do X"
    MILESTONE,       // "1000th goal in Premier League"
    CONTROVERSY,     // "The Hand of God"
    COMEBACK         // "Incredible comeback wins"
}

@Immutable
data class OnThisDayFact(
    val fact: HistoricalFact,
    val yearsAgo: Int
)

// ================== Season Comparison Models ==================

@Immutable
data class SeasonComparison(
    val league: String,
    val season1: SeasonStats,
    val season2: SeasonStats,
    val differences: SeasonDifferences
)

@Immutable
data class SeasonStats(
    val season: String,
    val champion: String,
    val championPoints: Int,
    val runnerUp: String,
    val runnerUpPoints: Int,
    val topScorer: String,
    val topScorerGoals: Int,
    val totalGoals: Int,
    val totalMatches: Int,
    val averageGoalsPerMatch: Float,
    val biggestWin: String, // "Team 7-0 Team"
    val mostWins: String,
    val mostDraws: String,
    val relegatedTeams: List<String>
)

@Immutable
data class SeasonDifferences(
    val pointsDifference: Int,
    val goalsDifference: Int,
    val competitivenessChange: String // "More competitive", "Less competitive"
)

// ================== League Records Models ==================

@Immutable
data class LeagueRecords(
    val leagueName: String,
    val mostChampionships: TeamRecord,
    val highestPoints: SeasonRecord,
    val mostGoalsScorer: PlayerRecord,
    val mostGoalsTeam: TeamRecord,
    val biggestWin: MatchRecord,
    val longestWinStreak: TeamRecord,
    val longestUnbeatenRun: TeamRecord
)

@Immutable
data class TeamRecord(
    val teamName: String,
    val value: Int,
    val season: String
)

@Immutable
data class PlayerRecord(
    val playerName: String,
    val value: Int,
    val season: String,
    val team: String
)

@Immutable
data class SeasonRecord(
    val teamName: String,
    val value: Int,
    val season: String
)

@Immutable
data class MatchRecord(
    val homeTeam: String,
    val awayTeam: String,
    val score: String,
    val date: String,
    val season: String
)

// ================== Player Model (for stats) ==================
// Using Player from AppModels.kt to avoid redeclaration

// ================== Helper Functions ==================

/**
 * Calculate quiz score percentage
 */
fun QuizScore.getPercentage(): Int {
    return ((correctAnswers.toFloat() / totalQuestions) * 100).toInt()
}

/**
 * Get difficulty color for UI
 */
fun QuizDifficulty.getColor(): androidx.compose.ui.graphics.Color {
    return when (this) {
        QuizDifficulty.EASY -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
        QuizDifficulty.MEDIUM -> androidx.compose.ui.graphics.Color(0xFFFFA726)
        QuizDifficulty.HARD -> androidx.compose.ui.graphics.Color(0xFFEF5350)
        QuizDifficulty.EXPERT -> androidx.compose.ui.graphics.Color(0xFF9C27B0)
    }
}

/**
 * Get fact category icon emoji
 */
fun FactCategory.getIcon(): String {
    return when (this) {
        FactCategory.RECORD -> "🏆"
        FactCategory.LEGEND -> "⭐"
        FactCategory.MOMENT -> "⚡"
        FactCategory.RIVALRY -> "⚔️"
        FactCategory.UNDERDOG -> "🦁"
        FactCategory.ACHIEVEMENT -> "🎯"
        FactCategory.MILESTONE -> "📊"
        FactCategory.CONTROVERSY -> "🤯"
        FactCategory.COMEBACK -> "🔥"
    }
}

/**
 * Get question type icon emoji
 */
fun QuestionType.getIcon(): String {
    return when (this) {
        QuestionType.MATCH_RESULT -> "⚽"
        QuestionType.TOP_SCORER -> "🥇"
        QuestionType.TEAM_STATS -> "📊"
        QuestionType.HEAD_TO_HEAD -> "⚔️"
        QuestionType.LEAGUE_STANDINGS -> "🏆"
        QuestionType.CHAMPION -> "👑"
        QuestionType.SCORE_GUESS -> "🎯"
        QuestionType.SEASON_STATS -> "📈"
    }
}
