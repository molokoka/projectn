package molokoka.project.n

data class ChessCoordinate(
    val file: Char,
    val rank: Int,
    val boardSize: Int
) {
    init {
        // TODO: check file limitations too
        require(file in 'a'..'z') { "File must be a letter (a-z)" }
        require(rank >= 1) { "Rank must be positive (1-based)" }
        require(rank <= boardSize) { "Rank must not exceed board size" }
    }

    val col: Int get() = file - 'a'
    val row: Int get() = boardSize - rank  // rank 8 -> row 0, rank 1 -> row 7

    companion object {
        fun fromRowCol(row: Int, col: Int, boardSize: Int): ChessCoordinate {
            val file = ('a' + col)
            val rank = boardSize - row
            return ChessCoordinate(file, rank, boardSize)
        }
    }
}