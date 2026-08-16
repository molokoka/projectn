package molokoka.project.n.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import molokoka.project.n.domain.BOARD_SIZE
import molokoka.project.n.domain.Coordinates
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.isLightSquare
import molokoka.project.n.ui.theme.AppTheme
import org.jetbrains.compose.resources.painterResource

private const val PieceScale = 0.82f
private const val CoordinateScale = 0.2f
private const val CoordinatePaddingScale = 0.05f

@Composable
fun ChessBoard(
    position: Position,
    selected: Coordinates?,
    boardSize: Dp,
    modifier: Modifier = Modifier,
    orientation: BoardOrientation = BoardOrientation.WHITE,
    onSquareClicked: (Coordinates) -> Unit
) {
    val colors = AppTheme.colors

    val squareSize = boardSize / BOARD_SIZE
    val pieceSize = squareSize * PieceScale
    val coordinatesPadding = squareSize * CoordinatePaddingScale
    val coordinatesTextStyle = AppTheme.typography.boardCoordinates.copy(
        fontSize = with(LocalDensity.current) { squareSize.toSp() } * CoordinateScale
    )

    val ranks = ranksInDrawOrder(orientation)
    val files = filesInDrawOrder(orientation)

    Column(modifier = modifier) {
        for (rank in ranks) {
            Row {
                for (file in files) {
                    val coordinate = Coordinates(file, rank)

                    val squareColor = when {
                        coordinate == selected -> colors.selectedSquare
                        coordinate.isLightSquare -> colors.lightSquare
                        else -> colors.darkSquare
                    }
                    val isLeftEdge = file == files.first
                    val isBottomEdge = rank == ranks.last

                    Box(
                        modifier = Modifier
                            .size(squareSize)
                            .background(squareColor)
                            .clickable { onSquareClicked(coordinate) }
                    ) {
                        if (isLeftEdge) {
                            BasicText(
                                text = coordinate.rank.toString(),
                                style = coordinatesTextStyle,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(coordinatesPadding)
                            )
                        }
                        if (isBottomEdge) {
                            BasicText(
                                text = coordinate.file.toString(),
                                style = coordinatesTextStyle,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(coordinatesPadding)
                            )
                        }
                        position.pieces[coordinate]?.let { piece ->
                            Image(
                                painter = painterResource(piece.drawable),
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(pieceSize)
                            )
                        }
                    }
                }
            }
        }
    }
}

private val BoardSize = 320.dp
private val SmallBoardSize = 140.dp
private val LargeBoardSize = 520.dp

@Composable
private fun ChessBoardPreview(
    position: Position,
    selected: Coordinates? = null,
    orientation: BoardOrientation = BoardOrientation.WHITE,
    boardSize: Dp = BoardSize
) {
    AppTheme {
        ChessBoard(
            position = position,
            selected = selected,
            orientation = orientation,
            boardSize = boardSize,
            onSquareClicked = {}
        )
    }
}

@Preview
@Composable
fun ChessBoardInitialPreview() {
    ChessBoardPreview(position = Position.INITIAL)
}

@Preview
@Composable
fun ChessBoardFlippedPreview() {
    ChessBoardPreview(
        position = Position.INITIAL,
        orientation = BoardOrientation.BLACK
    )
}

@Preview
@Composable
fun ChessBoardSelectedSquarePreview() {
    ChessBoardPreview(
        position = Position.INITIAL,
        selected = Coordinates.parse("d2")
    )
}

@Preview
@Composable
fun ChessBoardEmptyPreview() {
    ChessBoardPreview(position = Position(emptyMap()))
}

@Preview
@Composable
fun ChessBoardSparsePreview() {
    ChessBoardPreview(
        position = Position.parse("Ra1 Qh8 qd4"),
        selected = Coordinates.parse("d4")
    )
}

@Preview
@Composable
fun ChessBoardSmallPreview() {
    ChessBoardPreview(
        position = Position.INITIAL,
        boardSize = SmallBoardSize
    )
}

@Preview
@Composable
fun ChessBoardLargePreview() {
    ChessBoardPreview(
        position = Position.INITIAL,
        boardSize = LargeBoardSize
    )
}
