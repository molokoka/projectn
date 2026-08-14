package molokoka.project.n.computer

import kotlinx.coroutines.delay
import molokoka.project.n.domain.AnalyticsTree
import molokoka.project.n.domain.Move
import kotlin.random.Random

private const val MINIMUM_DELAY = 1_000L
private const val MAXIMUM_DELAY = 3_000L

class ScriptedComputerMoveSource : ComputerMoveSource {

    override suspend fun nextMove(tree: AnalyticsTree, path: List<Move>): Move? {
        @Suppress("ConvertLongToDuration")
        delay(Random.nextLong(MINIMUM_DELAY, MAXIMUM_DELAY))

        return listOf("a1a4", "b8b5", "c1c4", "d8d5").map(Move.Companion::parse)
            .getOrNull(path.size)
            ?.takeIf { move -> runCatching { tree.play(path, move) }.isSuccess }
    }
}
