package molokoka.project.n.domain.chess

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ChessCoordinatesTest {

    @Test
    fun acceptsEverySquareOnTheBoard() {
        for (file in FILE_RANGE) {
            for (rank in RANK_RANGE) {
                val coordinate = ChessCoordinates.create("$file$rank")
                assertEquals(file, coordinate.file)
                assertEquals(rank, coordinate.rank)
            }
        }
    }

    @Test
    fun rejectsFileBeyondTheBoard() {
        assertFailsWith<IllegalArgumentException> { ChessCoordinates.create("i1") }
    }

    @Test
    fun rejectsRankBeyondTheBoard() {
        assertFailsWith<IllegalArgumentException> { ChessCoordinates.create("a9") }
    }

    @Test
    fun rejectsRankBelowOne() {
        assertFailsWith<IllegalArgumentException> { ChessCoordinates.create("a0") }
    }

    @Test
    fun rejectsSquareThatIsNotAFileAndARank() {
        assertFailsWith<IllegalArgumentException> { ChessCoordinates.create("a") }
        assertFailsWith<IllegalArgumentException> { ChessCoordinates.create("a11") }
        assertFailsWith<IllegalArgumentException> { ChessCoordinates.create("1a") }
    }

    @Test
    fun rendersAsTheSquareName() {
        assertEquals("a1", ChessCoordinates.create("a1").toString())
        assertEquals("h8", ChessCoordinates.create("h8").toString())
    }
}
