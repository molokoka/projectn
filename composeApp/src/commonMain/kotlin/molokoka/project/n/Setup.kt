package molokoka.project.n

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import molokoka.project.n.ui.PixelatedText
import org.jetbrains.compose.resources.stringResource
import projectn.composeapp.generated.resources.Res
import projectn.composeapp.generated.resources.n_queens
import projectn.composeapp.generated.resources.start_game

@Composable
fun Setup(onStartGame: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        PixelatedText(
            text = stringResource(Res.string.n_queens),
            pixelSize = 4.dp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        PixelatedText(
            text = stringResource(Res.string.start_game),
            pixelSize = 3.dp,
            color = Color.Blue,
            modifier = Modifier
                .clickable { onStartGame() }
                .padding(16.dp)
        )
    }
}
