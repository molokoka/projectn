package molokoka.project.n.analysis

import molokoka.project.n.domain.AnalyticsTree
import molokoka.project.n.domain.Coordinates
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.Side
import molokoka.project.n.domain.sideToMove
import molokoka.project.n.ui.BoardOrientation

data class AnalysisState(
    val orientation: BoardOrientation = BoardOrientation.WHITE,
    val tree: AnalyticsTree = AnalyticsTree(),
    val moves: List<Move> = emptyList(),
    val selected: Coordinates? = null,
    val computerMovePending: Boolean = false
) {
    val position: Position get() = tree.positionAt(moves)

    val sideToMove: Side get() = sideToMove(moves.size)
}

sealed interface AnalysisIntent {

    data object FlipBoard : AnalysisIntent

    data object Reset : AnalysisIntent

    data object RequestComputerMove : AnalysisIntent

    data class SelectNode(val path: List<Move>) : AnalysisIntent

    data class OnSquareClick(val coordinates: Coordinates) : AnalysisIntent

    data class ComputerMoveReady(val path: List<Move>, val move: Move) : AnalysisIntent

    data class ComputerMoveNotFound(val path: List<Move>) : AnalysisIntent
}

sealed interface AnalysisEffect {

    data object CancelComputerMove : AnalysisEffect

    data class StartComputerMove(
        val position: Position,
        val side: Side,
        val path: List<Move>
    ) : AnalysisEffect
}

typealias AnalysisUpdate = Pair<AnalysisState, AnalysisEffect?>

fun AnalysisState.reduce(intent: AnalysisIntent): AnalysisUpdate = when (intent) {
    AnalysisIntent.Reset -> {
        initialState()
    }

    is AnalysisIntent.OnSquareClick -> {
        onSquareClick(intent.coordinates)
    }

    is AnalysisIntent.SelectNode -> {
        selectNode(intent.path)
    }

    AnalysisIntent.RequestComputerMove -> {
        copy(computerMovePending = true) to
            AnalysisEffect.StartComputerMove(position, sideToMove, moves)
    }

    is AnalysisIntent.ComputerMoveReady -> {
        playComputerMove(intent.path, intent.move)
    }

    is AnalysisIntent.ComputerMoveNotFound -> {
        computerMoveNotFound(intent.path)
    }

    AnalysisIntent.FlipBoard -> {
        flipBoard()
    }
}

private fun initialState(): AnalysisUpdate =
    AnalysisState() to AnalysisEffect.CancelComputerMove

private fun AnalysisState.onSquareClick(coordinates: Coordinates): AnalysisUpdate =
    when (selected) {
        null -> select(coordinates) to null
        coordinates -> copy(selected = null) to null
        else -> playOrReselect(Move(selected, coordinates))
    }

private fun AnalysisState.select(coordinates: Coordinates): AnalysisState =
    if (position.pieces[coordinates]?.side == sideToMove) {
        copy(selected = coordinates)
    } else {
        this
    }

private fun AnalysisState.playOrReselect(move: Move): AnalysisUpdate =
    runCatching { tree.play(moves, move) }
        .fold(
            onSuccess = { played ->
                copy(
                    tree = played,
                    moves = moves + move,
                    selected = null,
                    computerMovePending = false
                ) to AnalysisEffect.CancelComputerMove
            },
            onFailure = {
                copy(selected = null)
                    .select(move.to) to null
            }
        )

private fun AnalysisState.selectNode(path: List<Move>): AnalysisUpdate =
    when {
        !tree.contains(path) -> this to null
        path == moves -> this to null
        else -> {
            copy(
                moves = path,
                selected = null,
                computerMovePending = false
            ) to AnalysisEffect.CancelComputerMove
        }
    }

private fun AnalysisState.playComputerMove(path: List<Move>, move: Move): AnalysisUpdate {
    if (path != moves) return this to null

    return runCatching { tree.play(path, move) }
        .fold(
            onSuccess = { played ->
                copy(
                    tree = played,
                    moves = path + move,
                    selected = null,
                    computerMovePending = false
                ) to null
            },
            onFailure = {
                copy(computerMovePending = false) to null
            }
        )
}

private fun AnalysisState.computerMoveNotFound(path: List<Move>): AnalysisUpdate {
    if (path != moves) return this to null

    return copy(computerMovePending = false) to null
}

private fun AnalysisState.flipBoard(): AnalysisUpdate =
    copy(
        orientation = when (orientation) {
            BoardOrientation.WHITE -> BoardOrientation.BLACK
            BoardOrientation.BLACK -> BoardOrientation.WHITE
        }
    ) to null
