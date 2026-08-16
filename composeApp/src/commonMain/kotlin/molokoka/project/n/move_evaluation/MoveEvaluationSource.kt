package molokoka.project.n.move_evaluation

import molokoka.project.n.domain.MoveNode
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position

interface MoveEvaluationSource {

    suspend fun evaluate(
        initialPosition: Position = Position.INITIAL,
        nodes: List<MoveNode> = emptyList()
    ): Map<List<Move>, MoveEvaluation>
}