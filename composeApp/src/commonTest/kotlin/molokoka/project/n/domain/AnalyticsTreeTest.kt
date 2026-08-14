package molokoka.project.n.domain

import molokoka.project.n.domain.util.fromDiagram
import molokoka.project.n.domain.util.moveTreeDiagram
import molokoka.project.n.domain.util.positionDiagram
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AnalyticsTreeTest {

    // positionAt

    @Test
    fun `shows the initial position at the root`() {
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))

        assertEquals("Ra1 ra8", tree.positionAt(emptyList()).toString())
    }

    @Test
    fun `replays a path onto the initial position`() {
        val initialPosition = fromDiagram(
            """
            8 r . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 . . . . . . . .
            4 . . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 R . . . . . . .
              a b c d e f g h
            """
        )

        val tree = AnalyticsTree(initialPosition)
            .play(emptyList(), Move.parse("a1a4"))
            .play(listOf(Move.parse("a1a4")), Move.parse("a8a5"))

        val position = tree.positionAt(listOf(Move.parse("a1a4"), Move.parse("a8a5")))

        assertEquals(
            """
            8 . . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 r . . . . . . .
            4 R . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 . . . . . . . .
              a b c d e f g h
            """.trimIndent(),
            position.positionDiagram()
        )
    }

    @Test
    fun `has no position at a path that was never played`() {
        assertFailsWith<IllegalArgumentException> {
            AnalyticsTree(Position.parse("Ra1 ra8"))
                .positionAt(listOf(Move.parse("a1a4")))
        }
    }

    // play

    @Test
    fun `adds a node at the root`() {
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))

        assertEquals(
            """
            Start
            └── a1a4
            """.trimIndent(),
            tree.moveTreeDiagram()
        )
    }

    @Test
    fun `adds a node at depth`() {
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))
            .play(listOf(Move.parse("a1a4")), Move.parse("a8a5"))

        assertEquals(
            """
            Start
            └── a1a4
                └── a8a5
            """.trimIndent(),
            tree.moveTreeDiagram()
        )
    }

    /**
     * A different move from the same node stands beside the first rather than
     * replacing it.
     */
    @Test
    fun `adds a sibling node for a different move`() {
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))
            .play(emptyList(), Move.parse("a1a3"))

        assertEquals(
            """
            Start
            ├── a1a4
            └── a1a3
            """.trimIndent(),
            tree.moveTreeDiagram()
        )
    }

    @Test
    fun `adds a sibling node at depth`() {
        val path = listOf(Move.parse("a1a4"))
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))
            .play(path, Move.parse("a8a5"))
            .play(path, Move.parse("a8a6"))

        assertEquals(
            """
            Start
            └── a1a4
                ├── a8a5
                └── a8a6
            """.trimIndent(),
            tree.moveTreeDiagram()
        )
    }

    /**
     * Playing a1a4 again from the root must reuse the existing node, keeping the
     * a8a5 branch beneath it, rather than adding a second a1a4 or replacing it.
     */
    @Test
    fun `reuses a node that already carries the move`() {
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))
            .play(listOf(Move.parse("a1a4")), Move.parse("a8a5"))
            .play(emptyList(), Move.parse("a1a4"))

        assertEquals(
            """
            Start
            └── a1a4
                └── a8a5
            """.trimIndent(),
            tree.moveTreeDiagram()
        )
    }

    /**
     * The rook standing on a4 (after two moves in) must be movable, even though a4 is
     * empty in the initial position.
     */
    @Test
    fun `plays a move from a node two levels deep`() {
        val path = listOf(Move.parse("a1a4"), Move.parse("a8a5"))
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))
            .play(listOf(Move.parse("a1a4")), Move.parse("a8a5"))

        val played = tree.play(path, Move.parse("a4b4"))

        assertEquals(
            """
            Start
            └── a1a4
                └── a8a5
                    └── a4b4
            """.trimIndent(),
            played.moveTreeDiagram()
        )
        assertEquals("Rb4 ra5", played.positionAt(path + Move.parse("a4b4")).toString())
    }

    @Test
    fun `refuses to play at a path that was never played`() {
        assertFailsWith<IllegalArgumentException> {
            AnalyticsTree(Position.parse("Ra1 ra8"))
                .play(listOf(Move.parse("a1a4")), Move.parse("a8a5"))
        }
    }

    /**
     * The root is depth zero, so white cannot move and black's rook.
     */
    @Test
    fun `rejects initial opposite piece move`() {
        assertFailsWith<IllegalArgumentException> {
            AnalyticsTree(Position.parse("Ra1 ra8"))
                .play(emptyList(), Move.parse("a8a5"))
        }
    }

    /**
     * One move deep it is black's turn, so white's rook cannot move again.
     */
    @Test
    fun `rejects an opposite piece move`() {
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))

        assertFailsWith<IllegalArgumentException> {
            tree.play(listOf(Move.parse("a1a4")), Move.parse("a4a6"))
        }
    }

    // paths

    /**
     * Depth first, so a branch is listed under the move it follows.
     */
    @Test
    fun `lists the path to every node depth first`() {
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))
            .play(listOf(Move.parse("a1a4")), Move.parse("a8a5"))
            .play(emptyList(), Move.parse("a1a3"))

        assertEquals(
            """
            Start
            ├── a1a4
            │   └── a8a5
            └── a1a3
            """.trimIndent(),
            tree.moveTreeDiagram()
        )
        assertEquals(
            listOf(
                listOf(Move.parse("a1a4")),
                listOf(Move.parse("a1a4"), Move.parse("a8a5")),
                listOf(Move.parse("a1a3"))
            ),
            tree.paths()
        )
    }

    @Test
    fun `lists no paths for an empty tree`() {
        assertEquals(emptyList(), AnalyticsTree(Position.parse("Ra1 ra8")).paths())
    }

    // contains

    @Test
    fun `contains the root of an empty tree`() {
        assertTrue(AnalyticsTree(Position.parse("Ra1 ra8")).contains(emptyList()))
    }

    @Test
    fun `contains a path that was played`() {
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))

        assertTrue(tree.contains(listOf(Move.parse("a1a4"))))
    }

    @Test
    fun `contains a path two levels deep`() {
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))
            .play(listOf(Move.parse("a1a4")), Move.parse("a8a5"))

        assertTrue(tree.contains(listOf(Move.parse("a1a4"), Move.parse("a8a5"))))
    }

    @Test
    fun `does not contain a path that was never played`() {
        assertFalse(AnalyticsTree(Position.parse("Ra1 ra8")).contains(listOf(Move.parse("a1a4"))))
    }

    /**
     * The first move is in the tree and the second is not, so the path as a
     * whole is absent.
     */
    @Test
    fun `does not contain a path whose last move was never played`() {
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))

        assertFalse(tree.contains(listOf(Move.parse("a1a4"), Move.parse("a8a5"))))
    }
}
