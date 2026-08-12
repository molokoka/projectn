package molokoka.project.n.domain.chess

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ChessCoordinatesTest {

    @Test
    fun acceptsEverySquareOnTheBoard() {
        for (file in FILE_RANGE) {
            for (rank in RANK_RANGE) {
                val coordinate = ChessCoordinates.create(file, rank)
                assertEquals(file, coordinate.file)
                assertEquals(rank, coordinate.rank)
            }
        }
    }

    @Test
    fun rejectsFileBeyondTheBoard() {
        assertFailsWith<IllegalArgumentException> { ChessCoordinates.create('i', 1) }
    }

    @Test
    fun rejectsRankBeyondTheBoard() {
        assertFailsWith<IllegalArgumentException> { ChessCoordinates.create('a', 9) }
    }

    @Test
    fun rejectsRankBelowOne() {
        assertFailsWith<IllegalArgumentException> { ChessCoordinates.create('a', 0) }
    }

    @Test
    fun mapsRowColToFileRank() {
        assertEquals(ChessCoordinates.create('a', 1), ChessCoordinates.fromRowCol(0, 0))
        assertEquals(ChessCoordinates.create('h', 8), ChessCoordinates.fromRowCol(7, 7))
    }
}
