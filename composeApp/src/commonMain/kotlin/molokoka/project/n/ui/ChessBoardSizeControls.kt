package molokoka.project.n.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import molokoka.project.n.domain.chess.MIN_BOARD_SIZE
import molokoka.project.n.domain.chess.MAX_BOARD_SIZE
import org.jetbrains.compose.resources.stringResource
import projectn.composeapp.generated.resources.Res
import projectn.composeapp.generated.resources.next
import projectn.composeapp.generated.resources.prev
import projectn.composeapp.generated.resources.size_format

@Composable
fun ChessBoardSizeControls(
    chessBoardSize: Int,
    onBoardSizeChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        PixelatedText(
            text = stringResource(Res.string.prev),
            pixelSize = 2.dp,
            color = if (chessBoardSize > MIN_BOARD_SIZE) Color.Blue else Color.Gray,
            modifier = Modifier
                .clickable(enabled = chessBoardSize > MIN_BOARD_SIZE) {
                    onBoardSizeChange((chessBoardSize - 1).coerceAtLeast(MIN_BOARD_SIZE))
                }
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.padding(horizontal = 4.dp))

        PixelatedText(
            text = stringResource(Res.string.size_format, chessBoardSize, chessBoardSize),
            pixelSize = 3.dp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.padding(horizontal = 4.dp))

        PixelatedText(
            text = stringResource(Res.string.next),
            pixelSize = 2.dp,
            color = if (chessBoardSize < MAX_BOARD_SIZE) Color.Blue else Color.Gray,
            modifier = Modifier
                .clickable(enabled = chessBoardSize < MAX_BOARD_SIZE) {
                    onBoardSizeChange((chessBoardSize + 1).coerceAtMost(MAX_BOARD_SIZE))
                }
                .padding(8.dp)
        )
    }
}