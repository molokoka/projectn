package molokoka.project.n.analysis

import androidx.lifecycle.SavedStateHandle
import kotlinx.serialization.json.Json

private const val STATE_KEY = "state"

private val savedStateJson = Json { encodeDefaults = false }

fun AnalysisState.saveTo(handle: SavedStateHandle) {
    handle[STATE_KEY] = savedStateJson.encodeToString(atRest())
}

fun SavedStateHandle.restoreAnalysisState(): AnalysisState? =
    get<String>(STATE_KEY)?.let { saved -> savedStateJson.decodeFromString(saved) }

private fun AnalysisState.atRest(): AnalysisState =
    copy(
        isComputerMovePending = false,
        pendingEvaluationGeneration = tree.evaluationGeneration
    )
