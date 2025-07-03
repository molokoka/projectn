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
import molokoka.project.n.ui.PixelatedText
import molokoka.project.n.ui.ChessBoardSizeControls
import org.jetbrains.compose.resources.stringResource
import projectn.composeapp.generated.resources.Res
import projectn.composeapp.generated.resources.choose_size
import projectn.composeapp.generated.resources.leaderboard
import projectn.composeapp.generated.resources.n_queens
import projectn.composeapp.generated.resources.start_game

@Composable
fun Setup(
    chessBoardSize: Int,
    onBoardSizeChange: (Int) -> Unit,
    onStartGame: () -> Unit,
    onShowLeaderboard: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        PixelatedText(
            text = stringResource(Res.string.n_queens),
            pixelSize = 4.dp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        PixelatedText(
            text = stringResource(Res.string.choose_size),
            pixelSize = 2.dp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        ChessBoardSizeControls(
            chessBoardSize = chessBoardSize,
            onBoardSizeChange = onBoardSizeChange
        )

        Spacer(modifier = Modifier.height(32.dp))

        PixelatedText(
            text = stringResource(Res.string.leaderboard),
            pixelSize = 3.dp,
            color = Color.Blue,
            modifier = Modifier
                .clickable { onShowLeaderboard() }
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

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
