package molokoka.project.n.move_evaluation

import kotlinx.coroutines.delay
import molokoka.project.n.domain.MoveNode
import molokoka.project.n.domain.paths
import molokoka.project.n.domain.Move
import kotlin.random.Random
import kotlin.random.nextLong
import kotlin.time.Duration.Companion.milliseconds

private val DELAY_RANGE_MILLIS = 1_000L..3_000L

class DelayedRandomMoveEvaluationSource(
    private val random: Random = Random.Default
) : MoveEvaluationSource {

    override suspend fun evaluate(nodes: List<MoveNode>): Map<List<Move>, MoveEvaluation> {
        delay(random.nextLong(DELAY_RANGE_MILLIS).milliseconds)

        return nodes.paths().associateWith { MoveEvaluation.entries.random(random) }
    }
}
