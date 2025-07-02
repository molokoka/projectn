package molokoka.project.n.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import projectn.composeapp.generated.resources.Res
import projectn.composeapp.generated.resources.enter_name
import projectn.composeapp.generated.resources.min_chars
import projectn.composeapp.generated.resources.save
import projectn.composeapp.generated.resources.skip

private const val NICKNAME_MIN_LENGTH = 5

@Composable
fun NicknameEntry(
    nickname: String,
    onNicknameChange: (String) -> Unit,
    onSave: () -> Unit,
    onSkip: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(500) // just looks better
        focusRequester.requestFocus()
    }

    PixelatedText(
        text = stringResource(Res.string.enter_name),
        pixelSize = 2.dp,
        color = Color.Black,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    ) {
        repeat(NICKNAME_MIN_LENGTH) { index ->
            val isCurrentPosition = index == nickname.length && nickname.length < NICKNAME_MIN_LENGTH
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .border(2.dp, Color.Black)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                val character = nickname.getOrNull(index)?.toString()?.uppercase() ?: ""
                if (character.isNotEmpty()) {
                    PixelatedText(
                        text = character,
                        pixelSize = 3.dp,
                        color = Color.Black
                    )
                } else if (isCurrentPosition) {
                    AnimatedCursor()
                }
            }
        }
    }

    // Hidden text field for input
    BasicTextField(
        value = nickname,
        onValueChange = { newValue ->
            if (newValue.length <= NICKNAME_MIN_LENGTH) {
                onNicknameChange(newValue.uppercase())
                if (newValue.length == NICKNAME_MIN_LENGTH) {
                    keyboardController?.hide()
                }
            }
        },
        modifier = Modifier
            .alpha(0.0f)
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
        text = if (nickname.length >= NICKNAME_MIN_LENGTH) stringResource(Res.string.save) else stringResource(
            Res.string.min_chars,
            NICKNAME_MIN_LENGTH
        ),
        pixelSize = 3.dp,
        color = if (nickname.length >= NICKNAME_MIN_LENGTH) Color.Blue else Color.Gray,
        modifier = Modifier
            .clickable(enabled = nickname.length >= NICKNAME_MIN_LENGTH) {
                onSave()
            }
            .padding(16.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    PixelatedText(
        text = stringResource(Res.string.skip),
        pixelSize = 3.dp,
        color = Color.Blue,
        modifier = Modifier
            .clickable { onSkip() }
            .padding(16.dp)
    )
}

@Composable
private fun AnimatedCursor() {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor_transition")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )
    
    PixelatedText(
        text = "_",
        pixelSize = 3.dp,
        color = Color.Black,
        modifier = Modifier.alpha(cursorAlpha)
    )
}