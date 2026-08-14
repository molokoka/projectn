package molokoka.project.n.domain.move_requirements

import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position

fun Position.requireValidQueenMove(move: Move) {
    val squaresBetween = when {
        isOnRank(move) -> squaresBetweenOnRank(move)
        isOnFile(move) -> squaresBetweenOnFile(move)
        isOnDiagonal(move) -> squaresBetweenOnDiagonal(move)
        else -> throw IllegalArgumentException(
            "Queen must move along a rank, a file, or a diagonal, was '$move'"
        )
    }

    require(squaresBetween.none { it in pieces }) {
        "Queen must not move past another piece, was '$move'"
    }
}
