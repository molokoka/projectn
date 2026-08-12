package molokoka.project.n.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import molokoka.project.n.domain.chess.BOARD_SIZE
import molokoka.project.n.domain.chess.ChessCoordinates
import molokoka.project.n.domain.nqueen.NQueenConflictVisualization
import molokoka.project.n.domain.nqueen.calculateNQueenConflicts

data class ChessBoardState(
    val queensPositions: Set<ChessCoordinates> = emptySet(),
    val nQueenConflictVisualization: NQueenConflictVisualization = NQueenConflictVisualization()
) {

    fun toggleQueen(newQueen: ChessCoordinates): ChessBoardState {
        val newQueensPositions = if (queensPositions.contains(newQueen)) {
            queensPositions - newQueen
        } else {
            if (queensPositions.size >= BOARD_SIZE) {
                return this
            }
            queensPositions + newQueen
        }
        val conflicts = calculateNQueenConflicts(newQueensPositions)

        return copy(
            queensPositions = newQueensPositions,
            nQueenConflictVisualization = conflicts
        )
    }

    fun hasQueen(coordinate: ChessCoordinates): Boolean = queensPositions.contains(coordinate)

    fun isConflictingQueen(coordinate: ChessCoordinates): Boolean =
        nQueenConflictVisualization.conflictingQueens.contains(coordinate) == true

    fun isConflictHighlightedSquare(coordinate: ChessCoordinates): Boolean =
        nQueenConflictVisualization.attackLines.contains(coordinate) == true
}

// TODO: make abstract chess board
@Composable
fun ChessBoard(
    modifier: Modifier = Modifier,
    boardState: ChessBoardState,
    onSquareClicked: (ChessCoordinates) -> Unit
) {
    val uiConfig = chessBoardUiConfig()

    Column(modifier = modifier) {
        repeat(BOARD_SIZE) { row ->
            Row {
                repeat(BOARD_SIZE) { col ->
                    val coordinate = ChessCoordinates.Companion.fromRowCol(row, col)

                    val isLightSquare = (row + col) % 2 == 0
                    val baseSquareColor = if (isLightSquare) uiConfig.lightSquareColor else uiConfig.darkSquareColor
                    val isLeftEdge = col == 0
                    val isBottomEdge = row == BOARD_SIZE - 1

                    val isConflictHighlight = boardState.isConflictHighlightedSquare(coordinate)
                    val squareColor = if (isConflictHighlight) {
                        uiConfig.withConflictHighlight(baseSquareColor)
                    } else {
                        baseSquareColor
                    }

                    val hasQueen = boardState.hasQueen(coordinate)
                    val isConflictingQueen = boardState.isConflictingQueen(coordinate)

                    Box(
                        modifier = Modifier
                            .size(uiConfig.squareSize)
                            .background(squareColor)
                            .clickable { onSquareClicked(coordinate) }
                    ) {

                        if (hasQueen) {
                            BasicText(
                                text = "♛",
                                style = if (isConflictingQueen) uiConfig.conflictingQueenTextStyle else uiConfig.queenTextStyle,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }

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
