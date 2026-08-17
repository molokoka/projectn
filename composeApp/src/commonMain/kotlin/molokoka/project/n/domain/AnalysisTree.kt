package molokoka.project.n.domain

import kotlinx.serialization.Serializable
import molokoka.project.n.move_evaluation.MoveEvaluation

@Serializable
data class MoveNode(
    val move: Move,
    val position: Position,
    val moveEvaluation: MoveEvaluation? = null,
    val nodes: List<MoveNode> = emptyList(),
)

@Serializable
data class AnalysisTree(
    val initialPosition: Position = Position.INITIAL,
    val nodes: List<MoveNode> = emptyList(),
    val evaluationGeneration: Int = 0
) {
    fun contains(path: List<Move>): Boolean = nodes.nodeForPath(path) != null

    fun positionAt(path: List<Move>): Position {
        requireNode(path)

        return nodeAt(path)?.position ?: initialPosition
    }

    fun add(path: List<Move>, move: Move, position: Position): AnalysisTree {
        requireNode(path)

        return copy(nodes = nodes.add(path, 0, move, position))
    }

    fun paths(): List<List<Move>> = nodes.paths(emptyList())

    fun applyEvaluations(
        generation: Int,
        evaluations: Map<List<Move>, MoveEvaluation>
    ): AnalysisTree =
        copy(
            nodes = nodes.applyEvaluations(evaluations, emptyList()),
            evaluationGeneration = generation
        )

    fun evaluationAt(path: List<Move>): MoveEvaluation? = nodeAt(path)?.moveEvaluation

    private fun nodeAt(path: List<Move>): MoveNode? =
        if (path.isEmpty()) {
            null
        } else {
            nodes.nodeForPath(path.dropLast(1))
                ?.firstOrNull { node -> node.move == path.last() }
        }

    private fun List<MoveNode>.paths(prefix: List<Move>): List<List<Move>> =
        flatMap { node ->
            val path = prefix + node.move

            listOf(path) + node.nodes.paths(path)
        }

    private fun List<MoveNode>.applyEvaluations(
        evaluations: Map<List<Move>, MoveEvaluation>,
        prefix: List<Move>
    ): List<MoveNode> =
        map { node ->
            val path = prefix + node.move

            node.copy(
                moveEvaluation = evaluations[path] ?: node.moveEvaluation,
                nodes = node.nodes.applyEvaluations(evaluations, path)
            )
        }

    private fun requireNode(path: List<Move>) {
        require(contains(path)) { "Tree has no node at '${path.joinToString(" ")}'" }
    }

    private fun List<MoveNode>.add(
        path: List<Move>,
        depth: Int,
        move: Move,
        position: Position
    ): List<MoveNode> =
        if (depth == path.size) {
            if (any { node -> node.move == move }) {
                this
            } else {
                this + MoveNode(move, position)
            }
        } else {
            map { node ->
                if (node.move == path[depth]) {
                    node.copy(nodes = node.nodes.add(path, depth + 1, move, position))
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
