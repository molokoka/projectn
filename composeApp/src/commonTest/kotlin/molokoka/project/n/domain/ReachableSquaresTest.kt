package molokoka.project.n.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReachableSquaresTest {

    @Test
    fun `an empty square reaches nothing`() {
        assertEquals(
            emptySet(),
            Position.parse("Qd4").reachableSquares(Coordinates.parse("e5"))
        )
    }

    @Test
    fun `a rook does not reach a diagonal square`() {
        assertFalse(
            Coordinates.parse("e5") in
                Position.parse("Rd4").reachableSquares(Coordinates.parse("d4"))
        )
    }

    @Test
    fun `a queen reaches a diagonal square`() {
        assertTrue(
            Coordinates.parse("e5") in
                Position.parse("Qd4").reachableSquares(Coordinates.parse("d4"))
        )
    }
}
