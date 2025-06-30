package molokoka.project.n.data

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@Serializable
data class LeaderboardEntry(
    val nickname: String,
    val boardSize: Int,
    val timeInSeconds: Long,
    val timestamp: Long = getCurrentTimeMillis()
) {
    fun getFormattedTime(): String {
        val minutes = timeInSeconds / 60
        val seconds = timeInSeconds % 60
        return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }
}

@Serializable
data class LeaderboardData(
    val entries: List<LeaderboardEntry> = emptyList()
) {
    fun getTopEntriesForSize(boardSize: Int, limit: Int = 10): List<LeaderboardEntry> {
        return entries
            .filter { it.boardSize == boardSize }
            .sortedWith(compareBy<LeaderboardEntry> { it.timeInSeconds }.thenBy { it.timestamp })
            .take(limit)
    }
    
    fun addEntry(entry: LeaderboardEntry): LeaderboardData {
        return copy(entries = entries + entry)
    }
    
    fun getBestTimeForSize(boardSize: Int): LeaderboardEntry? {
        return entries
            .filter { it.boardSize == boardSize }
            .minByOrNull { it.timeInSeconds }
    }
}

@OptIn(ExperimentalTime::class)
private fun getCurrentTimeMillis(): Long {
    return kotlin.time.Clock.System.now().toEpochMilliseconds()
}