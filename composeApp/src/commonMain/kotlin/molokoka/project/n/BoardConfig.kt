package molokoka.project.n

val FILE_CHARS = 'a'..'z'
const val LATIN_ALPHABET_SIZE = 28

data class BoardConfig(
    val minBoardSize: Int = 4,
    val maxBoardSize: Int = LATIN_ALPHABET_SIZE,
    val defaultBoardSize: Int = 8
)