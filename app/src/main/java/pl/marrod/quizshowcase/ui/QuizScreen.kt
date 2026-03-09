package pl.marrod.quizshowcase.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.marrod.quizshowcase.R
import pl.marrod.quizshowcase.data.Category
import pl.marrod.quizshowcase.ui.theme.QuizShowcaseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(viewModel: QuizViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.padding_medium)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.selectedCategory == null) {
                SetupSection(uiState, viewModel)
            } else if (!uiState.isQuizFinished) {
                QuizProgressSection(uiState)
                
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_standard)))

                QuestionSection(uiState, viewModel)
            } else {
                ResultsSection(uiState, viewModel)
            }
        }
    }

    if (uiState.showExitDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleExitDialog(false) },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.toggleExitDialog(false)
                    viewModel.resetQuiz() 
                }) {
                    Text(stringResource(R.string.exit_confirm_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.toggleExitDialog(false) }) {
                    Text(stringResource(R.string.cancel_button))
                }
            },
            title = { Text(stringResource(R.string.exit_dialog_title)) },
            text = { Text(stringResource(R.string.exit_dialog_message)) }
        )
    }
}

@Composable
fun ColumnScope.SetupSection(uiState: QuizUiState, viewModel: QuizViewModel) {
    Text(
        stringResource(R.string.welcome_message),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )
    
    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_standard)))

    TextField(
        value = uiState.userName,
        onValueChange = { viewModel.onUserNameChange(it) },
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
            checked = uiState.isDarkTheme,
            onCheckedChange = { viewModel.onThemeToggle(it) }
        )
    }

    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))

    Column {
        val difficultyLabel = when (uiState.difficulty.toInt()) {
            1 -> stringResource(R.string.difficulty_easy)
            2 -> stringResource(R.string.difficulty_medium)
            3 -> stringResource(R.string.difficulty_hard)
            else -> stringResource(R.string.difficulty_easy)
        }
        Text(stringResource(R.string.difficulty_label, difficultyLabel))
        Slider(
            value = uiState.difficulty,
            onValueChange = { viewModel.onDifficultyChange(it) },
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

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        modifier = Modifier.weight(1f)
    ) {
        items(uiState.categories) { category ->
            CategoryCard(category) { viewModel.selectCategory(category) }
        }
    }
    
    if (uiState.lifecycleEvents.isNotEmpty()) {
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_standard)))
        Text(stringResource(R.string.lifecycle_events_title), style = MaterialTheme.typography.labelSmall)
        val events = uiState.lifecycleEvents.takeLast(3)
        events.forEach { event ->
            Text(event, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_standard)))
    }
}

@Composable
fun CategoryCard(category: Category, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.card_height))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(category.icon, contentDescription = null, modifier = Modifier.size(dimensionResource(R.dimen.icon_size_medium)))
            Text(category.name, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun QuizProgressSection(uiState: QuizUiState) {
    Column {
        Text(
            stringResource(R.string.progress_label, (uiState.progress * 100).toInt()),
            style = MaterialTheme.typography.labelMedium
        )
        LinearProgressIndicator(
            progress = { uiState.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.progress_bar_height)),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

@Composable
fun ColumnScope.QuestionSection(uiState: QuizUiState, viewModel: QuizViewModel) {
    val question = uiState.currentQuestion ?: return

    Column(modifier = Modifier.weight(1f)) {
        Text(
            text = stringResource(R.string.question_counter, uiState.currentQuestionIndex + 1, uiState.questions.size),
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

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
            modifier = Modifier.weight(1f)
        ) {
            items(question.options.size) { index ->
                val option = question.options[index]
                Button(
                    onClick = { viewModel.onAnswerSelected(index) },
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
        onClick = { viewModel.toggleExitDialog(true) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.quit_quiz_button))
    }
    
    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_standard)))
}

@Composable
fun ColumnScope.ResultsSection(uiState: QuizUiState, viewModel: QuizViewModel) {
    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Rounded.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_large)),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_standard)))

        Text(
            stringResource(R.string.quiz_finished_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            stringResource(R.string.great_job_message, uiState.userName),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_large)), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.your_score_label), style = MaterialTheme.typography.labelLarge)
                Text(
                    "${uiState.score} / ${uiState.questions.size}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.resetQuiz() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.back_to_categories_button))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuizScreenPreview() {
    QuizShowcaseTheme {
        QuizScreen(viewModel = QuizViewModel())
    }
}
