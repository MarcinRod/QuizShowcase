package pl.marrod.quizshowcase.data

data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val categoryId: String
)
