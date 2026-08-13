package molokoka.project.n.domain

data class Position(val pieces: Map<Coordinates, Piece>) {

    override fun toString(): String =
        pieces.entries
            .sortedWith(compareBy({ it.key.rank }, { it.key.file }))
            .joinToString(" ") { (square, piece) -> "${piece.symbol}$square" }

    companion object {

        val INITIAL = parse(INITIAL_POSITION)

        fun parse(notation: String): Position {
            val pieces = notation
                .split(" ")
                .map { token -> Coordinates.parse(token.drop(1)) to Piece.fromSymbol(token[0]) }

            require(pieces.distinctBy { it.first }.size == pieces.size) {
                "Each square must hold at most one piece, was '$notation'"
            }

            return Position(pieces.toMap())
        }
    }
}
