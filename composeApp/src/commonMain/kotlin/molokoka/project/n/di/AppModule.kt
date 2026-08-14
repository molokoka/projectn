package molokoka.project.n.di

import molokoka.project.n.analysis.AnalysisViewModel
import molokoka.project.n.computer.ComputerMoveSource
import molokoka.project.n.computer.ScriptedComputerMoveSource
import molokoka.project.n.setup.SetupViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<ComputerMoveSource> { ScriptedComputerMoveSource() }

    viewModelOf(::AnalysisViewModel)
    viewModelOf(::SetupViewModel)
}
