package molokoka.project.n.analysis.move_evaluation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import molokoka.project.n.analysis.AnalysisTree
import molokoka.project.n.analysis.MoveNode
import molokoka.project.n.analysis.paths
import molokoka.project.n.domain.Move
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DelayedRandomMoveEvaluationSourceTest {

    private val source: MoveEvaluationSource = DelayedRandomMoveEvaluationSource()

    @Test
    fun `evaluates every move it is given`() = runTest {
        val nodes = branchingMoves()

        val evaluations = source.evaluate(nodes = nodes)

        nodes.paths().forEach { path -> assertNotNull(evaluations[path]) }
    }

    @Test
    fun `evaluates nothing when there are no moves`() = runTest {
        assertTrue(source.evaluate().isEmpty())
    }

    private fun branchingMoves(): List<MoveNode> =
        AnalysisTree()
            .play(emptyList(), Move.parse("b2b4"))
            .play(listOf(Move.parse("b2b4")), Move.parse("a7a5"))
            .play(listOf(Move.parse("b2b4"), Move.parse("a7a5")), Move.parse("d2d4"))
            .play(listOf(Move.parse("b2b4")), Move.parse("c7c5"))
            .play(emptyList(), Move.parse("d2d4"))
            .play(listOf(Move.parse("d2d4")), Move.parse("e7e5"))
            .nodes
}
