package molokoka.project.n.domain.nqueen

import molokoka.project.n.domain.chess.ChessCoordinates

fun isWinCondition(queens: Set<ChessCoordinates>, chessBoardSize: Int): Boolean {
    return queens.size == chessBoardSize && calculateNQueenConflicts(queens, chessBoardSize).conflictingQueens.isEmpty()
}