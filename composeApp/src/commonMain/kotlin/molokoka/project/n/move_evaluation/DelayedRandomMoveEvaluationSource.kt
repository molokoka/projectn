package molokoka.project.n.move_evaluation

import kotlinx.coroutines.delay
import molokoka.project.n.domain.AnalysisTree
import molokoka.project.n.domain.Move
import kotlin.random.Random
import kotlin.random.nextLong
import kotlin.time.Duration.Companion.milliseconds

private val DELAY_RANGE_MILLIS = 1_000L..3_000L

class DelayedRandomMoveEvaluationSource(
    private val random: Random = Random.Default
) : MoveEvaluationSource {

    override suspend fun evaluate(snapshotTree: AnalysisTree): Map<List<Move>, MoveEvaluation> {
        delay(random.nextLong(DELAY_RANGE_MILLIS).milliseconds)

        return snapshotTree.paths().associateWith { MoveEvaluation.entries.random(random) }
    }
}
