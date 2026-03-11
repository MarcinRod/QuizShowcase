package pl.marrod.quizshowcase.data

import androidx.compose.ui.graphics.vector.ImageVector

/** Klasa danych reprezentująca kategorię quizu, zawierająca unikalny identyfikator, nazwę i ikonę.
 */
data class Category(
    val id: String,
    val name: String,
    val icon: ImageVector
)
