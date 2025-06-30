package molokoka.project.n

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import kotlinx.coroutines.launch
import molokoka.project.n.data.LeaderboardEntry
import molokoka.project.n.data.LeaderboardRepository
import molokoka.project.n.views.PixelatedText
import org.koin.compose.koinInject
import org.jetbrains.compose.resources.stringResource
import projectn.composeapp.generated.resources.Res
import projectn.composeapp.generated.resources.choose_size
import projectn.composeapp.generated.resources.congrats
import projectn.composeapp.generated.resources.enter_name
import projectn.composeapp.generated.resources.leaderboard_title
import projectn.composeapp.generated.resources.min_chars
import projectn.composeapp.generated.resources.no_records_yet
import projectn.composeapp.generated.resources.play_again
import projectn.composeapp.generated.resources.save
import projectn.composeapp.generated.resources.skip
import projectn.composeapp.generated.resources.time_format
import projectn.composeapp.generated.resources.win_message

private const val NICKNAME_MIN_LENGTH = 5

@Composable
fun WinScreen(
    boardSize: Int,
    timeInSeconds: Long,
    onPlayAgain: () -> Unit,
    onBackToInit: () -> Unit
) {
    var nickname by remember { mutableStateOf("") }
    var showNicknameEntry by remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val leaderboardRepository: LeaderboardRepository = koinInject()
    var leaderboardEntries by remember { mutableStateOf<List<LeaderboardEntry>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val logger = Logger.withTag("WinScreen")
    
    val formattedTime = remember(timeInSeconds) {
        val minutes = timeInSeconds / 60
        val seconds = timeInSeconds % 60
        "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }
    
    LaunchedEffect(Unit) {
        if (showNicknameEntry) {
            focusRequester.requestFocus()
        }
        leaderboardEntries = leaderboardRepository.getTopEntriesForSize(boardSize, 5)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
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
            text = stringResource(Res.string.time_format, formattedTime),
            pixelSize = 3.dp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Show current leaderboard
        PixelatedText(
            text = stringResource(Res.string.leaderboard_title, boardSize, boardSize),
            pixelSize = 2.dp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (leaderboardEntries.isEmpty()) {
            PixelatedText(
                text = stringResource(Res.string.no_records_yet),
                pixelSize = 2.dp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        } else {
            leaderboardEntries.take(3).forEachIndexed { index, entry ->
                val timeFormatted = remember(entry.timeInSeconds) {
                    val minutes = entry.timeInSeconds / 60
                    val seconds = entry.timeInSeconds % 60
                    "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
                }
                
                PixelatedText(
                    text = "${index + 1}. ${entry.nickname.trim()} $timeFormatted",
                    pixelSize = 2.dp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (showNicknameEntry) {
            PixelatedText(
                text = stringResource(Res.string.enter_name),
                pixelSize = 2.dp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(NICKNAME_MIN_LENGTH) { index ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .border(2.dp, Color.Black)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        PixelatedText(
                            text = nickname.getOrNull(index)?.toString()?.uppercase() ?: "",
                            pixelSize = 3.dp,
                            color = Color.Black
                        )
                    }
                }
            }
            
            // Hidden text field for input
            BasicTextField(
                value = nickname,
                onValueChange = { newValue ->
                    if (newValue.length <= NICKNAME_MIN_LENGTH) {
                        nickname = newValue.uppercase()
                    }
                },
                modifier = Modifier
                    .size(0.dp)
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                )
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PixelatedText(
                text = if (nickname.length >= NICKNAME_MIN_LENGTH) stringResource(Res.string.save) else stringResource(Res.string.min_chars, NICKNAME_MIN_LENGTH),
                pixelSize = 3.dp,
                color = if (nickname.length >= NICKNAME_MIN_LENGTH) Color.Blue else Color.Gray,
                modifier = Modifier
                    .clickable(enabled = nickname.length >= NICKNAME_MIN_LENGTH) {
                        logger.i { "Saving leaderboard entry for ${nickname.trim()}" }
                        val entry = LeaderboardEntry(
                            nickname = nickname.padEnd(NICKNAME_MIN_LENGTH, ' '),
                            boardSize = boardSize,
                            timeInSeconds = timeInSeconds
                        )
                        coroutineScope.launch {
                            leaderboardRepository.addEntry(entry)
                            leaderboardEntries = leaderboardRepository.getTopEntriesForSize(boardSize, 5)
                        }
                        showNicknameEntry = false
                    }
                    .padding(16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PixelatedText(
                text = stringResource(Res.string.skip),
                pixelSize = 3.dp,
                color = Color.Blue,
                modifier = Modifier
                    .clickable { showNicknameEntry = false }
                    .padding(16.dp)
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
