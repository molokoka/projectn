package molokoka.project.n.domain

data class MoveNode(
    val move: Move,
    val nodes: List<MoveNode> = emptyList()
)

data class AnalyticsTree(
    val initialPosition: Position = Position.INITIAL,
    val nodes: List<MoveNode> = emptyList()
) {
    fun contains(path: List<Move>): Boolean = nodes.nodeForPath(path) != null

    fun positionAt(path: List<Move>): Position {
        requireNode(path)

        return initialPosition.play(path)
    }

    fun play(path: List<Move>, move: Move): AnalyticsTree {
        requireNode(path)

        return copy(nodes = nodes.play(path, 0, move))
    }

    fun paths(): List<List<Move>> = nodes.paths(emptyList())

    private fun List<MoveNode>.paths(prefix: List<Move>): List<List<Move>> =
        flatMap { node ->
            val path = prefix + node.move

            listOf(path) + node.nodes.paths(path)
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



