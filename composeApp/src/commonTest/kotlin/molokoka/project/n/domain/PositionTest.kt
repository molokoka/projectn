package molokoka.project.n.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PositionTest {

    @Test
    fun `places a parsed piece on its square`() {
        val position = Position.parse("Ra1")

        assertEquals(
            mapOf(Coordinates.create("a1") to Piece(PieceType.ROOK, Side.WHITE)),
            position.pieces
        )
    }

    @Test
    fun `reads every piece in the notation`() {
        val position = Position.parse("Ra1 qb8")

        assertEquals(
            mapOf(
                Coordinates.create("a1") to Piece(PieceType.ROOK, Side.WHITE),
                Coordinates.create("b8") to Piece(PieceType.QUEEN, Side.BLACK)
            ),
            position.pieces
        )
    }

    @Test
    fun `rejects two pieces on the same square`() {
        assertFailsWith<IllegalArgumentException> { Position.parse("Ra1 Qa1") }
    }

    @Test
    fun `rejects a piece token that is not three characters`() {
        assertFailsWith<IllegalArgumentException> { Position.parse("R") }
        assertFailsWith<IllegalArgumentException> { Position.parse("Ra11") }
    }

    @Test
    fun `rejects a piece token whose letter is not a piece`() {
        assertFailsWith<IllegalArgumentException> { Position.parse("Ka1") }
    }

    @Test
    fun `rejects a piece on a square beyond the board`() {
        assertFailsWith<IllegalArgumentException> { Position.parse("Ri1") }
    }

    @Test
    fun `writes a piece as its symbol and square`() {
        assertEquals("Ra1", Position.parse("Ra1").toString())
    }

    @Test
    fun `writes pieces in rank order`() {
        assertEquals("Ra1 qb8", Position.parse("qb8 Ra1").toString())
    }

    @Test
    fun `writes pieces in file order within a rank`() {
        assertEquals("Ra1 Qc1", Position.parse("Qc1 Ra1").toString())
    }
}