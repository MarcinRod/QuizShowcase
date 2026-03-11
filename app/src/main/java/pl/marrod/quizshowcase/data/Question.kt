package pl.marrod.quizshowcase.data

/** Klasa danych reprezentująca pytanie quizowe, zawierająca unikalny identyfikator, tekst pytania,
 * listę opcji odpowiedzi, indeks poprawnej odpowiedzi oraz identyfikator kategorii, do której należy pytanie.
 */
data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val categoryId: String
)
