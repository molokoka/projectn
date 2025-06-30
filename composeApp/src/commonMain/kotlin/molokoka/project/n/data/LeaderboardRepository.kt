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

class LeaderboardRepository(private val dataStore: DataStore<Preferences>) {
    
    private val leaderboardKey = stringPreferencesKey("leaderboard_data")
    private val logger = Logger.withTag("LeaderboardRepository")
    
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    val leaderboardData: Flow<LeaderboardData> = dataStore.data.map { preferences ->
        val jsonString = preferences[leaderboardKey] ?: ""
        if (jsonString.isEmpty()) {
            LeaderboardData()
        } else {
            try {
                json.decodeFromString<LeaderboardData>(jsonString)
            } catch (e: Exception) {
                LeaderboardData()
            }
        }
    }
    
    suspend fun addEntry(entry: LeaderboardEntry) {
        logger.i { "Adding entry: ${entry.nickname.trim()} - ${entry.boardSize}x${entry.boardSize} - ${entry.getFormattedTime()}" }
        dataStore.edit { preferences ->
            val currentDataJson = preferences[leaderboardKey] ?: ""
            val currentData = if (currentDataJson.isEmpty()) {
                LeaderboardData()
            } else {
                try {
                    json.decodeFromString<LeaderboardData>(currentDataJson)
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
    
    suspend fun getBestTimeForSize(boardSize: Int): LeaderboardEntry? {
        return leaderboardData.map { it.getBestTimeForSize(boardSize) }.first()
    }
    
    suspend fun clearLeaderboard() {
        dataStore.edit { preferences ->
            preferences[leaderboardKey] = json.encodeToString(LeaderboardData())
        }
    }
}