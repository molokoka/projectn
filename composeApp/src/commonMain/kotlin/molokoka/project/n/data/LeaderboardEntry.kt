package molokoka.project.n.data

import kotlinx.serialization.Serializable
import kotlin.time.Clock.System.now
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
data class LeaderboardEntry(
    val nickname: String,
    val chessBoardSize: Int,
    val completionTimeMillis: Long,
    val timestamp: Long = now().toEpochMilliseconds()
)

@Serializable
data class LeaderboardData(
    val entries: List<LeaderboardEntry> = emptyList()
) {
    fun getTopEntriesForSize(chessBoardSize: Int, limit: Int = 10): List<LeaderboardEntry> {
        return entries
            .filter { it.chessBoardSize == chessBoardSize }
            .sortedWith(compareBy<LeaderboardEntry> { it.completionTimeMillis }.thenBy { it.timestamp })
            .take(limit)
    }
    
    fun addEntry(entry: LeaderboardEntry): LeaderboardData {
        return copy(entries = entries + entry)
    }
}