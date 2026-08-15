package molokoka.project.n.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.Side
import molokoka.project.n.computer.ComputerMoveSource

class AnalysisViewModel(
    private val computerMoveSource: ComputerMoveSource
) : ViewModel() {

    private val _state = MutableStateFlow(AnalysisState())
    val state: StateFlow<AnalysisState> = _state.asStateFlow()

    private var computerMoveRequest: Job? = null

    fun onIntent(intent: AnalysisIntent) {
        val (newState, effect) = _state.value.reduce(intent)
        _state.value = newState
        if (effect != null) runEffect(effect)
    }

    private fun runEffect(effect: AnalysisEffect) = when (effect) {
        AnalysisEffect.CancelComputerMove -> cancelComputerMove()
        is AnalysisEffect.StartComputerMove ->
            startComputerMove(effect.position, effect.side, effect.path)
    }

    private fun startComputerMove(position: Position, side: Side, path: List<Move>) {
        computerMoveRequest?.cancel()

        computerMoveRequest = viewModelScope.launch {
            val intent = computerMoveSource.nextMove(position, side)
                ?.let { move -> AnalysisIntent.ComputerMoveReady(path, move) }
                ?: AnalysisIntent.ComputerMoveNotFound(path)

            onIntent(intent)
        }
    }

    private fun cancelComputerMove() {
        computerMoveRequest?.cancel()
        computerMoveRequest = null
    }
}
