@file:Suppress("ConvertLongToDuration")

package molokoka.project.n.analysis

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import molokoka.project.n.analysis.move_evaluation.MoveEvaluationSource
import molokoka.project.n.computer.ComputerMoveSource
import molokoka.project.n.domain.Coordinates
import molokoka.project.n.analysis.move_evaluation.MoveEvaluation
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

private class FakeComputerMoveSource(private val move: Move?) : ComputerMoveSource {

    override suspend fun nextMove(position: Position, side: Side): Move? {
        delay(DELAY)

        return move
    }
}

private class QueuedComputerMoveSource(private val moves: List<Move>) : ComputerMoveSource {

    private var answered = 0

    override suspend fun nextMove(position: Position, side: Side): Move? {
        val move = moves.getOrNull(answered)
        answered++

        delay(DELAY)

        return move
    }
}

private class FakeMoveEvaluationSource(
    private val moveEvaluation: MoveEvaluation = MoveEvaluation.WHITE_BETTER
) : MoveEvaluationSource {

    override suspend fun evaluate(
        initialPosition: Position,
        nodes: List<MoveNode>
    ): Map<List<Move>, MoveEvaluation> {
        delay(DELAY)

        return nodes.paths().associateWith { moveEvaluation }
    }
}

private class QueuedMoveEvaluationSource(
    private val answers: List<Pair<Long, MoveEvaluation>>
) : MoveEvaluationSource {

    private var answered = 0

    override suspend fun evaluate(
        initialPosition: Position,
        nodes: List<MoveNode>
    ): Map<List<Move>, MoveEvaluation> {
        val (answerDelay, evaluation) = answers[answered]
        answered++

        delay(answerDelay)

        return nodes.paths().associateWith { evaluation }
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
            FakeMoveEvaluationSource()
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
        val viewModel = viewModelWithMovePlayed(FakeMoveEvaluationSource(answer))

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
        val viewModel = viewModelWithMovePlayed(FakeMoveEvaluationSource(answer))

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
        val viewModel = viewModelWithMovePlayed(FakeMoveEvaluationSource(answer), computerMove)

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

    private fun viewModelPlaying(computerMove: Move?): AnalysisViewModel =
        AnalysisViewModel(FakeComputerMoveSource(computerMove), FakeMoveEvaluationSource())

    private fun viewModelWithMovePlayed(
        moveEvaluationSource: MoveEvaluationSource = FakeMoveEvaluationSource(),
        computerMove: Move? = null
    ): AnalysisViewModel =
        AnalysisViewModel(FakeComputerMoveSource(computerMove), moveEvaluationSource).apply {
            onIntent(AnalysisIntent.OnSquareClick(Coordinates.parse("b2")))
            onIntent(AnalysisIntent.OnSquareClick(Coordinates.parse("b4")))
        }
}
