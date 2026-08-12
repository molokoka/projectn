package molokoka.project.n.domain.nqueen

import molokoka.project.n.domain.chess.ChessCoordinates
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NQueenConflictVisualizationTest {

    @Test
    fun testNoConflicts() {
        // . . . . . . . .  8
        // . . . . . . . .  7
        // . . . . . . . .  6
        // . . . . . . . .  5
        // . . . . . . . .  4
        // . . . . . . . .  3
        // . . Q . . . . .  2
        // Q . . . . . . .  1
        // a b c d e f g h
        val queens = setOf(
            ChessCoordinates.create('a', 1),
            ChessCoordinates.create('c', 2)
        )

        val result = calculateNQueenConflicts(queens)

        assertTrue(result.conflictingQueens.isEmpty())
        assertTrue(result.attackLines.isEmpty())
    }

    @Test
    fun testHorizontalConflict() {
        // . . . . . . . .  8
        // . . . . . . . .  7
        // . . . . . . . .  6
        // . . . . . . . .  5
        // . . . . . . . .  4
        // . . . . . . . .  3
        // . . . . . . . .  2
        // Q X Q . . . . .  1  <- horizontal attack line
        // a b c d e f g h
        val queens = setOf(
            ChessCoordinates.create('a', 1),
            ChessCoordinates.create('c', 1)
        )

        val result = calculateNQueenConflicts(queens)

        assertEquals(2, result.conflictingQueens.size)
        assertTrue(result.conflictingQueens.contains(ChessCoordinates.create('a', 1)))
        assertTrue(result.conflictingQueens.contains(ChessCoordinates.create('c', 1)))

        val expectedAttackLine = setOf(
            ChessCoordinates.create('a', 1),
            ChessCoordinates.create('b', 1),
            ChessCoordinates.create('c', 1)
        )
        assertEquals(expectedAttackLine, result.attackLines)
    }

    @Test
    fun testVerticalConflict() {
        // . . . . . . . .  8
        // . . . . . . . .  7
        // . . . . . . . .  6
        // . . . . . . . .  5
        // . . . . . . . .  4
        // Q . . . . . . .  3  ^
        // X . . . . . . .  2  | vertical attack line
        // Q . . . . . . .  1  v
        // a b c d e f g h
        val queens = setOf(
            ChessCoordinates.create('a', 1),
            ChessCoordinates.create('a', 3)
        )

        val result = calculateNQueenConflicts(queens)

        assertEquals(2, result.conflictingQueens.size)
        assertTrue(result.conflictingQueens.contains(ChessCoordinates.create('a', 1)))
        assertTrue(result.conflictingQueens.contains(ChessCoordinates.create('a', 3)))

        val expectedAttackLine = setOf(
            ChessCoordinates.create('a', 1),
            ChessCoordinates.create('a', 2),
            ChessCoordinates.create('a', 3)
        )
        assertEquals(expectedAttackLine, result.attackLines)
    }

    @Test
    fun testDiagonalConflict() {
        // . . . . . . . .  8
        // . . . . . . . .  7
        // . . . . . . . .  6
        // . . . . . . . .  5
        // . . . . . . . .  4
        // . . Q . . . . .  3  /
        // . X . . . . . .  2  / diagonal attack line
        // Q . . . . . . .  1  /
        // a b c d e f g h
        val queens = setOf(
            ChessCoordinates.create('a', 1),
            ChessCoordinates.create('c', 3)
        )

        val result = calculateNQueenConflicts(queens)

        assertEquals(2, result.conflictingQueens.size)
        assertTrue(result.conflictingQueens.contains(ChessCoordinates.create('a', 1)))
        assertTrue(result.conflictingQueens.contains(ChessCoordinates.create('c', 3)))

        val expectedAttackLine = setOf(
            ChessCoordinates.create('a', 1),
            ChessCoordinates.create('b', 2),
            ChessCoordinates.create('c', 3)
        )
        assertEquals(expectedAttackLine, result.attackLines)
    }

    @Test
    fun testMultipleConflicts() {
        // . . . . . . . .  8
        // . . . . . . . .  7
        // . . . . . . . .  6
        // . . . . . . . .  5
        // . . . . . . . .  4
        // Q . . . . . . .  3  ^
        // X . . . . . . .  2  | vertical attack line
        // Q X Q . . . . .  1  <- horizontal attack line
        // a b c d e f g h
        val queens = setOf(
            ChessCoordinates.create('a', 1),
            ChessCoordinates.create('c', 1),
            ChessCoordinates.create('a', 3)
        )

        val result = calculateNQueenConflicts(queens)

        assertEquals(3, result.conflictingQueens.size)
        assertTrue(result.conflictingQueens.contains(ChessCoordinates.create('a', 1)))
        assertTrue(result.conflictingQueens.contains(ChessCoordinates.create('c', 1)))
        assertTrue(result.conflictingQueens.contains(ChessCoordinates.create('a', 3)))

        assertTrue(result.attackLines.contains(ChessCoordinates.create('a', 1)))
        assertTrue(result.attackLines.contains(ChessCoordinates.create('b', 1)))
        assertTrue(result.attackLines.contains(ChessCoordinates.create('c', 1)))
        assertTrue(result.attackLines.contains(ChessCoordinates.create('a', 2)))
        assertTrue(result.attackLines.contains(ChessCoordinates.create('a', 3)))
    }

    @Test
    fun testEmptySet() {
        // . . . . . . . .  8
        // . . . . . . . .  7
        // . . . . . . . .  6
        // . . . . . . . .  5
        // . . . . . . . .  4
        // . . . . . . . .  3
        // . . . . . . . .  2
        // . . . . . . . .  1
        // a b c d e f g h
        val queens = emptySet<ChessCoordinates>()

        val result = calculateNQueenConflicts(queens)

        assertTrue(result.conflictingQueens.isEmpty())
        assertTrue(result.attackLines.isEmpty())
    }
}
