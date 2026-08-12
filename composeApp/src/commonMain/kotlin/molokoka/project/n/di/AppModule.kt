package molokoka.project.n.di

import org.koin.dsl.module

/**
 * Dependency graph for the app.
 *
 * Empty for now - the leaderboard's DataStore-backed repository was the only
 * binding. Kept wired so the analysis board's view model has somewhere to go.
 */
val appModule = module {
}
