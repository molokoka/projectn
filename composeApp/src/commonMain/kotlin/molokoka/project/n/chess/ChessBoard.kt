package molokoka.project.n.chess

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

data class ChessBoardState(
    val boardSize: Int,
    val queensPositions: Set<ChessCoordinate> = emptySet(),
    val conflictVisualization: ConflictVisualization = ConflictVisualization()
) {

    fun toggleQueen(newQueen: ChessCoordinate): ChessBoardState {
        val newQueensPositions = if (queensPositions.contains(newQueen)) {
            queensPositions - newQueen
        } else {
            // Don't allow adding more than boardSize number of queens
            if (queensPositions.size >= boardSize) {
                return this // Return unchanged state
            }
            queensPositions + newQueen
        }
        val conflicts = calculateConflicts(newQueensPositions, boardSize)

        return copy(
            queensPositions = newQueensPositions,
            conflictVisualization = conflicts
        )
    }

    fun hasQueen(coordinate: ChessCoordinate): Boolean = queensPositions.contains(coordinate)

    fun isConflictingQueen(coordinate: ChessCoordinate): Boolean =
        conflictVisualization.conflictingQueens.contains(coordinate) == true

    fun isConflictHighlightedSquare(coordinate: ChessCoordinate): Boolean =
        conflictVisualization.highlightedSquares.contains(coordinate) == true
}

@Composable
fun ChessBoard(
    modifier: Modifier = Modifier,
    boardState: ChessBoardState,
    config: ChessBoardConfig,
    onSquareClicked: (ChessCoordinate) -> Unit
) {
    Column(modifier = modifier) {
        repeat(boardState.boardSize) { row ->
            Row {
                repeat(boardState.boardSize) { col ->
                    val coordinate = ChessCoordinate.fromRowCol(row, col, boardState.boardSize)

                    val isLightSquare = (row + col) % 2 == 0
                    val baseSquareColor = if (isLightSquare) config.lightSquareColor else config.darkSquareColor
                    val isLeftEdge = col == 0
                    val isBottomEdge = row == boardState.boardSize - 1

                    val isConflictHighlight = boardState.isConflictHighlightedSquare(coordinate)
                    val squareColor = if (isConflictHighlight)
                        baseSquareColor.copy(alpha = 0.7f).compositeOver(config.conflictHighlightColor)
                    else
                        baseSquareColor

                    val hasQueen = boardState.hasQueen(coordinate)
                    val isConflictingQueen = boardState.isConflictingQueen(coordinate)

                    Box(
                        modifier = Modifier
                            .size(config.squareSize)
                            .background(squareColor)
                            .clickable { onSquareClicked(coordinate) }
                    ) {
                        Queen(
                            modifier = Modifier.align(Alignment.Center),
                            show = hasQueen,
                            queenTextStyle = if (isConflictingQueen) config.conflictingQueenTextStyle else config.queenTextStyle
                        )

                        if (isLeftEdge) {
                            BasicText(
                                text = coordinate.rank.toString(),
                                style = config.coordinateTextStyle,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(1.dp)
                            )
                        }
                        if (isBottomEdge) {
                            BasicText(
                                text = coordinate.file.toString(),
                                style = config.coordinateTextStyle,
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

@Composable
private fun Queen(
    modifier: Modifier = Modifier,
    show: Boolean,
    queenTextStyle: TextStyle,
) {
    if (show) {
        BasicText(
            text = "♛",
            style = queenTextStyle,
            modifier = modifier,
        )
    }
}
