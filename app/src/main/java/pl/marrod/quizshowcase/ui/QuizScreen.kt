package pl.marrod.quizshowcase.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.marrod.quizshowcase.R
import pl.marrod.quizshowcase.data.Category
import pl.marrod.quizshowcase.data.Question
import pl.marrod.quizshowcase.ui.theme.QuizShowcaseTheme

// COMPOSABLE: Naming convention
// Composable functions that emit UI are named as NOUNS (PascalCase), not verbs.
// ✅ QuizScreen   ❌ showQuizScreen / drawQuizScreen
// This mirrors the mental model of a "UI component" rather than a procedure call.

// COMPOSABLE: State Hoisting
// QuizScreen receives shared state from MainActivity (userName, categories, etc.)
// and owns its own LOCAL state (selectedCategory, questions, score, etc.) directly
// as remember variables — because nothing outside QuizScreen needs those values.
//
//   MainActivity                          ← SHARED state lives here as individual properties
//       │  isDarkTheme, categories        ← passed DOWN as plain parameters — no wrapper class
//       │  lifecycleEvents                ← passed DOWN as a plain parameter
//       ▼
//   QuizScreen
//       ├── var selectedCategory         ← LOCAL state: only QuizScreen cares
//       ├── var questions / score        ← LOCAL state: only QuizScreen cares
//       ├── var userName                 ← LOCAL state: captured when a category is selected
//       │   onThemeToggle(isDark)        ← event for SHARED state flows UP via lambda
//       └──► MainActivity updates isDarkTheme → Compose recomposes QuizScreen
//           ▼
//       SetupSection
//           ├── var userName             ← LOCAL state: typed by the user here
//           └── var difficulty           ← LOCAL state: only SetupSection needs it
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    // COMPOSABLE: State Hoisting — each piece of SHARED state flows DOWN as its own parameter.
    // There is no wrapper data class — each parameter is a plain value owned by MainActivity.
    isDarkTheme: Boolean,
    categories: List<Category>,
    lifecycleEvents: List<String>,
    // COMPOSABLE: State Hoisting — only isDarkTheme needs an event up, everything else is local
    onThemeToggle: (Boolean) -> Unit,
) {
    // COMPOSABLE: Local state — these variables are owned by QuizScreen.
    // They use remember so Compose tracks changes and triggers recomposition.
    // Nothing outside this composable needs to read or write them, so there
    // is no reason to hoist them further up to MainActivity.
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var isQuizFinished by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }

    // Derived values computed from local state — no need to store separately.
    val currentQuestion = questions.getOrNull(currentQuestionIndex)
    val progress = if (questions.isNotEmpty()) currentQuestionIndex.toFloat() / questions.size else 0f

    // Local event handlers — they only touch local state, so they live here too.
    fun selectCategory(category: Category, name: String) {
        selectedCategory = category
        userName = name
        questions = getMockQuestions(category.id)
        currentQuestionIndex = 0
        score = 0
        isQuizFinished = false
    }

    fun onAnswerSelected(answerIndex: Int) {
        val isCorrect = currentQuestion?.correctAnswerIndex == answerIndex
        if (isCorrect) score++
        if (currentQuestionIndex + 1 < questions.size) {
            currentQuestionIndex++
        } else {
            isQuizFinished = true
        }
    }

    fun resetQuiz() {
        selectedCategory = null
        questions = emptyList()
        currentQuestionIndex = 0
        score = 0
        isQuizFinished = false
    }

    // COMPOSABLE: Containers — Scaffold
    // Scaffold is a Material3 layout container that provides slots for TopAppBar,
    // BottomBar, FAB, Snackbar, etc. and handles their positioning automatically.
    Scaffold(
        topBar = {
            // RESOURCES: stringResource(R.string.app_name) reads from res/values/strings.xml.
            // On a Polish device it automatically reads res/values-pl/strings.xml instead.
            // R.string.app_name is a generated Int constant — the R class is rebuilt on every sync.
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { /* Settings */ }) {
                        Icon(Icons.Rounded.Settings, contentDescription = stringResource(R.string.settings_description))
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        // COMPOSABLE: Containers — Column
        // Column stacks children vertically. Use it when the number of items is small and known.
        // For large/unknown lists use LazyColumn (only composes visible items).
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                // RESOURCES: dimensionResource() reads from res/values/dimens.xml → R.dimen.*
                .padding(horizontal = dimensionResource(R.dimen.padding_medium))
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (selectedCategory == null) {
                SetupSection(
                    isDarkTheme = isDarkTheme,
                    categories = categories,
                    lifecycleEvents = lifecycleEvents,
                    onThemeToggle = onThemeToggle,
                    onCategorySelect = { category, name -> selectCategory(category, name) }
                )
            } else if (!isQuizFinished) {
                QuizProgressSection(
                    progress = progress,
                    currentQuestionIndex = currentQuestionIndex,
                    totalQuestions = questions.size
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_standard)))

                QuestionSection(
                    question = currentQuestion,
                    currentQuestionIndex = currentQuestionIndex,
                    totalQuestions = questions.size,
                    onAnswerSelected = { onAnswerSelected(it) },
                    onToggleExitDialog = { showExitDialog = it }
                )
            } else {
                ResultsSection(
                    userName = userName,
                    score = score,
                    totalQuestions = questions.size,
                    onResetQuiz = { resetQuiz() }
                )
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    resetQuiz()
                }) {
                    Text(stringResource(R.string.exit_confirm_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(stringResource(R.string.cancel_button))
                }
            },
            title = { Text(stringResource(R.string.exit_dialog_title)) },
            text = { Text(stringResource(R.string.exit_dialog_message)) }
        )
    }
}

// COMPOSABLE: Naming — SetupSection is a noun describing the screen section, not an action.
@Composable
fun SetupSection(
    isDarkTheme: Boolean,
    categories: List<Category>,
    lifecycleEvents: List<String>,
    onThemeToggle: (Boolean) -> Unit,
    onCategorySelect: (Category, String) -> Unit
) {
    // COMPOSABLE: Local state — userName and difficulty belong here because:
    //   • Only SetupSection reads/writes them while the user is on the setup screen.
    //   • userName is forwarded to QuizScreen via onCategorySelect when the quiz starts.
    //   • difficulty is used locally to filter questions; passed along the same way.
    //
    // rememberSaveable persists the value across configuration changes (e.g. rotation).
    // Use it for anything the user has typed or selected that would be annoying to lose.
    var userName by rememberSaveable { mutableStateOf("") }

    // remember (without Saveable) is sufficient for difficulty: losing it on rotation is
    // acceptable — the user can easily reset a slider, unlike re-typing a name.
    var difficulty by remember { mutableStateOf(1f) }

    // COMPOSABLE: Containers — Column for vertical stacking of heterogeneous children.
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        // RESOURCES DEMO — stringResource()
        // stringResource(R.string.welcome_message) → res/values/strings.xml
        // On Polish locale    → res/values-pl/strings.xml  (alternative resource)
        // R.string.welcome_message is an Int constant generated by the Android build system.
        Text(
            stringResource(R.string.welcome_message),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_standard)))

        TextField(
            value = userName,
            onValueChange = { userName = it },
            // RESOURCES DEMO — R.string.user_name_label → res/values/strings.xml
            label = { Text(stringResource(R.string.user_name_label)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_standard)))

        // COMPOSABLE: Containers — Row for horizontal alignment of two elements.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.dark_theme_toggle), modifier = Modifier.weight(1f))
            Switch(
                checked = isDarkTheme,
                onCheckedChange = onThemeToggle
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))

        Column(modifier = Modifier.fillMaxWidth()) {
            val difficultyLabel = when (difficulty.toInt()) {
                1 -> stringResource(R.string.difficulty_easy)
                2 -> stringResource(R.string.difficulty_medium)
                3 -> stringResource(R.string.difficulty_hard)
                else -> stringResource(R.string.difficulty_easy)
            }
            // RESOURCES DEMO — format string with argument:
            // R.string.difficulty_label = "Difficulty: %1$s"  (res/values/strings.xml)
            // stringResource(R.string.difficulty_label, difficultyLabel) formats it at runtime.
            Text(stringResource(R.string.difficulty_label, difficultyLabel))
            Slider(
                value = difficulty,
                onValueChange = { difficulty = it },
                valueRange = 1f..3f,
                steps = 1
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))

        Text(
            stringResource(R.string.select_category_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))

        // COMPOSABLE: Containers — LazyVerticalGrid
        // Use LazyVerticalGrid when you have a fixed number of columns and potentially many items.
        // "Lazy" means only visible items are composed/measured — efficient for long lists.
        // Compare with: Column (all items composed) / LazyColumn (1 column) / LazyRow (1 row)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            modifier = Modifier.heightIn(max = 300.dp)
        ) {
            items(categories) { category ->
                // COMPOSABLE: Modifier — passing modifier to CategoryCard.
                // The caller controls layout concerns (size, padding, click area).
                // CategoryCard forwards it to its root composable — see CategoryCard below.
                CategoryCard(
                    category = category,
                    modifier = Modifier,
                    onClick = { onCategorySelect(category, userName) }
                )
            }
        }

        // ACTIVITY: Lifecycle Events Panel
        // This panel shows every lifecycle callback that was called since app start.
        // Rotate the screen — with a class-level mutableStateOf, the list resets.
        // Switch to rememberSaveable to see it survive the config change.
        if (lifecycleEvents.isNotEmpty()) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_standard)))
            Text(stringResource(R.string.lifecycle_events_title), style = MaterialTheme.typography.labelSmall)
            // COMPOSABLE: Containers — LazyRow
            // Use LazyRow for a horizontal scrolling list of items.
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsIndexed(lifecycleEvents) { index, event ->
                    Text(
                        text = event,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    if (index < lifecycleEvents.size - 1) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_standard)))
        }
    }
}

// COMPOSABLE: Naming — CategoryCard: noun + context = clear, self-documenting name.
//
// COMPOSABLE: Modifier — Best practice: always accept a Modifier parameter with a default of
// Modifier.  Apply it as the FIRST modifier on the root element. This lets callers control
// layout (size, padding, alignment) without needing to know the internals.
//
//   ✅  fun CategoryCard(modifier: Modifier = Modifier, ...)
//   ❌  fun CategoryCard(...)  // caller cannot influence layout
@Composable
fun CategoryCard(
    category: Category,
    modifier: Modifier = Modifier,   // COMPOSABLE: Modifier — default value lets callers omit it
    onClick: () -> Unit
) {
    // RESOURCES DEMO — colorResource()
    // colorResource(R.color.primary_light) reads directly from res/values/colors.xml.
    // In night mode it automatically reads res/values-night/colors.xml (alternative resource).
    // Compare with MaterialTheme.colorScheme.primary — the Material token system is PREFERRED
    // in Compose apps because it respects dynamic color (Android 12+) and the theme contract.
    // colorResource() is useful when you need exact brand colors or interoperability with Views.
    @Suppress("UNUSED_VARIABLE")
    val brandColor = colorResource(R.color.primary_light) // direct XML resource access — demo only

    Card(
        // COMPOSABLE: Modifier — the externally-provided modifier is applied FIRST,
        // then this component adds its own internal modifiers (height, clickable).
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.card_height))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            // MaterialTheme color — adapts to dynamic color / dark mode automatically
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                category.icon,
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_size_medium)),
                // RESOURCES DEMO — using brandColor (colorResource) vs MaterialTheme:
                // tint = brandColor            // exact XML color — ignores theme
                tint = MaterialTheme.colorScheme.onPrimaryContainer  // theme-aware (preferred)
            )
            Text(category.name, style = MaterialTheme.typography.labelLarge)
        }
    }
}

// COMPOSABLE: Naming — QuizProgressSection: describes what the section shows, not what it does.
@Composable
fun QuizProgressSection(
    progress: Float,
    currentQuestionIndex: Int,
    totalQuestions: Int,
    // COMPOSABLE: Modifier — accepting Modifier makes this composable reusable at any call site.
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // RESOURCES DEMO — R.string.progress_label = "Progress: %1$d%%"
        // The %% is an escaped % sign — stringResource formats the Int argument into %1$d.
        Text(
            stringResource(R.string.progress_label, (progress * 100).toInt()),
            style = MaterialTheme.typography.labelMedium
        )
        LinearProgressIndicator(
            progress = { progress },
            // RESOURCES DEMO — R.dimen.progress_bar_height → res/values/dimens.xml
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.progress_bar_height)),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

// COMPOSABLE: Naming — QuestionSection: noun describing the content region.
@Composable
fun QuestionSection(
    question: Question?,
    currentQuestionIndex: Int,
    totalQuestions: Int,
    onAnswerSelected: (Int) -> Unit,
    onToggleExitDialog: (Boolean) -> Unit
) {
    Column {
        question ?: return@Column

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.question_counter, currentQuestionIndex + 1, totalQuestions),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))

            Text(
                text = question.text,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))

            // COMPOSABLE: Containers — Column (non-lazy) for answer options.
            // We use Column instead of LazyColumn here because:
            //   • The number of options is always small (≤ 4) and fixed at runtime.
            //   • The parent is already inside a verticalScroll — nested LazyColumn
            //     requires a fixed height, which would be awkward here.
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
            ) {
                question.options.forEachIndexed { index, option ->
                    Button(
                        onClick = { onAnswerSelected(index) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(option, modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_standard)))

        OutlinedButton(
            onClick = { onToggleExitDialog(true) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.quit_quiz_button))
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_standard)))
    }
}

// COMPOSABLE: Naming — ResultsSection: describes the quiz-result content region.
@Composable
fun ResultsSection(
    userName: String,
    score: Int,
    totalQuestions: Int,
    onResetQuiz: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // RESOURCES: painterResource(R.drawable.ic_quiz_trophy)
        // Loads the vector drawable from res/drawable/ic_quiz_trophy.xml via the R class.
        // R.drawable.ic_quiz_trophy is an Int ID generated at build time.
        //
        // Compare with Material Icons used elsewhere in the app:
        //   Icon(Icons.Rounded.Science, ...)      — in-memory ImageVector, NO R class involved
        //   painterResource(R.drawable.*)         — XML asset, resolved through R class at runtime
        //
        // Use painterResource() for custom/brand assets; use Icons.* for standard Material icons.
        Image(
            painter = painterResource(R.drawable.ic_quiz_trophy),
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_large))
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_standard)))

        Text(
            stringResource(R.string.quiz_finished_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            stringResource(R.string.great_job_message, userName),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_large)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.your_score_label), style = MaterialTheme.typography.labelLarge)
                Text(
                    "$score / $totalQuestions",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onResetQuiz,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.back_to_categories_button))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPOSABLE: @Preview
// @Preview renders a composable in the IDE without running the app on a device.
// Best practices:
//   • One preview per significant UI state (setup, in-progress, results).
//   • Use name= and group= to organise previews in the Preview pane.
//   • Never call real data sources inside a preview — use hardcoded mock data.
//   • Wrap in QuizShowcaseTheme so the preview matches the real app's styling.
// ─────────────────────────────────────────────────────────────────────────────

private val mockCategories = listOf(
    Category("1", "Science", Icons.Rounded.Science),
    Category("2", "History", Icons.Rounded.History),
    Category("3", "Computing", Icons.Rounded.Computer),
    Category("4", "Geography", Icons.Rounded.Public)
)

private val mockQuestions = listOf(
    Question(1, "What is the chemical symbol for Water?", listOf("H2O", "O2", "CO2", "NaCl"), 0, "1"),
    Question(2, "Which planet is the Red Planet?", listOf("Earth", "Mars", "Jupiter", "Saturn"), 1, "1")
)

// COMPOSABLE: Preview — Setup screen (initial state, no category selected yet)
@Preview(name = "Setup Screen", group = "QuizShowcase", showBackground = true, showSystemUi = true)
@Composable
fun QuizSetupPreview() {
    QuizShowcaseTheme {
        QuizScreen(
            isDarkTheme = false,
            categories = mockCategories,
            lifecycleEvents = listOf("onCreate", "onStart", "onResume"),
            onThemeToggle = {},
        )
    }
}

// COMPOSABLE: Preview — Quiz in progress (category selected, question visible)
// Note: selectedCategory, questions etc. are now LOCAL state inside QuizScreen —
// they cannot be injected from outside, so this preview starts from the setup screen.
// To preview deeper states, extract QuizProgressSection / QuestionSection individually.
@Preview(name = "Quiz In Progress", group = "QuizShowcase", showBackground = true, showSystemUi = true)
@Composable
fun QuizInProgressPreview() {
    QuizShowcaseTheme {
        QuizProgressSection(
            progress = 0.5f,
            currentQuestionIndex = 1,
            totalQuestions = 2
        )
    }
}

// COMPOSABLE: Preview — Results screen (quiz finished)
@Preview(name = "Quiz Results", group = "QuizShowcase", showBackground = true, showSystemUi = true)
@Composable
fun QuizResultsPreview() {
    QuizShowcaseTheme {
        ResultsSection(
            userName = "Ada",
            score = 2,
            totalQuestions = 2,
            onResetQuiz = {}
        )
    }
}

// Helper — kept here because it is only ever used inside this composable tree.
fun getMockQuestions(categoryId: String): List<Question> {
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
