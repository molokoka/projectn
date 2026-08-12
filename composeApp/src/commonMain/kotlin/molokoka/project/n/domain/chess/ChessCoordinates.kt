package molokoka.project.n.domain.chess

data class ChessCoordinates(
    val file: Char,
    val rank: Int
) {
    init {
        require(file in FILE_RANGE) { "File must be in $FILE_RANGE, was '$file'" }
        require(rank in RANK_RANGE) { "Rank must be in $RANK_RANGE, was $rank" }
    }

    override fun toString(): String = "$file$rank"

    companion object {

        fun create(square: String): ChessCoordinates {
            require(square.length == 2) { "Square must be a file and a rank, like 'a1', was '$square'" }

            return ChessCoordinates(square[0], square[1] - '0')
        }
    }
}

val ChessCoordinates.isLightSquare: Boolean get() = (file.code + rank) % 2 != 0
