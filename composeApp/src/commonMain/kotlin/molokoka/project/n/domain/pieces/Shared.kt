package molokoka.project.n.domain.pieces

import molokoka.project.n.domain.Coordinates
import molokoka.project.n.domain.FILE_RANGE
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.RANK_RANGE
import kotlin.math.abs

internal fun isOnRank(move: Move): Boolean =
    move.from.rank == move.to.rank

internal fun isOnFile(move: Move): Boolean =
    move.from.file == move.to.file

internal fun isOnDiagonal(move: Move): Boolean =
    abs(move.to.file - move.from.file) == abs(move.to.rank - move.from.rank)

internal fun Position.reachableOnRank(origin: Coordinates): List<Coordinates> {
    val towardsH = (1..(FILE_RANGE.last - origin.file))
        .map { fileStep ->
            Coordinates(origin.file + fileStep, origin.rank)
        }
    val towardsA = (1..(origin.file - FILE_RANGE.first))
        .map { fileStep ->
            Coordinates(origin.file - fileStep, origin.rank)
        }

    return reachableAlong(origin, towardsH) + reachableAlong(origin, towardsA)
}

internal fun Position.reachableOnFile(origin: Coordinates): List<Coordinates> {
    val towardsEight = (1..(RANK_RANGE.last - origin.rank))
        .map { rankStep ->
            Coordinates(origin.file, origin.rank + rankStep)
        }
    val towardsOne = (1..(origin.rank - RANK_RANGE.first))
        .map { rankStep ->
            Coordinates(origin.file, origin.rank - rankStep)
        }

    return reachableAlong(origin, towardsEight) + reachableAlong(origin, towardsOne)
}

internal fun Position.reachableOnDiagonal(origin: Coordinates): List<Coordinates> {
    val towardsH8 = diagonalRay(origin, 1, 1)
    val towardsH1 = diagonalRay(origin, 1, -1)
    val towardsA8 = diagonalRay(origin, -1, 1)
    val towardsA1 = diagonalRay(origin, -1, -1)

    return reachableAlong(origin, towardsH8) + reachableAlong(origin, towardsH1) +
        reachableAlong(origin, towardsA8) + reachableAlong(origin, towardsA1)
}

private fun diagonalRay(
    origin: Coordinates,
    fileDirection: Int,
    rankDirection: Int
): List<Coordinates> {
    val filesToEdge =
        if (fileDirection > 0) FILE_RANGE.last - origin.file else origin.file - FILE_RANGE.first
    val ranksToEdge =
        if (rankDirection > 0) RANK_RANGE.last - origin.rank else origin.rank - RANK_RANGE.first

    return (1..minOf(filesToEdge, ranksToEdge))
        .map { diagonalStep ->
            Coordinates(
                origin.file + fileDirection * diagonalStep,
                origin.rank + rankDirection * diagonalStep
            )
        }
}

private fun Position.reachableAlong(
    origin: Coordinates,
    ray: List<Coordinates>
): List<Coordinates> {
    val blockerPieceIndex = ray.indexOfFirst { square -> square in pieces }

    if (blockerPieceIndex == -1) return ray

    val mover = pieces.getValue(origin)
    val blocker = pieces.getValue(ray[blockerPieceIndex])

    return if (blocker.side == mover.side) {
        ray.take(blockerPieceIndex)
    } else {
        ray.take(blockerPieceIndex + 1)
    }
}
