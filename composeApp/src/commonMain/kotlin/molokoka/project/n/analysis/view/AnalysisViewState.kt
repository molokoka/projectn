package molokoka.project.n.analysis.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import molokoka.project.n.domain.AnalysisTree
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Side
import molokoka.project.n.domain.sideToMove
import org.jetbrains.compose.resources.stringResource
import projectn.composeapp.generated.resources.Res
import projectn.composeapp.generated.resources.start

private const val DepthMarker = "."

data class MoveRowState(
    val path: List<Move>,
    val label: String,
    val side: Side?,
    val isSelected: Boolean
)

data class AnalysisViewState(
    val currentMoves: String,
    val moveCount: Int,
    val rows: List<MoveRowState>,
    val selectedRow: Int?
)

@Composable
internal fun rememberAnalysisViewState(
    tree: AnalysisTree,
    moves: List<Move>
): AnalysisViewState {
    val startLabel = stringResource(Res.string.start)

    return remember(tree, moves, startLabel) {
        analysisViewState(tree, moves, startLabel)
    }
}

internal fun analysisViewState(
    tree: AnalysisTree,
    moves: List<Move>,
    startLabel: String
): AnalysisViewState {
    val startRow = MoveRowState(
        path = emptyList(),
        label = startLabel,
        side = null,
        isSelected = moves.isEmpty()
    )

    val moveRows = tree.paths().map { path ->
        MoveRowState(
            path = path,
            label = DepthMarker.repeat(path.size) + " " + tree.moveWithEvaluation(path),
            side = sideToMove(path.size - 1),
            isSelected = path == moves
        )
    }

    val rows = listOf(startRow) + moveRows

    return AnalysisViewState(
        currentMoves = tree.currentMoves(moves),
        moveCount = moves.size,
        rows = rows,
        selectedRow = rows
            .indexOfFirst { row -> row.isSelected }
            .takeIf { rowIndex -> rowIndex >= 0 }
    )
}

private fun AnalysisTree.currentMoves(moves: List<Move>): String =
    moves.indices.joinToString(" ") { moveIndex ->
        moveWithEvaluation(moves.take(moveIndex + 1))
    }

private fun AnalysisTree.moveWithEvaluation(path: List<Move>): String {
    val move = path.last()
    val moveEvaluation = evaluationAt(path)

    return if (moveEvaluation == null) "$move" else "$move$moveEvaluation"
}
