package molokoka.project.n.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MoveTest {

    @Test
    fun `reads a move`() {
        assertEquals(
            Move(Coordinates.parse("b2"), Coordinates.parse("b4")),
            Move.parse("b2b4")
        )
    }

    @Test
    fun `writes a move`() {
        assertEquals(
            "b2b4",
            Move.parse("b2b4").toString()
        )
    }

    @Test
    fun `rejects a move that is not four characters`() {
        assertFailsWith<IllegalArgumentException> { Move.parse("") }
        assertFailsWith<IllegalArgumentException> { Move.parse("b2") }
        assertFailsWith<IllegalArgumentException> { Move.parse("b2b") }
        assertFailsWith<IllegalArgumentException> { Move.parse("b2b4b") }
    }

}
