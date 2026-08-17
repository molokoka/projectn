package molokoka.project.n.util

import molokoka.project.n.domain.AnalysisTree
import molokoka.project.n.move_evaluation.MoveEvaluation
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.MoveNode
import molokoka.project.n.domain.Position
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The move tree as an indented outline, root first:
 *
 * - `Start` for the root, which holds no move
 * - `├──` on a move with a sibling under it, `└──` on the last of its siblings
 * - `│` carrying an unfinished branch down past the replies to an earlier move
 * - the evaluation directly after the move, when the node has one
 */
fun AnalysisTree.moveTreeDiagram(): String {
    val rows = nodes.moveTreeRows("")

    return (listOf("Start") + rows).joinToString("\n")
}

private fun List<MoveNode>.moveTreeRows(indent: String): List<String> =
    indices.flatMap { index ->
        val node = this[index]
        val isLastSibling = index == lastIndex

        val branch = if (isLastSibling) "└── " else "├── "
        val replyIndent = indent + if (isLastSibling) "    " else "│   "

        listOf("$indent$branch${node.move}${node.moveEvaluation ?: ""}") +
            node.nodes.moveTreeRows(replyIndent)
    }

private val mockPosition = Position.parse("Ra1")

class MoveTreeDiagramUtilTest {

    @Test
    fun `draws an empty tree as the start node alone`() {
        assertEquals(
            "Start",
            AnalysisTree().moveTreeDiagram()
        )
    }

    @Test
    fun `draws a move under the start node`() {
        assertEquals(
            """
            Start
            └── b2b4
            """.trimIndent(),
            AnalysisTree()
                .add(emptyList(), Move.parse("b2b4"), mockPosition)
                .moveTreeDiagram()
        )
    }

    @Test
    fun `draws a branch on every move but the last`() {
        assertEquals(
            """
            Start
            ├── b2b4
            └── d2d4
            """.trimIndent(),
            AnalysisTree()
                .add(emptyList(), Move.parse("b2b4"), mockPosition)
                .add(emptyList(), Move.parse("d2d4"), mockPosition)
                .moveTreeDiagram()
        )
    }

    @Test
    fun `draws a tree that branches at both depths`() {
        assertEquals(
            """
            Start
            ├── b2b4
            │   ├── a7a5
            │   └── c7c5
            └── d2d4
                └── e7e5
            """.trimIndent(),
            AnalysisTree()
                .add(emptyList(), Move.parse("b2b4"), mockPosition)
                .add(listOf(Move.parse("b2b4")), Move.parse("a7a5"), mockPosition)
                .add(listOf(Move.parse("b2b4")), Move.parse("c7c5"), mockPosition)
                .add(emptyList(), Move.parse("d2d4"), mockPosition)
                .add(listOf(Move.parse("d2d4")), Move.parse("e7e5"), mockPosition)
                .moveTreeDiagram()
        )
    }

    @Test
    fun `indents a reply under the move it answers`() {
        assertEquals(
            """
            Start
            └── b2b4
                └── a7a5
            """.trimIndent(),
            AnalysisTree()
                .add(emptyList(), Move.parse("b2b4"), mockPosition)
                .add(listOf(Move.parse("b2b4")), Move.parse("a7a5"), mockPosition)
                .moveTreeDiagram()
        )
    }

    @Test
    fun `carries an unfinished branch down past a reply`() {
        assertEquals(
            """
            Start
            ├── b2b4
            │   └── a7a5
            └── d2d4
            """.trimIndent(),
            AnalysisTree()
                .add(emptyList(), Move.parse("b2b4"), mockPosition)
                .add(listOf(Move.parse("b2b4")), Move.parse("a7a5"), mockPosition)
                .add(emptyList(), Move.parse("d2d4"), mockPosition)
                .moveTreeDiagram()
        )
    }

    @Test
    fun `draws the evaluation after the move it belongs to`() {
        val move = Move.parse("b2b4")

        assertEquals(
            """
            Start
            └── b2b4+
            """.trimIndent(),
            AnalysisTree()
                .add(emptyList(), move, mockPosition)
                .applyEvaluations(1, mapOf(listOf(move) to MoveEvaluation.WHITE_BETTER))
                .moveTreeDiagram()
        )
    }

    @Test
    fun `draws no evaluation on a move that has none`() {
        val evaluated = Move.parse("b2b4")
        val unevaluated = Move.parse("d2d4")

        assertEquals(
            """
            Start
            ├── b2b4=
            └── d2d4
            """.trimIndent(),
            AnalysisTree()
                .add(emptyList(), evaluated, mockPosition)
                .add(emptyList(), unevaluated, mockPosition)
                .applyEvaluations(1, mapOf(listOf(evaluated) to MoveEvaluation.EQUAL))
                .moveTreeDiagram()
        )
    }

    @Test
    fun `draws the evaluation of a reply after the indented reply`() {
        val opening = Move.parse("b2b4")
        val reply = Move.parse("a7a5")

        assertEquals(
            """
            Start
            └── b2b4+
                └── a7a5-
            """.trimIndent(),
            AnalysisTree()
                .add(emptyList(), opening, mockPosition)
                .add(listOf(opening), reply, mockPosition)
                .applyEvaluations(
                    1,
                    mapOf(
                        listOf(opening) to MoveEvaluation.WHITE_BETTER,
                        listOf(opening, reply) to MoveEvaluation.BLACK_BETTER
                    )
                )
                .moveTreeDiagram()
        )
    }
}
