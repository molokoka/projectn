package molokoka.project.n.move_evaluation

import molokoka.project.n.domain.MoveNode
import molokoka.project.n.domain.Move

interface MoveEvaluationSource {

    suspend fun evaluate(nodes: List<MoveNode> = emptyList()): Map<List<Move>, MoveEvaluation>
}
