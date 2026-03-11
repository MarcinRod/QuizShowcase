package pl.marrod.quizshowcase.data

import android.content.res.Resources
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Science
import pl.marrod.quizshowcase.R

/** Obiekt singleton pełniący rolę źródła danych dla aplikacji quizowej, zawierający listę kategorii
 * oraz metodę do pobierania pytań na podstawie identyfikatora kategorii i zasobów. Jest to proste
 * rozwiązanie do celów demonstracyjnych, które można łatwo rozbudować o bardziej zaawansowane mechanizmy
 * zarządzania danymi, takie jak repozytoria, bazy danych czy sieciowe API.
 */
object DataSource {
    val categories: List<Category> = listOf(
        Category("1", "Science", Icons.Rounded.Science),
        Category("2", "History", Icons.Rounded.History),
        Category("3", "Computing", Icons.Rounded.Computer),
        Category("4", "Geography", Icons.Rounded.Public)
    )

    // ZASOBY:
    // Dwa podejścia obok siebie w każdej kategorii:
    // - Istniejące pytania - zakodowane ciągi bezpośrednio w kodzie Kotlin.
    // Proste, ale nieprzetłumaczalne i zaśmiecające plik źródłowy.
    // - Nowe pytania - tekst i opcje pochodzą z res/values/strings.xml
    // poprzez resources.getString() i resources.getStringArray().
    // Łatwe zarządzanie i tłumaczenie
    fun getQuestions(categoryId: String, resources: Resources): List<Question> = when (categoryId) {
        "1" -> listOf(
            // Hardcoded approach — text lives in Kotlin source
            Question(1, "What is the chemical symbol for Water?", listOf("H2O", "O2", "CO2", "NaCl"), 0, "1"),
            Question(2, "Which planet is known as the Red Planet?", listOf("Earth", "Mars", "Jupiter", "Saturn"), 1, "1"),
            // String-array approach — text and options resolved from res/values/strings.xml
            Question(9, resources.getString(R.string.q9_cat1_text), resources.getStringArray(R.array.q9_cat1_options).toList(), 2, "1")
        )
        "2" -> listOf(
            Question(3, "Who was the first President of the United States?", listOf("Thomas Jefferson", "Abraham Lincoln", "George Washington", "John Adams"), 2, "2"),
            Question(4, "In which year did World War II end?", listOf("1943", "1944", "1945", "1946"), 2, "2"),
            Question(10, resources.getString(R.string.q10_cat2_text), resources.getStringArray(R.array.q10_cat2_options).toList(), 1, "2")
        )
        "3" -> listOf(
            Question(5, "What does CPU stand for?", listOf("Central Process Unit", "Central Processing Unit", "Computer Personal Unit", "Central Processor Unifier"), 1, "3"),
            Question(6, "Which language is used for Android development?", listOf("Swift", "Kotlin", "C#", "Ruby"), 1, "3"),
            Question(11, resources.getString(R.string.q11_cat3_text), resources.getStringArray(R.array.q11_cat3_options).toList(), 0, "3")
        )
        "4" -> listOf(
            Question(7, "What is the capital of France?", listOf("Berlin", "Madrid", "Paris", "Rome"), 2, "4"),
            Question(8, "Which is the largest continent?", listOf("Africa", "Asia", "Europe", "Antarctica"), 1, "4"),
            Question(12, resources.getString(R.string.q12_cat4_text), resources.getStringArray(R.array.q12_cat4_options).toList(), 2, "4")
        )
        else -> emptyList()
    }
}
