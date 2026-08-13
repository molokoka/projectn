package molokoka.project.n.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import molokoka.project.n.domain.ChessCoordinates
import molokoka.project.n.domain.isLightSquare

@Composable
fun ChessBoard(
    modifier: Modifier = Modifier,
    orientation: BoardOrientation = BoardOrientation.WHITE,
    onSquareClicked: (ChessCoordinates) -> Unit
) {
    val uiConfig = chessBoardUiConfig()

    val ranks = ranksInDrawOrder(orientation)
    val files = filesInDrawOrder(orientation)

    Column(modifier = modifier) {
        for (rank in ranks) {
            Row {
                for (file in files) {
                    val coordinate = ChessCoordinates(file, rank)

                    val squareColor =
                        if (coordinate.isLightSquare) uiConfig.lightSquareColor else uiConfig.darkSquareColor
                    val isLeftEdge = file == files.first
                    val isBottomEdge = rank == ranks.last

                    Box(
                        modifier = Modifier
                            .size(uiConfig.squareSize)
                            .background(squareColor)
                            .clickable { onSquareClicked(coordinate) }
                    ) {
                        if (isLeftEdge) {
                            BasicText(
                                text = coordinate.rank.toString(),
                                style = uiConfig.coordinateTextStyle,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(1.dp)
                            )
                        }
                        if (isBottomEdge) {
                            BasicText(
                                text = coordinate.file.toString(),
                                style = uiConfig.coordinateTextStyle,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
