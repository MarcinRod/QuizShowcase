package pl.marrod.quizshowcase

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Science
import androidx.compose.runtime.*
import pl.marrod.quizshowcase.data.Category
import pl.marrod.quizshowcase.data.Question
import pl.marrod.quizshowcase.ui.QuizScreen
import pl.marrod.quizshowcase.ui.QuizUiState
import pl.marrod.quizshowcase.ui.theme.QuizShowcaseTheme

// ACTIVITY: Definition
// An Activity is a single, focused thing the user can do. It provides a window in which the app
// draws its UI. Every activity must be declared in AndroidManifest.xml.
// ComponentActivity is the Jetpack base class that integrates with Compose via setContent {}.
class MainActivity : ComponentActivity() {

    // ACTIVITY: State Management — option 1 of 3
    // mutableStateOf() at the class level makes Compose observe changes to _uiState.
    // ✅ Survives recomposition  ❌ Does NOT survive configuration change (rotation)
    // ❌ Does NOT survive process death
    // See onSaveInstanceState() below for the full comparison.
    private var _uiState by mutableStateOf(QuizUiState())

    // ACTIVITY: onCreate — the entry point of every Activity.
    // Called when the system first creates the Activity, or re-creates it after a config change.
    // Always call super.onCreate() first — it restores the saved instance state.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logLifecycle("onCreate")

        // ACTIVITY: applicationContext vs 'this'
        // 'this' (Activity) is a Context tied to the Activity lifecycle — use it for UI operations
        //   (inflating layouts, showing Toasts, starting activities).
        // applicationContext is tied to the app's lifetime — use it for long-lived objects
        //   (databases, repositories) to avoid leaking the Activity.
        Log.d("Context", "App package (via applicationContext): ${applicationContext.packageName}")
        Toast.makeText(
            this,               // Activity context — correct for a short-lived UI operation
            "App: ${applicationContext.packageName}",
            Toast.LENGTH_SHORT
        ).show()

        val initialCategories = listOf(
            Category("1", "Science", Icons.Rounded.Science),
            Category("2", "History", Icons.Rounded.History),
            Category("3", "Computing", Icons.Rounded.Computer),
            Category("4", "Geography", Icons.Rounded.Public)
        )

        if (_uiState.categories.isEmpty()) {
            _uiState = _uiState.copy(categories = initialCategories)
        }

        enableEdgeToEdge()
        setContent {
            // ACTIVITY: State Management — option 2 of 3
            // rememberSaveable { mutableStateOf() } is the RECOMMENDED Compose approach.
            // Unlike a class-level mutableStateOf property, rememberSaveable:
            //   ✅ Survives recomposition
            //   ✅ Survives configuration changes (rotation, font-size change)
            //   ✅ Survives process death (state is serialised into the saved-state Bundle)
            // The value must be either a primitive, a Parcelable, or use a custom Saver.
            //
            // BEFORE (class property — loses state on rotation):
            //   private var _uiState by mutableStateOf(QuizUiState())
            //
            // AFTER (rememberSaveable — survives rotation):
            //   var uiState by rememberSaveable { mutableStateOf(QuizUiState()) }
            //
            // We keep the class property here for demo purposes so students can see
            // BOTH approaches and compare their behaviour using the Lifecycle Events panel.

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

    // ACTIVITY: State Management — comparison table
    // ┌────────────────────────────────────┬──────────────┬─────────────────┬────────────────┐
    // │ Mechanism                          │ Recompose    │ Config change   │ Process death  │
    // ├────────────────────────────────────┼──────────────┼─────────────────┼────────────────┤
    // │ mutableStateOf (class property)    │ ✅ survives  │ ❌ lost         │ ❌ lost        │
    // │ rememberSaveable { mutableStateOf} │ ✅ survives  │ ✅ survives     │ ✅ (Bundle)    │
    // │ onSaveInstanceState / Bundle       │ ✅ survives  │ ✅ survives     │ ✅ survives    │
    // │ ViewModel                          │ ✅ survives  │ ✅ survives     │ ❌ lost*       │
    // └────────────────────────────────────┴──────────────┴─────────────────┴────────────────┘
    // * ViewModel + SavedStateHandle survives process death too.
    //
    // onSaveInstanceState is called before the Activity may be killed.
    // The Bundle is passed back into onCreate(savedInstanceState) and onRestoreInstanceState().
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Example: save a primitive into the Bundle manually.
        // For complex state, prefer rememberSaveable with a Parcelable data class.
        outState.putString("userName", _uiState.userName)
        outState.putInt("score", _uiState.score)
        Log.d("Lifecycle", "onSaveInstanceState — state saved to Bundle")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Called after onStart() when there IS a saved state to restore.
        // Complements onCreate(savedInstanceState) — either place works.
        val savedName = savedInstanceState.getString("userName", "")
        val savedScore = savedInstanceState.getInt("score", 0)
        _uiState = _uiState.copy(userName = savedName, score = savedScore)
        Log.d("Lifecycle", "onRestoreInstanceState — restored: name=$savedName, score=$savedScore")
    }

    // ACTIVITY: onStart — Activity becomes visible to the user.
    // Not yet in the foreground; user cannot interact yet.
    override fun onStart() {
        super.onStart()
        logLifecycle("onStart")
    }

    // ACTIVITY: onResume — Activity is in the foreground and interactive.
    // This is where you resume animations, sensors, camera, etc.
    override fun onResume() {
        super.onResume()
        logLifecycle("onResume")
    }

    // ACTIVITY: onPause — Activity is partially obscured (e.g. a dialog on top).
    // Pause anything that should not run when not in focus (e.g. video, sensors).
    // Must be fast — onResume of the next Activity won't start until this returns.
    override fun onPause() {
        super.onPause()
        logLifecycle("onPause")
    }

    // ACTIVITY: onStop — Activity is no longer visible to the user.
    // Release heavy resources (cameras, GPS). Introduced state saves should happen here.
    override fun onStop() {
        super.onStop()
        logLifecycle("onStop")
    }

    // ACTIVITY: onDestroy — Activity is being destroyed.
    // Either the user dismissed it, or the system is reclaiming memory.
    // Use isFinishing to distinguish: if isFinishing == false, it's a config change.
    override fun onDestroy() {
        super.onDestroy()
        logLifecycle("onDestroy — isFinishing=$isFinishing")
    }

    private fun logLifecycle(event: String) {
        Log.d("Lifecycle", event)
        _uiState = _uiState.copy(lifecycleEvents = _uiState.lifecycleEvents + event)
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
