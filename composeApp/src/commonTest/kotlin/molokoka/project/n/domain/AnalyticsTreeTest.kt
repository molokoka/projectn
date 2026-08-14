package molokoka.project.n.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AnalyticsTreeTest {

    // positionAt

    /**
     * ```
     * 8 r . . . . . . .
     * 7 . . . . . . . .
     * 6 . . . . . . . .
     * 5 . . . . . . . .
     * 4 . . . . . . . .   the root shows the initial position, no moves played
     * 3 . . . . . . . .
     * 2 . . . . . . . .
     * 1 R . . . . . . .
     *   a b c d e f g h
     * ```
     */
    @Test
    fun `shows the initial position at the root`() {
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))

        assertEquals("Ra1 ra8", tree.positionAt(emptyList()).toString())
    }

    /**
     * ```
     * 8 r . . . . . . .        8 . . . . . . . .
     * 7 . . . . . . . .        7 . . . . . . . .
     * 6 . . . . . . . .        6 . . . . . . . .
     * 5 . . . . . . . .  a1a4  5 r . . . . . . .
     * 4 . . . . . . . .  a8a5  4 R . . . . . . .
     * 3 . . . . . . . .        3 . . . . . . . .
     * 2 . . . . . . . .        2 . . . . . . . .
     * 1 R . . . . . . .        1 . . . . . . . .
     *   a b c d e f g h          a b c d e f g h
     * ```
     */
    @Test
    fun `replays a path onto the initial position`() {
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))
            .play(listOf(Move.parse("a1a4")), Move.parse("a8a5"))

        val position = tree.positionAt(listOf(Move.parse("a1a4"), Move.parse("a8a5")))

        assertEquals("Ra4 ra5", position.toString())
    }

    @Test
    fun `has no position at a path that was never played`() {
        assertFailsWith<IllegalArgumentException> {
            AnalyticsTree(Position.parse("Ra1 ra8"))
                .positionAt(listOf(Move.parse("a1a4")))
        }
    }

    // play

    /**
     * ```
     * Start
     * └── a1a4
     * ```
     */
    @Test
    fun `adds a node at the root`() {
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))

        assertEquals(listOf(MoveNode(Move.parse("a1a4"))), tree.nodes)
    }

    /**
     * ```
     * Start
     * └── a1a4
     *     └── a8a5
     * ```
     */
    @Test
    fun `adds a node at depth`() {
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))
            .play(listOf(Move.parse("a1a4")), Move.parse("a8a5"))

        assertEquals(
            listOf(
                MoveNode(
                    move = Move.parse("a1a4"),
                    nodes = listOf(MoveNode(Move.parse("a8a5")))
                )
            ),
            tree.nodes
        )
    }

    /**
     * A different move from the same node stands beside the first rather than
     * replacing it.
     *
     * ```
     * Start
     * ├── a1a4
     * └── a1a3
     * ```
     */
    @Test
    fun `adds a sibling node for a different move`() {
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))
            .play(emptyList(), Move.parse("a1a3"))

        assertEquals(
            listOf(
                MoveNode(Move.parse("a1a4")),
                MoveNode(Move.parse("a1a3"))
            ),
            tree.nodes
        )
    }

    /**
     * ```
     * Start
     * └── a1a4
     *     ├── a8a5
     *     └── a8a6
     * ```
     */
    @Test
    fun `adds a sibling node at depth`() {
        val path = listOf(Move.parse("a1a4"))
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))
            .play(path, Move.parse("a8a5"))
            .play(path, Move.parse("a8a6"))

        assertEquals(
            listOf(
                MoveNode(
                    move = Move.parse("a1a4"),
                    nodes = listOf(
                        MoveNode(Move.parse("a8a5")),
                        MoveNode(Move.parse("a8a6"))
                    )
                )
            ),
            tree.nodes
        )
    }

    /**
     * Playing a1a4 again from the root must reuse the existing node, keeping the
     * a8a5 branch beneath it, rather than adding a second a1a4 or replacing it.
     *
     * ```
     * Start
     * └── a1a4
     *     └── a8a5
     * ```
     */
    @Test
    fun `reuses a node that already carries the move`() {
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))
            .play(listOf(Move.parse("a1a4")), Move.parse("a8a5"))
            .play(emptyList(), Move.parse("a1a4"))

        assertEquals(
            listOf(
                MoveNode(
                    move = Move.parse("a1a4"),
                    nodes = listOf(MoveNode(Move.parse("a8a5")))
                )
            ),
            tree.nodes
        )
    }

    /**
     * The rook standing on a4 (after two moves in) must be movable, even though a4 is
     * empty in the initial position.
     *
     * ```
     * Start
     * └── a1a4
     *     └── a8a5
     *         └── a4a6
     * ```
     */
    @Test
    fun `plays a move from a node two levels deep`() {
        val path = listOf(Move.parse("a1a4"), Move.parse("a8a5"))
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))
            .play(listOf(Move.parse("a1a4")), Move.parse("a8a5"))

        val played = tree.play(path, Move.parse("a4a6"))

        assertEquals("ra5 Ra6", played.positionAt(path + Move.parse("a4a6")).toString())
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
     *
     * ```
     * Start
     * └── a8a5  rejected
     * ```
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
     *
     * ```
     * Start
     * └── a1a4
     *     └── a4a6  rejected
     * ```
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
     *
     * ```
     * Start
     * ├── a1a4
     * │   └── a8a5
     * └── a1a3
     * ```
     */
    @Test
    fun `lists the path to every node depth first`() {
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))
            .play(listOf(Move.parse("a1a4")), Move.parse("a8a5"))
            .play(emptyList(), Move.parse("a1a3"))

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

    /**
     * ```
     * Start
     * └── a1a4
     *     └── a8a5   <- contained
     * ```
     */
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
     *
     * ```
     * Start
     * └── a1a4
     * ```
     */
    @Test
    fun `does not contain a path whose last move was never played`() {
        val tree = AnalyticsTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))

        assertFalse(tree.contains(listOf(Move.parse("a1a4"), Move.parse("a8a5"))))
    }
}
