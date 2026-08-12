package molokoka.project.n

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import molokoka.project.n.ui.PixelatedText
import org.jetbrains.compose.resources.stringResource
import projectn.composeapp.generated.resources.Res
import projectn.composeapp.generated.resources.congrats
import projectn.composeapp.generated.resources.main_menu
import projectn.composeapp.generated.resources.play_again
import projectn.composeapp.generated.resources.win_message

@Composable
fun WinScreen(
    onPlayAgain: () -> Unit,
    onBackToInit: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Header()

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
            text = stringResource(Res.string.main_menu),
            pixelSize = 3.dp,
            color = Color.Blue,
            modifier = Modifier
                .clickable { onBackToInit() }
                .padding(16.dp)
        )
    }
}

@Composable
private fun Header() {
    PixelatedText(
        text = stringResource(Res.string.congrats),
        pixelSize = 4.dp,
        color = Color.Black,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    PixelatedText(
        text = stringResource(Res.string.win_message),
        pixelSize = 2.dp,
        color = Color.Black,
        modifier = Modifier.padding(bottom = 32.dp)
    )
}
