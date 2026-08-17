package molokoka.project.n.domain

import molokoka.project.n.move_evaluation.MoveEvaluation
import molokoka.project.n.util.moveTreeDiagram
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private val initialPosition = Position.parse("Ra1 ra8")
private val mockPosition = Position.parse("Ra1")

class AnalysisTreeTest {

    // positionAt

    @Test
    fun `shows the initial position at the root`() {
        val initialPosition = "Ra1 ra8"

        val tree = AnalysisTree(Position.parse(initialPosition))

        assertEquals(initialPosition, tree.positionAt(emptyList()).toString())
    }

    @Test
    fun `hands back the position a move was added with`() {
        val move = Move.parse("a1a4")
        val firstMovePosition = "Ra4 ra8"

        val tree = AnalysisTree(initialPosition)
            .add(emptyList(), move, Position.parse(firstMovePosition))

        assertEquals(firstMovePosition, tree.positionAt(listOf(move)).toString())
    }

    @Test
    fun `hands back the position a reply was added with`() {
        val opening = Move.parse("a1a4")
        val reply = Move.parse("a8a5")
        val secondMovePosition = "Ra4 ra5"

        val tree = AnalysisTree(initialPosition)
            .add(emptyList(), opening, mockPosition)
            .add(listOf(opening), reply, Position.parse(secondMovePosition))

        assertEquals(secondMovePosition, tree.positionAt(listOf(opening, reply)).toString())
    }

    @Test
    fun `keeps a separate position for each sibling`() {
        val move = Move.parse("a1a4")
        val sibling = Move.parse("a1a3")
        val movePosition = "Ra4 ra8"
        val siblingPosition = "Ra3 ra8"

        val tree = AnalysisTree(initialPosition)
            .add(emptyList(), move, Position.parse(movePosition))
            .add(emptyList(), sibling, Position.parse(siblingPosition))

        assertEquals(movePosition, tree.positionAt(listOf(move)).toString())
        assertEquals(siblingPosition, tree.positionAt(listOf(sibling)).toString())
    }

    @Test
    fun `has no position at a path that is not in the tree`() {
        assertFailsWith<IllegalArgumentException> {
            AnalysisTree(initialPosition)
                .positionAt(listOf(Move.parse("a1a4")))
        }
    }

    // add

    @Test
    fun `adds a node at the root`() {
        val move = Move.parse("a1a4")

        val tree = AnalysisTree(initialPosition)
            .add(emptyList(), move, mockPosition)

        assertEquals(
            """
            Start
            └── $move
            """.trimIndent(),
            tree.moveTreeDiagram()
        )
    }

    @Test
    fun `adds a node at depth`() {
        val opening = Move.parse("a1a4")
        val reply = Move.parse("a8a5")

        val tree = AnalysisTree(initialPosition)
            .add(emptyList(), opening, mockPosition)
            .add(listOf(opening), reply, mockPosition)

        assertEquals(
            """
            Start
            └── $opening
                └── $reply
            """.trimIndent(),
            tree.moveTreeDiagram()
        )
    }

    @Test
    fun `adds a node three levels deep`() {
        val opening = Move.parse("a1a4")
        val reply = Move.parse("a8a5")
        val deepMove = Move.parse("a4b4")

        val tree = AnalysisTree(initialPosition)
            .add(emptyList(), opening, mockPosition)
            .add(listOf(opening), reply, mockPosition)
            .add(listOf(opening, reply), deepMove, mockPosition)

        assertEquals(
            """
            Start
            └── $opening
                └── $reply
                    └── $deepMove
            """.trimIndent(),
            tree.moveTreeDiagram()
        )
    }

    @Test
    fun `adds a sibling node for a different move`() {
        val move = Move.parse("a1a4")
        val sibling = Move.parse("a1a3")

        val tree = AnalysisTree(initialPosition)
            .add(emptyList(), move, mockPosition)
            .add(emptyList(), sibling, mockPosition)

        assertEquals(
            """
            Start
            ├── $move
            └── $sibling
            """.trimIndent(),
            tree.moveTreeDiagram()
        )
    }

    @Test
    fun `adds a sibling node at depth`() {
        val opening = Move.parse("a1a4")
        val reply = Move.parse("a8a5")
        val sibling = Move.parse("a8a6")

        val tree = AnalysisTree(initialPosition)
            .add(emptyList(), opening, mockPosition)
            .add(listOf(opening), reply, mockPosition)
            .add(listOf(opening), sibling, mockPosition)

        assertEquals(
            """
            Start
            └── $opening
                ├── $reply
                └── $sibling
            """.trimIndent(),
            tree.moveTreeDiagram()
        )
    }

    @Test
    fun `reuses a node that already carries the move`() {
        val opening = Move.parse("a1a4")
        val reply = Move.parse("a8a5")

        val tree = AnalysisTree(initialPosition)
            .add(emptyList(), opening, mockPosition)
            .add(listOf(opening), reply, mockPosition)
            .add(emptyList(), opening, mockPosition)

        assertEquals(
            """
            Start
            └── $opening
                └── $reply
            """.trimIndent(),
            tree.moveTreeDiagram()
        )
    }

    @Test
    fun `keeps the position a reused node was first added with`() {
        val move = Move.parse("a1a4")
        val firstPosition = "Ra4 ra8"
        val laterPosition = "Ra4 ra5"

        val tree = AnalysisTree(initialPosition)
            .add(emptyList(), move, Position.parse(firstPosition))
            .add(emptyList(), move, Position.parse(laterPosition))

        assertEquals(firstPosition, tree.positionAt(listOf(move)).toString())
    }

    @Test
    fun `refuses to add at a path that is not in the tree`() {
        assertFailsWith<IllegalArgumentException> {
            AnalysisTree(initialPosition)
                .add(listOf(Move.parse("a1a4")), Move.parse("a8a5"), mockPosition)
        }
    }

    // paths

    @Test
    fun `lists the path to every node depth first`() {
        val opening = Move.parse("a1a4")
        val reply = Move.parse("a8a5")
        val variation = Move.parse("a1a3")

        val tree = AnalysisTree(initialPosition)
            .add(emptyList(), opening, mockPosition)
            .add(listOf(opening), reply, mockPosition)
            .add(emptyList(), variation, mockPosition)

        assertEquals(
            """
            Start
            ├── $opening
            │   └── $reply
            └── $variation
            """.trimIndent(),
            tree.moveTreeDiagram()
        )
        assertEquals(
            listOf(
                listOf(opening),
                listOf(opening, reply),
                listOf(variation)
            ),
            tree.paths()
        )
    }

    @Test
    fun `lists no paths for an empty tree`() {
        assertEquals(emptyList(), AnalysisTree(initialPosition).paths())
    }

    // contains

    @Test
    fun `contains the root of an empty tree`() {
        assertTrue(AnalysisTree(initialPosition).contains(emptyList()))
    }

    @Test
    fun `contains a path that was added`() {
        val move = Move.parse("a1a4")

        val tree = AnalysisTree(initialPosition)
            .add(emptyList(), move, mockPosition)

        assertTrue(tree.contains(listOf(move)))
    }

    @Test
    fun `contains a path two levels deep`() {
        val opening = Move.parse("a1a4")
        val reply = Move.parse("a8a5")

        val tree = AnalysisTree(initialPosition)
            .add(emptyList(), opening, mockPosition)
            .add(listOf(opening), reply, mockPosition)

        assertTrue(tree.contains(listOf(opening, reply)))
    }

    @Test
    fun `does not contain a path that was never added`() {
        assertFalse(AnalysisTree(initialPosition).contains(listOf(Move.parse("a1a4"))))
    }

    @Test
    fun `does not contain a path whose last move was never added`() {
        val opening = Move.parse("a1a4")

        val tree = AnalysisTree(initialPosition)
            .add(emptyList(), opening, mockPosition)

        assertFalse(tree.contains(listOf(opening, Move.parse("a8a5"))))
    }

    // withEvaluations

    @Test
    fun `attaches an evaluation to every move it is given`() {
        val opening = Move.parse("a1a4")
        val reply = Move.parse("a8a5")
        val openingAnswer = MoveEvaluation.WHITE_BETTER
        val replyAnswer = MoveEvaluation.BLACK_BETTER

        val tree = AnalysisTree(initialPosition)
            .add(emptyList(), opening, mockPosition)
            .add(listOf(opening), reply, mockPosition)
            .withEvaluations(
                1,
                mapOf(
                    listOf(opening) to openingAnswer,
                    listOf(opening, reply) to replyAnswer
                )
            )

        assertEquals(
            """
            Start
            └── $opening$openingAnswer
                └── $reply$replyAnswer
            """.trimIndent(),
            tree.moveTreeDiagram()
        )
    }

    @Test
    fun `keeps the evaluation of a move it is not given`() {
        val evaluated = Move.parse("a1a4")
        val evaluatedAnswer = MoveEvaluation.WHITE_BETTER
        val added = Move.parse("a1a3")
        val addedAnswer = MoveEvaluation.EQUAL

        val tree = AnalysisTree(initialPosition)
            .add(emptyList(), evaluated, mockPosition)
            .withEvaluations(1, mapOf(listOf(evaluated) to evaluatedAnswer))
            .add(emptyList(), added, mockPosition)
            .withEvaluations(2, mapOf(listOf(added) to addedAnswer))

        assertEquals(
            """
            Start
            ├── $evaluated$evaluatedAnswer
            └── $added$addedAnswer
            """.trimIndent(),
            tree.moveTreeDiagram()
        )
    }

    @Test
    fun `records the generation of the evaluations it attached`() {
        val move = Move.parse("a1a4")
        val generation = 7

        val tree = AnalysisTree(initialPosition)
            .add(emptyList(), move, mockPosition)
            .withEvaluations(generation, mapOf(listOf(move) to MoveEvaluation.WHITE_BETTER))

        assertEquals(generation, tree.evaluationGeneration)
    }

    // evaluationAt

    @Test
    fun `reads back the evaluation attached to a path`() {
        val move = Move.parse("a1a4")

        val tree = AnalysisTree(initialPosition)
            .add(emptyList(), move, mockPosition)
            .withEvaluations(1, mapOf(listOf(move) to MoveEvaluation.WHITE_BETTER))

        assertEquals(MoveEvaluation.WHITE_BETTER, tree.evaluationAt(listOf(move)))
    }

    @Test
    fun `reads back the evaluation of a reply`() {
        val opening = Move.parse("a1a4")
        val reply = Move.parse("a8a5")

        val tree = AnalysisTree(initialPosition)
            .add(emptyList(), opening, mockPosition)
            .add(listOf(opening), reply, mockPosition)
            .withEvaluations(1, mapOf(listOf(opening, reply) to MoveEvaluation.BLACK_BETTER))

        assertEquals(MoveEvaluation.BLACK_BETTER, tree.evaluationAt(listOf(opening, reply)))
    }

    @Test
    fun `has no evaluation at the start node`() {
        val move = Move.parse("a1a4")

        val tree = AnalysisTree(initialPosition)
            .add(emptyList(), move, mockPosition)
            .withEvaluations(1, mapOf(listOf(move) to MoveEvaluation.WHITE_BETTER))

        assertNull(tree.evaluationAt(emptyList()))
    }

    @Test
    fun `has no evaluation before one is attached`() {
        val move = Move.parse("a1a4")

        val tree = AnalysisTree(initialPosition)
            .add(emptyList(), move, mockPosition)

        assertNull(tree.evaluationAt(listOf(move)))
    }
}
