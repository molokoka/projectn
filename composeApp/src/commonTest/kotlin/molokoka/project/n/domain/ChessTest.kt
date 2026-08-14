package molokoka.project.n.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ChessTest {

    /**
     * ```
     * 8 . . . . . . . .        8 . . . . . . . .
     * 7 . . . . . . . .        7 . . . . . . . .
     * 6 . . . . . . . .        6 . . . . . . . .
     * 5 . . . . . . . .        5 . . . . . . . .
     * 4 . . . . . . . .        4 R . . . . . . .
     * 3 . . . . . . . .  a1a4  3 . . . . . . . .
     * 2 . . . . . . . .        2 . . . . . . . .
     * 1 R . . . . . . .        1 . . . . . . . .
     *   a b c d e f g h          a b c d e f g h
     * ```
     */
    @Test
    fun `moves a piece to an empty square`() {
        assertEquals(
            "Ra4",
            Position.parse("Ra1")
                .play(Move.parse("a1a4"), Side.WHITE)
                .toString()
        )
    }

    /**
     * ```
     * 8 . . . . . . . .        8 . . . . . . . .
     * 7 . . . . . . . .        7 . . . . . . . .
     * 6 . . . . . . . .        6 . . . . . . . .
     * 5 . . . . . . . .        5 . . . . . . . .
     * 4 r . . . . . . .        4 R . . . . . . .
     * 3 . . . . . . . .  a1a4  3 . . . . . . . .
     * 2 . . . . . . . .        2 . . . . . . . .
     * 1 R . . . . . . .        1 . . . . . . . .
     *   a b c d e f g h          a b c d e f g h
     * ```
     */
    @Test
    fun `captures an opposing piece`() {
        assertEquals(
            "Ra4",
            Position.parse("Ra1 ra4")
                .play(Move.parse("a1a4"), Side.WHITE)
                .toString()
        )
    }

    /**
     * ```
     * 8 . . . . . . . .
     * 7 . . . . . . . .
     * 6 . . . . . . . .
     * 5 . . . . . . . .
     * 4 R . . . . . . .
     * 3 . . . . . . . .   a1a4 is rejected: a4 holds a rook of the same side
     * 2 . . . . . . . .
     * 1 R . . . . . . .
     *   a b c d e f g h
     * ```
     */
    @Test
    fun `rejects landing on a piece of the same side`() {
        assertFailsWith<IllegalArgumentException> {
            Position.parse("Ra1 Ra4")
                .play(Move.parse("a1a4"), Side.WHITE)
        }
    }

    /**
     * ```
     * 8 . . . . . . . .
     * 7 . . . . . . . .
     * 6 . . . . . . . .
     * 5 . . . . . . . .
     * 4 . . . . . . . .
     * 3 . . . . . . . .   a1a1 is rejected: a1 holds the moving piece itself
     * 2 . . . . . . . .
     * 1 R . . . . . . .
     *   a b c d e f g h
     * ```
     */
    @Test
    fun `rejects a move to its own square`() {
        assertFailsWith<IllegalArgumentException> {
            Position.parse("Ra1")
                .play(Move.parse("a1a1"), Side.WHITE)
        }
    }

    /**
     * ```
     * 8 . . . . . . . .
     * 7 . . . . . . . .
     * 6 . . . . . . . .
     * 5 . . . . . . . .
     * 4 . . . . . . . .
     * 3 . . . . . . . .   b1b4 is rejected: b1 is empty, there is nothing to move
     * 2 . . . . . . . .
     * 1 R . . . . . . .
     *   a b c d e f g h
     * ```
     */
    @Test
    fun `rejects a move from an empty square`() {
        assertFailsWith<IllegalArgumentException> {
            Position.parse("Ra1")
                .play(Move.parse("b1b4"), Side.WHITE)
        }
    }

    @Test
    fun `rejects a move to a file beyond the board`() {
        assertFailsWith<IllegalArgumentException> {
            Position.parse("Ra1")
                .play(Move.parse("a1i1"), Side.WHITE)
        }
    }

    @Test
    fun `rejects a move to a rank beyond the board`() {
        assertFailsWith<IllegalArgumentException> {
            Position.parse("Ra1")
                .play(Move.parse("a1a9"), Side.WHITE)
        }
    }

    /**
     * ```
     * 8 r . . . . . . .
     * 7 . . . . . . . .
     * 6 . . . . . . . .
     * 5 . . . . . . . .
     * 4 . . . . . . . .   a8a5 is rejected: a8 is black and white is to move
     * 3 . . . . . . . .
     * 2 . . . . . . . .
     * 1 R . . . . . . .
     *   a b c d e f g h
     * ```
     */
    @Test
    fun `rejects moving an opposing piece`() {
        assertFailsWith<IllegalArgumentException> {
            Position.parse("Ra1 ra8")
                .play(Move.parse("a8a5"), Side.WHITE)
        }
    }

    /**
     * ```
     * 8 r . . . . . . .        8 . . . . . . . .
     * 7 . . . . . . . .        7 . . . . . . . .
     * 6 . . . . . . . .        6 . . . . . . . .
     * 5 . . . . . . . .        5 r . . . . . . .
     * 4 . . . . . . . .  a8a5  4 . . . . . . . .
     * 3 . . . . . . . .        3 . . . . . . . .
     * 2 . . . . . . . .        2 . . . . . . . .
     * 1 . . . . . . . .        1 . . . . . . . .
     *   a b c d e f g h          a b c d e f g h
     * ```
     */
    @Test
    fun `moves a piece of the side to move`() {
        assertEquals(
            "ra5",
            Position.parse("ra8")
                .play(Move.parse("a8a5"), Side.BLACK)
                .toString()
        )
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
    fun `plays a line of moves alternating sides`() {
        assertEquals(
            "Ra4 ra5",
            Position.parse("Ra1 ra8")
                .play(listOf(Move.parse("a1a4"), Move.parse("a8a5")))
                .toString()
        )
    }

    @Test
    fun `plays no moves at all`() {
        assertEquals(
            "Ra1",
            Position.parse("Ra1")
                .play(emptyList())
                .toString()
        )
    }

    /**
     * ```
     * 8 . . . . . . . .
     * 7 . . . . . . . .
     * 6 . . . . . . . .
     * 5 . . . . . . . .
     * 4 . . . . . . . .   a1a4 then b1b4 is rejected: both are white, and the
     * 3 . . . . . . . .   second move of a line belongs to black
     * 2 . . . . . . . .
     * 1 R R . . . . . .
     *   a b c d e f g h
     * ```
     */
    @Test
    fun `rejects a line where one side moves twice`() {
        assertFailsWith<IllegalArgumentException> {
            Position.parse("Ra1 Rb1")
                .play(listOf(Move.parse("a1a4"), Move.parse("b1b5")))
        }
    }

    @Test
    fun `starts a line with white to move`() {
        assertEquals(Side.WHITE, sideToMove(0))
    }

    @Test
    fun `gives black the move after one move`() {
        assertEquals(Side.BLACK, sideToMove(1))
    }
}
