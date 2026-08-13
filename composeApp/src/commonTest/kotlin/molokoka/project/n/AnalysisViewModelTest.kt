package molokoka.project.n

import molokoka.project.n.analysis.AnalysisViewModel
import molokoka.project.n.ui.BoardOrientation.BLACK
import molokoka.project.n.ui.BoardOrientation.WHITE
import kotlin.test.Test
import kotlin.test.assertEquals

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
}
