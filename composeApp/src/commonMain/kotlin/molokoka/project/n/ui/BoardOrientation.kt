package molokoka.project.n.ui

import molokoka.project.n.domain.chess.FILE_RANGE
import molokoka.project.n.domain.chess.RANK_RANGE

enum class BoardOrientation {
    WHITE,
    BLACK
}

fun ranksInDrawOrder(orientation: BoardOrientation): IntProgression =
    when (orientation) {
        BoardOrientation.WHITE -> RANK_RANGE.reversed()
        BoardOrientation.BLACK -> RANK_RANGE
    }

fun filesInDrawOrder(orientation: BoardOrientation): CharProgression =
    when (orientation) {
        BoardOrientation.WHITE -> FILE_RANGE
        BoardOrientation.BLACK -> FILE_RANGE.reversed()
    }
