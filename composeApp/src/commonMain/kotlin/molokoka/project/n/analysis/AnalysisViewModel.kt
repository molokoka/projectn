package molokoka.project.n.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import molokoka.project.n.domain.AnalyticsTree
import molokoka.project.n.domain.Move
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
        is AnalysisEffect.StartComputerMove -> startComputerMove(effect.tree, effect.path)
    }

    private fun startComputerMove(tree: AnalyticsTree, path: List<Move>) {
        computerMoveRequest?.cancel()

        computerMoveRequest = viewModelScope.launch {
            val move = computerMoveSource.nextMove(tree, path)

            onIntent(AnalysisIntent.ComputerMoveReady(path, move))
        }
    }

    private fun cancelComputerMove() {
        computerMoveRequest?.cancel()
        computerMoveRequest = null
    }
}
