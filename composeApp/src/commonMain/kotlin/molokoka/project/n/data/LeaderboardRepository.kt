package molokoka.project.n.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import molokoka.project.n.utils.formatTimeInMillis

class LeaderboardRepository(private val dataStore: DataStore<Preferences>) {
    
    private val leaderboardKey = stringPreferencesKey("leaderboard_data")
    private val logger = Logger.withTag("LeaderboardRepository")
    
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    val leaderboardData: Flow<LeaderboardData> = dataStore.data.map { preferences ->
        val leaderboardJson = preferences[leaderboardKey] ?: ""
        if (leaderboardJson.isEmpty()) {
            LeaderboardData()
        } else {
            try {
                json.decodeFromString<LeaderboardData>(leaderboardJson)
            } catch (_: Exception) {
                LeaderboardData()
            }
        }
    }
    
    suspend fun addEntry(entry: LeaderboardEntry) {
        logger.i { "Adding entry: ${entry.nickname.trim()} - ${entry.boardSize}x${entry.boardSize} - ${formatTimeInMillis(entry.timeInMillis)}" }
        dataStore.edit { preferences ->
            val currentLeaderboardJson = preferences[leaderboardKey] ?: ""
            val currentData = if (currentLeaderboardJson.isEmpty()) {
                LeaderboardData()
            } else {
                try {
                    json.decodeFromString<LeaderboardData>(currentLeaderboardJson)
                } catch (e: Exception) {
                    logger.e(e) { "Error decoding leaderboard data" }
                    LeaderboardData()
                }
            }
            
            val updatedData = currentData.addEntry(entry)
            preferences[leaderboardKey] = json.encodeToString(updatedData)
            logger.d { "Entry saved. Total entries: ${updatedData.entries.size}" }
        }
    }
    
    suspend fun getTopEntriesForSize(boardSize: Int, limit: Int = 10): List<LeaderboardEntry> {
        val result = leaderboardData.map { it.getTopEntriesForSize(boardSize, limit) }.first()
        logger.d { "Retrieved ${result.size} entries for ${boardSize}x${boardSize}" }
        return result
    }
}