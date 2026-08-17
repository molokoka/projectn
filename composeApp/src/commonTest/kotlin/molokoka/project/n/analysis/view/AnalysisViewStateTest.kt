package molokoka.project.n.analysis.view

import molokoka.project.n.domain.AnalysisTree
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.Side
import molokoka.project.n.domain.play
import molokoka.project.n.move_evaluation.MoveEvaluation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val StartRowLabel = "Start"

private fun AnalysisTree.playing(path: List<Move>, move: Move): AnalysisTree =
    add(path, move, Position.INITIAL.play(path + move))

class AnalysisViewStateTest {

    class TheCurrentMovesLine {

        @Test
        fun `the current moves line is empty before any move`() {
            val viewState = analysisViewState(
                tree = AnalysisTree(),
                moves = emptyList(),
                startLabel = StartRowLabel
            )

            assertEquals("", viewState.currentMoves)
        }

        @Test
        fun `the current moves line lists the moves of the visible line in order`() {
            val whiteMove = Move.parse("b2a2")
            val blackMove = Move.parse("b8b5")
            val tree = AnalysisTree()
                .playing(emptyList(), whiteMove)
                .playing(listOf(whiteMove), blackMove)

            val viewState = analysisViewState(
                tree = tree,
                moves = listOf(whiteMove, blackMove),
                startLabel = StartRowLabel
            )

            assertEquals("$whiteMove $blackMove", viewState.currentMoves)
        }

        @Test
        fun `the current moves line leaves out the lines that are not selected`() {
            val whiteMove = Move.parse("b2a2")
            val blackMove = Move.parse("b8b5")
            val blackAlternative = Move.parse("d8d5")
            val tree = AnalysisTree()
                .playing(emptyList(), whiteMove)
                .playing(listOf(whiteMove), blackMove)
                .playing(listOf(whiteMove), blackAlternative)

            val viewState = analysisViewState(
                tree = tree,
                moves = listOf(whiteMove, blackAlternative),
                startLabel = StartRowLabel
            )

            assertEquals("$whiteMove $blackAlternative", viewState.currentMoves)
        }

        @Test
        fun `the current moves line marks an evaluated move with its evaluation`() {
            val evaluationGeneration = 1
            val whiteMove = Move.parse("b2a2")
            val whiteMoveEvaluation = MoveEvaluation.WHITE_BETTER
            val tree = AnalysisTree()
                .playing(emptyList(), whiteMove)
                .applyEvaluations(
                    generation = evaluationGeneration,
                    evaluations = mapOf(listOf(whiteMove) to whiteMoveEvaluation)
                )

            val viewState = analysisViewState(
                tree = tree,
                moves = listOf(whiteMove),
                startLabel = StartRowLabel
            )

            assertEquals("$whiteMove$whiteMoveEvaluation", viewState.currentMoves)
        }

        @Test
        fun `the move count is the length of the visible line`() {
            val whiteMove = Move.parse("b2a2")
            val blackMove = Move.parse("b8b5")
            val tree = AnalysisTree()
                .playing(emptyList(), whiteMove)
                .playing(listOf(whiteMove), blackMove)

            val viewState = analysisViewState(
                tree = tree,
                moves = listOf(whiteMove, blackMove),
                startLabel = StartRowLabel
            )

            assertEquals(2, viewState.moveCount)
        }
    }

    class TheRows {

        @Test
        fun `an empty tree shows only the start row`() {
            val viewState = analysisViewState(
                tree = AnalysisTree(),
                moves = emptyList(),
                startLabel = StartRowLabel
            )

            assertEquals(listOf(StartRowLabel), viewState.rows.map { row -> row.label })
        }

        @Test
        fun `the first row is the start of the game`() {
            val whiteMove = Move.parse("b2a2")
            val tree = AnalysisTree().playing(emptyList(), whiteMove)

            val startRow = analysisViewState(
                tree = tree,
                moves = listOf(whiteMove),
                startLabel = StartRowLabel
            ).rows.first()

            assertEquals(StartRowLabel, startRow.label)
            assertEquals(emptyList(), startRow.path)
            assertNull(startRow.side)
        }

        @Test
        fun `each row is indented by the depth of its move`() {
            val whiteMove = Move.parse("b2a2")
            val blackMove = Move.parse("b8b5")
            val tree = AnalysisTree()
                .playing(emptyList(), whiteMove)
                .playing(listOf(whiteMove), blackMove)

            val viewState = analysisViewState(
                tree = tree,
                moves = listOf(whiteMove, blackMove),
                startLabel = StartRowLabel
            )

            assertEquals(
                listOf(StartRowLabel, ". $whiteMove", ".. $blackMove"),
                viewState.rows.map { row -> row.label }
            )
        }

        @Test
        fun `each row names the side that played its move`() {
            val whiteMove = Move.parse("b2a2")
            val blackMove = Move.parse("b8b5")
            val tree = AnalysisTree()
                .playing(emptyList(), whiteMove)
                .playing(listOf(whiteMove), blackMove)

            val viewState = analysisViewState(
                tree = tree,
                moves = listOf(whiteMove, blackMove),
                startLabel = StartRowLabel
            )

            assertEquals(
                listOf(null, Side.WHITE, Side.BLACK),
                viewState.rows.map { row -> row.side }
            )
        }

        @Test
        fun `a row marks its move with its evaluation`() {
            val evaluationGeneration = 1
            val whiteMove = Move.parse("b2a2")
            val whiteMoveEvaluation = MoveEvaluation.BLACK_BETTER
            val tree = AnalysisTree()
                .playing(emptyList(), whiteMove)
                .applyEvaluations(
                    generation = evaluationGeneration,
                    evaluations = mapOf(listOf(whiteMove) to whiteMoveEvaluation)
                )

            val viewState = analysisViewState(
                tree = tree,
                moves = listOf(whiteMove),
                startLabel = StartRowLabel
            )

            assertEquals(
                listOf(StartRowLabel, ". $whiteMove$whiteMoveEvaluation"),
                viewState.rows.map { row -> row.label }
            )
        }

        @Test
        fun `a row leads to the line it belongs to`() {
            val whiteMove = Move.parse("b2a2")
            val blackMove = Move.parse("b8b5")
            val blackAlternative = Move.parse("d8d5")
            val tree = AnalysisTree()
                .playing(emptyList(), whiteMove)
                .playing(listOf(whiteMove), blackMove)
                .playing(listOf(whiteMove), blackAlternative)

            val viewState = analysisViewState(
                tree = tree,
                moves = listOf(whiteMove),
                startLabel = StartRowLabel
            )

            assertEquals(
                listOf(
                    emptyList(),
                    listOf(whiteMove),
                    listOf(whiteMove, blackMove),
                    listOf(whiteMove, blackAlternative)
                ),
                viewState.rows.map { row -> row.path }
            )
        }
    }

    class TheSelection {

        @Test
        fun `the start row is selected before any move`() {
            val whiteMove = Move.parse("b2a2")
            val tree = AnalysisTree().playing(emptyList(), whiteMove)

            val viewState = analysisViewState(
                tree = tree,
                moves = emptyList(),
                startLabel = StartRowLabel
            )

            assertEquals(0, viewState.selectedRow)
            assertTrue(viewState.rows.first().isSelected)
        }

        @Test
        fun `the row of the visible line is selected`() {
            val whiteMove = Move.parse("b2a2")
            val blackMove = Move.parse("b8b5")
            val tree = AnalysisTree()
                .playing(emptyList(), whiteMove)
                .playing(listOf(whiteMove), blackMove)

            val viewState = analysisViewState(
                tree = tree,
                moves = listOf(whiteMove, blackMove),
                startLabel = StartRowLabel
            )

            assertEquals(2, viewState.selectedRow)
            assertEquals(
                listOf(listOf(whiteMove, blackMove)),
                viewState.rows.filter { row -> row.isSelected }.map { row -> row.path }
            )
        }

        @Test
        fun `no row is selected for a line the tree does not hold`() {
            val whiteMove = Move.parse("b2a2")
            val unplayedBlackMove = Move.parse("b8b5")
            val tree = AnalysisTree().playing(emptyList(), whiteMove)

            val viewState = analysisViewState(
                tree = tree,
                moves = listOf(whiteMove, unplayedBlackMove),
                startLabel = StartRowLabel
            )

            assertNull(viewState.selectedRow)
            assertEquals(
                emptyList(),
                viewState.rows.filter { row -> row.isSelected }.map { row -> row.path }
            )
        }
    }
}
