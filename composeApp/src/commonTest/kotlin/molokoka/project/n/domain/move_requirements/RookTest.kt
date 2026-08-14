package molokoka.project.n.domain.move_requirements

import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.Side
import molokoka.project.n.domain.move_requirements.util.reachableMovesDiagram
import molokoka.project.n.domain.play
import kotlin.test.Test
import kotlin.test.assertEquals

class RookTest {

    /** Every square it reaches with nothing in the way. */
    class OnAnEmptyBoard {

        @Test
        fun `a rook reaches its whole rank and file`() {
            assertEquals(
                """
                x x x . x x x x
                x x x . x x x x
                x x x . x x x x
                x x x . x x x x
                . . . R . . . .
                x x x . x x x x
                x x x . x x x x
                x x x . x x x x
                """.trimIndent(),
                Position.parse("Rd4").reachableMovesDiagram("d4")
            )
        }

        @Test
        fun `a rook on a1 reaches the two edges meeting there`() {
            assertEquals(
                """
                . x x x x x x x
                . x x x x x x x
                . x x x x x x x
                . x x x x x x x
                . x x x x x x x
                . x x x x x x x
                . x x x x x x x
                R . . . . . . .
                """.trimIndent(),
                Position.parse("Ra1").reachableMovesDiagram("a1")
            )
        }

        @Test
        fun `a rook on h1 reaches the two edges meeting there`() {
            assertEquals(
                """
                x x x x x x x .
                x x x x x x x .
                x x x x x x x .
                x x x x x x x .
                x x x x x x x .
                x x x x x x x .
                x x x x x x x .
                . . . . . . . R
                """.trimIndent(),
                Position.parse("Rh1").reachableMovesDiagram("h1")
            )
        }

        @Test
        fun `a rook on a8 reaches the two edges meeting there`() {
            assertEquals(
                """
                R . . . . . . .
                . x x x x x x x
                . x x x x x x x
                . x x x x x x x
                . x x x x x x x
                . x x x x x x x
                . x x x x x x x
                . x x x x x x x
                """.trimIndent(),
                Position.parse("Ra8").reachableMovesDiagram("a8")
            )
        }

        @Test
        fun `a rook on h8 reaches the two edges meeting there`() {
            assertEquals(
                """
                . . . . . . . R
                x x x x x x x .
                x x x x x x x .
                x x x x x x x .
                x x x x x x x .
                x x x x x x x .
                x x x x x x x .
                x x x x x x x .
                """.trimIndent(),
                Position.parse("Rh8").reachableMovesDiagram("h8")
            )
        }

        @Test
        fun `a rook on an edge reaches three directions`() {
            assertEquals(
                """
                x x x . x x x x
                x x x . x x x x
                x x x . x x x x
                x x x . x x x x
                x x x . x x x x
                x x x . x x x x
                x x x . x x x x
                . . . R . . . .
                """.trimIndent(),
                Position.parse("Rd1").reachableMovesDiagram("d1")
            )
        }

        @Test
        fun `a black rook reaches the same squares`() {
            assertEquals(
                """
                x x x . x x x x
                x x x . x x x x
                x x x . x x x x
                x x x . x x x x
                . . . r . . . .
                x x x . x x x x
                x x x . x x x x
                x x x . x x x x
                """.trimIndent(),
                Position.parse("rd4").reachableMovesDiagram("d4", Side.BLACK)
            )
        }
    }

    /** Where pieces of either side stop it. */
    class WithPiecesInTheWay {

        @Test
        fun `a rook stops short of its own pieces on every side`() {
            assertEquals(
                """
                x x x x x x x x
                x x x x x x x x
                x x x Q x x x x
                x x x . x x x x
                x R . R . Q x x
                x x x . x x x x
                x x x R x x x x
                x x x x x x x x
                """.trimIndent(),
                Position.parse("Rd4 Qd6 Qf4 Rd2 Rb4").reachableMovesDiagram("d4")
            )
        }

        @Test
        fun `a rook reaches up to the opposing pieces on every side`() {
            assertEquals(
                """
                x x x x x x x x
                x x x x x x x x
                x x x q x x x x
                x x x . x x x x
                x r . R . q x x
                x x x . x x x x
                x x x r x x x x
                x x x x x x x x
                """.trimIndent(),
                Position.parse("Rd4 qd6 qf4 rd2 rb4").reachableMovesDiagram("d4")
            )
        }

        @Test
        fun `a rook captures an opposing piece along a file`() {
            assertEquals(
                "Rd6",
                Position.parse("Rd4 qd6")
                    .play(Move.parse("d4d6"), Side.WHITE)
                    .toString()
            )
        }

        @Test
        fun `a rook captures an opposing piece along a rank`() {
            assertEquals(
                "Rf4",
                Position.parse("Rd4 qf4")
                    .play(Move.parse("d4f4"), Side.WHITE)
                    .toString()
            )
        }

        @Test
        fun `a rook hemmed in by its own pieces has nowhere to go`() {
            assertEquals(
                """
                x x x x x x x x
                x x x x x x x x
                x x x x x x x x
                x x x Q x x x x
                x x R R Q x x x
                x x x R x x x x
                x x x x x x x x
                x x x x x x x x
                """.trimIndent(),
                Position.parse("Rd4 Qd5 Qe4 Rd3 Rc4").reachableMovesDiagram("d4")
            )
        }

        @Test
        fun `a rook in the a1 corner is stopped on both its rays`() {
            assertEquals(
                """
                x x x x x x x x
                x x x x x x x x
                x x x x x x x x
                x x x x x x x x
                Q x x x x x x x
                . x x x x x x x
                . x x x x x x x
                R . . q x x x x
                """.trimIndent(),
                Position.parse("Ra1 Qa4 qd1").reachableMovesDiagram("a1")
            )
        }

        @Test
        fun `a rook in the h8 corner is stopped on both its rays`() {
            assertEquals(
                """
                x x x q . . . R
                x x x x x x x .
                x x x x x x x .
                x x x x x x x Q
                x x x x x x x x
                x x x x x x x x
                x x x x x x x x
                x x x x x x x x
                """.trimIndent(),
                Position.parse("Rh8 Qh5 qd8").reachableMovesDiagram("h8")
            )
        }

        @Test
        fun `a rook hemmed in by opposing pieces captures any of them`() {
            assertEquals(
                """
                x x x x x x x x
                x x x x x x x x
                x x x x x x x x
                x x x q x x x x
                x x r R q x x x
                x x x r x x x x
                x x x x x x x x
                x x x x x x x x
                """.trimIndent(),
                Position.parse("Rd4 qd5 qe4 rd3 rc4").reachableMovesDiagram("d4")
            )
        }
    }

    /** A scattered position, as a smoke test. */
    class InAMixedPosition {

        @Test
        fun `a rook runs out on each ray at whatever it meets first`() {
            assertEquals(
                """
                x x x x x x x x
                x x x x q x x x
                x x x x . x x x
                x r . . R . Q x
                x x x x . x x x
                x x x x . x x x
                x x x x R x x x
                x x x x x x x x
                """.trimIndent(),
                Position.parse("Re5 qe7 Re2 rb5 Qg5").reachableMovesDiagram("e5")
            )
        }
    }
}
