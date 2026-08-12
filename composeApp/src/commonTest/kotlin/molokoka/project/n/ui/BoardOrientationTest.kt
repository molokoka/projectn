package molokoka.project.n.ui

import molokoka.project.n.ui.BoardOrientation.BLACK
import molokoka.project.n.ui.BoardOrientation.WHITE
import kotlin.test.Test
import kotlin.test.assertEquals

class BoardOrientationTest {

    @Test
    fun `from white orientation rows are drawn from the top rank down`() {
        assertEquals(listOf(7, 6, 5, 4, 3, 2, 1, 0), rowsInDrawOrder(8, WHITE).toList())
    }

    @Test
    fun `from white orientation columns are drawn from the 'a' file rightwards`() {
        assertEquals(listOf(0, 1, 2, 3, 4, 5, 6, 7), colsInDrawOrder(8, WHITE).toList())
    }

    @Test
    fun `from black orientation rows are drawn from the bottom rank up`() {
        assertEquals(listOf(0, 1, 2, 3, 4, 5, 6, 7), rowsInDrawOrder(8, BLACK).toList())
    }

    @Test
    fun `from black orientation columns are drawn from the 'h' file leftwards`() {
        assertEquals(listOf(7, 6, 5, 4, 3, 2, 1, 0), colsInDrawOrder(8, BLACK).toList())
    }
}
