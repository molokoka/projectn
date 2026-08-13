package molokoka.project.n

import molokoka.project.n.analysis.AnalysisViewModel
import molokoka.project.n.domain.Coordinates
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.Side
import molokoka.project.n.ui.BoardOrientation.BLACK
import molokoka.project.n.ui.BoardOrientation.WHITE
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AnalysisViewModelTest {

    @Test
    fun `the board starts from the white orientation`() {
        assertEquals(WHITE, AnalysisViewModel().state.value.orientation)
    }

    @Test
    fun `flipping the board switches to the black orientation`() {
        val viewModel = AnalysisViewModel()

        viewModel.flipBoard()

        assertEquals(BLACK, viewModel.state.value.orientation)
    }

    @Test
    fun `flipping the board twice returns to the white orientation`() {
        val viewModel = AnalysisViewModel()

        viewModel.flipBoard()
        viewModel.flipBoard()

        assertEquals(WHITE, viewModel.state.value.orientation)
    }

    @Test
    fun `resetting a flipped board returns to the white orientation`() {
        val viewModel = AnalysisViewModel()
        viewModel.flipBoard()

        viewModel.reset()

        assertEquals(WHITE, viewModel.state.value.orientation)
    }

    @Test
    fun `the board starts from the initial position`() {
        assertEquals(Position.INITIAL, AnalysisViewModel().state.value.position)
    }

    @Test
    fun `clicking a piece of the side to move selects it`() {
        val viewModel = AnalysisViewModel()

        viewModel.onSquareClicked(Coordinates.parse("a1"))

        assertEquals(Coordinates.parse("a1"), viewModel.state.value.selected)
    }

    @Test
    fun `clicking a piece of the other side selects nothing`() {
        val viewModel = AnalysisViewModel()

        viewModel.onSquareClicked(Coordinates.parse("b8"))

        assertNull(viewModel.state.value.selected)
    }

    @Test
    fun `clicking an empty square selects nothing`() {
        val viewModel = AnalysisViewModel()

        viewModel.onSquareClicked(Coordinates.parse("d4"))

        assertNull(viewModel.state.value.selected)
    }

    @Test
    fun `clicking the selected square deselects it`() {
        val viewModel = AnalysisViewModel()
        viewModel.onSquareClicked(Coordinates.parse("a1"))

        viewModel.onSquareClicked(Coordinates.parse("a1"))

        assertNull(viewModel.state.value.selected)
    }

    @Test
    fun `clicking a legal target plays the move`() {
        val viewModel = AnalysisViewModel()
        viewModel.onSquareClicked(Coordinates.parse("a1"))

        viewModel.onSquareClicked(Coordinates.parse("d4"))

        assertEquals(listOf(Move.parse("a1d4")), viewModel.state.value.moves)
        assertNull(viewModel.state.value.selected)
    }

    @Test
    fun `playing a move gives the other side the move`() {
        val viewModel = AnalysisViewModel()
        viewModel.onSquareClicked(Coordinates.parse("a1"))

        viewModel.onSquareClicked(Coordinates.parse("d4"))

        assertEquals(Side.BLACK, viewModel.state.value.sideToMove)
    }

    @Test
    fun `clicking another piece of the side to move reselects it`() {
        val viewModel = AnalysisViewModel()
        viewModel.onSquareClicked(Coordinates.parse("a1"))

        viewModel.onSquareClicked(Coordinates.parse("c1"))

        assertEquals(Coordinates.parse("c1"), viewModel.state.value.selected)
        assertEquals(emptyList(), viewModel.state.value.moves)
    }

    @Test
    fun `resetting clears the moves played`() {
        val viewModel = AnalysisViewModel()
        viewModel.onSquareClicked(Coordinates.parse("a1"))
        viewModel.onSquareClicked(Coordinates.parse("d4"))

        viewModel.reset()

        assertEquals(emptyList(), viewModel.state.value.moves)
        assertEquals(Position.INITIAL, viewModel.state.value.position)
    }
}
