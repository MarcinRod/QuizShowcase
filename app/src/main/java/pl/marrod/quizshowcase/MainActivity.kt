package pl.marrod.quizshowcase

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Science
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import pl.marrod.quizshowcase.data.Category
import pl.marrod.quizshowcase.data.Question
import pl.marrod.quizshowcase.ui.QuizScreen
import pl.marrod.quizshowcase.ui.QuizUiState
import pl.marrod.quizshowcase.ui.theme.QuizShowcaseTheme

class MainActivity : ComponentActivity() {

    // Using mutableStateOf at the class level to preserve it across the activity lifecycle methods
    // In a real app, this might be in a ViewModel, but for this "no-MVVM" task, we manage it here.
    private var _uiState by mutableStateOf(QuizUiState())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logLifecycle("onCreate")
        
        val initialCategories = listOf(
            Category("1", "Science", Icons.Rounded.Science),
            Category("2", "History", Icons.Rounded.History),
            Category("3", "Computing", Icons.Rounded.Computer),
            Category("4", "Geography", Icons.Rounded.Public)
        )
        
        // Initialize categories if empty (e.g., first run)
        if (_uiState.categories.isEmpty()) {
            _uiState = _uiState.copy(categories = initialCategories)
        }

        enableEdgeToEdge()
        setContent {
            // Using rememberSaveable for individual primitive/stable values is easier than a full custom saver for QuizUiState
            // but since we are refactoring to Single Screen architecture with state hoisting, 
            // we'll keep the state in the Activity class property and ensure it survives simple config changes 
            // (Note: Activity property doesn't survive process death, but rememberSaveable does)

            QuizShowcaseTheme(darkTheme = _uiState.isDarkTheme) {
                QuizScreen(
                    uiState = _uiState,
                    onUserNameChange = { name -> _uiState = _uiState.copy(userName = name) },
                    onThemeToggle = { isDark -> _uiState = _uiState.copy(isDarkTheme = isDark) },
                    onDifficultyChange = { value -> _uiState = _uiState.copy(difficulty = value) },
                    onCategorySelect = { category -> selectCategory(category) },
                    onAnswerSelected = { index -> onAnswerSelected(index) },
                    onToggleExitDialog = { show -> _uiState = _uiState.copy(showExitDialog = show) },
                    onResetQuiz = { resetQuiz() }
                )
            }
        }
    }

    private fun selectCategory(category: Category) {
        val questions = getMockQuestions(category.id)
        _uiState = _uiState.copy(
            selectedCategory = category,
            questions = questions,
            currentQuestionIndex = 0,
            score = 0,
            isQuizFinished = false
        )
    }

    private fun onAnswerSelected(answerIndex: Int) {
        val isCorrect = _uiState.currentQuestion?.correctAnswerIndex == answerIndex
        val newScore = if (isCorrect) _uiState.score + 1 else _uiState.score
        
        if (_uiState.currentQuestionIndex + 1 < _uiState.questions.size) {
            _uiState = _uiState.copy(
                currentQuestionIndex = _uiState.currentQuestionIndex + 1,
                score = newScore
            )
        } else {
            _uiState = _uiState.copy(
                score = newScore,
                isQuizFinished = true
            )
        }
    }

    private fun resetQuiz() {
        _uiState = _uiState.copy(
            selectedCategory = null,
            questions = emptyList(),
            currentQuestionIndex = 0,
            score = 0,
            isQuizFinished = false
        )
    }

    override fun onStart() {
        super.onStart()
        logLifecycle("onStart")
    }

    override fun onResume() {
        super.onResume()
        logLifecycle("onResume")
    }

    override fun onPause() {
        super.onPause()
        logLifecycle("onPause")
    }

    override fun onStop() {
        super.onStop()
        logLifecycle("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        logLifecycle("onDestroy")
    }

    private fun logLifecycle(event: String) {
        Log.d("Lifecycle", event)
        _uiState = _uiState.copy(lifecycleEvents = _uiState.lifecycleEvents + event)
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
