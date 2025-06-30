package molokoka.project.n.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import android.content.Context
import java.io.File

private lateinit var appContext: Context

actual fun createDataStore(): DataStore<Preferences> {
    if (!::appContext.isInitialized) {
        throw IllegalStateException("DataStore not initialized. Make sure to call initializeDataStore() first.")
    }
    
    return PreferenceDataStoreFactory.create(
        produceFile = {
            File(appContext.filesDir, "datastore/nqueens_leaderboard.preferences_pb")
        }
    )
}

fun initializeDataStore(context: Context) {
    appContext = context.applicationContext
}