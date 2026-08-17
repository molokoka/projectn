package molokoka.project.n.move_evaluation

import molokoka.project.n.domain.AnalysisTree
import molokoka.project.n.domain.Move

interface MoveEvaluationSource {

    suspend fun evaluate(snapshotTree: AnalysisTree): Map<List<Move>, MoveEvaluation>
}
