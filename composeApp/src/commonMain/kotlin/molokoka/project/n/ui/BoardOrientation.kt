package molokoka.project.n.ui

enum class BoardOrientation {
    WHITE,
    BLACK
}

fun rowsInDrawOrder(boardSize: Int, orientation: BoardOrientation): IntProgression =
    when (orientation) {
        BoardOrientation.WHITE -> boardSize - 1 downTo 0
        BoardOrientation.BLACK -> 0 until boardSize
    }

fun colsInDrawOrder(boardSize: Int, orientation: BoardOrientation): IntProgression =
    when (orientation) {
        BoardOrientation.WHITE -> 0 until boardSize
        BoardOrientation.BLACK -> boardSize - 1 downTo 0
    }
