package molokoka.project.n.domain

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

    require(pieces[move.to]?.side != fromPiece.side) {
        "Move must land on an empty square or an opposing piece, was '$move'"
    }

    return Position(pieces - move.from + (move.to to fromPiece))
}
