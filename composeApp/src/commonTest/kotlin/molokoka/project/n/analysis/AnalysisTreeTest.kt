package molokoka.project.n.analysis

import molokoka.project.n.move_evaluation.MoveEvaluation
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import molokoka.project.n.util.fromDiagram
import molokoka.project.n.util.moveTreeDiagram
import molokoka.project.n.util.positionDiagram
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AnalysisTreeTest {

    // positionAt

    @Test
    fun `shows the initial position at the root`() {
        val tree = AnalysisTree(Position.parse("Ra1 ra8"))

        assertEquals("Ra1 ra8", tree.positionAt(emptyList()).toString())
    }

    @Test
    fun `replays a path onto the initial position`() {
        val initialPosition = fromDiagram(
            """
            8 r . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 . . . . . . . .
            4 . . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 R . . . . . . .
              a b c d e f g h
            """
        )

        val tree = AnalysisTree(initialPosition)
            .play(emptyList(), Move.parse("a1a4"))
            .play(listOf(Move.parse("a1a4")), Move.parse("a8a5"))

        val position = tree.positionAt(listOf(Move.parse("a1a4"), Move.parse("a8a5")))

        assertEquals(
            """
            8 . . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 r . . . . . . .
            4 R . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 . . . . . . . .
              a b c d e f g h
            """.trimIndent(),
            position.positionDiagram()
        )
    }

    @Test
    fun `has no position at a path that was never played`() {
        assertFailsWith<IllegalArgumentException> {
            AnalysisTree(Position.parse("Ra1 ra8"))
                .positionAt(listOf(Move.parse("a1a4")))
        }
    }

    // play

    @Test
    fun `adds a node at the root`() {
        val move = Move.parse("a1a4")

        val tree = AnalysisTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), move)

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

        val tree = AnalysisTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), opening)
            .play(listOf(opening), reply)

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
    fun `adds a sibling node for a different move`() {
        val move = Move.parse("a1a4")
        val sibling = Move.parse("a1a3")

        val tree = AnalysisTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), move)
            .play(emptyList(), sibling)

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

        val tree = AnalysisTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), opening)
            .play(listOf(opening), reply)
            .play(listOf(opening), sibling)

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

        val tree = AnalysisTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), opening)
            .play(listOf(opening), reply)
            .play(emptyList(), opening)

        assertEquals(
            """
            Start
            └── $opening
                └── $reply
            """.trimIndent(),
            tree.moveTreeDiagram()
        )
    }

    /**
     * The rook standing on a4 (after two moves in) must be movable, even though a4 is
     * empty in the initial position.
     */
    @Test
    fun `plays a move from a node two levels deep`() {
        val opening = Move.parse("a1a4")
        val reply = Move.parse("a8a5")
        val deepMove = Move.parse("a4b4")
        val path = listOf(opening, reply)

        val played = AnalysisTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), opening)
            .play(listOf(opening), reply)
            .play(path, deepMove)

        assertEquals(
            """
            Start
            └── $opening
                └── $reply
                    └── $deepMove
            """.trimIndent(),
            played.moveTreeDiagram()
        )
        assertEquals("Rb4 ra5", played.positionAt(path + deepMove).toString())
    }

    @Test
    fun `refuses to play at a path that was never played`() {
        assertFailsWith<IllegalArgumentException> {
            AnalysisTree(Position.parse("Ra1 ra8"))
                .play(listOf(Move.parse("a1a4")), Move.parse("a8a5"))
        }
    }

    @Test
    fun `rejects moving a black piece when white is to move`() {
        assertFailsWith<IllegalArgumentException> {
            AnalysisTree(Position.parse("Ra1 ra8"))
                .play(emptyList(), Move.parse("a8a5"))
        }
    }

    @Test
    fun `rejects moving a white piece when black is to move`() {
        val tree = AnalysisTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))

        assertFailsWith<IllegalArgumentException> {
            tree.play(listOf(Move.parse("a1a4")), Move.parse("a4a6"))
        }
    }

    // paths

    @Test
    fun `lists the path to every node depth first`() {
        val opening = Move.parse("a1a4")
        val reply = Move.parse("a8a5")
        val variation = Move.parse("a1a3")

        val tree = AnalysisTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), opening)
            .play(listOf(opening), reply)
            .play(emptyList(), variation)

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
        assertEquals(emptyList(), AnalysisTree(Position.parse("Ra1 ra8")).paths())
    }

    // contains

    @Test
    fun `contains the root of an empty tree`() {
        assertTrue(AnalysisTree(Position.parse("Ra1 ra8")).contains(emptyList()))
    }

    @Test
    fun `contains a path that was played`() {
        val tree = AnalysisTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))

        assertTrue(tree.contains(listOf(Move.parse("a1a4"))))
    }

    @Test
    fun `contains a path two levels deep`() {
        val tree = AnalysisTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))
            .play(listOf(Move.parse("a1a4")), Move.parse("a8a5"))

        assertTrue(tree.contains(listOf(Move.parse("a1a4"), Move.parse("a8a5"))))
    }

    @Test
    fun `does not contain a path that was never played`() {
        assertFalse(AnalysisTree(Position.parse("Ra1 ra8")).contains(listOf(Move.parse("a1a4"))))
    }

    @Test
    fun `does not contain a path whose last move was never played`() {
        val tree = AnalysisTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), Move.parse("a1a4"))

        assertFalse(tree.contains(listOf(Move.parse("a1a4"), Move.parse("a8a5"))))
    }

    // withEvaluations

    @Test
    fun `attaches an evaluation to every move it is given`() {
        val opening = Move.parse("a1a4")
        val reply = Move.parse("a8a5")
        val openingAnswer = MoveEvaluation.WHITE_BETTER
        val replyAnswer = MoveEvaluation.BLACK_BETTER

        val tree = AnalysisTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), opening)
            .play(listOf(opening), reply)
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

        val tree = AnalysisTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), evaluated)
            .withEvaluations(1, mapOf(listOf(evaluated) to evaluatedAnswer))
            .play(emptyList(), added)
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

        val tree = AnalysisTree(Position.parse("Ra1 ra8"))
            .play(emptyList(), move)
            .withEvaluations(generation, mapOf(listOf(move) to MoveEvaluation.WHITE_BETTER))

        assertEquals(generation, tree.evaluationGeneration)
    }

    // evaluationAt

    @Test
    fun `reads back the evaluation attached to a path`() {
        val move = Move.parse("b2b4")
        val tree = AnalysisTree()
            .play(emptyList(), move)
            .withEvaluations(1, mapOf(listOf(move) to MoveEvaluation.WHITE_BETTER))

        assertEquals(MoveEvaluation.WHITE_BETTER, tree.evaluationAt(listOf(move)))
    }

    @Test
    fun `reads back the evaluation of a reply`() {
        val opening = Move.parse("b2b4")
        val reply = Move.parse("a7a5")
        val tree = AnalysisTree()
            .play(emptyList(), opening)
            .play(listOf(opening), reply)
            .withEvaluations(1, mapOf(listOf(opening, reply) to MoveEvaluation.BLACK_BETTER))

        assertEquals(MoveEvaluation.BLACK_BETTER, tree.evaluationAt(listOf(opening, reply)))
    }

    @Test
    fun `has no evaluation at the start node`() {
        val move = Move.parse("b2b4")
        val tree = AnalysisTree()
            .play(emptyList(), move)
            .withEvaluations(1, mapOf(listOf(move) to MoveEvaluation.WHITE_BETTER))

        assertNull(tree.evaluationAt(emptyList()))
    }

    @Test
    fun `has no evaluation before one is attached`() {
        val move = Move.parse("b2b4")

        assertNull(AnalysisTree().play(emptyList(), move).evaluationAt(listOf(move)))
    }
}
