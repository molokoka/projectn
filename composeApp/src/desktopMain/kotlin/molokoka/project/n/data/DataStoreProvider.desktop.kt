package molokoka.project.n.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File

actual fun createDataStore(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create(
        produceFile = {
            File(System.getProperty("user.home"), ".nqueens/nqueens_leaderboard.preferences_pb")
        }
    )
}