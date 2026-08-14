package molokoka.project.n.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class DiagramTest {

    @Test
    fun `renders an empty board as dots under the file labels`() {
        assertEquals(
            """
            8 . . . . . . . .
            7 . . . . . . . .
            6 . . . . . . . .
            5 . . . . . . . .
            4 . . . . . . . .
            3 . . . . . . . .
            2 . . . . . . . .
            1 . . . . . . . .
              a b c d e f g h
            """.trimIndent(),
            Position(emptyMap()).asDiagram()
        )
    }

    @Test
    fun `renders a piece on its file`() {
        assertEquals(
            "1 . . . R . . . .",
            Position.parse("Rd1").asDiagram().lines()[7]
        )
    }

    @Test
    fun `renders the eighth rank on the first line`() {
        assertEquals(
            "8 r . . . . . . .",
            Position.parse("ra8").asDiagram().lines().first()
        )
    }

    @Test
    fun `renders the initial position`() {
        assertEquals(
            """
            8 . r . r . r . r
            7 q . q . q . q .
            6 . . . . . . . .
            5 . . . . . . . .
            4 . . . . . . . .
            3 . . . . . . . .
            2 . Q . Q . Q . Q
            1 R . R . R . R .
              a b c d e f g h
            """.trimIndent(),
            Position.INITIAL.asDiagram()
        )
    }
}
