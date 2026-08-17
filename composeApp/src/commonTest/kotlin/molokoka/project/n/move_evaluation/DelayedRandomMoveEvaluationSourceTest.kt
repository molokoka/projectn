package molokoka.project.n.move_evaluation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import molokoka.project.n.domain.AnalysisTree
import molokoka.project.n.domain.MoveNode
import molokoka.project.n.domain.paths
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
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

    private fun branchingMoves(): List<MoveNode> {
        val mockPosition = Position.parse("Ra1")

        return AnalysisTree()
            .add(emptyList(), Move.parse("b2b4"), mockPosition)
            .add(listOf(Move.parse("b2b4")), Move.parse("a7a5"), mockPosition)
            .add(listOf(Move.parse("b2b4"), Move.parse("a7a5")), Move.parse("d2d4"), mockPosition)
            .add(listOf(Move.parse("b2b4")), Move.parse("c7c5"), mockPosition)
            .add(emptyList(), Move.parse("d2d4"), mockPosition)
            .add(listOf(Move.parse("d2d4")), Move.parse("e7e5"), mockPosition)
            .nodes
    }
}
