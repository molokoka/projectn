package molokoka.project.n.domain.chess

const val FIRST_FILE = 'a'
const val LATIN_ALPHABET_SIZE = 28 // interconnected with file range
val FILE_RANGE = FIRST_FILE..'z'

const val MIN_BOARD_SIZE = 4
const val MAX_BOARD_SIZE = LATIN_ALPHABET_SIZE
const val DEFAULT_BOARD_SIZE = 8