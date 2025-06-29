package molokoka.project.n.chess

import kotlin.math.abs

data class ConflictVisualization(
    val conflictingQueens: Set<ChessCoordinate> = mutableSetOf(),
    val highlightedSquares: Set<ChessCoordinate> = mutableSetOf()
)

fun calculateConflicts(queens: Set<ChessCoordinate>, boardSize: Int): ConflictVisualization {
    val conflictingQueens = mutableSetOf<ChessCoordinate>()
    val highlightedSquares = mutableSetOf<ChessCoordinate>()

    // Find all pairs of conflicting queens
    val queensList = queens.toList()
    for (i in queensList.indices) {
        for (j in i + 1 until queensList.size) {
            val queen1 = queensList[i]
            val queen2 = queensList[j]

            if (areQueensInConflict(queen1, queen2)) {
                conflictingQueens.add(queen1)
                conflictingQueens.add(queen2)
                highlightedSquares.addAll(getAttackLine(queen1, queen2, boardSize))
            }
        }
    }

    return ConflictVisualization(conflictingQueens, highlightedSquares)
}

private fun areQueensInConflict(queen1: ChessCoordinate, queen2: ChessCoordinate): Boolean {
    return queen1.rank == queen2.rank ||  // Same rank (horizontal)
            queen1.file == queen2.file ||  // Same file (vertical)
            abs(queen1.row - queen2.row) == abs(queen1.col - queen2.col)  // Same diagonal
}

private fun getAttackLine(queen1: ChessCoordinate, queen2: ChessCoordinate, boardSize: Int): Set<ChessCoordinate> {
    val highlighted = mutableSetOf<ChessCoordinate>()

    when {
        // Same rank (horizontal line)
        queen1.rank == queen2.rank -> {
            val startFile = minOf(queen1.file, queen2.file)
            val endFile = maxOf(queen1.file, queen2.file)
            for (file in startFile..endFile) {
                highlighted.add(ChessCoordinate(file, queen1.rank, boardSize))
            }
        }
        // Same file (vertical line)
        queen1.file == queen2.file -> {
            val startRank = minOf(queen1.rank, queen2.rank)
            val endRank = maxOf(queen1.rank, queen2.rank)
            for (rank in startRank..endRank) {
                highlighted.add(ChessCoordinate(queen1.file, rank, boardSize))
            }
        }
        // Diagonal line
        abs(queen1.row - queen2.row) == abs(queen1.col - queen2.col) -> {
            val rowStep = if (queen2.row > queen1.row) 1 else -1
            val colStep = if (queen2.col > queen1.col) 1 else -1

            var currentRow = queen1.row
            var currentCol = queen1.col

            while (currentRow != queen2.row || currentCol != queen2.col) {
                val file = ('a' + currentCol)
                val rank = boardSize - currentRow
                highlighted.add(ChessCoordinate(file, rank, boardSize))
                currentRow += rowStep
                currentCol += colStep
            }
            // Add the final square
            highlighted.add(queen2)
        }
    }

    return highlighted
}