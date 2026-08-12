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
            ChessCoordinates.create("a1"),
            ChessCoordinates.create("c2")
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
            ChessCoordinates.create("a1"),
            ChessCoordinates.create("c1")
        )

        val result = calculateNQueenConflicts(queens)

        assertEquals(2, result.conflictingQueens.size)
        assertTrue(result.conflictingQueens.contains(ChessCoordinates.create("a1")))
        assertTrue(result.conflictingQueens.contains(ChessCoordinates.create("c1")))

        val expectedAttackLine = setOf(
            ChessCoordinates.create("a1"),
            ChessCoordinates.create("b1"),
            ChessCoordinates.create("c1")
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
            ChessCoordinates.create("a1"),
            ChessCoordinates.create("a3")
        )

        val result = calculateNQueenConflicts(queens)

        assertEquals(2, result.conflictingQueens.size)
        assertTrue(result.conflictingQueens.contains(ChessCoordinates.create("a1")))
        assertTrue(result.conflictingQueens.contains(ChessCoordinates.create("a3")))

        val expectedAttackLine = setOf(
            ChessCoordinates.create("a1"),
            ChessCoordinates.create("a2"),
            ChessCoordinates.create("a3")
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
            ChessCoordinates.create("a1"),
            ChessCoordinates.create("c3")
        )

        val result = calculateNQueenConflicts(queens)

        assertEquals(2, result.conflictingQueens.size)
        assertTrue(result.conflictingQueens.contains(ChessCoordinates.create("a1")))
        assertTrue(result.conflictingQueens.contains(ChessCoordinates.create("c3")))

        val expectedAttackLine = setOf(
            ChessCoordinates.create("a1"),
            ChessCoordinates.create("b2"),
            ChessCoordinates.create("c3")
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
            ChessCoordinates.create("a1"),
            ChessCoordinates.create("c1"),
            ChessCoordinates.create("a3")
        )

        val result = calculateNQueenConflicts(queens)

        assertEquals(3, result.conflictingQueens.size)
        assertTrue(result.conflictingQueens.contains(ChessCoordinates.create("a1")))
        assertTrue(result.conflictingQueens.contains(ChessCoordinates.create("c1")))
        assertTrue(result.conflictingQueens.contains(ChessCoordinates.create("a3")))

        assertTrue(result.attackLines.contains(ChessCoordinates.create("a1")))
        assertTrue(result.attackLines.contains(ChessCoordinates.create("b1")))
        assertTrue(result.attackLines.contains(ChessCoordinates.create("c1")))
        assertTrue(result.attackLines.contains(ChessCoordinates.create("a2")))
        assertTrue(result.attackLines.contains(ChessCoordinates.create("a3")))
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
