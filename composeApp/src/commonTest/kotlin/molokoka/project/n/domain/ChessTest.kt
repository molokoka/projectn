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

    // Rook movement. Extract with the rook rules when they leave play().

    /**
     * ```
     * 8 . . . . . . . .        8 . . . . . . . .
     * 7 . . . . . . . .        7 . . . . . . . .
     * 6 . . . . . . . .        6 . . . . . . . .
     * 5 . . . . . . . .        5 . . . . . . . .
     * 4 . . . . . . . .        4 . . . . . . . .
     * 3 . . . . . . . .  a1d1  3 . . . . . . . .
     * 2 . . . . . . . .        2 . . . . . . . .
     * 1 R . . . . . . .        1 . . . R . . . .
     *   a b c d e f g h          a b c d e f g h
     * ```
     */
    @Test
    fun `moves a rook along a rank`() {
        assertEquals(
            "Rd1",
            Position.parse("Ra1")
                .play(Move.parse("a1d1"), Side.WHITE)
                .toString()
        )
    }

    /**
     * ```
     * 8 . . . . . . . .        8 . . . . . . . .
     * 7 . . . . . . . .        7 . . . . . . . .
     * 6 . . . r . . . .        6 . . . . . . . .
     * 5 . . . . . . . .        5 . . . . . . . .
     * 4 . . . . . . . .  d6d2  4 . . . . . . . .
     * 3 . . . . . . . .        3 . . . . . . . .
     * 2 . . . . . . . .        2 . . . r . . . .
     * 1 . . . . . . . .        1 . . . . . . . .
     *   a b c d e f g h          a b c d e f g h
     * ```
     */
    @Test
    fun `moves a rook along a file`() {
        assertEquals(
            "rd2",
            Position.parse("rd6")
                .play(Move.parse("d6d2"), Side.BLACK)
                .toString()
        )
    }

    /**
     * ```
     * 8 . . . . . . . .
     * 7 . . . . . . . .
     * 6 . . . . . . . .
     * 5 . . . . . . . .
     * 4 . . . . . . . .   a1d4 is rejected: a rook does not move diagonally
     * 3 . . . . . . . .
     * 2 . . . . . . . .
     * 1 R . . . . . . .
     *   a b c d e f g h
     * ```
     */
    @Test
    fun `rejects a diagonal rook move`() {
        assertFailsWith<IllegalArgumentException> {
            Position.parse("Ra1")
                .play(Move.parse("a1d4"), Side.WHITE)
        }
    }

    /**
     * ```
     * 8 . . . . . . . .
     * 7 . . . . . . . .
     * 6 . . . . . . . .
     * 5 . . . . . . . .
     * 4 . . . . . . . .
     * 3 . . . . . . . .   a1b3 is rejected: b3 lies off both the rank and the file
     * 2 . . . . . . . .
     * 1 R . . . . . . .
     *   a b c d e f g h
     * ```
     */
    @Test
    fun `rejects a rook move that is neither along a rank nor a file`() {
        assertFailsWith<IllegalArgumentException> {
            Position.parse("Ra1")
                .play(Move.parse("a1b3"), Side.WHITE)
        }
    }

    /**
     * ```
     * 8 . . . . . . . .
     * 7 . . . . . . . .
     * 6 . . . . . . . .
     * 5 . . . . . . . .
     * 4 . . . . . . . .   a1a5 is rejected: the rook on a3 blocks the way
     * 3 R . . . . . . .
     * 2 . . . . . . . .
     * 1 R . . . . . . .
     *   a b c d e f g h
     * ```
     */
    @Test
    fun `rejects a rook move past a piece of the same side along a file`() {
        assertFailsWith<IllegalArgumentException> {
            Position.parse("Ra1 Ra3")
                .play(Move.parse("a1a5"), Side.WHITE)
        }
    }

    /**
     * ```
     * 8 . . . . . . . .
     * 7 . . . . . . . .
     * 6 . . . . . . . .
     * 5 . . . . . . . .
     * 4 . . . . . . . .   a1a5 is rejected: the rook on a3 may be captured, not passed
     * 3 r . . . . . . .
     * 2 . . . . . . . .
     * 1 R . . . . . . .
     *   a b c d e f g h
     * ```
     */
    @Test
    fun `rejects a rook move past an opposing piece along a file`() {
        assertFailsWith<IllegalArgumentException> {
            Position.parse("Ra1 ra3")
                .play(Move.parse("a1a5"), Side.WHITE)
        }
    }

    /**
     * ```
     * 8 . . . . . . . .
     * 7 . . . . . . . .
     * 6 . . . . . . . .
     * 5 . . . . . . . .
     * 4 . . . . . . . .
     * 3 . . . . . . . .   a1e1 is rejected: the rook on c1 blocks the way
     * 2 . . . . . . . .
     * 1 R . R . . . . .
     *   a b c d e f g h
     * ```
     */
    @Test
    fun `rejects a rook move past a piece of the same side along a rank`() {
        assertFailsWith<IllegalArgumentException> {
            Position.parse("Ra1 Rc1")
                .play(Move.parse("a1e1"), Side.WHITE)
        }
    }

    /**
     * ```
     * 8 . . . . . . . .
     * 7 . . . . . . . .
     * 6 . . . . . . . .
     * 5 . . . . . . . .
     * 4 . . . . . . . .
     * 3 . . . . . . . .   a1e1 is rejected: the rook on c1 may be captured, not passed
     * 2 . . . . . . . .
     * 1 R . r . . . . .
     *   a b c d e f g h
     * ```
     */
    @Test
    fun `rejects a rook move past an opposing piece along a rank`() {
        assertFailsWith<IllegalArgumentException> {
            Position.parse("Ra1 rc1")
                .play(Move.parse("a1e1"), Side.WHITE)
        }
    }

    /**
     * ```
     * 8 . . . . . . . .        8 . . . . . . . .
     * 7 . . . . . . . .        7 . . . . . . . .
     * 6 . . . . . . . .        6 . . . . . . . .
     * 5 . . . . . . . .        5 . . . . . . . .
     * 4 . . . . . . . .  a1c1  4 . . . . . . . .
     * 3 . . . . . . . .        3 . . . . . . . .
     * 2 . . . . . . . .        2 . . . . . . . .
     * 1 R . r . r . . .        1 . . R . r . . .
     *   a b c d e f g h          a b c d e f g h
     * ```
     */
    @Test
    fun `captures the first opposing piece along a rook's rank`() {
        assertEquals(
            "Rc1 re1",
            Position.parse("Ra1 rc1 re1")
                .play(Move.parse("a1c1"), Side.WHITE)
                .toString()
        )
    }

    /**
     * ```
     * 8 . . . . . . . .        8 R . . . . . . .
     * 7 . . . . . . . .        7 . . . . . . . .
     * 6 . . . . . . . .        6 . . . . . . . .
     * 5 . . . . . . . .        5 . . . . . . . .
     * 4 . . . . . . . .  a1a8  4 . . . . . . . .
     * 3 . . . . . . . .        3 . . . . . . . .
     * 2 . . . . . . . .        2 . . . . . . . .
     * 1 R . . . . . . .        1 . . . . . . . .
     *   a b c d e f g h          a b c d e f g h
     * ```
     */
    @Test
    fun `moves a rook any distance along a clear file`() {
        assertEquals(
            "Ra8",
            Position.parse("Ra1")
                .play(Move.parse("a1a8"), Side.WHITE)
                .toString()
        )
    }

    /**
     * ```
     * 8 . . . . . . . .        8 . . . . . . . .
     * 7 . . . . . . . .        7 . . . . . . . .
     * 6 . . . . . . . .        6 . . . . . . . .
     * 5 . . . . . . . .        5 . . . . . . . .
     * 4 . . . . . . . .  a1h1  4 . . . . . . . .
     * 3 . . . . . . . .        3 . . . . . . . .
     * 2 . . . . . . . .        2 . . . . . . . .
     * 1 R . . . . . . .        1 . . . . . . . R
     *   a b c d e f g h          a b c d e f g h
     * ```
     */
    @Test
    fun `moves a rook any distance along a clear rank`() {
        assertEquals(
            "Rh1",
            Position.parse("Ra1")
                .play(Move.parse("a1h1"), Side.WHITE)
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
