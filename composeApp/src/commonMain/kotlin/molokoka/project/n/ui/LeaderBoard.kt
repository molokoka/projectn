package molokoka.project.n.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import molokoka.project.n.data.LeaderboardEntry
import molokoka.project.n.utils.formatTimeInMillis
import org.jetbrains.compose.resources.stringResource
import projectn.composeapp.generated.resources.Res
import projectn.composeapp.generated.resources.leaderboard_title
import projectn.composeapp.generated.resources.no_records_yet

@Composable
fun LeaderBoard(
    entries: List<LeaderboardEntry>,
    chessBoardSize: Int? = null,
    maxEntries: Int = 10,
) {
    if (chessBoardSize != null) {
        PixelatedText(
            text = stringResource(Res.string.leaderboard_title, chessBoardSize, chessBoardSize),
            pixelSize = 2.dp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }

    if (entries.isEmpty()) {
        PixelatedText(
            text = stringResource(Res.string.no_records_yet),
            pixelSize = 2.dp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )
    } else {
        entries.take(maxEntries).forEachIndexed { index, entry ->
            val timeFormatted = remember(entry.timeInMillis) { formatTimeInMillis(entry.timeInMillis) }

            PixelatedText(
                text = "${index + 1}. ${entry.nickname.trim()} $timeFormatted",
                pixelSize = 2.dp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}