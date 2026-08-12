package molokoka.project.n.domain.nqueen

import molokoka.project.n.domain.chess.ChessCoordinates
import kotlin.math.abs

data class NQueenConflictVisualization(
    val conflictingQueens: Set<ChessCoordinates> = mutableSetOf(),
    val attackLines: Set<ChessCoordinates> = mutableSetOf()
)

fun calculateNQueenConflicts(queens: Set<ChessCoordinates>): NQueenConflictVisualization {
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
                attackLines.addAll(getAttackLine(queen1, queen2))
            }
        }
    }

    return NQueenConflictVisualization(conflictingQueens, attackLines)
}

private fun areQueensInConflict(queen1: ChessCoordinates, queen2: ChessCoordinates): Boolean {
    return queen1.rank == queen2.rank ||  // Same rank (horizontal)
            queen1.file == queen2.file ||  // Same file (vertical)
            abs(queen1.rank - queen2.rank) == abs(queen1.file - queen2.file)  // Same diagonal
}

private fun getAttackLine(queen1: ChessCoordinates, queen2: ChessCoordinates): Set<ChessCoordinates> {
    val attachLine = mutableSetOf<ChessCoordinates>()

    when {
        // Same rank (horizontal line)
        queen1.rank == queen2.rank -> {
            val startFile = minOf(queen1.file, queen2.file)
            val endFile = maxOf(queen1.file, queen2.file)
            for (file in startFile..endFile) {
                attachLine.add(ChessCoordinates(file, queen1.rank))
            }
        }
        // Same file (vertical line)
        queen1.file == queen2.file -> {
            val startRank = minOf(queen1.rank, queen2.rank)
            val endRank = maxOf(queen1.rank, queen2.rank)
            for (rank in startRank..endRank) {
                attachLine.add(ChessCoordinates(queen1.file, rank))
            }
        }
        // Diagonal line
        abs(queen1.rank - queen2.rank) == abs(queen1.file - queen2.file) -> {
            val rankStep = if (queen2.rank > queen1.rank) 1 else -1
            val fileStep = if (queen2.file > queen1.file) 1 else -1

            var currentRank = queen1.rank
            var currentFile = queen1.file

            // Add all squares along the diagonal from queen1 to queen2
            while (currentRank != queen2.rank || currentFile != queen2.file) {
                attachLine.add(ChessCoordinates(currentFile, currentRank))
                currentRank += rankStep
                currentFile += fileStep
            }
            // Add the final square
            attachLine.add(queen2)
        }
    }

    return attachLine
}