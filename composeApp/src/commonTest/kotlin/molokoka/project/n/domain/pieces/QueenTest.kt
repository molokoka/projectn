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

class QueenTest {

    /** Every square it reaches with nothing in the way. */
    class OnAnEmptyBoard {

        @Test
        fun `a queen reaches every line through its square`() {
            Position.parse("Qd4").assertQueenMoves(
                "d4",
                """
                8 x x x . x x x .
                7 . x x . x x . x
                6 x . x . x . x x
                5 x x . . . x x x
                4 . . . Q . . . .
                3 x x . . . x x x
                2 x . x . x . x x
                1 . x x . x x . x
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a queen on a1 reaches two edges and the long diagonal`() {
            Position.parse("Qa1").assertQueenMoves(
                "a1",
                """
                8 . x x x x x x .
                7 . x x x x x . x
                6 . x x x x . x x
                5 . x x x . x x x
                4 . x x . x x x x
                3 . x . x x x x x
                2 . . x x x x x x
                1 Q . . . . . . .
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a queen on h1 reaches two edges and the long diagonal`() {
            Position.parse("Qh1").assertQueenMoves(
                "h1",
                """
                8 . x x x x x x .
                7 x . x x x x x .
                6 x x . x x x x .
                5 x x x . x x x .
                4 x x x x . x x .
                3 x x x x x . x .
                2 x x x x x x . .
                1 . . . . . . . Q
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a queen on a8 reaches two edges and the long diagonal`() {
            Position.parse("Qa8").assertQueenMoves(
                "a8",
                """
                8 Q . . . . . . .
                7 . . x x x x x x
                6 . x . x x x x x
                5 . x x . x x x x
                4 . x x x . x x x
                3 . x x x x . x x
                2 . x x x x x . x
                1 . x x x x x x .
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a queen on h8 reaches two edges and the long diagonal`() {
            Position.parse("Qh8").assertQueenMoves(
                "h8",
                """
                8 . . . . . . . Q
                7 x x x x x x . .
                6 x x x x x . x .
                5 x x x x . x x .
                4 x x x . x x x .
                3 x x . x x x x .
                2 x . x x x x x .
                1 . x x x x x x .
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a queen on an edge reaches five directions`() {
            Position.parse("Qd1").assertQueenMoves(
                "d1",
                """
                8 x x x . x x x x
                7 x x x . x x x x
                6 x x x . x x x x
                5 x x x . x x x .
                4 . x x . x x . x
                3 x . x . x . x x
                2 x x . . . x x x
                1 . . . Q . . . .
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a black queen reaches the same squares`() {
            Position.parse("qd4").assertQueenMoves(
                "d4",
                """
                8 x x x . x x x .
                7 . x x . x x . x
                6 x . x . x . x x
                5 x x . . . x x x
                4 . . . q . . . .
                3 x x . . . x x x
                2 x . x . x . x x
                1 . x x . x x . x
                  a b c d e f g h
                """,
                Side.BLACK
            )
        }
    }

    /** Where pieces of either side stop it. */
    class WithPiecesInTheWay {

        @Test
        fun `a queen stops short of its own pieces on every side`() {
            Position.parse("Qd4 Rb6 Qd6 Rf6 Qb4 Rf4 Rb2 Qd2 Rf2").assertQueenMoves(
                "d4",
                """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x # x # x # x x
                5 x x . . . x x x
                4 x # . Q . # x x
                3 x x . . . x x x
                2 x # x # x # x x
                1 x x x x x x x x
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a queen reaches up to the opposing pieces on every side`() {
            Position.parse("Qd4 rb6 qd6 rf6 qb4 rf4 rb2 qd2 rf2").assertQueenMoves(
                "d4",
                """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x o x o x o x x
                5 x x . . . x x x
                4 x o . Q . o x x
                3 x x . . . x x x
                2 x o x o x o x x
                1 x x x x x x x x
                  a b c d e f g h
                """
            )
        }

        /**
         * Nothing stands on rank four or file d, so a diagonal that is cut short here can only
         * be cut short by the piece actually on the diagonal.
         */
        @Test
        fun `a queen stopped on a diagonal still reaches everything else`() {
            Position.parse("Qd4 qf6").assertQueenMoves(
                "d4",
                """
                8 x x x . x x x x
                7 . x x . x x x x
                6 x . x . x o x x
                5 x x . . . x x x
                4 . . . Q . . . .
                3 x x . . . x x x
                2 x . x . x . x x
                1 . x x . x x . x
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a queen hemmed in by its own pieces has nowhere to go`() {
            Position.parse("Qd4 Rc5 Qd5 Re5 Qc4 Re4 Rc3 Qd3 Re3").assertQueenMoves(
                "d4",
                """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x x x x x x x x
                5 x x # # # x x x
                4 x x # Q # x x x
                3 x x # # # x x x
                2 x x x x x x x x
                1 x x x x x x x x
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a queen hemmed in by opposing pieces captures any of them`() {
            Position.parse("Qd4 rc5 qd5 re5 qc4 re4 rc3 qd3 re3").assertQueenMoves(
                "d4",
                """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x x x x x x x x
                5 x x o o o x x x
                4 x x o Q o x x x
                3 x x o o o x x x
                2 x x x x x x x x
                1 x x x x x x x x
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a queen in the a1 corner is stopped on all three rays`() {
            Position.parse("Qa1 Ra4 qd1 rd4").assertQueenMoves(
                "a1",
                """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x x x x x x x x
                5 x x x x x x x x
                4 # x x o x x x x
                3 . x . x x x x x
                2 . . x x x x x x
                1 Q . . o x x x x
                  a b c d e f g h
                """
            )
        }

        @Test
        fun `a queen in the h8 corner is stopped on all three rays`() {
            Position.parse("Qh8 Rh5 re8 qe5").assertQueenMoves(
                "h8",
                """
                8 x x x x o . . Q
                7 x x x x x x . .
                6 x x x x x . x .
                5 x x x x o x x #
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
        fun `a queen runs out on each ray at whatever it meets first`() {
            Position.parse("Qc6 qc8 Rb5 qe6 Qa4 rf3").assertQueenMoves(
                "c6",
                """
                8 . x o x . x x x
                7 x . . . x x x x
                6 . . Q . o x x x
                5 x # . . x x x x
                4 # x . x . x x x
                3 x x . x x o x x
                2 x x . x x x x x
                1 x x . x x x x x
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
        fun `a queen in the centre reaches every square on its four lines`() {
            assertEquals(
                setOf(
                    "a4", "b4", "c4", "e4", "f4", "g4", "h4",
                    "d1", "d2", "d3", "d5", "d6", "d7", "d8",
                    "a1", "b2", "c3", "e5", "f6", "g7", "h8",
                    "a7", "b6", "c5", "e3", "f2", "g1"
                ).map(Coordinates::parse).toSet(),
                Position.parse("Qd4").queenReachableSquares(Coordinates.parse("d4"))
            )
        }

        @Test
        fun `a queen in the a1 corner reaches every square on its three lines`() {
            assertEquals(
                setOf(
                    "b1", "c1", "d1", "e1", "f1", "g1", "h1",
                    "a2", "a3", "a4", "a5", "a6", "a7", "a8",
                    "b2", "c3", "d4", "e5", "f6", "g7", "h8"
                ).map(Coordinates::parse).toSet(),
                Position.parse("Qa1").queenReachableSquares(Coordinates.parse("a1"))
            )
        }

        @Test
        fun `a queen in the a1 corner reaches no further than the pieces on its lines`() {
            assertEquals(
                setOf(
                    "b1", "c1", "d1",
                    "a2", "a3",
                    "b2", "c3", "d4"
                ).map(Coordinates::parse).toSet(),
                Position.parse("Qa1 Ra4 qd1 rd4").queenReachableSquares(Coordinates.parse("a1"))
            )
        }

        @Test
        fun `a queen in the centre plays along every line`() {
            val position = Position.parse("Qd4")

            assertEquals("Qa4", position.play(Move.parse("d4a4"), Side.WHITE).toString())
            assertEquals("Qd8", position.play(Move.parse("d4d8"), Side.WHITE).toString())
            assertEquals("Qh8", position.play(Move.parse("d4h8"), Side.WHITE).toString())
            assertEquals("Qg1", position.play(Move.parse("d4g1"), Side.WHITE).toString())
        }

        @Test
        fun `a queen in the a1 corner plays along every line`() {
            val position = Position.parse("Qa1")

            assertEquals("Qh1", position.play(Move.parse("a1h1"), Side.WHITE).toString())
            assertEquals("Qa8", position.play(Move.parse("a1a8"), Side.WHITE).toString())
            assertEquals("Qh8", position.play(Move.parse("a1h8"), Side.WHITE).toString())
        }

        @Test
        fun `a queen in the a1 corner plays no further than the pieces on its lines`() {
            val position = Position.parse("Qa1 Ra4 qd1 rd4")

            assertEquals(
                "Qd1 Ra4 rd4",
                position.play(Move.parse("a1d1"), Side.WHITE).toString()
            )
            assertEquals(
                "qd1 Ra4 Qd4",
                position.play(Move.parse("a1d4"), Side.WHITE).toString()
            )
            assertFailsWith<IllegalArgumentException> {
                position.play(Move.parse("a1a4"), Side.WHITE)
            }
            assertFailsWith<IllegalArgumentException> {
                position.play(Move.parse("a1e1"), Side.WHITE)
            }
        }
    }
}

private fun Position.assertQueenMoves(
    from: String,
    expectedDiagram: String,
    side: Side = Side.WHITE
) {
    val origin = Coordinates.parse(from)

    val expected = reachableSquaresFromDiagram(expectedDiagram)
    val generated = queenReachableSquares(origin)
    val played = playableSquares(origin, side)

    assertEquals(expected, generated, reachableMovesDiagram(pieces, origin, generated))
    assertEquals(expected, played, reachableMovesDiagram(pieces, origin, played))
}
