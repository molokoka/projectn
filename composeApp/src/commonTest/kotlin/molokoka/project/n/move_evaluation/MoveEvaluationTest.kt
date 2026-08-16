package molokoka.project.n.move_evaluation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MoveEvaluationTest {

    @Test
    fun `reads a plus as white being better`() {
        assertEquals(MoveEvaluation.WHITE_BETTER, MoveEvaluation.fromSymbol("+"))
    }

    @Test
    fun `reads a minus as black being better`() {
        assertEquals(MoveEvaluation.BLACK_BETTER, MoveEvaluation.fromSymbol("-"))
    }

    @Test
    fun `reads an equals sign as an equal position`() {
        assertEquals(MoveEvaluation.EQUAL, MoveEvaluation.fromSymbol("="))
    }

    @Test
    fun `writes white being better as a plus`() {
        assertEquals("+", MoveEvaluation.WHITE_BETTER.toString())
    }

    @Test
    fun `rejects a symbol that is not an evaluation sign`() {
        assertFailsWith<IllegalArgumentException> { MoveEvaluation.fromSymbol("?") }
    }
}
