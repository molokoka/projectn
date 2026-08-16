package molokoka.project.n.computer_move

import kotlinx.coroutines.delay
import molokoka.project.n.domain.Coordinates
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.Side
import molokoka.project.n.domain.reachableSquares
import kotlin.random.Random
import kotlin.random.nextLong
import kotlin.time.Duration.Companion.milliseconds

private val DELAY_RANGE_MILLIS = 1_000L..3_000L

class DelayedRandomComputerMoveSource(
    private val random: Random = Random.Default
) : ComputerMoveSource {

    override suspend fun nextMove(position: Position, side: Side): Move? {
        delay(random.nextLong(DELAY_RANGE_MILLIS).milliseconds)

        return position.randomMove(side)
    }

    private fun Position.randomMove(side: Side): Move? {
        val origin = pieceSquaresInRandomOrder(side)
            .firstOrNull { square -> reachableSquares(square).isNotEmpty() }
            ?: return null

        val destinations = reachableSquares(origin)

        return Move(origin, destinations.random(random))
    }

    private fun Position.pieceSquaresInRandomOrder(side: Side): List<Coordinates> {
        val squares = pieces.filterValues { piece -> piece.side == side }.keys

        return squares.shuffled(random)
    }
}
