package molokoka.project.n.domain.nqueen

import molokoka.project.n.domain.chess.BOARD_SIZE
import molokoka.project.n.domain.chess.ChessCoordinates

fun isWinCondition(queens: Set<ChessCoordinates>): Boolean {
    return queens.size == BOARD_SIZE && calculateNQueenConflicts(queens).conflictingQueens.isEmpty()
}
