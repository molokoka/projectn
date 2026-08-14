package molokoka.project.n.computer

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import molokoka.project.n.domain.AnalyticsTree
import molokoka.project.n.domain.Move
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ScriptedComputerMoveSourceTest {

    private val source = ScriptedComputerMoveSource()

    @Test
    fun `picks a move for the start node`() = runTest {
        assertEquals(
            Move.parse("a1a4"),
            source.nextMove(AnalyticsTree(), emptyList())
        )
    }

    @Test
    fun `picks a reply for the side to move`() = runTest {
        val path = listOf(Move.parse("a1a4"))
        val tree = AnalyticsTree().play(emptyList(), path.single())

        assertEquals(Move.parse("b8b5"), source.nextMove(tree, path))
    }

    @Test
    fun `picks nothing once the line runs out`() = runTest {
        val path = listOf("a1a4", "b8b5", "c1c4", "d8d5").map(Move::parse)
        val tree = path.foldIndexed(AnalyticsTree()) { index, tree, move ->
            tree.play(path.take(index), move)
        }

        assertNull(source.nextMove(tree, path))
    }

    @Test
    fun `picks nothing when its move is not playable from the node`() = runTest {
        // the white queen on b7 blocks the b file, so b8b5 cannot be played
        val path = listOf(Move.parse("b2b7"))
        val tree = AnalyticsTree().play(emptyList(), path.single())

        assertNull(source.nextMove(tree, path))
    }

    @Test
    fun `takes at least a second to answer`() = runTest {
        source.nextMove(AnalyticsTree(), emptyList())

        assertTrue(currentTime >= 1_000)
    }
}
