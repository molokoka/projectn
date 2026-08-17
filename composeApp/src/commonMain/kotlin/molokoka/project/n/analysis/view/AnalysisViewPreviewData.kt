package molokoka.project.n.analysis.view

import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Move.Companion.parse
import molokoka.project.n.domain.Side.BLACK
import molokoka.project.n.domain.Side.WHITE

private const val StartLabel = "Start"

private fun line(vararg moves: String): List<Move> = moves.map(::parse)

private val startRow = MoveRowState(
    path = emptyList(),
    label = StartLabel,
    side = null,
    isSelected = false
)

private val openingLineRows = listOf(
    startRow,
    MoveRowState(line("b2a2"), ". b2a2", WHITE, isSelected = false),
    MoveRowState(line("b2a2", "b8b5"), ".. b8b5", BLACK, isSelected = false),
    MoveRowState(line("b2a2", "b8b5", "h2g2"), "... h2g2", WHITE, isSelected = false),
    MoveRowState(line("b2a2", "b8b5", "h2g2", "a7a4"), ".... a7a4", BLACK, isSelected = false)
)

internal val PreviewEmptyViewState = AnalysisViewState(
    currentMoves = "",
    moveCount = 0,
    rows = listOf(startRow.copy(isSelected = true)),
    selectedRow = 0
)

internal val PreviewLineViewState = AnalysisViewState(
    currentMoves = "b2a2 b8b5 h2g2 a7a4",
    moveCount = 4,
    rows = openingLineRows.mapIndexed { rowIndex, row ->
        row.copy(isSelected = rowIndex == openingLineRows.lastIndex)
    },
    selectedRow = openingLineRows.lastIndex
)

internal val PreviewStartSelectedViewState = AnalysisViewState(
    currentMoves = "",
    moveCount = 0,
    rows = openingLineRows.mapIndexed { rowIndex, row ->
        row.copy(isSelected = rowIndex == 0)
    },
    selectedRow = 0
)

internal val PreviewEvaluatedLineViewState = AnalysisViewState(
    currentMoves = "b2a2- b8b5= h2g2+ a7a4- g1f1= d8d5+ c1c5- h8h5=",
    moveCount = 8,
    rows = listOf(
        startRow,
        MoveRowState(line("b2a2"), ". b2a2-", WHITE, isSelected = false),
        MoveRowState(line("b2a2", "b8b5"), ".. b8b5=", BLACK, isSelected = false),
        MoveRowState(line("b2a2", "b8b5", "h2g2"), "... h2g2+", WHITE, isSelected = false),
        MoveRowState(
            line("b2a2", "b8b5", "h2g2", "a7a4"),
            ".... a7a4-",
            BLACK,
            isSelected = false
        ),
        MoveRowState(
            line("b2a2", "b8b5", "h2g2", "a7a4", "g1f1"),
            "..... g1f1=",
            WHITE,
            isSelected = false
        ),
        MoveRowState(
            line("b2a2", "b8b5", "h2g2", "a7a4", "g1f1", "d8d5"),
            "...... d8d5+",
            BLACK,
            isSelected = false
        ),
        MoveRowState(
            line("b2a2", "b8b5", "h2g2", "a7a4", "g1f1", "d8d5", "c1c5"),
            "....... c1c5-",
            WHITE,
            isSelected = false
        ),
        MoveRowState(
            line("b2a2", "b8b5", "h2g2", "a7a4", "g1f1", "d8d5", "c1c5", "h8h5"),
            "........ h8h5=",
            BLACK,
            isSelected = true
        )
    ),
    selectedRow = 8
)

internal val PreviewBranchedLineViewState = AnalysisViewState(
    currentMoves = "b2a2 d8d5",
    moveCount = 2,
    rows = openingLineRows + MoveRowState(
        line("b2a2", "d8d5"),
        ".. d8d5",
        BLACK,
        isSelected = true
    ),
    selectedRow = openingLineRows.size
)
