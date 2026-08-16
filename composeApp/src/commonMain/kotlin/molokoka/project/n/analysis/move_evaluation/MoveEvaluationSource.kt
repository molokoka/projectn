package molokoka.project.n.analysis.move_evaluation

import molokoka.project.n.analysis.MoveNode
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position

interface MoveEvaluationSource {

    suspend fun evaluate(
        initialPosition: Position = Position.INITIAL,
        nodes: List<MoveNode> = emptyList()
    ): Map<List<Move>, MoveEvaluation>
}