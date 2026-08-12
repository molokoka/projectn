package molokoka.project.n.domain.chess

data class ChessCoordinates(
    val file: Char,
    val rank: Int
) {
    val col: Int = file - FIRST_FILE
    val row: Int = rank - 1

    companion object {

        fun create(file: Char, rank: Int): ChessCoordinates {
            require(file in FILE_RANGE) { "File must be in $FILE_RANGE, was '$file'" }
            require(rank in RANK_RANGE) { "Rank must be in $RANK_RANGE, was $rank" }

            return ChessCoordinates(file, rank)
        }

        fun fromRowCol(row: Int, col: Int): ChessCoordinates =
            create(FIRST_FILE + col, row + 1)
    }
}

val ChessCoordinates.isLightSquare: Boolean get() = (row + col) % 2 != 0
