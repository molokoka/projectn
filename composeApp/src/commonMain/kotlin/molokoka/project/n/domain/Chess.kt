package molokoka.project.n.domain

import molokoka.project.n.domain.pieces.PieceType
import molokoka.project.n.domain.pieces.requireValidQueenMove
import molokoka.project.n.domain.pieces.requireValidRookMove

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

    when (fromPiece.type) {
        PieceType.ROOK -> requireValidRookMove(move)
        PieceType.QUEEN -> requireValidQueenMove(move)
    }

    return Position(pieces - move.from + (move.to to fromPiece))
}