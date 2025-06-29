package molokoka.project.n

object GameLogic {
    fun isWinCondition(queens: Set<ChessCoordinate>, boardSize: Int): Boolean {
        // Win condition: boardSize queens placed with no conflicts
        return queens.size == boardSize && calculateConflicts(queens, boardSize).conflictingQueens.isEmpty()
    }
}