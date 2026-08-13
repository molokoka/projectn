package molokoka.project.n.di

import molokoka.project.n.analysis.AnalysisViewModel
import molokoka.project.n.setup.SetupViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::AnalysisViewModel)
    viewModelOf(::SetupViewModel)
}
