package molokoka.project.n.domain.move_requirements

import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.Side
import molokoka.project.n.domain.move_requirements.util.reachableMovesDiagram
import molokoka.project.n.domain.play
import kotlin.test.Test
import kotlin.test.assertEquals

class QueenTest {

    /** Every square it reaches with nothing in the way. */
    class OnAnEmptyBoard {

        @Test
        fun `a queen reaches every line through its square`() {
            assertEquals(
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
                """.trimIndent(),
                Position.parse("Qd4").reachableMovesDiagram("d4")
            )
        }

        @Test
        fun `a queen on a1 reaches two edges and the long diagonal`() {
            assertEquals(
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
                """.trimIndent(),
                Position.parse("Qa1").reachableMovesDiagram("a1")
            )
        }

        @Test
        fun `a queen on h1 reaches two edges and the long diagonal`() {
            assertEquals(
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
                """.trimIndent(),
                Position.parse("Qh1").reachableMovesDiagram("h1")
            )
        }

        @Test
        fun `a queen on a8 reaches two edges and the long diagonal`() {
            assertEquals(
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
                """.trimIndent(),
                Position.parse("Qa8").reachableMovesDiagram("a8")
            )
        }

        @Test
        fun `a queen on h8 reaches two edges and the long diagonal`() {
            assertEquals(
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
                """.trimIndent(),
                Position.parse("Qh8").reachableMovesDiagram("h8")
            )
        }

        @Test
        fun `a queen on an edge reaches five directions`() {
            assertEquals(
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
                """.trimIndent(),
                Position.parse("Qd1").reachableMovesDiagram("d1")
            )
        }

        @Test
        fun `a black queen reaches the same squares`() {
            assertEquals(
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
                """.trimIndent(),
                Position.parse("qd4").reachableMovesDiagram("d4", Side.BLACK)
            )
        }
    }

    /** Where pieces of either side stop it. */
    class WithPiecesInTheWay {

        @Test
        fun `a queen stops short of its own pieces on every side`() {
            assertEquals(
                """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x R x Q x R x x
                5 x x . . . x x x
                4 x Q . Q . R x x
                3 x x . . . x x x
                2 x R x Q x R x x
                1 x x x x x x x x
                  a b c d e f g h
                """.trimIndent(),
                Position.parse("Qd4 Rb6 Qd6 Rf6 Qb4 Rf4 Rb2 Qd2 Rf2")
                    .reachableMovesDiagram("d4")
            )
        }

        @Test
        fun `a queen reaches up to the opposing pieces on every side`() {
            assertEquals(
                """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x r x q x r x x
                5 x x . . . x x x
                4 x q . Q . r x x
                3 x x . . . x x x
                2 x r x q x r x x
                1 x x x x x x x x
                  a b c d e f g h
                """.trimIndent(),
                Position.parse("Qd4 rb6 qd6 rf6 qb4 rf4 rb2 qd2 rf2")
                    .reachableMovesDiagram("d4")
            )
        }

        /**
         * Nothing stands on rank four or file d, so a diagonal that is cut short here can only
         * be cut short by the piece actually on the diagonal.
         */
        @Test
        fun `a queen stopped on a diagonal still reaches everything else`() {
            assertEquals(
                """
                8 x x x . x x x x
                7 . x x . x x x x
                6 x . x . x q x x
                5 x x . . . x x x
                4 . . . Q . . . .
                3 x x . . . x x x
                2 x . x . x . x x
                1 . x x . x x . x
                  a b c d e f g h
                """.trimIndent(),
                Position.parse("Qd4 qf6").reachableMovesDiagram("d4")
            )
        }

        @Test
        fun `a queen captures an opposing piece along a diagonal`() {
            assertEquals(
                "Qf6",
                Position.parse("Qd4 rf6")
                    .play(Move.parse("d4f6"), Side.WHITE)
                    .toString()
            )
        }

        @Test
        fun `a queen hemmed in by its own pieces has nowhere to go`() {
            assertEquals(
                """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x x x x x x x x
                5 x x R Q R x x x
                4 x x Q Q R x x x
                3 x x R Q R x x x
                2 x x x x x x x x
                1 x x x x x x x x
                  a b c d e f g h
                """.trimIndent(),
                Position.parse("Qd4 Rc5 Qd5 Re5 Qc4 Re4 Rc3 Qd3 Re3")
                    .reachableMovesDiagram("d4")
            )
        }

        @Test
        fun `a queen in the a1 corner is stopped on all three rays`() {
            assertEquals(
                """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x x x x x x x x
                5 x x x x x x x x
                4 R x x r x x x x
                3 . x . x x x x x
                2 . . x x x x x x
                1 Q . . q x x x x
                  a b c d e f g h
                """.trimIndent(),
                Position.parse("Qa1 Ra4 qd1 rd4").reachableMovesDiagram("a1")
            )
        }

        @Test
        fun `a queen in the h8 corner is stopped on all three rays`() {
            assertEquals(
                """
                8 x x x x r . . Q
                7 x x x x x x . .
                6 x x x x x . x .
                5 x x x x q x x R
                4 x x x x x x x x
                3 x x x x x x x x
                2 x x x x x x x x
                1 x x x x x x x x
                  a b c d e f g h
                """.trimIndent(),
                Position.parse("Qh8 Rh5 re8 qe5").reachableMovesDiagram("h8")
            )
        }

        @Test
        fun `a queen hemmed in by opposing pieces captures any of them`() {
            assertEquals(
                """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x x x x x x x x
                5 x x r q r x x x
                4 x x q Q r x x x
                3 x x r q r x x x
                2 x x x x x x x x
                1 x x x x x x x x
                  a b c d e f g h
                """.trimIndent(),
                Position.parse("Qd4 rc5 qd5 re5 qc4 re4 rc3 qd3 re3")
                    .reachableMovesDiagram("d4")
            )
        }
    }

    /** A scattered position, as a smoke test. */
    class InAMixedPosition {

        @Test
        fun `a queen runs out on each ray at whatever it meets first`() {
            assertEquals(
                """
                8 . x q x . x x x
                7 x . . . x x x x
                6 . . Q . q x x x
                5 x R . . x x x x
                4 Q x . x . x x x
                3 x x . x x r x x
                2 x x . x x x x x
                1 x x . x x x x x
                  a b c d e f g h
                """.trimIndent(),
                Position.parse("Qc6 qc8 Rb5 qe6 Qa4 rf3").reachableMovesDiagram("c6")
            )
        }
    }
}
