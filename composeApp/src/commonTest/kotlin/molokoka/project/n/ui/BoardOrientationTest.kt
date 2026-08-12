package molokoka.project.n.ui

import molokoka.project.n.ui.BoardOrientation.BLACK
import molokoka.project.n.ui.BoardOrientation.WHITE
import kotlin.test.Test
import kotlin.test.assertEquals

class BoardOrientationTest {

    @Test
    fun `from white orientation ranks are drawn from the top rank down`() {
        assertEquals(listOf(8, 7, 6, 5, 4, 3, 2, 1), ranksInDrawOrder(WHITE).toList())
    }

    @Test
    fun `from white orientation files are drawn from the 'a' file rightwards`() {
        assertEquals("abcdefgh".toList(), filesInDrawOrder(WHITE).toList())
    }

    @Test
    fun `from black orientation ranks are drawn from the bottom rank up`() {
        assertEquals(listOf(1, 2, 3, 4, 5, 6, 7, 8), ranksInDrawOrder(BLACK).toList())
    }

    @Test
    fun `from black orientation files are drawn from the 'h' file leftwards`() {
        assertEquals("hgfedcba".toList(), filesInDrawOrder(BLACK).toList())
    }
}
