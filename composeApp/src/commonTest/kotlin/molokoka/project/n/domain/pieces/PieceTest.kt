package molokoka.project.n.domain.pieces

import molokoka.project.n.domain.Side
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PieceTest {

    @Test
    fun `reads an uppercase symbol as a white piece`() {
        assertEquals(Piece(PieceType.ROOK, Side.WHITE), Piece.fromSymbol('R'))
    }

    @Test
    fun `reads a lowercase symbol as a black piece`() {
        assertEquals(Piece(PieceType.QUEEN, Side.BLACK), Piece.fromSymbol('q'))
    }

    @Test
    fun `writes a white piece as an uppercase symbol`() {
        assertEquals('R', Piece(PieceType.ROOK, Side.WHITE).symbol)
    }

    @Test
    fun `writes a black piece as a lowercase symbol`() {
        assertEquals('q', Piece(PieceType.QUEEN, Side.BLACK).symbol)
    }

    @Test
    fun `rejects a symbol that is not a piece letter`() {
        assertFailsWith<IllegalArgumentException> { Piece.fromSymbol('k') }
    }
}
