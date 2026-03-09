package pl.marrod.quizshowcase.ui

import pl.marrod.quizshowcase.data.Category
import pl.marrod.quizshowcase.data.Question

data class QuizUiState(
    val userName: String = "",
    val isDarkTheme: Boolean = false,
    val difficulty: Float = 1f, // 1: Easy, 2: Medium, 3: Hard
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val score: Int = 0,
    val isQuizFinished: Boolean = false,
    val showExitDialog: Boolean = false,
    val isLoading: Boolean = false,
    val lifecycleEvents: List<String> = emptyList()
) {
    val currentQuestion: Question? = questions.getOrNull(currentQuestionIndex)
    val progress: Float = if (questions.isNotEmpty()) (currentQuestionIndex).toFloat() / questions.size else 0f
}
