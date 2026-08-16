package molokoka.project.n.domain

import molokoka.project.n.util.fromDiagram
import molokoka.project.n.util.positionDiagram
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ChessTest {

    @Test
    fun `moves a piece to an empty square`() {
        val position = fromDiagram(
            """
            8 . . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 . . . . . . . .
            4 . . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 R . . . . . . .
              a b c d e f g h
            """
        )

        assertEquals(
            """
            8 . . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 . . . . . . . .
            4 R . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 . . . . . . . .
              a b c d e f g h
            """.trimIndent(),
            position.play(Move.parse("a1a4"), Side.WHITE).positionDiagram()
        )
    }

    @Test
    fun `captures an opposing piece`() {
        val position = fromDiagram(
            """
            8 . . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 . . . . . . . .
            4 r . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 R . . . . . . .
              a b c d e f g h
            """
        )

        assertEquals(
            """
            8 . . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 . . . . . . . .
            4 R . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 . . . . . . . .
              a b c d e f g h
            """.trimIndent(),
            position.play(Move.parse("a1a4"), Side.WHITE).positionDiagram()
        )
    }

    @Test
    fun `rejects landing on a piece of the same side`() {
        val position = fromDiagram(
            """
            8 . . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 . . . . . . . .
            4 R . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 R . . . . . . .
              a b c d e f g h
            """
        )

        assertFailsWith<IllegalArgumentException> {
            position.play(Move.parse("a1a4"), Side.WHITE)
        }
    }

    @Test
    fun `rejects a move to its own square`() {
        val position = fromDiagram(
            """
            8 . . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 . . . . . . . .
            4 . . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 R . . . . . . .
              a b c d e f g h
            """
        )

        assertFailsWith<IllegalArgumentException> {
            position.play(Move.parse("a1a1"), Side.WHITE)
        }
    }

    @Test
    fun `rejects a move from an empty square`() {
        val position = fromDiagram(
            """
            8 . . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 . . . . . . . .
            4 . . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 R . . . . . . .
              a b c d e f g h
            """
        )

        assertFailsWith<IllegalArgumentException> {
            position.play(Move.parse("b1b4"), Side.WHITE)
        }
    }

    @Test
    fun `rejects a move to a file beyond the board`() {
        val position = fromDiagram(
            """
            8 . . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 . . . . . . . .
            4 . . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 R . . . . . . .
              a b c d e f g h
            """
        )

        assertFailsWith<IllegalArgumentException> {
            position.play(Move.parse("a1i1"), Side.WHITE)
        }
    }

    @Test
    fun `rejects a move to a rank beyond the board`() {
        val position = fromDiagram(
            """
            8 . . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 . . . . . . . .
            4 . . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 R . . . . . . .
              a b c d e f g h
            """
        )

        assertFailsWith<IllegalArgumentException> {
            position.play(Move.parse("a1a9"), Side.WHITE)
        }
    }

    @Test
    fun `rejects moving an opposing piece`() {
        val position = fromDiagram(
            """
            8 r . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 . . . . . . . .
            4 . . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 R . . . . . . .
              a b c d e f g h
            """
        )

        assertFailsWith<IllegalArgumentException> {
            position.play(Move.parse("a8a5"), Side.WHITE)
        }
    }

    @Test
    fun `moves a piece of the side to move`() {
        val position = fromDiagram(
            """
            8 r . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 . . . . . . . .
            4 . . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 . . . . . . . .
              a b c d e f g h
            """
        )

        assertEquals(
            """
            8 . . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 r . . . . . . .
            4 . . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 . . . . . . . .
              a b c d e f g h
            """.trimIndent(),
            position.play(Move.parse("a8a5"), Side.BLACK).positionDiagram()
        )
    }

    @Test
    fun `plays a line of moves alternating sides`() {
        val position = fromDiagram(
            """
            8 r . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 . . . . . . . .
            4 . . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 R . . . . . . .
              a b c d e f g h
            """
        )

        assertEquals(
            """
            8 . . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 r . . . . . . .
            4 R . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 . . . . . . . .
              a b c d e f g h
            """.trimIndent(),
            position.play(listOf(Move.parse("a1a4"), Move.parse("a8a5"))).positionDiagram()
        )
    }

    @Test
    fun `plays no moves at all`() {
        val position = fromDiagram(
            """
            8 . . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 . . . . . . . .
            4 . . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 R . . . . . . .
              a b c d e f g h
            """
        )

        assertEquals(
            """
            8 . . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 . . . . . . . .
            4 . . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 R . . . . . . .
              a b c d e f g h
            """.trimIndent(),
            position.play(emptyList()).positionDiagram()
        )
    }

    @Test
    fun `rejects a line where one side moves twice`() {
        val position = fromDiagram(
            """
            8 . . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 . . . . . . . .
            4 . . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 R R . . . . . .
              a b c d e f g h
            """
        )

        assertFailsWith<IllegalArgumentException> {
            position.play(listOf(Move.parse("a1a4"), Move.parse("b1b5")))
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
