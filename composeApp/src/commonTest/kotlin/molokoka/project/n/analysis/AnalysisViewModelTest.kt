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
import molokoka.project.n.computer.ComputerMoveSource
import molokoka.project.n.domain.AnalyticsTree
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.util.moveTreeDiagram
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private const val DELAY = 1_000L

private class FakeComputerMoveSource(private val move: Move?) : ComputerMoveSource {

    override suspend fun nextMove(tree: AnalyticsTree, path: List<Move>): Move? {
        delay(DELAY)

        return move
    }
}

private class QueuedComputerMoveSource(private val moves: List<Move>) : ComputerMoveSource {

    private var answered = 0

    override suspend fun nextMove(tree: AnalyticsTree, path: List<Move>): Move? {
        val move = moves.getOrNull(answered)
        answered++

        delay(DELAY)

        return move
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
    fun `plays no move before the delay elapses`() = runTest {
        val viewModel = viewModelPlaying(Move.parse("a1a4"))

        viewModel.onIntent(AnalysisIntent.RequestComputerMove)
        advanceTimeBy(DELAY - 1)

        assertEquals(AnalyticsTree(), viewModel.state.value.tree)
    }

    @Test
    fun `plays the move it was given once the delay elapses`() = runTest {
        val viewModel = viewModelPlaying(Move.parse("a1a4"))

        viewModel.onIntent(AnalysisIntent.RequestComputerMove)
        advanceUntilIdle()

        assertEquals(
            """
            Start
            └── a1a4
            """.trimIndent(),
            viewModel.state.value.tree.moveTreeDiagram()
        )
    }

    @Test
    fun `plays no move when the source has none to give`() = runTest {
        val viewModel = viewModelPlaying(null)

        viewModel.onIntent(AnalysisIntent.RequestComputerMove)
        advanceUntilIdle()

        assertEquals(AnalyticsTree(), viewModel.state.value.tree)
    }

    @Test
    fun `resetting stops a computer move already under way`() = runTest {
        val viewModel = viewModelPlaying(Move.parse("a1a4"))

        viewModel.onIntent(AnalysisIntent.RequestComputerMove)
        viewModel.onIntent(AnalysisIntent.Reset)
        advanceUntilIdle()

        assertEquals(AnalyticsTree(), viewModel.state.value.tree)
    }

    @Test
    fun `requesting another computer move discards the one under way`() = runTest {
        val viewModel = AnalysisViewModel(
            QueuedComputerMoveSource(listOf(Move.parse("a1a4"), Move.parse("c1c4")))
        )

        viewModel.onIntent(AnalysisIntent.RequestComputerMove)
        advanceTimeBy(DELAY / 2)
        viewModel.onIntent(AnalysisIntent.RequestComputerMove)
        advanceUntilIdle()

        assertEquals(
            """
            Start
            └── c1c4
            """.trimIndent(),
            viewModel.state.value.tree.moveTreeDiagram()
        )
    }

    private fun viewModelPlaying(move: Move?): AnalysisViewModel =
        AnalysisViewModel(FakeComputerMoveSource(move))
}
