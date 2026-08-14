package molokoka.project.n.domain.move_requirements

import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position

fun Position.requireValidRookMove(move: Move) {
    val squaresBetween = when {
        isOnRank(move) -> squaresBetweenOnRank(move)
        isOnFile(move) -> squaresBetweenOnFile(move)
        else -> throw IllegalArgumentException(
            "Rook must move along a rank or a file, was '$move'"
        )
    }

    require(squaresBetween.none { it in pieces }) {
        "Rook must not move past another piece, was '$move'"
    }
}
