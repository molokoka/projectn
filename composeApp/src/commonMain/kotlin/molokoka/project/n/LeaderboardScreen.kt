package molokoka.project.n

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import molokoka.project.n.data.LeaderboardEntry
import molokoka.project.n.data.LeaderboardRepository
import molokoka.project.n.views.PixelatedText
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import projectn.composeapp.generated.resources.Res
import projectn.composeapp.generated.resources.back
import projectn.composeapp.generated.resources.leaderboard
import projectn.composeapp.generated.resources.leaderboard_size
import projectn.composeapp.generated.resources.no_records_yet

@Composable
fun LeaderboardScreen(
    boardSize: Int,
    onBackToInit: () -> Unit
) {
    val leaderboardRepository: LeaderboardRepository = koinInject()
    var leaderboardEntries by remember { mutableStateOf<List<LeaderboardEntry>>(emptyList()) }
    
    LaunchedEffect(boardSize) {
        leaderboardEntries = leaderboardRepository.getTopEntriesForSize(boardSize, 10)
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        PixelatedText(
            text = stringResource(Res.string.leaderboard),
            pixelSize = 4.dp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        PixelatedText(
            text = stringResource(Res.string.leaderboard_size, boardSize, boardSize),
            pixelSize = 3.dp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        if (leaderboardEntries.isEmpty()) {
            PixelatedText(
                text = stringResource(Res.string.no_records_yet),
                pixelSize = 2.dp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        } else {
            leaderboardEntries.forEachIndexed { index, entry ->
                val timeFormatted = remember(entry.timeInSeconds) {
                    val minutes = entry.timeInSeconds / 60
                    val seconds = entry.timeInSeconds % 60
                    "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
                }
                
                PixelatedText(
                    text = "${index + 1}. ${entry.nickname.trim()} $timeFormatted",
                    pixelSize = 2.dp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        PixelatedText(
            text = stringResource(Res.string.back),
            pixelSize = 3.dp,
            color = Color.Blue,
            modifier = Modifier
                .clickable { onBackToInit() }
                .padding(16.dp)
        )
    }
}