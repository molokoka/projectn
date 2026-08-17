@file:Suppress("ConvertLongToDuration")

package molokoka.project.n.analysis

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import molokoka.project.n.move_evaluation.MoveEvaluationSource
import molokoka.project.n.computer_move.ComputerMoveSource
import molokoka.project.n.domain.AnalysisTree
import molokoka.project.n.domain.Coordinates
import molokoka.project.n.move_evaluation.MoveEvaluation
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.Side
import molokoka.project.n.util.moveTreeDiagram
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val DELAY = 1_000L
private const val LONG_DELAY = 3_000L

private class QueuedComputerMoveSource(private val moves: List<Move?>) : ComputerMoveSource {

    private var answered = 0

    override suspend fun nextMove(position: Position, side: Side): Move? {
        val move = moves[answered]
        answered++

        delay(DELAY)

        return move
    }
}

private class QueuedMoveEvaluationSource(
    private val answers: List<Pair<Long, MoveEvaluation>>
) : MoveEvaluationSource {

    private var answered = 0

    override suspend fun evaluate(snapshotTree: AnalysisTree): Map<List<Move>, MoveEvaluation> {
        val (answerDelay, evaluation) = answers[answered]
        answered++

        delay(answerDelay)

        return snapshotTree.paths().associateWith { evaluation }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class AnalysisViewModelTest {

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `requesting computer move shows loading`() = runTest {
        val computerMove = Move.parse("a1a4")
        val viewModel = viewModelPlaying(computerMove)

        viewModel.onIntent(AnalysisIntent.RequestComputerMove)

        assertTrue(viewModel.state.value.isComputerMovePending)
    }

    @Test
    fun `plays the computer move when it arrives`() = runTest {
        val computerMove = Move.parse("a1a4")
        val viewModel = viewModelPlaying(computerMove)

        viewModel.onIntent(AnalysisIntent.RequestComputerMove)
        advanceUntilIdle()

        assertEquals(
            """
            Start
            └── $computerMove
            """.trimIndent(),
            viewModel.state.value.tree.moveTreeDiagram()
        )
        assertFalse(viewModel.state.value.isComputerMovePending)
    }

    @Test
    fun `hides the loading when no computer move is found`() = runTest {
        val noMoveFound: Move? = null
        val viewModel = viewModelPlaying(noMoveFound)

        viewModel.onIntent(AnalysisIntent.RequestComputerMove)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isComputerMovePending)
    }

    @Test
    fun `resetting stops a computer move already under way`() = runTest {
        val computerMove = Move.parse("a1a4")
        val viewModel = viewModelPlaying(computerMove)

        viewModel.onIntent(AnalysisIntent.RequestComputerMove)
        viewModel.onIntent(AnalysisIntent.Reset)
        advanceUntilIdle()

        assertEquals(AnalysisTree(), viewModel.state.value.tree)
        assertFalse(viewModel.state.value.isComputerMovePending)
    }

    @Test
    fun `requesting another computer move discards the one under way`() = runTest {
        val discardedMove = Move.parse("a1a4")
        val playedMove = Move.parse("c1c4")
        val viewModel = AnalysisViewModel(
            QueuedComputerMoveSource(listOf(discardedMove, playedMove)),
            QueuedMoveEvaluationSource(emptyList()),
            SavedStateHandle()
        )

        viewModel.onIntent(AnalysisIntent.RequestComputerMove)
        advanceTimeBy(DELAY / 2)
        viewModel.onIntent(AnalysisIntent.RequestComputerMove)
        advanceUntilIdle()

        assertEquals(
            """
            Start
            └── $playedMove
            """.trimIndent(),
            viewModel.state.value.tree.moveTreeDiagram()
        )
        assertFalse(viewModel.state.value.isComputerMovePending)
    }

    @Test
    fun `changing visible position and going back stops a computer move already under way`() = runTest {
        val playedMove = Move.parse("b2b4")
        val computerMove = Move.parse("a7a5")
        val viewModel = viewModelWithMovePlayed(computerMove = computerMove)

        viewModel.onIntent(AnalysisIntent.RequestComputerMove)
        viewModel.onIntent(AnalysisIntent.SelectNode(emptyList()))
        viewModel.onIntent(AnalysisIntent.SelectNode(listOf(playedMove)))
        advanceUntilIdle()

        assertEquals(
            """
            Start
            └── $playedMove
            """.trimIndent(),
            viewModel.state.value.tree.moveTreeDiagram()
        )
        assertFalse(viewModel.state.value.isComputerMovePending)
    }

    // move evaluation

    @Test
    fun `requesting moves evaluation shows loading`() = runTest {
        val viewModel = viewModelWithMovePlayed()

        viewModel.onIntent(AnalysisIntent.RequestMovesEvaluation)

        assertTrue(viewModel.state.value.isMoveEvaluationPending)
    }

    @Test
    fun `applies the evaluations after the delay`() = runTest {
        val answer = MoveEvaluation.WHITE_BETTER
        val viewModel = viewModelWithMovePlayed(QueuedMoveEvaluationSource(listOf(DELAY to answer)))

        viewModel.onIntent(AnalysisIntent.RequestMovesEvaluation)
        advanceUntilIdle()

        assertEquals(
            """
            Start
            └── b2b4$answer
            """.trimIndent(),
            viewModel.state.value.tree.moveTreeDiagram()
        )
        assertFalse(viewModel.state.value.isMoveEvaluationPending)
    }

    @Test
    fun `shows the results of an older request while a newer one is still running`() = runTest {
        val olderAnswer = MoveEvaluation.WHITE_BETTER
        val newerAnswer = MoveEvaluation.BLACK_BETTER
        val viewModel = viewModelWithMovePlayed(
            QueuedMoveEvaluationSource(
                listOf(
                    DELAY to olderAnswer,
                    LONG_DELAY to newerAnswer
                )
            )
        )

        viewModel.onIntent(AnalysisIntent.RequestMovesEvaluation)
        viewModel.onIntent(AnalysisIntent.RequestMovesEvaluation)
        advanceTimeBy(DELAY + 1)

        assertEquals(
            """
            Start
            └── b2b4$olderAnswer
            """.trimIndent(),
            viewModel.state.value.tree.moveTreeDiagram()
        )
        assertTrue(viewModel.state.value.isMoveEvaluationPending)
    }

    @Test
    fun `the newer results take precedence once the newer request completes`() = runTest {
        val olderAnswer = MoveEvaluation.WHITE_BETTER
        val newerAnswer = MoveEvaluation.BLACK_BETTER
        val viewModel = viewModelWithMovePlayed(
            QueuedMoveEvaluationSource(
                listOf(
                    DELAY to olderAnswer,
                    LONG_DELAY to newerAnswer
                )
            )
        )

        viewModel.onIntent(AnalysisIntent.RequestMovesEvaluation)
        viewModel.onIntent(AnalysisIntent.RequestMovesEvaluation)
        advanceUntilIdle()

        assertEquals(
            """
            Start
            └── b2b4$newerAnswer
            """.trimIndent(),
            viewModel.state.value.tree.moveTreeDiagram()
        )
        assertFalse(viewModel.state.value.isMoveEvaluationPending)
    }

    @Test
    fun `an older request may not overwrite the newer results`() = runTest {
        val olderAnswer = MoveEvaluation.WHITE_BETTER
        val newerAnswer = MoveEvaluation.BLACK_BETTER
        val viewModel = viewModelWithMovePlayed(
            QueuedMoveEvaluationSource(
                listOf(
                    LONG_DELAY to olderAnswer,
                    DELAY to newerAnswer
                )
            )
        )

        viewModel.onIntent(AnalysisIntent.RequestMovesEvaluation)
        viewModel.onIntent(AnalysisIntent.RequestMovesEvaluation)
        advanceUntilIdle()

        assertEquals(
            """
            Start
            └── b2b4$newerAnswer
            """.trimIndent(),
            viewModel.state.value.tree.moveTreeDiagram()
        )
        assertFalse(viewModel.state.value.isMoveEvaluationPending)
    }

    @Test
    fun `selecting another position leaves the move evaluation running`() = runTest {
        val answer = MoveEvaluation.WHITE_BETTER
        val viewModel = viewModelWithMovePlayed(QueuedMoveEvaluationSource(listOf(DELAY to answer)))

        viewModel.onIntent(AnalysisIntent.RequestMovesEvaluation)
        viewModel.onIntent(AnalysisIntent.SelectNode(emptyList()))
        advanceUntilIdle()

        assertEquals(
            """
            Start
            └── b2b4$answer
            """.trimIndent(),
            viewModel.state.value.tree.moveTreeDiagram()
        )
        assertFalse(viewModel.state.value.isMoveEvaluationPending)
    }

    @Test
    fun `resetting stops a move evaluation already under way`() = runTest {
        val viewModel = viewModelWithMovePlayed()

        viewModel.onIntent(AnalysisIntent.RequestMovesEvaluation)
        viewModel.onIntent(AnalysisIntent.Reset)
        advanceUntilIdle()

        assertEquals(AnalysisState(), viewModel.state.value)
    }

    @Test
    fun `a move played during a move evaluation is not in its snapshot`() = runTest {
        val answer = MoveEvaluation.WHITE_BETTER
        val computerMove = Move.parse("a7a5")
        val viewModel = viewModelWithMovePlayed(
            QueuedMoveEvaluationSource(listOf(DELAY to answer)),
            computerMove
        )

        viewModel.onIntent(AnalysisIntent.RequestMovesEvaluation)
        viewModel.onIntent(AnalysisIntent.RequestComputerMove)
        advanceUntilIdle()

        assertEquals(
            """
            Start
            └── b2b4$answer
                └── $computerMove
            """.trimIndent(),
            viewModel.state.value.tree.moveTreeDiagram()
        )
        assertFalse(viewModel.state.value.isMoveEvaluationPending)
    }

    @Test
    fun `a view model rebuilt on the same saved state comes up on the played moves`() = runTest {
        val opening = Move.parse("b2b4")
        val handle = SavedStateHandle()

        AnalysisViewModel(
            QueuedComputerMoveSource(emptyList()),
            QueuedMoveEvaluationSource(emptyList()),
            handle
        ).apply {
            onIntent(AnalysisIntent.OnSquareClick(Coordinates.parse("b2")))
            onIntent(AnalysisIntent.OnSquareClick(Coordinates.parse("b4")))
        }

        val rebuilt = AnalysisViewModel(
            QueuedComputerMoveSource(emptyList()),
            QueuedMoveEvaluationSource(emptyList()),
            handle
        )

        assertEquals(
            """
            Start
            └── $opening
            """.trimIndent(),
            rebuilt.state.value.tree.moveTreeDiagram()
        )
        assertEquals(listOf(opening), rebuilt.state.value.moves)
        assertFalse(rebuilt.state.value.isComputerMovePending)
        assertFalse(rebuilt.state.value.isMoveEvaluationPending)
    }

    private fun viewModelPlaying(computerMove: Move?): AnalysisViewModel =
        AnalysisViewModel(
            QueuedComputerMoveSource(listOf(computerMove)),
            QueuedMoveEvaluationSource(emptyList()),
            SavedStateHandle()
        )

    private fun viewModelWithMovePlayed(
        moveEvaluationSource: MoveEvaluationSource =
            QueuedMoveEvaluationSource(listOf(DELAY to MoveEvaluation.WHITE_BETTER)),
        computerMove: Move? = null
    ): AnalysisViewModel =
        AnalysisViewModel(
            QueuedComputerMoveSource(listOf(computerMove)),
            moveEvaluationSource,
            SavedStateHandle()
        ).apply {
            onIntent(AnalysisIntent.OnSquareClick(Coordinates.parse("b2")))
            onIntent(AnalysisIntent.OnSquareClick(Coordinates.parse("b4")))
        }
}
