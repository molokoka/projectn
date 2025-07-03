package molokoka.project.n.di

import molokoka.project.n.data.LeaderBoardRepository
import molokoka.project.n.data.createDataStore
import org.koin.dsl.module

val appModule = module {
    single { createDataStore() }
    single { LeaderBoardRepository(get()) }
}