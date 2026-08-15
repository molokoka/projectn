package molokoka.project.n.domain.pieces.util

import molokoka.project.n.domain.BOARD_SIZE
import molokoka.project.n.domain.Coordinates
import molokoka.project.n.domain.FILE_RANGE
import molokoka.project.n.domain.FIRST_FILE
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.pieces.Piece
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.RANK_RANGE
import molokoka.project.n.domain.Side
import molokoka.project.n.domain.play
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

fun reachableMovesDiagram(
    pieces: Map<Coordinates, Piece>,
    origin: Coordinates,
    reachable: Set<Coordinates>
): String {
    val ranks = RANK_RANGE.reversed().map { rank ->
        val squares = FILE_RANGE.joinToString(" ") { file ->
            val square = Coordinates(file, rank)

            squareMark(pieces[square], square == origin, square in reachable)
        }

        "$rank $squares"
    }
    val files = "  " + FILE_RANGE.joinToString(" ")

    return (ranks + files).joinToString("\n")
}

private fun squareMark(piece: Piece?, isOrigin: Boolean, isReachable: Boolean): String =
    when {
        isOrigin && piece != null -> piece.symbol.toString()
        piece != null -> if (isReachable) "o" else "#"
        else -> if (isReachable) "." else "x"
    }

fun reachableSquaresFromDiagram(diagram: String): Set<Coordinates> {
    val rankRows = diagram.trimIndent()
        .lines()
        .filter { row -> row.isNotEmpty() && row.first().isDigit() }

    require(rankRows.size == BOARD_SIZE) {
        "A board must draw $BOARD_SIZE ranks, was ${rankRows.size}"
    }

    return rankRows
        .flatMap { row ->
            val rank = row.first().digitToInt()
            val squares = row.drop(2).split(" ")

            require(squares.size == BOARD_SIZE) {
                "Rank $rank must draw $BOARD_SIZE squares, was ${squares.size}"
            }

            squares.mapIndexedNotNull { fileOffset, square ->
                if (square == "." || square == "o") {
                    Coordinates(FIRST_FILE + fileOffset, rank)
                } else {
                    null
                }
            }
        }
        .toSet()
}

fun Position.playableSquares(origin: Coordinates, side: Side): Set<Coordinates> =
    RANK_RANGE
        .flatMap { rank ->
            FILE_RANGE.map { file -> Coordinates(file, rank) }
        }
        .filter { square ->
            runCatching { play(Move(origin, square), side) }.isSuccess
        }
        .toSet()

class ReachableMovesDiagramUtilTest {

    class ReadingABoard {

        @Test
        fun `reads a dot as a square the mover reaches`() {
            assertEquals(
                setOf(Coordinates.parse("d4")),
                reachableSquaresFromDiagram(
                    """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x x x x x x x x
                5 x x x x x x x x
                4 x x x . x x x x
                3 x x x x x x x x
                2 x x x x x x x x
                1 x x x x x x x x
                  a b c d e f g h
                    """
                )
            )
        }

        @Test
        fun `reads a capture as a square the mover reaches`() {
            assertEquals(
                setOf(Coordinates.parse("d4")),
                reachableSquaresFromDiagram(
                    """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x x x x x x x x
                5 x x x x x x x x
                4 x x x o x x x x
                3 x x x x x x x x
                2 x x x x x x x x
                1 x x x x x x x x
                  a b c d e f g h
                    """
                )
            )
        }

        @Test
        fun `reads a cross as a square the mover does not reach`() {
            assertEquals(
                emptySet(),
                reachableSquaresFromDiagram(
                    """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x x x x x x x x
                5 x x x x x x x x
                4 x x x x x x x x
                3 x x x x x x x x
                2 x x x x x x x x
                1 x x x x x x x x
                  a b c d e f g h
                    """
                )
            )
        }

        @Test
        fun `reads a blocker as a square the mover does not reach`() {
            assertEquals(
                emptySet(),
                reachableSquaresFromDiagram(
                    """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x x x x x x x x
                5 x x x x x x x x
                4 x x x # x x x x
                3 x x x x x x x x
                2 x x x x x x x x
                1 x x x x x x x x
                  a b c d e f g h
                    """
                )
            )
        }

        @Test
        fun `reads the mover as a square it does not reach`() {
            assertEquals(
                emptySet(),
                reachableSquaresFromDiagram(
                    """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x x x x x x x x
                5 x x x x x x x x
                4 x x x R x x x x
                3 x x x x x x x x
                2 x x x x x x x x
                1 x x x x x x x x
                  a b c d e f g h
                    """
                )
            )
        }

        @Test
        fun `ignores indentation and the file legend`() {
            assertEquals(
                setOf(Coordinates.parse("d4")),
                reachableSquaresFromDiagram(
                    """
                            8 x x x x x x x x
                            7 x x x x x x x x
                            6 x x x x x x x x
                            5 x x x x x x x x
                            4 x x x . x x x x
                            3 x x x x x x x x
                            2 x x x x x x x x
                            1 x x x x x x x x
                              a b c d e f g h
                    """
                )
            )
        }

        @Test
        fun `rejects a board without eight ranks`() {
            assertFailsWith<IllegalArgumentException> {
                reachableSquaresFromDiagram(
                    """
                    8 x x x x x x x x
                    7 x x x x x x x x
                    6 x x x x x x x x
                    5 x x x x x x x x
                    4 x x x x x x x x
                    3 x x x x x x x x
                    2 x x x x x x x x
                      a b c d e f g h
                    """
                )
            }
        }

        @Test
        fun `rejects a rank without eight squares`() {
            assertFailsWith<IllegalArgumentException> {
                reachableSquaresFromDiagram(
                    """
                    8 x x x x x x x x
                    7 x x x x x x x x
                    6 x x x x x x x x
                    5 x x x x x x x x
                    4 x x x x x x x
                    3 x x x x x x x x
                    2 x x x x x x x x
                    1 x x x x x x x x
                      a b c d e f g h
                    """
                )
            }
        }
    }

    class DrawingAndReadingBack {

        @Test
        fun `reads back the exact set it draws`() {
            val reachable = setOf("a4", "f4", "h8").map(Coordinates::parse).toSet()

            assertEquals(
                reachable,
                reachableSquaresFromDiagram(
                    reachableMovesDiagram(
                        Position.parse("Rd4 qf4 Qb4").pieces,
                        Coordinates.parse("d4"),
                        reachable
                    )
                )
            )
        }
    }

    class DrawingABoard {

        @Test
        fun `draws an empty square the mover reaches as a dot`() {
            assertEquals(
                """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x x x x x x x x
                5 x x x x x x x x
                4 x x x . x x x x
                3 x x x x x x x x
                2 x x x x x x x x
                1 x x x x x x x x
                  a b c d e f g h
                """.trimIndent(),
                reachableMovesDiagram(
                    emptyMap(),
                    Coordinates.parse("a1"),
                    setOf(Coordinates.parse("d4"))
                )
            )
        }

        @Test
        fun `draws an empty square the mover does not reach as a cross`() {
            assertEquals(
                """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x x x x x x x x
                5 x x x x x x x x
                4 x x x x x x x x
                3 x x x x x x x x
                2 x x x x x x x x
                1 x x x x x x x x
                  a b c d e f g h
                """.trimIndent(),
                reachableMovesDiagram(emptyMap(), Coordinates.parse("a1"), emptySet())
            )
        }

        @Test
        fun `draws an occupied square the mover reaches as a capture`() {
            assertEquals(
                """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x x x x x x x x
                5 x x x x x x x x
                4 x x x R x o x x
                3 x x x x x x x x
                2 x x x x x x x x
                1 x x x x x x x x
                  a b c d e f g h
                """.trimIndent(),
                reachableMovesDiagram(
                    Position.parse("Rd4 qf4").pieces,
                    Coordinates.parse("d4"),
                    setOf(Coordinates.parse("f4"))
                )
            )
        }

        @Test
        fun `draws an occupied square the mover does not reach as a blocker`() {
            assertEquals(
                """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x x x x x x x x
                5 x x x x x x x x
                4 x x x R x # x x
                3 x x x x x x x x
                2 x x x x x x x x
                1 x x x x x x x x
                  a b c d e f g h
                """.trimIndent(),
                reachableMovesDiagram(
                    Position.parse("Rd4 Qf4").pieces,
                    Coordinates.parse("d4"),
                    emptySet()
                )
            )
        }

        @Test
        fun `draws the mover as its own symbol`() {
            assertEquals(
                """
                8 x x x x x x x x
                7 x x x x x x x x
                6 x x x x x x x x
                5 x x x x x x x x
                4 x x x r x x x x
                3 x x x x x x x x
                2 x x x x x x x x
                1 x x x x x x x x
                  a b c d e f g h
                """.trimIndent(),
                reachableMovesDiagram(
                    Position.parse("rd4").pieces,
                    Coordinates.parse("d4"),
                    emptySet()
                )
            )
        }

        @Test
        fun `draws rank eight on the top row and rank one on the bottom`() {
            assertEquals(
                """
                8 . . . . . . . .
                7 x x x x x x x x
                6 x x x x x x x x
                5 x x x x x x x x
                4 x x x x x x x x
                3 x x x x x x x x
                2 x x x x x x x x
                1 x x x x x x x x
                  a b c d e f g h
                """.trimIndent(),
                reachableMovesDiagram(
                    emptyMap(),
                    Coordinates.parse("a1"),
                    setOf("a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8")
                        .map(Coordinates::parse)
                        .toSet()
                )
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
                reachableMovesDiagram(
                    emptyMap(),
                    Coordinates.parse("h8"),
                    setOf("a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8")
                        .map(Coordinates::parse)
                        .toSet()
                )
            )
        }
    }

    class FindingWhatPlayAccepts {

        @Test
        fun `finds every square the side to move may play to`() {
            assertEquals(
                setOf("b1", "c1", "d1", "a2", "a3", "a4", "a5", "a6", "a7", "a8")
                    .map(Coordinates::parse)
                    .toSet(),
                Position.parse("Ra1 qd1").playableSquares(Coordinates.parse("a1"), Side.WHITE)
            )
        }

        @Test
        fun `finds nothing for a side that does not own the piece`() {
            assertEquals(
                emptySet(),
                Position.parse("Ra1 qd1").playableSquares(Coordinates.parse("a1"), Side.BLACK)
            )
        }
    }
}
