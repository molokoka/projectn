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
 * A reachable moves board representation
 *
 * - every square in [pieces] as that piece's own symbol
 * - `.` on an empty square [isReachable] accepts
 * - `x` on an empty square it does not
 */
fun reachableMovesDiagram(
    pieces: Map<Coordinates, Piece>,
    isReachable: (Coordinates) -> Boolean
): String {
    val ranks = RANK_RANGE.reversed().map { rank ->
        val squares = FILE_RANGE.joinToString(" ") { file ->
            val square = Coordinates(file, rank)

            pieces[square]?.symbol?.toString() ?: if (isReachable(square)) "." else "x"
        }

        "$rank $squares"
    }
    val files = "  " + FILE_RANGE.joinToString(" ")

    return (ranks + files).joinToString("\n")
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
            8 x x x x x x x x
            7 x x x x x x x x
            6 x x x x x x x x
            5 x x x x x x x x
            4 . . . . . . . .
            3 x x x x x x x x
            2 x x x x x x x x
            1 x x x x x x x x
              a b c d e f g h
            """.trimIndent(),
            reachableMovesDiagram(emptyMap()) { square -> square.rank == 4 }
        )
    }

    @Test
    fun `draws the a file on the left and the h file on the right`() {
        assertEquals(
            """
            8 . x x x x x x x
            7 . x x x x x x x
            6 . x x x x x x x
            5 . x x x x x x x
            4 . x x x x x x x
            3 . x x x x x x x
            2 . x x x x x x x
            1 . x x x x x x x
              a b c d e f g h
            """.trimIndent(),
            reachableMovesDiagram(emptyMap()) { square -> square.file == 'a' }
        )
    }

    @Test
    fun `draws every piece as its own symbol`() {
        assertEquals(
            """
            8 x x x x x x x q
            7 x x x x x x x x
            6 x x x x x x x x
            5 x x x x x x x x
            4 x r x Q x x x x
            3 x x x x x x x x
            2 x x x x x x x x
            1 R x x x x x x x
              a b c d e f g h
            """.trimIndent(),
            reachableMovesDiagram(Position.parse("Ra1 Qd4 rb4 qh8").pieces) { false }
        )
    }

    @Test
    fun `draws a piece in place of the mark on an occupied square`() {
        assertEquals(
            """
            8 . . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 . . . . . . . .
            4 . . . R . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 . . . . . . . .
              a b c d e f g h
            """.trimIndent(),
            reachableMovesDiagram(Position.parse("Rd4").pieces) { true }
        )
    }
}
