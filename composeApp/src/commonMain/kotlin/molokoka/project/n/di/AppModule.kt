package molokoka.project.n.di

import molokoka.project.n.analysis.AnalysisViewModel
import molokoka.project.n.computer_move.ComputerMoveSource
import molokoka.project.n.computer_move.DelayedRandomComputerMoveSource
import molokoka.project.n.move_evaluation.DelayedRandomMoveEvaluationSource
import molokoka.project.n.move_evaluation.MoveEvaluationSource
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<ComputerMoveSource> { DelayedRandomComputerMoveSource() }
    single<MoveEvaluationSource> { DelayedRandomMoveEvaluationSource() }

    viewModelOf(::AnalysisViewModel)
}
