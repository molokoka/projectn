package molokoka.project.n.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CoordinatesTest {

    @Test
    fun `parses the file and rank of every square on the board`() {
        for (file in FILE_RANGE) {
            for (rank in RANK_RANGE) {
                val coordinate = Coordinates.parse("$file$rank")
                assertEquals(file, coordinate.file)
                assertEquals(rank, coordinate.rank)
            }
        }
    }

    @Test
    fun `rejects a file beyond the board`() {
        assertFailsWith<IllegalArgumentException> { Coordinates.parse("i1") }
    }

    @Test
    fun `rejects a file before the board`() {
        assertFailsWith<IllegalArgumentException> { Coordinates.parse("A1") }
    }

    @Test
    fun `rejects a rank beyond the board`() {
        assertFailsWith<IllegalArgumentException> { Coordinates.parse("a9") }
    }

    @Test
    fun `rejects a rank below one`() {
        assertFailsWith<IllegalArgumentException> { Coordinates.parse("a0") }
    }

    @Test
    fun `rejects a square that is not two characters`() {
        assertFailsWith<IllegalArgumentException> { Coordinates.parse("a") }
        assertFailsWith<IllegalArgumentException> { Coordinates.parse("a11") }
    }

    @Test
    fun `rejects a square with the file and rank the wrong way round`() {
        assertFailsWith<IllegalArgumentException> { Coordinates.parse("1a") }
    }

    @Test
    fun `renders as its file and rank`() {
        assertEquals("a1", Coordinates.parse("a1").toString())
        assertEquals("h8", Coordinates.parse("h8").toString())
    }
}
