# QuizShowcase

An Android quiz application written in **Kotlin** using **Jetpack Compose**, created as an educational project demonstrating key concepts of modern Android development.

## Project Goal

QuizShowcase is a showcase project designed for learning and presenting the following topics:

| Topic | What is demonstrated |
| --- | --- |
| **Activity Lifecycle** | Overridden `onCreate` → `onDestroy` methods with Logcat logging and a visible event history |
| **Jetpack Compose – State Hoisting** | Shared state flows down as parameters; events flow up through lambdas |
| **`remember` vs `rememberSaveable**` | Intentional state loss after screen rotation (quiz state) vs state retention (username) |
| **Android Resources** | `stringResource`, `dimensionResource`, `painterResource`, localization, `R` class |
| **Compose Layouts** | `Column`, `Row`, `LazyColumn`, `LazyRow`, `LazyVerticalGrid`, `Scaffold` |
| **Material 3** | `TopAppBar`, `Card`, `Button`, `Slider`, `Switch`, `LinearProgressIndicator`, `AlertDialog` |

## Project Structure

```
app/src/main/java/pl/marrod/quizshowcase/
├── MainActivity.kt          # Entry point; Activity lifecycle; Theme State Hoisting
├── data/
│   ├── Category.kt          # Data class for quiz category
│   ├── Question.kt          # Data class for quiz question
│   └── DataSource.kt        # Singleton – data source (categories and questions)
└── ui/
    ├── QuizScreen.kt        # Main quiz screen and all composable sections
    └── theme/               # Material 3 theme (colors, typography, shapes)

```

## App Screens

### 1. Setup Screen (`SetupSection`)

* Text field for the username (retained by `rememberSaveable`)
* Dark/light theme switch
* Difficulty level slider (1–3)
* Quiz category grid (`LazyVerticalGrid`)
* Lifecycle event history displayed as a scrollable `LazyRow` list

### 2. Questions Screen (`QuestionSection` + `QuizProgressSection`)

* Progress bar with percentage completion indicator
* Question text and four answer buttons
* Exit quiz button with a confirmation dialog (`AlertDialog`)

### 3. Results Screen (`ResultsSection`)

* Trophy loaded via `painterResource` (XML drawable resource)
* Final score in `X / Y` format
* Back to category selection button
