package molokoka.project.n

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import molokoka.project.n.views.PixelatedText
import org.jetbrains.compose.resources.stringResource
import projectn.composeapp.generated.resources.Res
import projectn.composeapp.generated.resources.choose_different_size
import projectn.composeapp.generated.resources.congratulations
import projectn.composeapp.generated.resources.play_again
import projectn.composeapp.generated.resources.win_message

@Composable
fun WinScreen(
    boardSize: Int,
    onPlayAgain: () -> Unit,
    onBackToInit: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        PixelatedText(
            text = stringResource(Res.string.congratulations),
            pixelSize = 4.dp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        PixelatedText(
            text = stringResource(Res.string.win_message, boardSize, boardSize),
            pixelSize = 2.dp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 32.dp)
        )

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
            text = stringResource(Res.string.choose_different_size),
            pixelSize = 3.dp,
            color = Color.Blue,
            modifier = Modifier
                .clickable { onBackToInit() }
                .padding(16.dp)
        )
    }
}