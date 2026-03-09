package pl.marrod.quizshowcase

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import pl.marrod.quizshowcase.ui.QuizScreen
import pl.marrod.quizshowcase.ui.QuizViewModel
import pl.marrod.quizshowcase.ui.theme.QuizShowcaseTheme

class MainActivity : ComponentActivity() {
    
    private val viewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logLifecycle("onCreate")
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            
            QuizShowcaseTheme(darkTheme = uiState.isDarkTheme) {
                QuizScreen(viewModel = viewModel)
            }
        }
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
        viewModel.addLifecycleEvent(event)
    }
}
