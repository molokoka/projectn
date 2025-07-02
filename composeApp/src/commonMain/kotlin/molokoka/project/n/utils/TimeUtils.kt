package molokoka.project.n.utils

/**
 * Formats time in milliseconds to MM:SS format
 * @param timeInMillis the elapsed time in milliseconds
 * @return formatted time string in MM:SS format
 */
fun formatTimeInMillis(timeInMillis: Long): String {
    val timeInSeconds = timeInMillis / 1000
    val minutes = timeInSeconds / 60
    val seconds = timeInSeconds % 60
    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}