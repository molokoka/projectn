package molokoka.project.n.ui

import molokoka.project.n.domain.Side
import molokoka.project.n.domain.pieces.Piece
import molokoka.project.n.domain.pieces.PieceType

val Piece.glyph: Char
    get() = when (type) {
        PieceType.ROOK -> if (side == Side.WHITE) '♖' else '♜'
        PieceType.QUEEN -> if (side == Side.WHITE) '♕' else '♛'
    }
