package molokoka.project.n.di

import molokoka.project.n.analysis.AnalysisViewModel
import molokoka.project.n.computer.ComputerMoveSource
import molokoka.project.n.computer.DelayedRandomComputerMoveSource
import molokoka.project.n.analysis.move_evaluation.DelayedRandomMoveEvaluationSource
import molokoka.project.n.analysis.move_evaluation.MoveEvaluationSource
import molokoka.project.n.setup.SetupViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<ComputerMoveSource> { DelayedRandomComputerMoveSource() }
    single<MoveEvaluationSource> { DelayedRandomMoveEvaluationSource() }

    viewModelOf(::AnalysisViewModel)
    viewModelOf(::SetupViewModel)
}
