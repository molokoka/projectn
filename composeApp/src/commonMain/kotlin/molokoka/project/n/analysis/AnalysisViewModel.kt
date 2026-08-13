package molokoka.project.n.analysis

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import molokoka.project.n.domain.Coordinates
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.Side
import molokoka.project.n.domain.play
import molokoka.project.n.domain.sideToMove
import molokoka.project.n.ui.BoardOrientation

data class AnalysisState(
    val orientation: BoardOrientation = BoardOrientation.WHITE,
    val moves: List<Move> = emptyList(),
    val selected: Coordinates? = null
) {
    val position: Position get() = Position.INITIAL.play(moves)

    val sideToMove: Side get() = sideToMove(moves.size)
}

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

    fun onSquareClicked(coordinates: Coordinates) {
        _state.update { state ->
            when (state.selected) {
                null -> state.select(coordinates)
                coordinates -> state.copy(selected = null)
                else -> state.playOrReselect(Move(state.selected, coordinates))
            }
        }
    }

    private fun AnalysisState.select(coordinates: Coordinates): AnalysisState =
        if (position.pieces[coordinates]?.side == sideToMove) copy(selected = coordinates) else this

    private fun AnalysisState.playOrReselect(move: Move): AnalysisState =
        runCatching { position.play(move, sideToMove) }
            .fold(
                onSuccess = { copy(moves = moves + move, selected = null) },
                onFailure = { copy(selected = null).select(move.to) }
            )
}
