package molokoka.project.n.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect fun createDataStore(): DataStore<Preferences>

// Global instance
val leaderboardDataStore: DataStore<Preferences> by lazy {
    createDataStore()
}

// Repository instance
val leaderboardRepository: LeaderboardRepository by lazy {
    LeaderboardRepository(leaderboardDataStore)
}