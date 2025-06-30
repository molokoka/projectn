package molokoka.project.n.utils

/**
 * Formats time in seconds to MM:SS format
 * @param timeInSeconds the time in seconds
 * @return formatted time string in MM:SS format (e.g., "05:23", "12:07")
 */
fun formatTime(timeInSeconds: Long): String {
    val minutes = timeInSeconds / 60
    val seconds = timeInSeconds % 60
    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}

/**
 * Formats elapsed time from milliseconds to MM:SS format
 * @param elapsedMillis the elapsed time in milliseconds
 * @return formatted time string in MM:SS format
 */
fun formatElapsedTime(elapsedMillis: Long): String {
    val elapsedSeconds = elapsedMillis / 1000
    return formatTime(elapsedSeconds)
}