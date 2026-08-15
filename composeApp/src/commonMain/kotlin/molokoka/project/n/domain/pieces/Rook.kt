package molokoka.project.n.domain.pieces

import molokoka.project.n.domain.Coordinates
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position

internal fun Position.rookReachableSquares(origin: Coordinates): Set<Coordinates> =
    (reachableOnRank(origin) + reachableOnFile(origin)).toSet()

fun Position.requireValidRookMove(move: Move) {
    val reachable = when {
        isOnRank(move) -> reachableOnRank(move.from)
        isOnFile(move) -> reachableOnFile(move.from)
        else -> throw IllegalArgumentException(
            "Rook must move along a rank or a file, was '$move'"
        )
    }

    require(move.to in reachable) {
        "Rook must not move past another piece, was '$move'"
    }
}
