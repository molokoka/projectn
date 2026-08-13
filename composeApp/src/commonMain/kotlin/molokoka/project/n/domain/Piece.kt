package molokoka.project.n.domain

data class Piece(val type: PieceType, val side: Side) {

    val symbol: Char
        get() = if (side == Side.WHITE) type.letter.uppercaseChar() else type.letter

    companion object {

        fun fromSymbol(symbol: Char): Piece {
            val type = PieceType.entries.firstOrNull { it.letter == symbol.lowercaseChar() }
            requireNotNull(type) { "Symbol must be a piece letter, was '$symbol'" }

            return Piece(type, if (symbol.isUpperCase()) Side.WHITE else Side.BLACK)
        }
    }
}