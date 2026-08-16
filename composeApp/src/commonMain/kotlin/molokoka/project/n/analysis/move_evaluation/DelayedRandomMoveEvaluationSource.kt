package molokoka.project.n.analysis.move_evaluation

import kotlinx.coroutines.delay
import molokoka.project.n.analysis.MoveNode
import molokoka.project.n.analysis.paths
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import kotlin.random.Random
import kotlin.random.nextLong
import kotlin.time.Duration.Companion.milliseconds

private val DELAY_RANGE_MILLIS = 1_000L..3_000L

class DelayedRandomMoveEvaluationSource(
    private val random: Random = Random.Default
) : MoveEvaluationSource {

    override suspend fun evaluate(
        initialPosition: Position,
        nodes: List<MoveNode>
    ): Map<List<Move>, MoveEvaluation> {
        delay(random.nextLong(DELAY_RANGE_MILLIS).milliseconds)

        return nodes.paths().associateWith { MoveEvaluation.entries.random(random) }
    }
}
