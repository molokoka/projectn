package molokoka.project.n.computer

import kotlinx.coroutines.test.runTest
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.Side
import molokoka.project.n.domain.play
import molokoka.project.n.domain.util.fromDiagram
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DelayedRandomComputerMoveSourceTest {

    private fun computerMoveSource() = DelayedRandomComputerMoveSource(Random(20_260_815))

    @Test
    fun `generates a move for white at start`() = runTest {
        val move = assertNotNull(computerMoveSource().nextMove(Position.INITIAL, Side.WHITE))

        assertEquals(Side.WHITE, Position.INITIAL.pieces.getValue(move.from).side)
    }

    @Test
    fun `generates a move for black once white has moved`() = runTest {
        val position = Position.INITIAL.play(Move.parse("a1a4"), Side.WHITE)

        val move = assertNotNull(computerMoveSource().nextMove(position, Side.BLACK))

        assertEquals(Side.BLACK, position.pieces.getValue(move.from).side)
    }

    @Test
    fun `generates a move the position accepts`() = runTest {
        val move = assertNotNull(computerMoveSource().nextMove(Position.INITIAL, Side.WHITE))

        val played = runCatching { Position.INITIAL.play(move, Side.WHITE) }

        assertNotNull(
            played.getOrNull(),
            "the position rejected '$move': ${played.exceptionOrNull()?.message}"
        )
    }

    @Test
    fun `finds last possible move`() = runTest {
        val boardWithOnePossibleMove = fromDiagram(
            """
            8 R R R R R R R .
            7 R R R R R R R R
            6 R R R R R R R R
            5 R R R R R R R R
            4 R R R R R R R R
            3 R R R R R R R R
            2 R R R R R R R R
            1 R R R R R R R R
              a b c d e f g h
            """
        )

        val move = assertNotNull(computerMoveSource().nextMove(boardWithOnePossibleMove, Side.WHITE))

        assertContains(setOf("g8h8", "h7h8").map(Move::parse), move)
    }

    @Test
    fun `returns nothing when no piece of the side to move can move`() = runTest {
        val boardWithNoMoves = fromDiagram(
            """
            8 R R R R R R R R
            7 R R R R R R R R
            6 R R R R R R R R
            5 R R R R R R R R
            4 R R R R R R R R
            3 R R R R R R R R
            2 R R R R R R R R
            1 R R R R R R R R
              a b c d e f g h
            """
        )

        assertNull(computerMoveSource().nextMove(boardWithNoMoves, Side.WHITE))
    }

    @Test
    fun `returns nothing when the side to move has no piece`() = runTest {
        assertNull(computerMoveSource().nextMove(Position.parse("qd4 rf6"), Side.WHITE))
    }
}
