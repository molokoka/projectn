package molokoka.project.n

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import projectn.composeapp.generated.resources.choose_size
import projectn.composeapp.generated.resources.n_queens
import projectn.composeapp.generated.resources.next
import projectn.composeapp.generated.resources.prev
import projectn.composeapp.generated.resources.size_format
import projectn.composeapp.generated.resources.start_game

@Composable
fun InitScreen(
    boardSize: Int,
    onBoardSizeChange: (Int) -> Unit,
    boardConfig: BoardConfig,
    onStartGame: () -> Unit
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

        SizeControls(
            boardSize = boardSize,
            onBoardSizeChange = onBoardSizeChange,
            boardConfig = boardConfig
        )

        Spacer(modifier = Modifier.height(32.dp))

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

@Composable
private fun SizeControls(
    boardSize: Int,
    onBoardSizeChange: (Int) -> Unit,
    boardConfig: BoardConfig
) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PixelatedText(
            text = stringResource(Res.string.prev),
            pixelSize = 2.dp,
            color = if (boardSize > boardConfig.minBoardSize) Color.Blue else Color.Gray,
            modifier = Modifier
                .clickable(enabled = boardSize > boardConfig.minBoardSize) {
                    onBoardSizeChange((boardSize - 1).coerceAtLeast(boardConfig.minBoardSize))
                }
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.padding(horizontal = 16.dp))

        PixelatedText(
            text = stringResource(Res.string.size_format, boardSize, boardSize),
            pixelSize = 3.dp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.padding(horizontal = 16.dp))

        PixelatedText(
            text = stringResource(Res.string.next),
            pixelSize = 2.dp,
            color = if (boardSize < boardConfig.maxBoardSize) Color.Blue else Color.Gray,
            modifier = Modifier
                .clickable(enabled = boardSize < boardConfig.maxBoardSize) {
                    onBoardSizeChange((boardSize + 1).coerceAtMost(boardConfig.maxBoardSize))
                }
                .padding(8.dp)
        )
    }
}