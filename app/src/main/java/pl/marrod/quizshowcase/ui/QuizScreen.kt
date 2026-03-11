package pl.marrod.quizshowcase.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.marrod.quizshowcase.R
import pl.marrod.quizshowcase.data.Category
import pl.marrod.quizshowcase.data.DataSource
import pl.marrod.quizshowcase.data.Question
import pl.marrod.quizshowcase.ui.theme.QuizShowcaseTheme


/**
 * Główny ekran aplikacji quizowej. Zarządza logiką nawigacji między trzema widokami:
 * ekranem konfiguracji, ekranem pytań oraz ekranem wyników.
 *
 * Wyniesienie stanu (State Hoisting) — każdy fragment WSPÓŁDZIELONEGO stanu
 * przepływa w dół jako osobny parametr.
 *
 * @param isDarkTheme czy aktywny jest ciemny motyw.
 * @param categories lista dostępnych kategorii pytań.
 * @param lifecycleEvents lista zdarzeń cyklu życia zarejestrowanych od startu aplikacji.
 * @param onThemeToggle callback wywoływany przy przełączaniu motywu; przekazuje nową wartość flagi.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    isDarkTheme: Boolean,
    categories: List<Category>,
    lifecycleEvents: List<String>,
    // Wyniesienie stanu — tylko isDarkTheme wymaga zdarzenia w górę, reszta jest lokalna
    onThemeToggle: (Boolean) -> Unit,
) {
    // Stan lokalny — te zmienne należą do QuizScreen.
    // Używają remember, żeby Compose śledził zmiany i wyzwalał rekompozycję.
    // Nic poza tą funkcją kompozycyjną nie odczytuje ani nie zapisuje tych wartości,
    // więc nie ma powodu wynosić ich wyżej do MainActivity.
    var selectedCategory by remember { mutableStateOf<Category?>(null) } // <— pokazanie utraty stanu przy rotacji ekranu — nie używamy rememberSaveable
    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var isQuizFinished by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    // userName jest stanem lokalnym, bo jest używane tylko w QuizScreen
    // i przekazywane do ResultsSection po zakończeniu quizu.
    var userName by remember { mutableStateOf("") }

    // LocalContext daje dostęp do Android Context wewnątrz funkcji kompozycyjnych.
    // Używamy go tutaj, aby uzyskać instancję Resources przekazywaną do DataSource.getQuestions(),
    // dzięki czemu może ona rozwiązywać wpisy R.string.* i R.array.* w czasie wykonania.
    val resources = LocalContext.current.resources

    // Wartości pochodne obliczane ze stanu lokalnego — nie trzeba ich osobno przechowywać.
    val currentQuestion = questions.getOrNull(currentQuestionIndex)
    val progress = if (questions.isNotEmpty()) currentQuestionIndex.toFloat() / questions.size else 0f

    // Lokalna obsługa zdarzeń — działa wyłącznie na stanie lokalnym, więc są zdefiniowane tutaj.
    // Umieszczone wewnątrz funkcji kompozycyjnej, bo bezpośrednio modyfikują zmienne stanu zdefiniowane powyżej.
    fun selectCategory(category: Category, name: String) {
        selectedCategory = category
        userName = name
        questions = DataSource.getQuestions(category.id, resources)
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

    // Scaffold to kontener układu Material3, który ułatwia implementację standardowych elementów
    // interfejsu, takich jak AppBar, BottomBar, FAB, Snackbar itp.
    Scaffold(
        topBar = {
            //  stringResource(R.string.app_name) odczytuje dane z res/values/strings.xml.
            // Na polskim urządzeniu automatycznie sięga po res/values-pl/strings.xml.
            // R.string.app_name to wygenerowana stała Int — klasa R jest przebudowywana przy każdej synchronizacji.
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { /* Ustawienia */ }) {
                        Icon(Icons.Rounded.Settings, contentDescription = stringResource(R.string.settings_description))
                    }
                }
            )
        }
    ) { innerPadding ->
        // Column układa dzieci pionowo. Używaj go, gdy liczba elementów jest mała i znana.
        // Dla dużych lub list o nieznanej długości stosuj LazyColumn (tworzy tylko widoczne elementy).
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                // dimensionResource() odczytuje dane z res/values/dimens.xml → R.dimen.*
                // Zasoby wymiarów zapewniają spójne odstępy i rozmiary w całej aplikacji
                // i można je nadpisywać dla różnych rozmiarów ekranu.
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
                QuizProgressSection( progress = progress )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_standard)))

                QuestionSection(
                    question = currentQuestion,
                    currentQuestionIndex = currentQuestionIndex,
                    totalQuestions = questions.size,
                    onAnswerSelected = { onAnswerSelected(it) },
                    onShowExitDialog = { showExitDialog = true }
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

/**
 * Sekcja konfiguracji quizu wyświetlana przed jego rozpoczęciem.
 * Umożliwia wprowadzenie nazwy użytkownika, wybór motywu, poziomu trudności
 * oraz kategorii pytań. Pokazuje też historię zdarzeń cyklu życia.
 *
 * @param isDarkTheme czy aktywny jest ciemny motyw.
 * @param categories lista dostępnych kategorii pytań.
 * @param lifecycleEvents lista zdarzeń cyklu życia do wyświetlenia.
 * @param onThemeToggle callback wywoływany przy przełączaniu motywu.
 * @param onCategorySelect callback wywoływany po wyborze kategorii; przyjmuje wybraną kategorię i nazwę użytkownika.
 */
@Composable
fun SetupSection(
    isDarkTheme: Boolean,
    categories: List<Category>,
    lifecycleEvents: List<String>,
    onThemeToggle: (Boolean) -> Unit,
    onCategorySelect: (Category, String) -> Unit
) {

    // rememberSaveable zachowuje wartość przez zmiany konfiguracji (np. obrót ekranu).
    // Używaj go dla wszystkiego, co użytkownik wpisał lub wybrał, a co byłoby uciążliwe
    // do ponownego wprowadzania.
    var userName by rememberSaveable { mutableStateOf("") }

    // Zwykłe remember (bez Saveable) dla pokazania utraty stanu przy rotacji ekranu.
    var difficulty by remember { mutableStateOf(1f) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            stringResource(R.string.welcome_message),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_standard)))

        TextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text(stringResource(R.string.user_name_label)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_standard)))

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
            } // można też wykorzystać string-array i indeks difficulty.toInt() - 1

            // Format stringa z argumentem:
            // R.string.difficulty_label = "Trudność: %1$s"  (res/values/strings.xml)
            // stringResource(R.string.difficulty_label, difficultyLabel) formatuje go w czasie wykonania.
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


        // LazyVerticalGrid stosuj, gdy masz stałą liczbę kolumn i potencjalnie wiele elementów.
        // „Lazy" oznacza, że tylko widoczne elementy są tworzone i mierzone — efektywne dla długich list.
        // Porównaj z: Column (wszystkie elementy kompozycji) / LazyColumn (1 kolumna) / LazyRow (1 wiersz)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            modifier = Modifier.heightIn(max = 300.dp)
        ) {
            items(categories) { category ->
               CategoryCard(
                    category = category,
                    modifier = Modifier,
                    onClick = { onCategorySelect(category, userName) }
                )
            }
        }

        // Panel pokazuje każde zdarzenie cyklu życia wywołane od startu aplikacji.
        // Obróć ekran — przy mutableStateOf na poziomie klasy lista się zeruje.

        if (lifecycleEvents.isNotEmpty()) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_standard)))
            Text(stringResource(R.string.lifecycle_events_title), style = MaterialTheme.typography.labelSmall)
            // Kontenery — LazyRow
            // Stosuj LazyRow do poziomej listy elementów.
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

/**
 * Karta reprezentująca pojedynczą kategorię quizu.
 * Wyświetla ikonę i nazwę kategorii w karcie Material3.
 *
 * @param category obiekt kategorii zawierający ikonę i nazwę.
 * @param modifier zewnętrzny [Modifier] przekazywany przez wywołującego; domyślna wartość pozwala go pominąć.
 * @param onClick callback wywoływany po kliknięciu karty.
 */
@Composable
fun CategoryCard(
    category: Category,
    modifier: Modifier = Modifier,   // Modifier — domyślna wartość pozwala wywołującemu go pominąć
    onClick: () -> Unit
) {

    Card(
        // Zewnętrznie przekazany modifier jest stosowany JAKO PIERWSZY,
        // następnie komponent dodaje własne wewnętrzne modyfikatory (height, clickable).
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.card_height))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            // Kolor MaterialTheme — automatycznie dostosowuje się do Dynamic Color i trybu ciemnego
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
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(category.name, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/**
 * Sekcja paska postępu quizu pokazująca, ile pytań zostało już udzielonych.
 *
 * @param progress wartość postępu w zakresie 0.0–1.0 (0% – 100%).
 * @param modifier zewnętrzny [Modifier] umożliwiający wielokrotne użycie funkcji kompozycyjnej w dowolnym miejscu.
 */
@Composable
fun QuizProgressSection(
    progress: Float,
    // Przyjmowanie Modifier sprawia, że funkcja kompozycyjna może być wielokrotnego użytku i
    // łatwo integrowana z różnymi układami. Wywołujący może przekazać własny Modifier,
    // aby dostosować wygląd i zachowanie tej sekcji bez modyfikowania jej wewnętrznej logiki.
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            stringResource(R.string.progress_label, (progress * 100).toInt()),
            style = MaterialTheme.typography.labelMedium
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.progress_bar_height)),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

/**
 * Sekcja wyświetlająca bieżące pytanie quizu wraz z opcjami odpowiedzi.
 * Zawiera też przycisk wyjścia otwierający dialog potwierdzenia.
 *
 * @param question bieżące pytanie do wyświetlenia lub `null`, jeśli brak pytania.
 * @param currentQuestionIndex indeks (0-based) bieżącego pytania.
 * @param totalQuestions łączna liczba pytań w quizie.
 * @param onAnswerSelected callback wywoływany po wybraniu odpowiedzi; przyjmuje indeks wybranej opcji.
 * @param onShowExitDialog callback wywoływany po kliknięciu przycisku wyjścia.
 */
@Composable
fun QuestionSection(
    question: Question?,
    currentQuestionIndex: Int,
    totalQuestions: Int,
    onAnswerSelected: (Int) -> Unit,
    onShowExitDialog: (Boolean) -> Unit
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

            // Używamy Column zamiast LazyColumn, ponieważ:
            //   • Liczba opcji jest zawsze mała (≤ 4) i stała w czasie wykonania.
            //   • Rodzic jest już wewnątrz verticalScroll - nie chcemy zagnieżdżać kolejnej listy
            //   przewijalnej.
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
            onClick = { onShowExitDialog(true) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.quit_quiz_button))
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_standard)))
    }
}


/**
 * Sekcja wyników wyświetlana po zakończeniu quizu.
 * Pokazuje końcowy wynik użytkownika z możliwością powrotu do wyboru kategorii.
 *
 * @param userName nazwa użytkownika wprowadzona w ekranie konfiguracji.
 * @param score liczba poprawnych odpowiedzi udzielonych przez użytkownika.
 * @param totalQuestions łączna liczba pytań w quizie.
 * @param onResetQuiz callback wywoływany po kliknięciu przycisku powrotu do kategorii.
 */
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
        // painterResource(R.drawable.ic_quiz_trophy)
        // Wczytuje vector drawable z res/drawable/ic_quiz_trophy.xml przez klasę R.
        // R.drawable.ic_quiz_trophy to stała Int generowana w czasie budowania.
        //
        // Porównaj z ikonami Material używanymi w innych miejscach aplikacji:
        //   Icon(Icons.Rounded.Science, ...)      — ImageVector w pamięci, klasa R NIE jest używana
        //   painterResource(R.drawable.*)         — zasób XML, rozwiązywany przez klasę R w czasie wykonania
        //
        // Używaj painterResource() dla własnych zasobów graficznych;
        // używaj Icons.* dla standardowych ikon Material (nie wszystkie są w ten sposób dostępne).
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
// @Preview renderuje podgląd funkcji kompozycyjnej w IDE bez uruchamiania aplikacji na urządzeniu.
// Dobre praktyki:
//   • Jeden podgląd na każdy istotny stan UI (konfiguracja, trwający quiz, wyniki).
//   • Używaj name= i group= do organizowania podglądów w panelu Preview.
//   • Nigdy nie wywołuj prawdziwych źródeł danych w podglądzie — używaj  danych testowych.
//   • Używaj QuizShowcaseTheme, żeby podgląd odpowiadał stylowi rzeczywistej aplikacji.
// ─────────────────────────────────────────────────────────────────────────────



@Preview(name = "Setup Screen", group = "QuizShowcase", showBackground = true, showSystemUi = true)
@Composable
fun QuizSetupPreview() {
    QuizShowcaseTheme {
        QuizScreen(
            isDarkTheme = false,
            categories = DataSource.categories,
            lifecycleEvents = listOf("onCreate", "onStart", "onResume"),
            onThemeToggle = {},
        )
    }
}


@Preview(name = "Quiz In Progress", group = "QuizShowcase", showBackground = true, showSystemUi = true)
@Composable
fun QuizInProgressPreview() {
    QuizShowcaseTheme {
        QuizProgressSection(
            progress = 0.5f
        )
    }
}

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

