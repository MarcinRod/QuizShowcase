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
import pl.marrod.quizshowcase.ui.QuizScreen
import pl.marrod.quizshowcase.ui.theme.QuizShowcaseTheme

// ACTIVITY: Definition
// An Activity is a single, focused thing the user can do. It provides a window in which the app
// draws its UI. Every activity must be declared in AndroidManifest.xml.
// ComponentActivity is the Jetpack base class that integrates with Compose via setContent {}.
class MainActivity : ComponentActivity() {

    // ACTIVITY: State Management — option 1 of 3
    // Each piece of state is a separate mutableStateOf property.
    // Compose observes each one independently and only recomposes what changed.
    // ✅ Survives recomposition  ❌ Does NOT survive configuration change (rotation)
    // ❌ Does NOT survive process death
    // See onSaveInstanceState() below for the full comparison.

    // isDarkTheme lives here because it drives QuizShowcaseTheme() which wraps the whole UI.
    private var isDarkTheme by mutableStateOf(false)

    // categories lives here because it is initialised once in onCreate from hard-coded data.
    private var categories by mutableStateOf<List<Category>>(emptyList())

    // lifecycleEvents lives here because it is written in every lifecycle callback below.
    private var lifecycleEvents by mutableStateOf<List<String>>(emptyList())

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

        if (categories.isEmpty()) {
            categories = listOf(
                Category("1", "Science", Icons.Rounded.Science),
                Category("2", "History", Icons.Rounded.History),
                Category("3", "Computing", Icons.Rounded.Computer),
                Category("4", "Geography", Icons.Rounded.Public)
            )
        }

        enableEdgeToEdge()
        setContent {
            // ACTIVITY: State Management — option 2 of 3
            // rememberSaveable { mutableStateOf() } is the RECOMMENDED Compose approach.
            // Unlike a class-level mutableStateOf property, rememberSaveable:
            //   ✅ Survives recomposition
            //   ✅ Survives configuration changes (rotation, font-size change)
            //   ✅ Survives process death (state is serialised into the saved-state Bundle)
            //
            // BEFORE (class property — loses state on rotation):
            //   private var isDarkTheme by mutableStateOf(false)
            //
            // AFTER (rememberSaveable — survives rotation):
            //   var isDarkTheme by rememberSaveable { mutableStateOf(false) }
            //
            // We keep the class properties here for demo purposes so students can see
            // BOTH approaches and compare their behaviour using the Lifecycle Events panel.

            QuizShowcaseTheme(darkTheme = isDarkTheme) {
                QuizScreen(
                    isDarkTheme = isDarkTheme,
                    categories = categories,
                    lifecycleEvents = lifecycleEvents,
                    onThemeToggle = { isDarkTheme = it },
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
        // categories and lifecycleEvents are not saved — they are cheap to recreate on restore.
        // isDarkTheme is a Boolean; saving it here demonstrates manual Bundle usage.
        outState.putBoolean("isDarkTheme", isDarkTheme)
        Log.d("Lifecycle", "onSaveInstanceState — state saved to Bundle")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isDarkTheme = savedInstanceState.getBoolean("isDarkTheme", false)
        Log.d("Lifecycle", "onRestoreInstanceState — restored: isDarkTheme=$isDarkTheme")
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
        lifecycleEvents = lifecycleEvents + event
    }
}
