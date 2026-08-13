package molokoka.project.n.analysis

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import molokoka.project.n.domain.ChessCoordinates
import molokoka.project.n.ui.BoardOrientation

data class AnalysisState(
    val orientation: BoardOrientation = BoardOrientation.WHITE
)

class AnalysisViewModel : ViewModel() {

    private val _state = MutableStateFlow(AnalysisState())
    val state: StateFlow<AnalysisState> = _state.asStateFlow()

    fun flipBoard() {
        _state.update {
            it.copy(
                orientation = when (it.orientation) {
                    BoardOrientation.WHITE -> BoardOrientation.BLACK
                    BoardOrientation.BLACK -> BoardOrientation.WHITE
                }
            )
        }
    }

    fun reset() {
        _state.value = AnalysisState()
    }

    fun onSquareClicked(coordinates: ChessCoordinates) {
    }
}
