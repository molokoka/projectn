package molokoka.project.n.domain.chess

data class ChessCoordinates(
    val file: Char,
    val rank: Int
) {
    val col: Int = file - FIRST_FILE
    val row: Int = rank - 1

    companion object {

        fun create(file: Char, rank: Int, chessBoardSize: Int): ChessCoordinates {
            require(file in FILE_RANGE) { "File must be a letter (a-z)" }
            require(file <= (FIRST_FILE + chessBoardSize - 1)) { "File must not exceed board size" }
            require(rank >= 1) { "Rank must be positive (1-based)" }
            require(rank <= chessBoardSize) { "Rank must not exceed board size" }

            return ChessCoordinates(file, rank)
        }
        
        fun fromRowCol(row: Int, col: Int, chessBoardSize: Int): ChessCoordinates {
            val file = (FIRST_FILE + col)
            val rank = row + 1  // row 0 -> rank 1, row 7 -> rank 8
            return create(file, rank, chessBoardSize)
        }
    }
}