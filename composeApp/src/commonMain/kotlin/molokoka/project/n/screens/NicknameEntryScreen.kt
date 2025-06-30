package molokoka.project.n.screens

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import molokoka.project.n.data.LeaderboardEntry
import molokoka.project.n.utils.formatTime
import molokoka.project.n.views.PixelatedText

@Composable
fun NicknameEntryScreen(
    boardSize: Int,
    timeInSeconds: Long,
    onSaveEntry: (LeaderboardEntry) -> Unit,
    onSkip: () -> Unit
) {
    var nickname by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    val formattedTime = remember(timeInSeconds) { formatTime(timeInSeconds) }
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PixelatedText(
            text = "NEW RECORD",
            pixelSize = 4.dp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        PixelatedText(
            text = "${boardSize}X${boardSize} - ${formattedTime}",
            pixelSize = 3.dp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        PixelatedText(
            text = "ENTER NAME",
            pixelSize = 2.dp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Nickname input field
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) { index ->
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
                if (newValue.length <= 5) {
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
                    if (nickname.length >= 3) {
                        val entry = LeaderboardEntry(
                            nickname = nickname.padEnd(5, ' '),
                            boardSize = boardSize,
                            timeInSeconds = timeInSeconds
                        )
                        onSaveEntry(entry)
                    }
                    keyboardController?.hide()
                }
            )
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        PixelatedText(
            text = if (nickname.length >= 3) "SAVE" else "MIN 3 CHARS",
            pixelSize = 3.dp,
            color = if (nickname.length >= 3) Color.Blue else Color.Gray,
            modifier = Modifier
                .clickable(enabled = nickname.length >= 3) {
                    val entry = LeaderboardEntry(
                        nickname = nickname.padEnd(5, ' '),
                        boardSize = boardSize,
                        timeInSeconds = timeInSeconds
                    )
                    onSaveEntry(entry)
                }
                .padding(16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        PixelatedText(
            text = "SKIP",
            pixelSize = 2.dp,
            color = Color.Red,
            modifier = Modifier
                .clickable { onSkip() }
                .padding(16.dp)
        )
    }
}