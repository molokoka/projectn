package molokoka.project.n.util

import molokoka.project.n.domain.BOARD_SIZE
import molokoka.project.n.domain.Coordinates
import molokoka.project.n.domain.FILE_RANGE
import molokoka.project.n.domain.FIRST_FILE
import molokoka.project.n.domain.pieces.Piece
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.RANK_RANGE
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * A position board representation
 *
 * - every square in [pieces] as that piece's own symbol
 * - `.` on an empty square
 */
fun Position.positionDiagram(): String {
    val ranks = RANK_RANGE.reversed().map { rank ->
        val squares = FILE_RANGE.joinToString(" ") { file ->
            val square = Coordinates(file, rank)

            pieces[square]?.symbol?.toString() ?: "."
        }

        "$rank $squares"
    }
    val files = "  " + FILE_RANGE.joinToString(" ")

    return (ranks + files).joinToString("\n")
}

/**
 * The position a board representation draws, so a test can set one up the same
 * way it asserts one. Indentation and the file labels are ignored.
 */
fun fromDiagram(diagram: String): Position {
    val rankRows = diagram.trimIndent()
        .lines()
        .filter { row -> row.isNotEmpty() && row.first().isDigit() }

    require(rankRows.size == BOARD_SIZE) {
        "A board must draw $BOARD_SIZE ranks, was ${rankRows.size}"
    }

    val pieces = rankRows
        .flatMap { row ->
            val rank = row.first().digitToInt()
            val squares = row.drop(2).split(" ")

            require(squares.size == BOARD_SIZE) {
                "Rank $rank must draw $BOARD_SIZE squares, was ${squares.size}"
            }

            squares.mapIndexedNotNull { fileOffset, square ->
                if (square == ".") {
                    null
                } else {
                    Coordinates(FIRST_FILE + fileOffset, rank) to Piece.fromSymbol(square.first())
                }
            }
        }
        .toMap()

    return Position(pieces)
}

class PositionDiagramUtilTest {

    @Test
    fun `reads a piece from the square it is drawn on`() {
        assertEquals(
            Position.parse("Rd4"),
            fromDiagram(
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
                """
            )
        )
    }

    @Test
    fun `rejects a rank with the wrong number of squares`() {
        assertFailsWith<IllegalArgumentException> {
            fromDiagram(
                """
                8 . . . . . . . .
                7 . . . . . . . .
                6 . . . . . . . .
                5 . . . . . . . .
                4 . . . R . . .
                3 . . . . . . . .
                2 . . . . . . . .
                1 . . . . . . . .
                  a b c d e f g h
                """
            )
        }
    }

    @Test
    fun `rejects a board with a rank missing`() {
        assertFailsWith<IllegalArgumentException> {
            fromDiagram(
                """
                8 . . . . . . . .
                7 . . . . . . . .
                6 . . . . . . . .
                5 . . . . . . . .
                4 . . . R . . . .
                3 . . . . . . . .
                2 . . . . . . . .
                  a b c d e f g h
                """
            )
        }
    }

    @Test
    fun `reads back the diagram it draws`() {
        assertEquals(
            Position.INITIAL,
            fromDiagram(Position.INITIAL.positionDiagram())
        )
    }

    @Test
    fun `draws every piece as its own symbol`() {
        assertEquals(
            """
            8 . . . . . . . q
            7 . . . . . . . .
            6 . . . . . . . .
            5 . . . . . . . .
            4 . r . Q . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 R . . . . . . .
              a b c d e f g h
            """.trimIndent(),
            Position.parse("Ra1 Qd4 rb4 qh8").positionDiagram()
        )
    }

    @Test
    fun `draws the initial position`() {
        assertEquals(
            """
            8 . r . r . r . r
            7 q . q . q . q .
            6 . . . . . . . .
            5 . . . . . . . .
            4 . . . . . . . .
            3 . . . . . . . .
            2 . Q . Q . Q . Q
            1 R . R . R . R .
              a b c d e f g h
            """.trimIndent(),
            Position.INITIAL.positionDiagram()
        )
    }
}
