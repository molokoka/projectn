package molokoka.project.n.analysis

import molokoka.project.n.domain.Coordinates
import molokoka.project.n.move_evaluation.MoveEvaluation
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.Side
import molokoka.project.n.domain.sideToMove
import molokoka.project.n.ui.BoardOrientation

data class AnalysisState(
    val orientation: BoardOrientation = BoardOrientation.WHITE,
    val tree: AnalysisTree = AnalysisTree(),
    val moves: List<Move> = emptyList(),
    val selected: Coordinates? = null,
    val isComputerMovePending: Boolean = false,
    val pendingEvaluationGeneration: Int = 0,
) {
    val position: Position get() = tree.positionAt(moves)

    val sideToMove: Side get() = sideToMove(moves.size)

    val isMoveEvaluationPending: Boolean
        get() = pendingEvaluationGeneration > tree.evaluationGeneration
}

sealed interface AnalysisIntent {

    data object FlipBoard : AnalysisIntent

    data object Reset : AnalysisIntent

    data object RequestComputerMove : AnalysisIntent

    data class SelectNode(val path: List<Move>) : AnalysisIntent

    data class OnSquareClick(val coordinates: Coordinates) : AnalysisIntent

    data class ComputerMoveReady(val path: List<Move>, val move: Move) : AnalysisIntent

    data class ComputerMoveNotFound(val path: List<Move>) : AnalysisIntent

    data object RequestMovesEvaluation : AnalysisIntent

    data class MovesEvaluationReady(
        val generation: Int,
        val evaluations: Map<List<Move>, MoveEvaluation>
    ) : AnalysisIntent
}

sealed interface AnalysisEffect {

    data object CancelComputerMove : AnalysisEffect

    data class StartComputerMove(
        val position: Position,
        val side: Side,
        val path: List<Move>
    ) : AnalysisEffect

    data object CancelMoveEvaluation : AnalysisEffect

    data class StartMovesEvaluation(
        val generation: Int,
        val tree: AnalysisTree
    ) : AnalysisEffect
}

typealias AnalysisUpdate = Pair<AnalysisState, List<AnalysisEffect>>
private fun emptyEffects(): List<AnalysisEffect> = emptyList()

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
        copy(isComputerMovePending = true) to
            listOf(AnalysisEffect.StartComputerMove(position, sideToMove, moves))
    }

    is AnalysisIntent.ComputerMoveReady -> {
        playComputerMove(intent.path, intent.move)
    }

    is AnalysisIntent.ComputerMoveNotFound -> {
        computerMoveNotFound(intent.path)
    }

    AnalysisIntent.RequestMovesEvaluation -> {
        startMovesEvaluation()
    }

    is AnalysisIntent.MovesEvaluationReady -> {
        movesEvaluationReady(intent.generation, intent.evaluations)
    }

    AnalysisIntent.FlipBoard -> {
        flipBoard()
    }
}

private fun initialState(): AnalysisUpdate =
    AnalysisState() to listOf(
        AnalysisEffect.CancelComputerMove,
        AnalysisEffect.CancelMoveEvaluation
    )

private fun AnalysisState.onSquareClick(coordinates: Coordinates): AnalysisUpdate =
    when (selected) {
        null -> select(coordinates) to emptyEffects()
        coordinates -> copy(selected = null) to emptyEffects()
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
                    isComputerMovePending = false
                ) to listOf(AnalysisEffect.CancelComputerMove)
            },
            onFailure = {
                copy(selected = null)
                    .select(move.to) to emptyEffects()
            }
        )

private fun AnalysisState.selectNode(path: List<Move>): AnalysisUpdate =
    when {
        !tree.contains(path) -> this to emptyEffects()
        path == moves -> this to emptyEffects()
        else -> {
            copy(
                moves = path,
                selected = null,
                isComputerMovePending = false
            ) to listOf(AnalysisEffect.CancelComputerMove)
        }
    }

private fun AnalysisState.playComputerMove(path: List<Move>, move: Move): AnalysisUpdate {
    if (path != moves) return this to emptyEffects()

    return runCatching { tree.play(path, move) }
        .fold(
            onSuccess = { played ->
                copy(
                    tree = played,
                    moves = path + move,
                    selected = null,
                    isComputerMovePending = false
                ) to emptyEffects()
            },
            onFailure = {
                copy(isComputerMovePending = false) to emptyEffects()
            }
        )
}

private fun AnalysisState.computerMoveNotFound(path: List<Move>): AnalysisUpdate {
    if (path != moves) return this to emptyEffects()

    return copy(isComputerMovePending = false) to emptyEffects()
}

private fun AnalysisState.startMovesEvaluation(): AnalysisUpdate {
    val generation = pendingEvaluationGeneration + 1

    return copy(pendingEvaluationGeneration = generation) to
        listOf(AnalysisEffect.StartMovesEvaluation(generation, tree))
}

private fun AnalysisState.movesEvaluationReady(
    receivedEvaluationGeneration: Int,
    evaluations: Map<List<Move>, MoveEvaluation>
): AnalysisUpdate =
    if (receivedEvaluationGeneration < tree.evaluationGeneration ||
        receivedEvaluationGeneration > pendingEvaluationGeneration
    ) {
        this to emptyEffects()
    } else {
        copy(tree = tree.withEvaluations(receivedEvaluationGeneration, evaluations)) to emptyEffects()
    }

private fun AnalysisState.flipBoard(): AnalysisUpdate =
    copy(
        orientation = when (orientation) {
            BoardOrientation.WHITE -> BoardOrientation.BLACK
            BoardOrientation.BLACK -> BoardOrientation.WHITE
        }
    ) to emptyEffects()
