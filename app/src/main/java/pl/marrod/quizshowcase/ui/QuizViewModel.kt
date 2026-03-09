package pl.marrod.quizshowcase.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pl.marrod.quizshowcase.data.Category
import pl.marrod.quizshowcase.data.Question
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.Public

class QuizViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val categories = listOf(
            Category("1", "Science", Icons.Rounded.Science),
            Category("2", "History", Icons.Rounded.History),
            Category("3", "Computing", Icons.Rounded.Computer),
            Category("4", "Geography", Icons.Rounded.Public)
        )
        _uiState.update { it.copy(categories = categories) }
    }

    fun onUserNameChange(name: String) {
        _uiState.update { it.copy(userName = name) }
    }

    fun onThemeToggle(isDark: Boolean) {
        _uiState.update { it.copy(isDarkTheme = isDark) }
    }

    fun onDifficultyChange(value: Float) {
        _uiState.update { it.copy(difficulty = value) }
    }

    fun selectCategory(category: Category) {
        val questions = getMockQuestions(category.id)
        _uiState.update { 
            it.copy(
                selectedCategory = category,
                questions = questions,
                currentQuestionIndex = 0,
                score = 0,
                isQuizFinished = false
            ) 
        }
    }

    fun onAnswerSelected(answerIndex: Int) {
        val currentState = _uiState.value
        val isCorrect = currentState.currentQuestion?.correctAnswerIndex == answerIndex
        val newScore = if (isCorrect) currentState.score + 1 else currentState.score
        
        if (currentState.currentQuestionIndex + 1 < currentState.questions.size) {
            _uiState.update { 
                it.copy(
                    currentQuestionIndex = currentState.currentQuestionIndex + 1,
                    score = newScore
                ) 
            }
        } else {
            _uiState.update { 
                it.copy(
                    score = newScore,
                    isQuizFinished = true
                ) 
            }
        }
    }

    fun toggleExitDialog(show: Boolean) {
        _uiState.update { it.copy(showExitDialog = show) }
    }

    fun resetQuiz() {
        _uiState.update { 
            it.copy(
                selectedCategory = null,
                questions = emptyList(),
                currentQuestionIndex = 0,
                score = 0,
                isQuizFinished = false
            ) 
        }
    }

    fun addLifecycleEvent(event: String) {
        _uiState.update { 
            it.copy(lifecycleEvents = it.lifecycleEvents + event)
        }
    }

    private fun getMockQuestions(categoryId: String): List<Question> {
        return when (categoryId) {
            "1" -> listOf(
                Question(1, "What is the chemical symbol for Water?", listOf("H2O", "O2", "CO2", "NaCl"), 0, "1"),
                Question(2, "Which planet is known as the Red Planet?", listOf("Earth", "Mars", "Jupiter", "Saturn"), 1, "1")
            )
            "2" -> listOf(
                Question(3, "Who was the first President of the United States?", listOf("Thomas Jefferson", "Abraham Lincoln", "George Washington", "John Adams"), 2, "2"),
                Question(4, "In which year did World War II end?", listOf("1943", "1944", "1945", "1946"), 2, "2")
            )
            "3" -> listOf(
                Question(5, "What does CPU stand for?", listOf("Central Process Unit", "Central Processing Unit", "Computer Personal Unit", "Central Processor Unifier"), 1, "3"),
                Question(6, "Which language is used for Android development?", listOf("Swift", "Kotlin", "C#", "Ruby"), 1, "3")
            )
            "4" -> listOf(
                Question(7, "What is the capital of France?", listOf("Berlin", "Madrid", "Paris", "Rome"), 2, "4"),
                Question(8, "Which is the largest continent?", listOf("Africa", "Asia", "Europe", "Antarctica"), 1, "4")
            )
            else -> emptyList()
        }
    }
}
