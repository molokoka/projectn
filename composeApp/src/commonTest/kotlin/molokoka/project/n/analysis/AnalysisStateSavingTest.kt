package molokoka.project.n.analysis

import androidx.lifecycle.SavedStateHandle
import molokoka.project.n.domain.AnalysisTree
import molokoka.project.n.domain.Move
import molokoka.project.n.move_evaluation.MoveEvaluation
import molokoka.project.n.ui.BoardOrientation
import molokoka.project.n.util.moveTreeDiagram
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

private fun AnalysisState.savedAndRestored(): AnalysisState {
    val handle = SavedStateHandle()
    saveTo(handle)

    return checkNotNull(handle.restoreAnalysisState())
}

class AnalysisStateSavingTest {

    @Test
    fun `restores nothing when no state was ever saved`() {
        assertNull(SavedStateHandle().restoreAnalysisState())
    }

    @Test
    fun `restores every branch of a tree that branches at both depths`() {
        val opening = Move.parse("b2b4")
        val variation = Move.parse("d2d4")
        val reply = Move.parse("a7a5")
        val alternativeReply = Move.parse("c7c5")
        val variationReply = Move.parse("e7e5")

        val state = AnalysisState(
            tree = AnalysisTree()
                .play(emptyList(), opening)
                .play(listOf(opening), reply)
                .play(listOf(opening), alternativeReply)
                .play(emptyList(), variation)
                .play(listOf(variation), variationReply)
        )

        assertEquals(
            """
            Start
            ├── b2b4
            │   ├── a7a5
            │   └── c7c5
            └── d2d4
                └── e7e5
            """.trimIndent(),
            state.savedAndRestored().tree.moveTreeDiagram()
        )
    }

    @Test
    fun `restores an evaluation onto the move it belongs to`() {
        val opening = Move.parse("b2b4")
        val reply = Move.parse("a7a5")
        val openingEvaluation = MoveEvaluation.WHITE_BETTER
        val replyEvaluation = MoveEvaluation.BLACK_BETTER

        val state = AnalysisState(
            tree = AnalysisTree()
                .play(emptyList(), opening)
                .play(listOf(opening), reply)
                .withEvaluations(
                    generation = 1,
                    evaluations = mapOf(
                        listOf(opening) to openingEvaluation,
                        listOf(opening, reply) to replyEvaluation
                    )
                )
        )

        assertEquals(
            """
            Start
            └── b2b4$openingEvaluation
                └── a7a5$replyEvaluation
            """.trimIndent(),
            state.savedAndRestored().tree.moveTreeDiagram()
        )
    }

    @Test
    fun `restores a move that was never evaluated without an evaluation`() {
        val evaluated = Move.parse("b2b4")
        val unevaluated = Move.parse("d2d4")
        val evaluation = MoveEvaluation.EQUAL

        val state = AnalysisState(
            tree = AnalysisTree()
                .play(emptyList(), evaluated)
                .play(emptyList(), unevaluated)
                .withEvaluations(1, mapOf(listOf(evaluated) to evaluation))
        )

        assertEquals(
            """
            Start
            ├── b2b4$evaluation
            └── d2d4
            """.trimIndent(),
            state.savedAndRestored().tree.moveTreeDiagram()
        )
    }

    @Test
    fun `restores the selected node`() {
        val opening = Move.parse("b2b4")
        val reply = Move.parse("a7a5")

        val state = AnalysisState(
            tree = AnalysisTree()
                .play(emptyList(), opening)
                .play(listOf(opening), reply),
            moves = listOf(opening, reply)
        )

        assertEquals(listOf(opening, reply), state.savedAndRestored().moves)
    }

    @Test
    fun `restores the start node as the selection when no move was selected`() {
        val state = AnalysisState(
            tree = AnalysisTree().play(emptyList(), Move.parse("b2b4"))
        )

        assertEquals(emptyList(), state.savedAndRestored().moves)
    }

    @Test
    fun `restores the board orientation it was flipped to`() {
        val state = AnalysisState(orientation = BoardOrientation.BLACK)

        assertEquals(BoardOrientation.BLACK, state.savedAndRestored().orientation)
    }

    @Test
    fun `restores a computer move that was pending as no longer pending`() {
        val state = AnalysisState(
            tree = AnalysisTree().play(emptyList(), Move.parse("b2b4")),
            isComputerMovePending = true
        )

        assertFalse(state.savedAndRestored().isComputerMovePending)
    }

    @Test
    fun `restores a move evaluation that was pending as no longer loading`() {
        val state = AnalysisState(
            tree = AnalysisTree().play(emptyList(), Move.parse("b2b4")),
            pendingEvaluationGeneration = 1
        )

        assertFalse(state.savedAndRestored().isMoveEvaluationPending)
    }

    @Test
    fun `restores an evaluated tree as no longer loading`() {
        val opening = Move.parse("b2b4")

        val state = AnalysisState(
            tree = AnalysisTree()
                .play(emptyList(), opening)
                .withEvaluations(1, mapOf(listOf(opening) to MoveEvaluation.WHITE_BETTER)),
            pendingEvaluationGeneration = 1
        )

        assertFalse(state.savedAndRestored().isMoveEvaluationPending)
    }
}
