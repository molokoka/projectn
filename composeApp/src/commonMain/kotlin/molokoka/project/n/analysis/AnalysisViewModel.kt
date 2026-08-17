package molokoka.project.n.analysis

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import molokoka.project.n.move_evaluation.MoveEvaluationSource
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.Side
import molokoka.project.n.computer_move.ComputerMoveSource
import molokoka.project.n.domain.AnalysisTree
import molokoka.project.n.log.log

private const val TAG = "AnalysisViewModel"

class AnalysisViewModel(
    private val computerMoveSource: ComputerMoveSource,
    private val moveEvaluationSource: MoveEvaluationSource,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AnalysisState())
    val state: StateFlow<AnalysisState> = _state.asStateFlow()

    private var computerMoveRequest: Job? = null
    private val moveEvaluationScope =
        viewModelScope + SupervisorJob(viewModelScope.coroutineContext.job)

    private val instance = "#${hashCode().toString(16)}"

    init {
        log(TAG, "$instance created")

        savedStateHandle.restoreAnalysisState()?.let { restored ->
            _state.value = restored

            log(TAG, "$instance restored ${restored.toLog()}")
        }
    }

    override fun onCleared() {
        log(TAG, "$instance cleared")
    }

    fun onIntent(intent: AnalysisIntent) {
        val (newState, effects) = _state.value.reduce(intent)
        _state.value = newState

        log(TAG, "$instance intent=$intent")
        log(TAG, "$instance state ${newState.toLog()}")

        newState.saveTo(savedStateHandle)

        log(TAG, "$instance saved ${savedStateHandle.toLog()}")

        effects.forEach { effect -> runEffect(effect) }
    }

    private fun SavedStateHandle.toLog(): String =
        keys().sorted().joinToString(" | ") { key -> "$key=${get<Any>(key)}" }

    private fun AnalysisState.toLog(): String {
        val path = moves.joinToString(" ").ifEmpty { "Start" }
        val square = selected?.toString() ?: "-"
        val evaluation = "${tree.evaluationGeneration}/$pendingEvaluationGeneration"

        return "moves=$path nodes=${tree.paths().size} selected=$square " +
                "computerMove=$isComputerMovePending evaluation=$evaluation"
    }

    private fun runEffect(effect: AnalysisEffect) = when (effect) {
        AnalysisEffect.CancelComputerMove -> cancelComputerMove()
        AnalysisEffect.CancelMoveEvaluation -> cancelMoveEvaluation()
        is AnalysisEffect.StartComputerMove ->
            startComputerMove(effect.position, effect.side, effect.path)
        is AnalysisEffect.StartMovesEvaluation ->
            startMovesEvaluation(effect.generation, effect.tree)
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

    private fun startMovesEvaluation(generation: Int, tree: AnalysisTree) {
        moveEvaluationScope.launch {
            val evaluations = moveEvaluationSource.evaluate(tree.nodes)

            onIntent(AnalysisIntent.MovesEvaluationReady(generation, evaluations))
        }
    }

    private fun cancelComputerMove() {
        computerMoveRequest?.cancel()
        computerMoveRequest = null
    }

    private fun cancelMoveEvaluation() {
        moveEvaluationScope.coroutineContext.cancelChildren()
    }
}
