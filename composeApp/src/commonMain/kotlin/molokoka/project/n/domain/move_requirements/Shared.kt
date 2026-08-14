package molokoka.project.n.domain.move_requirements

import molokoka.project.n.domain.Coordinates
import molokoka.project.n.domain.Move
import kotlin.math.abs

internal fun isOnRank(move: Move): Boolean =
    move.from.rank == move.to.rank

internal fun isOnFile(move: Move): Boolean =
    move.from.file == move.to.file

internal fun isOnDiagonal(move: Move): Boolean =
    abs(move.to.file - move.from.file) == abs(move.to.rank - move.from.rank)

internal fun squaresBetweenOnRank(move: Move): List<Coordinates> {
    val start = minOf(move.from.file, move.to.file) + 1
    val end = maxOf(move.from.file, move.to.file)

    return (start until end)
        .map { fileStep ->
            Coordinates(fileStep, move.from.rank)
        }
}

internal fun squaresBetweenOnFile(move: Move): List<Coordinates> {
    val start = minOf(move.from.rank, move.to.rank) + 1
    val end = maxOf(move.from.rank, move.to.rank)

    return (start until end)
        .map { rankStep ->
            Coordinates(move.from.file, rankStep)
        }
}

internal fun squaresBetweenOnDiagonal(move: Move): List<Coordinates> {
    val fileDirection = if (move.from.file < move.to.file) 1 else -1
    val rankDirection = if (move.from.rank < move.to.rank) 1 else -1

    val start = 1
    val end = abs(move.to.file - move.from.file)

    return (start until end)
        .map { diagonalStep ->
            Coordinates(
                move.from.file + fileDirection * diagonalStep,
                move.from.rank + rankDirection * diagonalStep
            )
        }
}
