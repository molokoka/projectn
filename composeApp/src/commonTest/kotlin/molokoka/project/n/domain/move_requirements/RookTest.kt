package molokoka.project.n.domain.move_requirements

import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.Side
import molokoka.project.n.domain.play
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RookTest {

    /** Middle of the board, one direction at a time. */
    class FromTheMiddle {
        /**
         * ```
         * 8 . . . . . . . .        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . R . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . R . . . .  d4d6  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . .        1 . . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `moves a rook up a file`() {
            assertEquals(
                "Rd6",
                Position.parse("Rd4")
                    .play(Move.parse("d4d6"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 . . . . . . . .        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . R . . . .  d4d2  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . R . . . .
         * 1 . . . . . . . .        1 . . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `moves a rook down a file`() {
            assertEquals(
                "Rd2",
                Position.parse("Rd4")
                    .play(Move.parse("d4d2"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 . . . . . . . .        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . R . . . .  d4f4  4 . . . . . R . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . .        1 . . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `moves a rook toward the h file`() {
            assertEquals(
                "Rf4",
                Position.parse("Rd4")
                    .play(Move.parse("d4f4"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 . . . . . . . .        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . R . . . .  d4b4  4 . R . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . .        1 . . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `moves a rook toward the a file`() {
            assertEquals(
                "Rb4",
                Position.parse("Rd4")
                    .play(Move.parse("d4b4"), Side.WHITE)
                    .toString()
            )
        }
    }

    /** Middle out to the edge, each direction. */
    class OutToTheEdge {
        /**
         * ```
         * 8 . . . . . . . .        8 . . . R . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . R . . . .  d4d8  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . .        1 . . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `moves a rook to the eighth rank`() {
            assertEquals(
                "Rd8",
                Position.parse("Rd4")
                    .play(Move.parse("d4d8"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 . . . . . . . .        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . R . . . .  d4d1  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . .        1 . . . R . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `moves a rook to the first rank`() {
            assertEquals(
                "Rd1",
                Position.parse("Rd4")
                    .play(Move.parse("d4d1"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 . . . . . . . .        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . R . . . .  d4h4  4 . . . . . . . R
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . .        1 . . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `moves a rook to the h file`() {
            assertEquals(
                "Rh4",
                Position.parse("Rd4")
                    .play(Move.parse("d4h4"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 . . . . . . . .        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . R . . . .  d4a4  4 R . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . .        1 . . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `moves a rook to the a file`() {
            assertEquals(
                "Ra4",
                Position.parse("Rd4")
                    .play(Move.parse("d4a4"), Side.WHITE)
                    .toString()
            )
        }
    }

    /** Stops on the square before a blocking piece. */
    class StopsBeforeAPiece {
        /**
         * ```
         * 8 . . . . . . . .        8 . . . . . . . .
         * 7 . . . R . . . .        7 . . . R . . . .
         * 6 . . . . . . . .        6 . . . R . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . R . . . .  d4d6  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . .        1 . . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `moves a rook to the square before a piece up a file`() {
            assertEquals(
                "Rd6 Rd7",
                Position.parse("Rd4 Rd7")
                    .play(Move.parse("d4d6"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 . . . . . . . .        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . R . . . .  d4d2  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . R . . . .
         * 1 . . . R . . . .        1 . . . R . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `moves a rook to the square before a piece down a file`() {
            assertEquals(
                "Rd1 Rd2",
                Position.parse("Rd4 Rd1")
                    .play(Move.parse("d4d2"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 . . . . . . . .        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . R . . . R  d4g4  4 . . . . . . R R
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . .        1 . . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `moves a rook to the square before a piece toward the h file`() {
            assertEquals(
                "Rg4 Rh4",
                Position.parse("Rd4 Rh4")
                    .play(Move.parse("d4g4"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 . . . . . . . .        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 R . . R . . . .  d4b4  4 R R . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . .        1 . . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `moves a rook to the square before a piece toward the a file`() {
            assertEquals(
                "Ra4 Rb4",
                Position.parse("Rd4 Ra4")
                    .play(Move.parse("d4b4"), Side.WHITE)
                    .toString()
            )
        }
    }

    /** Captures an opposing piece in each direction. */
    class Captures {
        /**
         * ```
         * 8 . . . . . . . .        8 . . . . . . . .
         * 7 . . . r . . . .        7 . . . R . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . R . . . .  d4d7  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . .        1 . . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `captures an opposing piece up a file`() {
            assertEquals(
                "Rd7",
                Position.parse("Rd4 rd7")
                    .play(Move.parse("d4d7"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 . . . . . . . .        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . R . . . .  d4d1  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . r . . . .        1 . . . R . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `captures an opposing piece down a file`() {
            assertEquals(
                "Rd1",
                Position.parse("Rd4 rd1")
                    .play(Move.parse("d4d1"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 . . . . . . . .        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . R . . . r  d4h4  4 . . . . . . . R
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . .        1 . . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `captures an opposing piece toward the h file`() {
            assertEquals(
                "Rh4",
                Position.parse("Rd4 rh4")
                    .play(Move.parse("d4h4"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 . . . . . . . .        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 r . . R . . . .  d4a4  4 R . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . .        1 . . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `captures an opposing piece toward the a file`() {
            assertEquals(
                "Ra4",
                Position.parse("Rd4 ra4")
                    .play(Move.parse("d4a4"), Side.WHITE)
                    .toString()
            )
        }
    }

    /** Rejects jumping over a piece, each direction. */
    class JumpingOverPieces {
        /**
         * ```
         * 8 . . . . . . . .   d4d8 is rejected: the piece on d6 is in the way
         * 7 . . . . . . . .
         * 6 . . . r . . . .
         * 5 . . . . . . . .
         * 4 . . . R . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a rook move over a piece up a file`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rd4 rd6")
                    .play(Move.parse("d4d8"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . . . . . .   d4d1 is rejected: the piece on d2 is in the way
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . R . . . .
         * 3 . . . . . . . .
         * 2 . . . r . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a rook move over a piece down a file`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rd4 rd2")
                    .play(Move.parse("d4d1"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . . . . . .   d4h4 is rejected: the piece on f4 is in the way
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . R . r . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a rook move over a piece toward the h file`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rd4 rf4")
                    .play(Move.parse("d4h4"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . . . . . .   d4a4 is rejected: the piece on b4 is in the way
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . r . R . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a rook move over a piece toward the a file`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rd4 rb4")
                    .play(Move.parse("d4a4"), Side.WHITE)
            }
        }
    }

    /** Border squares: plain moves and captures. */
    class FromTheBorder {
        /**
         * ```
         * 8 . . . . . . . .        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . . . . . .  a1d1  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
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
         * 8 R . . . . . . .        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 r . . . . . . .        5 R . . . . . . .
         * 4 . . . . . . . .  a8a5  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . .        1 . . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `captures an opposing piece from the eighth rank`() {
            assertEquals(
                "Ra5",
                Position.parse("Ra8 ra5")
                    .play(Move.parse("a8a5"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 . . . . . . . .        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . . . . . .  h1c1  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . r . . . . R        1 . . R . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `captures an opposing piece from the h file`() {
            assertEquals(
                "Rc1",
                Position.parse("Rh1 rc1")
                    .play(Move.parse("h1c1"), Side.WHITE)
                    .toString()
            )
        }
    }

    /** Border squares: blocked by either side's piece. */
    class BlockedFromTheBorder {
        /**
         * ```
         * 8 . . . . . . . .   a1a5 is rejected: the rook on a3 blocks the way
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . . . . . .
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
         * 8 . . . . . . . .   a1a5 is rejected: the rook on a3 may be captured, not passed
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . . . . . .
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
         * 8 . . . . . . . .   a1e1 is rejected: the rook on c1 blocks the way
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . . . . . .
         * 3 . . . . . . . .
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
         * 8 . . . . . . . .   a1e1 is rejected: the rook on c1 may be captured, not passed
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . . . . . .
         * 3 . . . . . . . .
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
         * Blocking down a file, where the walk steps toward rank one.
         *
         * ```
         * 8 R . . . . . . .
         * 7 . . . . . . . .   a8a3 is rejected: the rook on a5 blocks the way
         * 6 . . . . . . . .
         * 5 r . . . . . . .
         * 4 . . . . . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a rook move past a piece down a file`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Ra8 ra5")
                    .play(Move.parse("a8a3"), Side.WHITE)
            }
        }

        /**
         * Blocking along a rank, where the walk steps toward the a file.
         *
         * ```
         * 8 . . . . . . . .   f1b1 is rejected: the rook on d1 blocks the way
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . . . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . R . R . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a rook move past a piece toward the a file`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rf1 Rd1")
                    .play(Move.parse("f1b1"), Side.WHITE)
            }
        }
    }

    /** Corner to corner, clear sweeps. */
    class CornerSweeps {
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
        fun `sweeps a rook from a1 to a8`() {
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
        fun `sweeps a rook from a1 to h1`() {
            assertEquals(
                "Rh1",
                Position.parse("Ra1")
                    .play(Move.parse("a1h1"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 R . . . . . . .        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . . . . . .  a8a1  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . .        1 R . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `sweeps a rook from a8 to a1`() {
            assertEquals(
                "Ra1",
                Position.parse("Ra8")
                    .play(Move.parse("a8a1"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 R . . . . . . .        8 . . . . . . . R
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . . . . . .  a8h8  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . .        1 . . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `sweeps a rook from a8 to h8`() {
            assertEquals(
                "Rh8",
                Position.parse("Ra8")
                    .play(Move.parse("a8h8"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 . . . . . . . .        8 . . . . . . . R
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . . . . . .  h1h8  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . R        1 . . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `sweeps a rook from h1 to h8`() {
            assertEquals(
                "Rh8",
                Position.parse("Rh1")
                    .play(Move.parse("h1h8"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 . . . . . . . .        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . . . . . .  h1a1  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . R        1 R . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `sweeps a rook from h1 to a1`() {
            assertEquals(
                "Ra1",
                Position.parse("Rh1")
                    .play(Move.parse("h1a1"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 . . . . . . . R        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . . . . . .  h8h1  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . .        1 . . . . . . . R
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `sweeps a rook from h8 to h1`() {
            assertEquals(
                "Rh1",
                Position.parse("Rh8")
                    .play(Move.parse("h8h1"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 . . . . . . . R        8 R . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . . . . . .  h8a8  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . .        1 . . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `sweeps a rook from h8 to a8`() {
            assertEquals(
                "Ra8",
                Position.parse("Rh8")
                    .play(Move.parse("h8a8"), Side.WHITE)
                    .toString()
            )
        }
    }

    /** Corner to corner, sweeps that capture. */
    class CornerSweepCaptures {
        /**
         * ```
         * 8 r . . . . . . .        8 R . . . . . . .
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
        fun `sweeps a rook from a1 to capture on a8`() {
            assertEquals(
                "Ra8",
                Position.parse("Ra1 ra8")
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
         * 1 R . . . . . . r        1 . . . . . . . R
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `sweeps a rook from a1 to capture on h1`() {
            assertEquals(
                "Rh1",
                Position.parse("Ra1 rh1")
                    .play(Move.parse("a1h1"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 R . . . . . . .        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . . . . . .  a8a1  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 r . . . . . . .        1 R . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `sweeps a rook from a8 to capture on a1`() {
            assertEquals(
                "Ra1",
                Position.parse("Ra8 ra1")
                    .play(Move.parse("a8a1"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 R . . . . . . r        8 . . . . . . . R
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . . . . . .  a8h8  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . .        1 . . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `sweeps a rook from a8 to capture on h8`() {
            assertEquals(
                "Rh8",
                Position.parse("Ra8 rh8")
                    .play(Move.parse("a8h8"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 . . . . . . . r        8 . . . . . . . R
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . . . . . .  h1h8  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . R        1 . . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `sweeps a rook from h1 to capture on h8`() {
            assertEquals(
                "Rh8",
                Position.parse("Rh1 rh8")
                    .play(Move.parse("h1h8"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 . . . . . . . .        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . . . . . .  h1a1  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 r . . . . . . R        1 R . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `sweeps a rook from h1 to capture on a1`() {
            assertEquals(
                "Ra1",
                Position.parse("Rh1 ra1")
                    .play(Move.parse("h1a1"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 . . . . . . . R        8 . . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . . . . . .  h8h1  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . r        1 . . . . . . . R
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `sweeps a rook from h8 to capture on h1`() {
            assertEquals(
                "Rh1",
                Position.parse("Rh8 rh1")
                    .play(Move.parse("h8h1"), Side.WHITE)
                    .toString()
            )
        }

        /**
         * ```
         * 8 r . . . . . . R        8 R . . . . . . .
         * 7 . . . . . . . .        7 . . . . . . . .
         * 6 . . . . . . . .        6 . . . . . . . .
         * 5 . . . . . . . .        5 . . . . . . . .
         * 4 . . . . . . . .  h8a8  4 . . . . . . . .
         * 3 . . . . . . . .        3 . . . . . . . .
         * 2 . . . . . . . .        2 . . . . . . . .
         * 1 . . . . . . . .        1 . . . . . . . .
         *   a b c d e f g h          a b c d e f g h
         * ```
         */
        @Test
        fun `sweeps a rook from h8 to capture on a8`() {
            assertEquals(
                "Ra8",
                Position.parse("Rh8 ra8")
                    .play(Move.parse("h8a8"), Side.WHITE)
                    .toString()
            )
        }
    }

    /** Corner sweeps blocked by the same side. */
    class CornerSweepsBlockedBySameSide {
        /**
         * ```
         * 8 . . . . . . . .   a1a8 is rejected: the piece on a4 blocks the way
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 R . . . . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 R . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a sweep from a1 to a8 past a piece of the same side`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Ra1 Ra4")
                    .play(Move.parse("a1a8"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . . . . . .   a1h1 is rejected: the piece on d1 blocks the way
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . . . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 R . . R . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a sweep from a1 to h1 past a piece of the same side`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Ra1 Rd1")
                    .play(Move.parse("a1h1"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 R . . . . . . .
         * 7 . . . . . . . .   a8a1 is rejected: the piece on a5 blocks the way
         * 6 . . . . . . . .
         * 5 R . . . . . . .
         * 4 . . . . . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a sweep from a8 to a1 past a piece of the same side`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Ra8 Ra5")
                    .play(Move.parse("a8a1"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 R . . R . . . .
         * 7 . . . . . . . .   a8h8 is rejected: the piece on d8 blocks the way
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . . . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a sweep from a8 to h8 past a piece of the same side`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Ra8 Rd8")
                    .play(Move.parse("a8h8"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . . . . . .   h1h8 is rejected: the piece on h4 blocks the way
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . . . . . R
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . R
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a sweep from h1 to h8 past a piece of the same side`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rh1 Rh4")
                    .play(Move.parse("h1h8"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . . . . . .   h1a1 is rejected: the piece on d1 blocks the way
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . . . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . R . . . R
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a sweep from h1 to a1 past a piece of the same side`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rh1 Rd1")
                    .play(Move.parse("h1a1"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . . . . . R
         * 7 . . . . . . . .   h8h1 is rejected: the piece on h5 blocks the way
         * 6 . . . . . . . .
         * 5 . . . . . . . R
         * 4 . . . . . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a sweep from h8 to h1 past a piece of the same side`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rh8 Rh5")
                    .play(Move.parse("h8h1"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . R . . . R
         * 7 . . . . . . . .   h8a8 is rejected: the piece on d8 blocks the way
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . . . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a sweep from h8 to a8 past a piece of the same side`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rh8 Rd8")
                    .play(Move.parse("h8a8"), Side.WHITE)
            }
        }
    }

    /** Corner sweeps blocked by an opposing piece. */
    class CornerSweepsBlockedByOpponent {
        /**
         * ```
         * 8 . . . . . . . .   a1a8 is rejected: the piece on a4 may be captured, not passed
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 r . . . . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 R . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a sweep from a1 to a8 past an opposing piece`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Ra1 ra4")
                    .play(Move.parse("a1a8"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . . . . . .   a1h1 is rejected: the piece on d1 may be captured, not passed
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . . . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 R . . r . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a sweep from a1 to h1 past an opposing piece`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Ra1 rd1")
                    .play(Move.parse("a1h1"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 R . . . . . . .
         * 7 . . . . . . . .   a8a1 is rejected: the piece on a5 may be captured, not passed
         * 6 . . . . . . . .
         * 5 r . . . . . . .
         * 4 . . . . . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a sweep from a8 to a1 past an opposing piece`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Ra8 ra5")
                    .play(Move.parse("a8a1"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 R . . r . . . .
         * 7 . . . . . . . .   a8h8 is rejected: the piece on d8 may be captured, not passed
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . . . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a sweep from a8 to h8 past an opposing piece`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Ra8 rd8")
                    .play(Move.parse("a8h8"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . . . . . .   h1h8 is rejected: the piece on h4 may be captured, not passed
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . . . . . r
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . R
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a sweep from h1 to h8 past an opposing piece`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rh1 rh4")
                    .play(Move.parse("h1h8"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . . . . . .   h1a1 is rejected: the piece on d1 may be captured, not passed
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . . . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . r . . . R
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a sweep from h1 to a1 past an opposing piece`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rh1 rd1")
                    .play(Move.parse("h1a1"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . . . . . R
         * 7 . . . . . . . .   h8h1 is rejected: the piece on h5 may be captured, not passed
         * 6 . . . . . . . .
         * 5 . . . . . . . r
         * 4 . . . . . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a sweep from h8 to h1 past an opposing piece`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rh8 rh5")
                    .play(Move.parse("h8h1"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . r . . . R
         * 7 . . . . . . . .   h8a8 is rejected: the piece on d8 may be captured, not passed
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . . . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a sweep from h8 to a8 past an opposing piece`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rh8 rd8")
                    .play(Move.parse("h8a8"), Side.WHITE)
            }
        }
    }

    /** Rejects diagonal moves, all four directions. */
    class Diagonals {
        /**
         * ```
         * 8 . . . . . . . .   a1d4 is rejected: a rook does not move diagonally
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . . . . . .
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
         * 8 . . . . . . . .   d4g7 is rejected: a rook does not move diagonally
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . R . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a diagonal rook move up and toward the h file`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rd4")
                    .play(Move.parse("d4g7"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . . . . . .   d4a7 is rejected: a rook does not move diagonally
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . R . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a diagonal rook move up and toward the a file`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rd4")
                    .play(Move.parse("d4a7"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . . . . . .   d4g1 is rejected: a rook does not move diagonally
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . R . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a diagonal rook move down and toward the h file`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rd4")
                    .play(Move.parse("d4g1"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . . . . . .   d4a1 is rejected: a rook does not move diagonally
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . R . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a diagonal rook move down and toward the a file`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rd4")
                    .play(Move.parse("d4a1"), Side.WHITE)
            }
        }
    }

    /** Rejects knight-shaped moves, all eight. */
    class KnightShapes {
        /**
         * ```
         * 8 . . . . . . . .   a1b3 is rejected: b3 lies off both the rank and the file
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . . . . . .
         * 3 . . . . . . . .
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
         * 8 . . . . . . . .   d4e6 is rejected: e6 lies off both the rank and the file
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . R . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a knight-shaped rook move to e6`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rd4")
                    .play(Move.parse("d4e6"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . . . . . .   d4f5 is rejected: f5 lies off both the rank and the file
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . R . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a knight-shaped rook move to f5`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rd4")
                    .play(Move.parse("d4f5"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . . . . . .   d4f3 is rejected: f3 lies off both the rank and the file
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . R . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a knight-shaped rook move to f3`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rd4")
                    .play(Move.parse("d4f3"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . . . . . .   d4e2 is rejected: e2 lies off both the rank and the file
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . R . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a knight-shaped rook move to e2`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rd4")
                    .play(Move.parse("d4e2"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . . . . . .   d4c2 is rejected: c2 lies off both the rank and the file
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . R . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a knight-shaped rook move to c2`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rd4")
                    .play(Move.parse("d4c2"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . . . . . .   d4b3 is rejected: b3 lies off both the rank and the file
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . R . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a knight-shaped rook move to b3`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rd4")
                    .play(Move.parse("d4b3"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . . . . . .   d4b5 is rejected: b5 lies off both the rank and the file
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . R . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a knight-shaped rook move to b5`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rd4")
                    .play(Move.parse("d4b5"), Side.WHITE)
            }
        }

        /**
         * ```
         * 8 . . . . . . . .   d4c6 is rejected: c6 lies off both the rank and the file
         * 7 . . . . . . . .
         * 6 . . . . . . . .
         * 5 . . . . . . . .
         * 4 . . . R . . . .
         * 3 . . . . . . . .
         * 2 . . . . . . . .
         * 1 . . . . . . . .
         *   a b c d e f g h
         * ```
         */
        @Test
        fun `rejects a knight-shaped rook move to c6`() {
            assertFailsWith<IllegalArgumentException> {
                Position.parse("Rd4")
                    .play(Move.parse("d4c6"), Side.WHITE)
            }
        }
    }
}
