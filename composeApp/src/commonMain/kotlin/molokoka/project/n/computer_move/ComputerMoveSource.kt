package molokoka.project.n.computer_move

import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.Side

interface ComputerMoveSource {

    suspend fun nextMove(position: Position, side: Side): Move?
}
