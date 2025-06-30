package molokoka.project.n.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*
import okio.Path.Companion.toPath

@OptIn(ExperimentalForeignApi::class)
actual fun createDataStore(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            val documentDirectory = NSFileManager.defaultManager.URLsForDirectory(
                directory = NSDocumentDirectory,
                inDomains = NSUserDomainMask
            ).first() as NSURL
            val dataStoreFile = documentDirectory.URLByAppendingPathComponent("nqueens_leaderboard.preferences_pb")
            val path = dataStoreFile?.path ?: throw IllegalStateException("Cannot access iOS documents directory")
            path.toPath()
        }
    )
}