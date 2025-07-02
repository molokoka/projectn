package molokoka.project.n

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import molokoka.project.n.data.LeaderboardEntry
import molokoka.project.n.data.LeaderboardRepository
import molokoka.project.n.utils.formatTimeInMillis
import molokoka.project.n.ui.Leaderboard
import molokoka.project.n.ui.NicknameEntry
import molokoka.project.n.ui.PixelatedText
import org.koin.compose.koinInject
import org.jetbrains.compose.resources.stringResource
import projectn.composeapp.generated.resources.Res
import projectn.composeapp.generated.resources.choose_size
import projectn.composeapp.generated.resources.congrats
import projectn.composeapp.generated.resources.play_again
import projectn.composeapp.generated.resources.time_format
import projectn.composeapp.generated.resources.win_message

private const val NICKNAME_MIN_LENGTH = 5

data class WinScreenState(
    val nickname: String = "",
    val showNicknameEntry: Boolean = true,
    val leaderboardEntries: List<LeaderboardEntry> = emptyList(),
    val formattedTime: String
)

@Composable
fun WinScreen(
    boardSize: Int,
    timeInMillis: Long,
    onPlayAgain: () -> Unit,
    onBackToInit: () -> Unit
) {
    val leaderboardRepository: LeaderboardRepository = koinInject()
    val coroutineScope = rememberCoroutineScope()

    var state by remember {
        mutableStateOf(
            WinScreenState(
                formattedTime = formatTimeInMillis(timeInMillis)
            )
        )
    }

    LaunchedEffect(Unit) {
        state = state.copy(
            leaderboardEntries = leaderboardRepository.getTopEntriesForSize(boardSize, 5)
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        Header(boardSize, state)

        Leaderboard(
            entries = state.leaderboardEntries,
            maxEntries = 3,
            boardSize = boardSize,
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (state.showNicknameEntry) {
            NicknameEntry(
                nickname = state.nickname,
                onNicknameChange = { newNickname ->
                    state = state.copy(nickname = newNickname)
                },
                onSave = {
                    val entry = LeaderboardEntry(
                        nickname = state.nickname.padEnd(NICKNAME_MIN_LENGTH, ' '),
                        boardSize = boardSize,
                        timeInMillis = timeInMillis
                    )
                    coroutineScope.launch {
                        leaderboardRepository.addEntry(entry)
                        state = state.copy(
                            leaderboardEntries = leaderboardRepository.getTopEntriesForSize(boardSize, 5),
                            showNicknameEntry = false
                        )
                    }
                },
                onSkip = {
                    state = state.copy(showNicknameEntry = false)
                }
            )
        } else {
            Spacer(modifier = Modifier.height(32.dp))

            PixelatedText(
                text = stringResource(Res.string.play_again),
                pixelSize = 3.dp,
                color = Color.Blue,
                modifier = Modifier
                    .clickable { onPlayAgain() }
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            PixelatedText(
                text = stringResource(Res.string.choose_size),
                pixelSize = 3.dp,
                color = Color.Blue,
                modifier = Modifier
                    .clickable { onBackToInit() }
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun Header(boardSize: Int, state: WinScreenState) {
    PixelatedText(
        text = stringResource(Res.string.congrats),
        pixelSize = 4.dp,
        color = Color.Black,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    PixelatedText(
        text = stringResource(Res.string.win_message, boardSize, boardSize),
        pixelSize = 2.dp,
        color = Color.Black,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    PixelatedText(
        text = stringResource(Res.string.time_format, state.formattedTime),
        pixelSize = 3.dp,
        color = Color.Gray,
        modifier = Modifier.padding(bottom = 32.dp)
    )
}

