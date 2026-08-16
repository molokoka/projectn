package molokoka.project.n.domain.pieces

import molokoka.project.n.domain.Coordinates
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.Side
import molokoka.project.n.util.playableSquares
import molokoka.project.n.util.reachableMovesDiagram
import molokoka.project.n.util.reachableSquaresFromDiagram
import molokoka.project.n.domain.play
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RookTest {

    /** Every square it reaches with nothing in the way. */
    class OnAnEmptyBoard {

        @Test
        fun `a rook reaches its whole rank and file`() {
            Position.parse("Rd4").assertRookMoves(
                "d4",
                """
                8 x x x . x x x x
                7 x x x . x x x x
                6 x x x . x x x x
                5 x x x . x x x x
                4 . . . R . . . .
                3 x x x . x x x x
                2 x x x . x x x x
                1 x x x . x x x x
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a rook on a1 reaches the two edges meeting there`() {
            Position.parse("Ra1").assertRookMoves(
                "a1",
                """
                8 . x x x x x x x
                7 . x x x x x x x
                6 . x x x x x x x
                5 . x x x x x x x
                4 . x x x x x x x
                3 . x x x x x x x
                2 . x x x x x x x
                1 R . . . . . . .
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a rook on h1 reaches the two edges meeting there`() {
            Position.parse("Rh1").assertRookMoves(
                "h1",
                """
                8 x x x x x x x .
                7 x x x x x x x .
                6 x x x x x x x .
                5 x x x x x x x .
                4 x x x x x x x .
                3 x x x x x x x .
                2 x x x x x x x .
                1 . . . . . . . R
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a rook on a8 reaches the two edges meeting there`() {
            Position.parse("Ra8").assertRookMoves(
                "a8",
                """
                8 R . . . . . . .
                7 . x x x x x x x
                6 . x x x x x x x
                5 . x x x x x x x
                4 . x x x x x x x
                3 . x x x x x x x
                2 . x x x x x x x
                1 . x x x x x x x
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a rook on h8 reaches the two edges meeting there`() {
            Position.parse("Rh8").assertRookMoves(
                "h8",
                """
                8 . . . . . . . R
                7 x x x x x x x .
                6 x x x x x x x .
                5 x x x x x x x .
                4 x x x x x x x .
                3 x x x x x x x .
                2 x x x x x x x .
                1 x x x x x x x .
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a rook on an edge reaches three directions`() {
            Position.parse("Rd1").assertRookMoves(
                "d1",
                """
                8 x x x . x x x x
                7 x x x . x x x x
                6 x x x . x x x x
                5 x x x . x x x x
                4 x x x . x x x x
                3 x x x . x x x x
                2 x x x . x x x x
                1 . . . R . . . .
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a black rook reaches the same squares`() {
            Position.parse("rd4").assertRookMoves(
                "d4",
                """
                8 x x x . x x x x
                7 x x x . x x x x
                6 x x x . x x x x
                5 x x x . x x x x
                4 . . . r . . . .
                3 x x x . x x x x
                2 x x x . x x x x
                1 x x x . x x x x
                  a b c d e f g h
                """,
                Side.BLACK
            )
        }
    }

    /** Where pieces of either side stop it. */
    class WithPiecesInTheWay {

        @Test
        fun `a rook stops short of its own pieces on every side`() {
            Position.parse("Rd4 Qd6 Qf4 Rd2 Rb4").assertRookMoves(
                "d4",
                """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x x x # x x x x
                5 x x x . x x x x
                4 x # . R . # x x
                3 x x x . x x x x
                2 x x x # x x x x
                1 x x x x x x x x
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a rook reaches up to the opposing pieces on every side`() {
            Position.parse("Rd4 qd6 qf4 rd2 rb4").assertRookMoves(
                "d4",
                """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x x x o x x x x
                5 x x x . x x x x
                4 x o . R . o x x
                3 x x x . x x x x
                2 x x x o x x x x
                1 x x x x x x x x
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a rook hemmed in by its own pieces has nowhere to go`() {
            Position.parse("Rd4 Qd5 Qe4 Rd3 Rc4").assertRookMoves(
                "d4",
                """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x x x x x x x x
                5 x x x # x x x x
                4 x x # R # x x x
                3 x x x # x x x x
                2 x x x x x x x x
                1 x x x x x x x x
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a rook hemmed in by opposing pieces captures any of them`() {
            Position.parse("Rd4 qd5 qe4 rd3 rc4").assertRookMoves(
                "d4",
                """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x x x x x x x x
                5 x x x o x x x x
                4 x x o R o x x x
                3 x x x o x x x x
                2 x x x x x x x x
                1 x x x x x x x x
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a rook in the a1 corner is stopped on both its rays`() {
            Position.parse("Ra1 Qa4 qd1").assertRookMoves(
                "a1",
                """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x x x x x x x x
                5 x x x x x x x x
                4 # x x x x x x x
                3 . x x x x x x x
                2 . x x x x x x x
                1 R . . o x x x x
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a rook in the h8 corner is stopped on both its rays`() {
            Position.parse("Rh8 Qh5 qd8").assertRookMoves(
                "h8",
                """
                8 x x x o . . . R
                7 x x x x x x x .
                6 x x x x x x x .
                5 x x x x x x x #
                4 x x x x x x x x
                3 x x x x x x x x
                2 x x x x x x x x
                1 x x x x x x x x
                  a b c d e f g h
                """
            )
        }
    }

    /** A scattered position, as a smoke test. */
    class InAMixedPosition {

        @Test
        fun `a rook runs out on each ray at whatever it meets first`() {
            Position.parse("Re5 qe7 Re2 rb5 Qg5").assertRookMoves(
                "e5",
                """
                8 x x x x x x x x
                7 x x x x o x x x
                6 x x x x . x x x
                5 x o . . R . # x
                4 x x x x . x x x
                3 x x x x . x x x
                2 x x x x # x x x
                1 x x x x x x x x
                  a b c d e f g h
                """
            )
        }
    }

    /**
     * The same rules stated as coordinates and moves rather than as a picture, so a change to how
     * the diagram is drawn cannot quietly take the assertions with it.
     */
    class PlainAssertions {

        @Test
        fun `a rook in the centre reaches every square on its two lines`() {
            assertEquals(
                setOf(
                    "a4", "b4", "c4", "e4", "f4", "g4", "h4",
                    "d1", "d2", "d3", "d5", "d6", "d7", "d8"
                ).map(Coordinates::parse).toSet(),
                Position.parse("Rd4").rookReachableSquares(Coordinates.parse("d4"))
            )
        }

        @Test
        fun `a rook in the a1 corner reaches every square on its two lines`() {
            assertEquals(
                setOf(
                    "b1", "c1", "d1", "e1", "f1", "g1", "h1",
                    "a2", "a3", "a4", "a5", "a6", "a7", "a8"
                ).map(Coordinates::parse).toSet(),
                Position.parse("Ra1").rookReachableSquares(Coordinates.parse("a1"))
            )
        }

        @Test
        fun `a rook in the a1 corner reaches no further than the pieces on its lines`() {
            assertEquals(
                setOf(
                    "b1", "c1", "d1",
                    "a2", "a3"
                ).map(Coordinates::parse).toSet(),
                Position.parse("Ra1 Qa4 qd1").rookReachableSquares(Coordinates.parse("a1"))
            )
        }

        @Test
        fun `a rook in the centre plays along every line`() {
            val position = Position.parse("Rd4")

            assertEquals("Ra4", position.play(Move.parse("d4a4"), Side.WHITE).toString())
            assertEquals("Rh4", position.play(Move.parse("d4h4"), Side.WHITE).toString())
            assertEquals("Rd8", position.play(Move.parse("d4d8"), Side.WHITE).toString())
            assertEquals("Rd1", position.play(Move.parse("d4d1"), Side.WHITE).toString())
        }

        @Test
        fun `a rook in the a1 corner plays along every line`() {
            val position = Position.parse("Ra1")

            assertEquals("Rh1", position.play(Move.parse("a1h1"), Side.WHITE).toString())
            assertEquals("Rd1", position.play(Move.parse("a1d1"), Side.WHITE).toString())
            assertEquals("Ra8", position.play(Move.parse("a1a8"), Side.WHITE).toString())
            assertEquals("Ra4", position.play(Move.parse("a1a4"), Side.WHITE).toString())
        }

        @Test
        fun `a rook in the a1 corner plays no further than the pieces on its lines`() {
            val position = Position.parse("Ra1 Qa4 qd1")

            assertEquals(
                "Rd1 Qa4",
                position.play(Move.parse("a1d1"), Side.WHITE).toString()
            )
            assertFailsWith<IllegalArgumentException> {
                position.play(Move.parse("a1a4"), Side.WHITE)
            }
            assertFailsWith<IllegalArgumentException> {
                position.play(Move.parse("a1a5"), Side.WHITE)
            }
            assertFailsWith<IllegalArgumentException> {
                position.play(Move.parse("a1e1"), Side.WHITE)
            }
        }
    }
}

private fun Position.assertRookMoves(
    from: String,
    expectedDiagram: String,
    side: Side = Side.WHITE
) {
    val origin = Coordinates.parse(from)

    val expected = reachableSquaresFromDiagram(expectedDiagram)
    val generated = rookReachableSquares(origin)
    val played = playableSquares(origin, side)

    assertEquals(expected, generated, reachableMovesDiagram(pieces, origin, generated))
    assertEquals(expected, played, reachableMovesDiagram(pieces, origin, played))
}
