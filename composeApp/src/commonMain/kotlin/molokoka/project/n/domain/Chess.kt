package molokoka.project.n.domain

import kotlin.math.sign

fun sideToMove(playedMoves: Int): Side =
    if (playedMoves % 2 == 0) Side.WHITE else Side.BLACK

fun Position.play(moves: List<Move>): Position =
    moves.foldIndexed(this) { index, position, move ->
        position.play(move, sideToMove(index))
    }

fun Position.play(move: Move, side: Side): Position {

    val fromPiece = requireNotNull(pieces[move.from]) {
        "Move must start from an occupied square, was '$move'"
    }

    require(fromPiece.side == side) {
        "Move must move a piece of the side to move, was '$move' for $side"
    }

    if (fromPiece.type == PieceType.ROOK) {
        require(move.from.file == move.to.file || move.from.rank == move.to.rank) {
            "Rook must move along a rank or a file, was '$move'"
        }

        require(squaresBetween(move.from, move.to).none { it in pieces }) {
            "Rook must not move past another piece, was '$move'"
        }
    }

    require(pieces[move.to]?.side != fromPiece.side) {
        "Move must land on an empty square or an opposing piece, was '$move'"
    }

    return Position(pieces - move.from + (move.to to fromPiece))
}

/**
 * The squares strictly between [from] and [to], which must lie on a rank, a file, or a
 * diagonal. Any other pair never reaches [to] and throws once the walk steps off the board,
 * so the caller checks the direction first - `play` does so in its rook branch.
 */
private fun squaresBetween(from: Coordinates, to: Coordinates): List<Coordinates> {
    val filesMoveDirection = (to.file - from.file).sign
    val ranksMoveDirection = (to.rank - from.rank).sign

    return generateSequence(1) { step -> step + 1 }
        .map { step -> Coordinates(from.file + filesMoveDirection * step, from.rank + ranksMoveDirection * step) }
        .takeWhile { square -> square != to }
        .toList()
}
