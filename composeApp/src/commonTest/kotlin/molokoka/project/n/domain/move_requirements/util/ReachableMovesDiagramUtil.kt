package molokoka.project.n.domain.move_requirements.util

import molokoka.project.n.domain.Coordinates
import molokoka.project.n.domain.FILE_RANGE
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Piece
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.RANK_RANGE
import molokoka.project.n.domain.Side
import molokoka.project.n.domain.play
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A board with rank eight on top and the a file on the left:
 *
 * - every square in [pieces] as that piece's own symbol
 * - `.` on an empty square [isReachable] accepts
 * - `x` on an empty square it does not
 *
 * A piece hides the mark, so whether an occupied square is reachable is not drawn.
 */
fun reachableMovesDiagram(
    pieces: Map<Coordinates, Piece>,
    isReachable: (Coordinates) -> Boolean
): String =
    RANK_RANGE.reversed().joinToString("\n") { rank ->
        FILE_RANGE.joinToString(" ") { file ->
            val square = Coordinates(file, rank)

            pieces[square]?.symbol?.toString() ?: if (isReachable(square)) "." else "x"
        }
    }

fun Position.reachableMovesDiagram(from: String, side: Side = Side.WHITE): String {
    val origin = Coordinates.parse(from)

    return reachableMovesDiagram(pieces) { square ->
        runCatching { play(Move(origin, square), side) }.isSuccess
    }
}

/**
 * The diagram only draws: which squares are reachable is the caller's answer, given here as a
 * plain lambda so no movement rule takes part in these tests.
 */
class ReachableMovesDiagramUtilTest {

    @Test
    fun `draws rank eight on the top row and rank one on the bottom`() {
        assertEquals(
            """
            x x x x x x x x
            x x x x x x x x
            x x x x x x x x
            x x x x x x x x
            . . . . . . . .
            x x x x x x x x
            x x x x x x x x
            x x x x x x x x
            """.trimIndent(),
            reachableMovesDiagram(emptyMap()) { square -> square.rank == 4 }
        )
    }

    @Test
    fun `draws the a file on the left and the h file on the right`() {
        assertEquals(
            """
            . x x x x x x x
            . x x x x x x x
            . x x x x x x x
            . x x x x x x x
            . x x x x x x x
            . x x x x x x x
            . x x x x x x x
            . x x x x x x x
            """.trimIndent(),
            reachableMovesDiagram(emptyMap()) { square -> square.file == 'a' }
        )
    }

    @Test
    fun `draws every piece as its own symbol`() {
        assertEquals(
            """
            x x x x x x x q
            x x x x x x x x
            x x x x x x x x
            x x x x x x x x
            x r x Q x x x x
            x x x x x x x x
            x x x x x x x x
            R x x x x x x x
            """.trimIndent(),
            reachableMovesDiagram(Position.parse("Ra1 Qd4 rb4 qh8").pieces) { false }
        )
    }

    @Test
    fun `draws a piece in place of the mark on an occupied square`() {
        assertEquals(
            """
            . . . . . . . .
            . . . . . . . .
            . . . . . . . .
            . . . . . . . .
            . . . R . . . .
            . . . . . . . .
            . . . . . . . .
            . . . . . . . .
            """.trimIndent(),
            reachableMovesDiagram(Position.parse("Rd4").pieces) { true }
        )
    }
}
