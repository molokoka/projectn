package molokoka.project.n.analysis

import molokoka.project.n.analysis.move_evaluation.MoveEvaluation
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.play

data class MoveNode(
    val move: Move,
    val moveEvaluation: MoveEvaluation? = null,
    val nodes: List<MoveNode> = emptyList(),
)

data class AnalysisTree(
    val initialPosition: Position = Position.INITIAL,
    val nodes: List<MoveNode> = emptyList(),
    val evaluationGeneration: Int = 0
) {
    fun contains(path: List<Move>): Boolean = nodes.nodeForPath(path) != null

    fun positionAt(path: List<Move>): Position {
        requireNode(path)

        return initialPosition.play(path)
    }

    fun play(path: List<Move>, move: Move): AnalysisTree {
        requireNode(path)

        return copy(nodes = nodes.play(path, 0, move))
    }

    fun paths(): List<List<Move>> = nodes.paths()

    fun withEvaluations(
        generation: Int,
        evaluations: Map<List<Move>, MoveEvaluation>
    ): AnalysisTree =
        copy(
            nodes = nodes.withEvaluations(evaluations, emptyList()),
            evaluationGeneration = generation
        )

    fun evaluationAt(path: List<Move>): MoveEvaluation? =
        if (path.isEmpty()) {
            null
        } else {
            nodes.nodeForPath(path.dropLast(1))
                ?.firstOrNull { node -> node.move == path.last() }
                ?.moveEvaluation
        }

    private fun List<MoveNode>.withEvaluations(
        evaluations: Map<List<Move>, MoveEvaluation>,
        prefix: List<Move>
    ): List<MoveNode> =
        map { node ->
            val path = prefix + node.move

            node.copy(
                moveEvaluation = evaluations[path] ?: node.moveEvaluation,
                nodes = node.nodes.withEvaluations(evaluations, path)
            )
        }

    private fun requireNode(path: List<Move>) {
        require(contains(path)) { "Tree has no node at '${path.joinToString(" ")}'" }
    }

    private fun List<MoveNode>.play(path: List<Move>, depth: Int, move: Move): List<MoveNode> =
        if (depth == path.size) {
            if (any { it.move == move }) {
                this
            } else {
                // verify that move is playable
                requireNotNull(initialPosition.play(path + move))

                this + MoveNode(move)
            }
        } else {
            map { node ->
                if (node.move == path[depth]) {
                    node.copy(nodes = node.nodes.play(path, depth + 1, move))
                } else {
                    node
                }
            }
        }
    
    private fun List<MoveNode>.nodeForPath(path: List<Move>, depth: Int = 0): List<MoveNode>? =
        if (depth == path.size) {
            this
        } else {
            firstOrNull { it.move == path[depth] }?.nodes?.nodeForPath(path, depth + 1)
        }
}

//todo think about it
fun List<MoveNode>.paths(prefix: List<Move> = emptyList()): List<List<Move>> =
    flatMap { node ->
        val path = prefix + node.move

        listOf(path) + node.nodes.paths(path)
    }



