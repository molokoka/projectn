package molokoka.project.n.domain

import molokoka.project.n.domain.pieces.PieceType
import molokoka.project.n.domain.pieces.queenReachableSquares
import molokoka.project.n.domain.pieces.rookReachableSquares

fun Position.reachableSquares(origin: Coordinates): Set<Coordinates> {
    val piece = pieces[origin] ?: return emptySet()

    return when (piece.type) {
        PieceType.ROOK -> rookReachableSquares(origin)
        PieceType.QUEEN -> queenReachableSquares(origin)
    }
}
