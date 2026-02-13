package com.msdc.baobuzz.features.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msdc.baobuzz.core.api.HistoricalRepository
import com.msdc.baobuzz.core.models.QuizDifficulty
import com.msdc.baobuzz.core.models.QuizQuestion
import com.msdc.baobuzz.core.models.QuizScore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

/**
 * QuizViewModel - Powers the Daily Football Quiz Feature 🎯
 *
 * Features:
 * - Daily quiz with 10 questions
 * - Multiple question types (8 types)
 * - Score tracking and history
 * - Difficulty levels
 * - Real-time answer validation
 */
@HiltViewModel
class QuizViewModel @Inject constructor(
    private val historicalRepository: HistoricalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var currentQuestions: List<QuizQuestion> = emptyList()
    private var currentQuestionIndex = 0
    private var correctAnswers = 0
    private var startTime = System.currentTimeMillis()

    init {
        checkDailyQuizStatus()
    }

    /**
     * Check if user has already completed today's quiz
     */
    private fun checkDailyQuizStatus() {
        viewModelScope.launch {
            try {
                // TODO: Check from database if quiz was completed today
                val today = getCurrentDate()
                val lastQuizDate = "" // Load from database

                if (today == lastQuizDate) {
                    // Quiz already completed today
                    _uiState.value = QuizUiState.AlreadyCompleted(
                        score = 8, // Load from database
                        totalQuestions = 10
                    )
                } else {
                    _uiState.value = QuizUiState.Ready
                }
            } catch (e: Exception) {
                _uiState.value = QuizUiState.Ready
            }
        }
    }

    /**
     * Start a new quiz
     */
    fun startQuiz(difficulty: QuizDifficulty = QuizDifficulty.MEDIUM) {
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading

            try {
                currentQuestions = historicalRepository.generateDailyQuiz(difficulty)
                currentQuestionIndex = 0
                correctAnswers = 0
                startTime = System.currentTimeMillis()

                if (currentQuestions.isNotEmpty()) {
                    _uiState.value = QuizUiState.Question(
                        question = currentQuestions[0],
                        questionNumber = 1,
                        totalQuestions = currentQuestions.size,
                        score = 0,
                        selectedAnswer = null,
                        isAnswered = false
                    )
                } else {
                    _uiState.value = QuizUiState.Error("Failed to generate quiz questions")
                }
            } catch (e: Exception) {
                _uiState.value = QuizUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Submit an answer for the current question
     */
    fun submitAnswer(answer: String) {
        val currentState = _uiState.value
        if (currentState !is QuizUiState.Question || currentState.isAnswered) return

        val isCorrect = answer == currentState.question.correctAnswer
        if (isCorrect) {
            correctAnswers++
        }

        // Update state to show answer result
        _uiState.value = currentState.copy(
            selectedAnswer = answer,
            isAnswered = true
        )
    }

    /**
     * Move to next question or show results
     */
    fun nextQuestion() {
        currentQuestionIndex++

        if (currentQuestionIndex < currentQuestions.size) {
            // Show next question
            val currentState = _uiState.value as? QuizUiState.Question
            _uiState.value = QuizUiState.Question(
                question = currentQuestions[currentQuestionIndex],
                questionNumber = currentQuestionIndex + 1,
                totalQuestions = currentQuestions.size,
                score = correctAnswers * 10, // 10 points per correct answer
                selectedAnswer = null,
                isAnswered = false
            )
        } else {
            // Quiz completed - show results
            val timeSpent = (System.currentTimeMillis() - startTime) / 1000 // seconds
            val finalScore = correctAnswers * 10
            val accuracy = (correctAnswers.toFloat() / currentQuestions.size) * 100

            val quizScore = QuizScore(
                id = UUID.randomUUID().toString(),
                date = getCurrentDate(),
                score = finalScore,
                totalQuestions = currentQuestions.size,
                correctAnswers = correctAnswers,
                accuracy = accuracy,
                timeSpent = timeSpent,
                difficulty = QuizDifficulty.MEDIUM
            )

            // Save quiz result
            saveQuizResult(quizScore)

            _uiState.value = QuizUiState.Completed(quizScore)
        }
    }

    /**
     * Restart quiz
     */
    fun restartQuiz() {
        _uiState.value = QuizUiState.Ready
    }

    /**
     * Save quiz result to database
     */
    private fun saveQuizResult(score: QuizScore) {
        viewModelScope.launch {
            try {
                // TODO: Save to Room database
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}

/**
 * UI State for Quiz Screen
 */
sealed class QuizUiState {
    object Loading : QuizUiState()
    object Ready : QuizUiState()

    data class Question(
        val question: QuizQuestion,
        val questionNumber: Int,
        val totalQuestions: Int,
        val score: Int,
        val selectedAnswer: String?,
        val isAnswered: Boolean
    ) : QuizUiState()

    data class Completed(val score: QuizScore) : QuizUiState()

    data class AlreadyCompleted(
        val score: Int,
        val totalQuestions: Int
    ) : QuizUiState()

    data class Error(val message: String) : QuizUiState()
}
