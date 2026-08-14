package molokoka.project.n.computer

import molokoka.project.n.domain.AnalyticsTree
import molokoka.project.n.domain.Move

interface ComputerMoveSource {

    suspend fun nextMove(tree: AnalyticsTree, path: List<Move>): Move?
}
