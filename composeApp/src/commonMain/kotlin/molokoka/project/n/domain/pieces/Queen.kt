package molokoka.project.n.domain.pieces

import molokoka.project.n.domain.Coordinates
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position

internal fun Position.queenReachableSquares(origin: Coordinates): Set<Coordinates> =
    (reachableOnRank(origin) + reachableOnFile(origin) + reachableOnDiagonal(origin)).toSet()

fun Position.requireValidQueenMove(move: Move) {
    val reachable = when {
        isOnRank(move) -> reachableOnRank(move.from)
        isOnFile(move) -> reachableOnFile(move.from)
        isOnDiagonal(move) -> reachableOnDiagonal(move.from)
        else -> throw IllegalArgumentException(
            "Queen must move along a rank, a file, or a diagonal, was '$move'"
        )
    }

    require(move.to in reachable) {
        "Queen must not move past another piece, was '$move'"
    }
}
