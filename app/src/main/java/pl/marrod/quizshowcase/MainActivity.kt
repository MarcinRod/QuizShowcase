package pl.marrod.quizshowcase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import pl.marrod.quizshowcase.ui.ModifierDemoScreen
import pl.marrod.quizshowcase.ui.theme.QuizShowcaseTheme

// TODO: Omówienie zagadnienia Aktywności (cykl życia) + Manifest
// TODO: Omówienie zagadnienia zasobów oraz kontekstu - stworzenie string, dimens, drawable i pokazanie jak z nich korzystać
// TODO: Omówienie Composables, stworzenie pierwszego composable, pokazanie podstawowych elementów (Text, Button, TextField, Column, Row, Box, Image) + Preview
// TODO: Omówienie Modifier + pokazanie przykładów (padding, fillMaxSize, background, clickable, etc.)
// TODO: Omówienie zagadnienia stanu + pokazanie przykładów (remember, mutableStateOf, etc.)
// TODO: Pokazanie jak tworzyć bardziej rozbudowane composables + przekazywanie parametrów do nich
// TODO: Wykorzystanie różnego rodzaju list (LazyColumn, LazyRow)
// TODO: Pokazanie MaterialDesign3 + aplikacja z przykładami
//

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {

            QuizShowcaseTheme() {

            }
        }


    }
}
