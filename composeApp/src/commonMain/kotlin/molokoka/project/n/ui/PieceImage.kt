package molokoka.project.n.ui

import molokoka.project.n.domain.Side
import molokoka.project.n.domain.pieces.Piece
import molokoka.project.n.domain.pieces.PieceType
import org.jetbrains.compose.resources.DrawableResource
import projectn.composeapp.generated.resources.Res
import projectn.composeapp.generated.resources.queen_black
import projectn.composeapp.generated.resources.queen_white
import projectn.composeapp.generated.resources.rook_black
import projectn.composeapp.generated.resources.rook_white

val Piece.drawable: DrawableResource
    get() = when (type) {
        PieceType.ROOK ->
            if (side == Side.WHITE) Res.drawable.rook_white else Res.drawable.rook_black
        PieceType.QUEEN ->
            if (side == Side.WHITE) Res.drawable.queen_white else Res.drawable.queen_black
    }
