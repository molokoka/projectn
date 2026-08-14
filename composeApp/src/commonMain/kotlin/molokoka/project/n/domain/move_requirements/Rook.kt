package molokoka.project.n.domain.move_requirements

import molokoka.project.n.domain.Coordinates
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position

fun Position.requireValidRookMove(move: Move) {
    require(move.from.file == move.to.file || move.from.rank == move.to.rank) {
        "Rook must move along a rank or a file, was '$move'"
    }

    require(squaresBetween(move.from, move.to).none { it in pieces }) {
        "Rook must not move past another piece, was '$move'"
    }
}

private fun squaresBetween(from: Coordinates, to: Coordinates): List<Coordinates> =
    if (from.file == to.file) {
        ranksBetween(from.rank, to.rank).map { rank -> Coordinates(from.file, rank) }
    } else {
        filesBetween(from.file, to.file).map { file -> Coordinates(file, from.rank) }
    }

private fun ranksBetween(from: Int, to: Int): IntRange =
    (minOf(from, to) + 1) until maxOf(from, to)

private fun filesBetween(from: Char, to: Char): CharRange =
    (minOf(from, to) + 1) until maxOf(from, to)
