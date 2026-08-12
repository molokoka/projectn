package molokoka.project.n.domain.chess

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SquareColorTest {

    @Test
    fun `a1 is dark`() {
        assertFalse(ChessCoordinates.create('a', 1).isLightSquare)
    }

    @Test
    fun `h1 is light`() {
        assertTrue(ChessCoordinates.create('h', 1).isLightSquare)
    }

    @Test
    fun `colour alternates between horizontally adjacent squares`() {
        for (rank in 1..8) {
            for (file in 'a'..'g') {
                assertNotEquals(
                    ChessCoordinates.create(file, rank).isLightSquare,
                    ChessCoordinates.create(file + 1, rank).isLightSquare
                )
            }
        }
    }

    @Test
    fun `colour alternates between vertically adjacent squares`() {
        for (rank in 1..7) {
            for (file in 'a'..'h') {
                assertNotEquals(
                    ChessCoordinates.create(file, rank).isLightSquare,
                    ChessCoordinates.create(file, rank + 1).isLightSquare
                )
            }
        }
    }
}
