package com.msdc.baobuzz.core.api

import com.msdc.baobuzz.core.api.interfaces.OpenFootballApi
import com.msdc.baobuzz.core.models.*
import com.msdc.baobuzz.models.openfootball.toDomainMatches
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * HistoricalRepository - The Brain of Quiz & Facts Features! 🧠
 *
 * This repository manages all historical football data for:
 * - Daily Quiz generation (10 questions, 8 types)
 * - Historical Facts rotation
 * - Season Comparison tool
 * - League Records
 *
 * Data Source: OpenFootball API (GitHub, FREE, 1950s-present)
 * Caching: Room database (30-day TTL)
 */
@Singleton
class HistoricalRepository @Inject constructor(
    private val openFootballApi: OpenFootballApi
    // TODO: Add HistoricalDataDao when Room entities are created
) {

    // ================== Configuration ==================

    companion object {
        // Supported leagues for historical data
        val SUPPORTED_LEAGUES = mapOf(
            "en.1" to "Premier League",
            "es.1" to "La Liga",
            "de.1" to "Bundesliga",
            "it.1" to "Serie A",
            "fr.1" to "Ligue 1"
        )

        // Available seasons (going back to 2010 for now, can extend)
        val AVAILABLE_SEASONS = (2010..2023).map { year ->
            "$year-${(year + 1).toString().takeLast(2)}"
        }

        // Quiz configuration
        const val QUESTIONS_PER_QUIZ = 10
        const val POINTS_PER_QUESTION = 10
    }

    // ================== Quiz Data Methods ==================

    /**
     * Generate a complete daily quiz with mixed question types
     * Returns 10 questions with varying difficulty
     */
    suspend fun generateDailyQuiz(difficulty: QuizDifficulty = QuizDifficulty.MEDIUM): List<QuizQuestion> {
        val questions = mutableListOf<QuizQuestion>()

        try {
            // Question mix: 2 of each type (8 types × 1-2 = 10 questions)
            questions.addAll(generateMatchResultQuestions(2, difficulty))
            questions.addAll(generateTopScorerQuestions(2, difficulty))
            questions.addAll(generateChampionQuestions(1, difficulty))
            questions.addAll(generateTeamStatsQuestions(2, difficulty))
            questions.addAll(generateLeagueStandingsQuestions(1, difficulty))
            questions.addAll(generateScoreGuessQuestions(2, difficulty))

            // Shuffle for variety
            return questions.shuffled().take(QUESTIONS_PER_QUIZ)
        } catch (e: Exception) {
            // Fallback: Return sample questions if API fails
            return getSampleQuestions()
        }
    }

    /**
     * Generate "Who won this match?" questions
     */
    private suspend fun generateMatchResultQuestions(count: Int, difficulty: QuizDifficulty): List<QuizQuestion> {
        val questions = mutableListOf<QuizQuestion>()

        repeat(count) {
            val league = SUPPORTED_LEAGUES.keys.random()
            val season = getRandomSeasonByDifficulty(difficulty)

            try {
                val response = openFootballApi.getSeasonMatches(season, league)
                val matches = response.matches?.toDomainMatches(season, SUPPORTED_LEAGUES[league]!!)

                if (!matches.isNullOrEmpty()) {
                    // Find matches with clear winners (not draws)
                    val winnerMatches = matches.filter { it.winner != null }
                    if (winnerMatches.isNotEmpty()) {
                        val match = winnerMatches.random()
                        val wrongAnswers = generateWrongAnswers(match.winner!!, listOf(match.homeTeam, match.awayTeam))

                        questions.add(
                            QuizQuestion(
                                id = "match_${match.id}",
                                type = QuestionType.MATCH_RESULT,
                                question = "Who won: ${match.homeTeam} vs ${match.awayTeam} (${match.season})?",
                                options = (listOf(match.winner!!) + wrongAnswers).shuffled(),
                                correctAnswer = match.winner!!,
                                explanation = "${match.winner} won ${match.homeScore}-${match.awayScore}",
                                difficulty = difficulty,
                                points = difficulty.getPoints()
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Skip this question on error
            }
        }

        return questions
    }

    /**
     * Generate "Who was the champion?" questions
     */
    private suspend fun generateChampionQuestions(count: Int, difficulty: QuizDifficulty): List<QuizQuestion> {
        val questions = mutableListOf<QuizQuestion>()
        val champions = mapOf(
            "2015-16" to mapOf("en.1" to "Leicester City", "es.1" to "Barcelona", "de.1" to "Bayern Munich"),
            "2016-17" to mapOf("en.1" to "Chelsea", "es.1" to "Real Madrid", "de.1" to "Bayern Munich"),
            "2017-18" to mapOf("en.1" to "Manchester City", "es.1" to "Barcelona", "de.1" to "Bayern Munich"),
            "2018-19" to mapOf("en.1" to "Manchester City", "es.1" to "Barcelona", "de.1" to "Bayern Munich"),
            "2019-20" to mapOf("en.1" to "Liverpool", "es.1" to "Real Madrid", "de.1" to "Bayern Munich"),
            "2020-21" to mapOf("en.1" to "Manchester City", "es.1" to "Atletico Madrid", "de.1" to "Bayern Munich"),
            "2021-22" to mapOf("en.1" to "Manchester City", "es.1" to "Real Madrid", "de.1" to "Bayern Munich"),
            "2022-23" to mapOf("en.1" to "Manchester City", "es.1" to "Barcelona", "de.1" to "Bayern Munich")
        )

        repeat(count) {
            val season = getRandomSeasonByDifficulty(difficulty)
            val league = SUPPORTED_LEAGUES.keys.random()
            val champion = champions[season]?.get(league) ?: return@repeat

            val wrongAnswers = listOf(
                "Manchester United", "Arsenal", "Real Madrid", "Barcelona",
                "Bayern Munich", "Juventus", "Liverpool", "Chelsea"
            ).filter { it != champion }.shuffled().take(3)

            questions.add(
                QuizQuestion(
                    id = "champion_${season}_${league}",
                    type = QuestionType.CHAMPION,
                    question = "Who won ${SUPPORTED_LEAGUES[league]} in $season?",
                    options = (listOf(champion) + wrongAnswers).shuffled(),
                    correctAnswer = champion,
                    explanation = "$champion dominated the $season ${SUPPORTED_LEAGUES[league]} season!",
                    difficulty = difficulty,
                    points = difficulty.getPoints()
                )
            )
        }

        return questions
    }

    /**
     * Generate "Who was the top scorer?" questions
     */
    private suspend fun generateTopScorerQuestions(count: Int, difficulty: QuizDifficulty): List<QuizQuestion> {
        val questions = mutableListOf<QuizQuestion>()

        // Historical top scorers data (this would come from API in production)
        val topScorers = mapOf(
            "2015-16" to mapOf(
                "en.1" to "Harry Kane (25 goals)",
                "es.1" to "Luis Suarez (40 goals)",
                "de.1" to "Robert Lewandowski (30 goals)"
            ),
            "2016-17" to mapOf(
                "en.1" to "Harry Kane (29 goals)",
                "es.1" to "Lionel Messi (37 goals)",
                "de.1" to "Pierre-Emerick Aubameyang (31 goals)"
            ),
            "2017-18" to mapOf(
                "en.1" to "Mohamed Salah (32 goals)",
                "es.1" to "Lionel Messi (34 goals)",
                "de.1" to "Robert Lewandowski (29 goals)"
            )
        )

        repeat(count) {
            val season = topScorers.keys.random()
            val league = topScorers[season]?.keys?.random() ?: return@repeat
            val topScorer = topScorers[season]?.get(league) ?: return@repeat

            val wrongAnswers = listOf(
                "Cristiano Ronaldo", "Lionel Messi", "Robert Lewandowski",
                "Harry Kane", "Mohamed Salah", "Karim Benzema",
                "Erling Haaland", "Kylian Mbappé"
            ).filter { !topScorer.contains(it) }.shuffled().take(3).map { "$it (? goals)" }

            questions.add(
                QuizQuestion(
                    id = "scorer_${season}_${league}",
                    type = QuestionType.TOP_SCORER,
                    question = "Who was the top scorer in ${SUPPORTED_LEAGUES[league]} $season?",
                    options = (listOf(topScorer) + wrongAnswers).shuffled(),
                    correctAnswer = topScorer,
                    explanation = "$topScorer led the league in scoring!",
                    difficulty = difficulty,
                    points = difficulty.getPoints()
                )
            )
        }

        return questions
    }

    /**
     * Generate team statistics questions
     */
    private suspend fun generateTeamStatsQuestions(count: Int, difficulty: QuizDifficulty): List<QuizQuestion> {
        val questions = mutableListOf<QuizQuestion>()

        repeat(count) {
            val league = SUPPORTED_LEAGUES.keys.random()
            val season = getRandomSeasonByDifficulty(difficulty)

            try {
                val response = openFootballApi.getSeasonMatches(season, league)
                val matches = response.matches?.toDomainMatches(season, SUPPORTED_LEAGUES[league]!!)

                if (!matches.isNullOrEmpty()) {
                    // Calculate team with most goals
                    val teamGoals = mutableMapOf<String, Int>()
                    matches.forEach { match ->
                        teamGoals[match.homeTeam] = (teamGoals[match.homeTeam] ?: 0) + match.homeScore
                        teamGoals[match.awayTeam] = (teamGoals[match.awayTeam] ?: 0) + match.awayScore
                    }

                    val topTeam = teamGoals.maxByOrNull { it.value }
                    if (topTeam != null) {
                        val wrongAnswers = teamGoals.keys
                            .filter { it != topTeam.key }
                            .shuffled()
                            .take(3)

                        questions.add(
                            QuizQuestion(
                                id = "stats_${season}_${league}",
                                type = QuestionType.TEAM_STATS,
                                question = "Which team scored the most goals in ${SUPPORTED_LEAGUES[league]} $season?",
                                options = (listOf(topTeam.key) + wrongAnswers).shuffled(),
                                correctAnswer = topTeam.key,
                                explanation = "${topTeam.key} scored ${topTeam.value} goals that season!",
                                difficulty = difficulty,
                                points = difficulty.getPoints()
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Skip on error
            }
        }

        return questions
    }

    /**
     * Generate league standings questions
     */
    private suspend fun generateLeagueStandingsQuestions(count: Int, difficulty: QuizDifficulty): List<QuizQuestion> {
        val questions = mutableListOf<QuizQuestion>()

        // Sample standings data
        val standings = mapOf(
            "2015-16" to mapOf(
                "en.1" to listOf("Leicester City", "Arsenal", "Tottenham", "Manchester City", "Manchester United")
            ),
            "2017-18" to mapOf(
                "en.1" to listOf("Manchester City", "Manchester United", "Tottenham", "Liverpool", "Chelsea")
            )
        )

        repeat(count) {
            val season = standings.keys.random()
            val league = standings[season]?.keys?.random() ?: return@repeat
            val standing = standings[season]?.get(league) ?: return@repeat

            if (standing.size >= 4) {
                val position = Random.nextInt(2, 5) // Ask about 2nd to 4th place
                val correctTeam = standing[position - 1]
                val wrongAnswers = standing.filter { it != correctTeam }.shuffled().take(3)

                questions.add(
                    QuizQuestion(
                        id = "standing_${season}_${league}_$position",
                        type = QuestionType.LEAGUE_STANDINGS,
                        question = "Which team finished ${position}${getOrdinalSuffix(position)} in ${SUPPORTED_LEAGUES[league]} $season?",
                        options = (listOf(correctTeam) + wrongAnswers).shuffled(),
                        correctAnswer = correctTeam,
                        explanation = "$correctTeam finished ${position}${getOrdinalSuffix(position)} that season!",
                        difficulty = difficulty,
                        points = difficulty.getPoints()
                    )
                )
            }
        }

        return questions
    }

    /**
     * Generate score guessing questions
     */
    private suspend fun generateScoreGuessQuestions(count: Int, difficulty: QuizDifficulty): List<QuizQuestion> {
        val questions = mutableListOf<QuizQuestion>()

        repeat(count) {
            val league = SUPPORTED_LEAGUES.keys.random()
            val season = getRandomSeasonByDifficulty(difficulty)

            try {
                val response = openFootballApi.getSeasonMatches(season, league)
                val matches = response.matches?.toDomainMatches(season, SUPPORTED_LEAGUES[league]!!)

                if (!matches.isNullOrEmpty()) {
                    // Find matches with interesting scores (3+ goals)
                    val interestingMatches = matches.filter { (it.homeScore + it.awayScore) >= 3 }
                    if (interestingMatches.isNotEmpty()) {
                        val match = interestingMatches.random()
                        val correctScore = "${match.homeScore}-${match.awayScore}"

                        val wrongScores = listOf(
                            "${match.homeScore + 1}-${match.awayScore}",
                            "${match.homeScore}-${match.awayScore + 1}",
                            "${match.homeScore - 1}-${match.awayScore}"
                        ).filter { it != correctScore && !it.contains("-") }

                        if (wrongScores.size >= 3) {
                            questions.add(
                                QuizQuestion(
                                    id = "score_${match.id}",
                                    type = QuestionType.SCORE_GUESS,
                                    question = "What was the score: ${match.homeTeam} vs ${match.awayTeam} (${match.date})?",
                                    options = (listOf(correctScore) + wrongScores.take(3)).shuffled(),
                                    correctAnswer = correctScore,
                                    explanation = "${match.homeTeam} ${correctScore} ${match.awayTeam}",
                                    difficulty = difficulty,
                                    points = difficulty.getPoints()
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                // Skip on error
            }
        }

        return questions
    }

    // ================== Historical Facts Methods ==================

    /**
     * Get a random historical fact for daily rotation
     */
    suspend fun getDailyFact(): HistoricalFact {
        return getRandomFact()
    }

    /**
     * Get historical facts that happened "on this day"
     */
    suspend fun getOnThisDayFacts(date: String): List<OnThisDayFact> {
        // TODO: Implement date-based fact filtering
        // For now, return sample facts
        return getSampleFacts().take(3).mapIndexed { index, fact ->
            OnThisDayFact(fact, yearsAgo = (5..20).random())
        }
    }

    /**
     * Get facts by category
     */
    fun getFactsByCategory(category: FactCategory): Flow<List<HistoricalFact>> = flow {
        emit(getSampleFacts().filter { it.category == category })
    }

    /**
     * Get all historical facts
     */
    fun getAllFacts(): Flow<List<HistoricalFact>> = flow {
        emit(getSampleFacts())
    }

    // ================== Season Comparison Methods ==================

    /**
     * Compare two seasons for a specific league
     */
    suspend fun compareSeasons(league: String, season1: String, season2: String): SeasonComparison {
        // TODO: Implement real comparison using OpenFootball API
        return SeasonComparison(
            league = SUPPORTED_LEAGUES[league] ?: league,
            season1 = getSampleSeasonStats(season1),
            season2 = getSampleSeasonStats(season2),
            differences = SeasonDifferences(
                pointsDifference = 5,
                goalsDifference = 25,
                competitivenessChange = "More competitive"
            )
        )
    }

    /**
     * Get available seasons for a league
     */
    suspend fun getAvailableSeasons(league: String): List<String> {
        return AVAILABLE_SEASONS
    }

    // ================== Helper Methods ==================

    private fun getRandomSeasonByDifficulty(difficulty: QuizDifficulty): String {
        return when (difficulty) {
            QuizDifficulty.EASY -> AVAILABLE_SEASONS.takeLast(3).random() // Recent seasons
            QuizDifficulty.MEDIUM -> AVAILABLE_SEASONS.takeLast(7).random()
            QuizDifficulty.HARD -> AVAILABLE_SEASONS.take(AVAILABLE_SEASONS.size - 5).random()
            QuizDifficulty.EXPERT -> AVAILABLE_SEASONS.take(5).random() // Oldest seasons
        }
    }

    private fun generateWrongAnswers(correctAnswer: String, exclude: List<String>): List<String> {
        val allTeams = listOf(
            "Manchester United", "Manchester City", "Liverpool", "Arsenal", "Chelsea",
            "Tottenham", "Barcelona", "Real Madrid", "Atletico Madrid", "Sevilla",
            "Bayern Munich", "Borussia Dortmund", "RB Leipzig", "Juventus", "Inter Milan",
            "AC Milan", "Napoli", "Roma", "PSG", "Lyon", "Marseille"
        )

        return allTeams
            .filter { it != correctAnswer && it !in exclude }
            .shuffled()
            .take(3)
    }

    private fun getOrdinalSuffix(number: Int): String {
        return when {
            number % 100 in 11..13 -> "th"
            number % 10 == 1 -> "st"
            number % 10 == 2 -> "nd"
            number % 10 == 3 -> "rd"
            else -> "th"
        }
    }

    private fun QuizDifficulty.getPoints(): Int {
        return when (this) {
            QuizDifficulty.EASY -> 5
            QuizDifficulty.MEDIUM -> 10
            QuizDifficulty.HARD -> 15
            QuizDifficulty.EXPERT -> 25
        }
    }

    private fun getRandomFact(): HistoricalFact {
        return getSampleFacts().random()
    }

    // ================== Sample Data (Replace with real data) ==================

    private fun getSampleQuestions(): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "sample_1",
                type = QuestionType.CHAMPION,
                question = "Who won the Premier League in 2015-16?",
                options = listOf("Leicester City", "Arsenal", "Tottenham", "Manchester City"),
                correctAnswer = "Leicester City",
                explanation = "Leicester City won with 5000-1 odds!",
                difficulty = QuizDifficulty.MEDIUM
            )
        )
    }

    private fun getSampleFacts(): List<HistoricalFact> {
        return listOf(
            HistoricalFact(
                id = "fact_1",
                title = "Leicester City's Miracle",
                description = "Leicester City won the 2015-16 Premier League with 5000-1 pre-season odds, the greatest sporting upset in history.",
                date = "2016-05-02",
                category = FactCategory.MOMENT,
                league = "Premier League",
                season = "2015-16",
                isSpecial = true
            ),
            HistoricalFact(
                id = "fact_2",
                title = "Barcelona's Historic Comeback",
                description = "Barcelona overcame a 4-0 first leg deficit to beat PSG 6-1 in the 2017 Champions League, winning 6-5 on aggregate.",
                date = "2017-03-08",
                category = FactCategory.COMEBACK,
                league = "Champions League",
                season = "2016-17",
                isSpecial = true
            ),
            HistoricalFact(
                id = "fact_3",
                title = "Messi's Record",
                description = "Lionel Messi scored 91 goals in a calendar year (2012), a record that still stands.",
                date = "2012-12-31",
                category = FactCategory.RECORD,
                league = "Multiple",
                season = "2012"
            )
        )
    }

    private fun getSampleSeasonStats(season: String): SeasonStats {
        return SeasonStats(
            season = season,
            champion = "Manchester City",
            championPoints = 98,
            runnerUp = "Liverpool",
            runnerUpPoints = 97,
            topScorer = "Mohamed Salah",
            topScorerGoals = 32,
            totalGoals = 1018,
            totalMatches = 380,
            averageGoalsPerMatch = 2.68f,
            biggestWin = "Manchester City 6-0 Chelsea",
            mostWins = "Manchester City",
            mostDraws = "Southampton",
            relegatedTeams = listOf("Cardiff", "Fulham", "Huddersfield")
        )
    }
}
