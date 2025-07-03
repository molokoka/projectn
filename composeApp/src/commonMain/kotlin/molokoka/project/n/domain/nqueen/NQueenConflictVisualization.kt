package molokoka.project.n.domain.nqueen

import molokoka.project.n.domain.chess.ChessCoordinates
import kotlin.math.abs

data class NQueenConflictVisualization(
    val conflictingQueens: Set<ChessCoordinates> = mutableSetOf(),
    val attackLines: Set<ChessCoordinates> = mutableSetOf()
)

fun calculateNQueenConflicts(queens: Set<ChessCoordinates>, chessBoardSize: Int): NQueenConflictVisualization {
    val conflictingQueens = mutableSetOf<ChessCoordinates>()
    val attackLines = mutableSetOf<ChessCoordinates>()

    // Find all pairs of conflicting queens
    val queensList = queens.toList()
    for (i in queensList.indices) {
        for (j in i + 1 until queensList.size) {
            val queen1 = queensList[i]
            val queen2 = queensList[j]

            if (areQueensInConflict(queen1, queen2)) {
                conflictingQueens.add(queen1)
                conflictingQueens.add(queen2)
                attackLines.addAll(getAttackLine(queen1, queen2, chessBoardSize))
            }
        }
    }

    return NQueenConflictVisualization(conflictingQueens, attackLines)
}

private fun areQueensInConflict(queen1: ChessCoordinates, queen2: ChessCoordinates): Boolean {
    return queen1.rank == queen2.rank ||  // Same rank (horizontal)
            queen1.file == queen2.file ||  // Same file (vertical)
            abs(queen1.row - queen2.row) == abs(queen1.col - queen2.col)  // Same diagonal
}

private fun getAttackLine(queen1: ChessCoordinates, queen2: ChessCoordinates, chessBoardSize: Int): Set<ChessCoordinates> {
    val attachLine = mutableSetOf<ChessCoordinates>()

    when {
        // Same rank (horizontal line)
        queen1.rank == queen2.rank -> {
            val startFile = minOf(queen1.file, queen2.file)
            val endFile = maxOf(queen1.file, queen2.file)
            for (file in startFile..endFile) {
                attachLine.add(ChessCoordinates.Companion.create(file, queen1.rank, chessBoardSize))
            }
        }
        // Same file (vertical line)
        queen1.file == queen2.file -> {
            val startRank = minOf(queen1.rank, queen2.rank)
            val endRank = maxOf(queen1.rank, queen2.rank)
            for (rank in startRank..endRank) {
                attachLine.add(ChessCoordinates.Companion.create(queen1.file, rank, chessBoardSize))
            }
        }
        // Diagonal line
        abs(queen1.row - queen2.row) == abs(queen1.col - queen2.col) -> {
            val rowStep = if (queen2.row > queen1.row) 1 else -1
            val colStep = if (queen2.col > queen1.col) 1 else -1

            var currentRow = queen1.row
            var currentCol = queen1.col

            // Add all squares along the diagonal from queen1 to queen2
            while (currentRow != queen2.row || currentCol != queen2.col) {
                attachLine.add(ChessCoordinates.Companion.fromRowCol(currentRow, currentCol, chessBoardSize))
                currentRow += rowStep
                currentCol += colStep
            }
            // Add the final square
            attachLine.add(queen2)
        }
    }

    return attachLine
}