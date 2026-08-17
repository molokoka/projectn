package molokoka.project.n.analysis

import androidx.lifecycle.SavedStateHandle
import molokoka.project.n.domain.AnalysisTree
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Move.Companion.parse
import molokoka.project.n.move_evaluation.MoveEvaluation.BLACK_BETTER
import molokoka.project.n.move_evaluation.MoveEvaluation.EQUAL
import molokoka.project.n.move_evaluation.MoveEvaluation.WHITE_BETTER

private const val PreviewEvaluationGeneration = 1

private val whiteFirstMove = parse("b2a2")
private val blackFirstMove = parse("b8b5")
private val whiteSecondMove = parse("h2g2")
private val blackSecondMove = parse("a7a4")
private val whiteThirdMove = parse("g1f1")
private val blackThirdMove = parse("d8d5")
private val whiteFourthMove = parse("c1c5")
private val blackFourthMove = parse("h8h5")

private val blackAlternativeFirstMove = parse("d8d5")

private val beforeAnyMove = emptyList<Move>()
private val afterWhiteFirstMove = listOf(whiteFirstMove)
private val afterBlackFirstMove = afterWhiteFirstMove + blackFirstMove
private val afterWhiteSecondMove = afterBlackFirstMove + whiteSecondMove
private val afterBlackSecondMove = afterWhiteSecondMove + blackSecondMove
private val afterWhiteThirdMove = afterBlackSecondMove + whiteThirdMove
private val afterBlackThirdMove = afterWhiteThirdMove + blackThirdMove
private val afterWhiteFourthMove = afterBlackThirdMove + whiteFourthMove
private val afterBlackFourthMove = afterWhiteFourthMove + blackFourthMove

private val afterBlackAlternativeFirstMove = afterWhiteFirstMove + blackAlternativeFirstMove

private val openingLineTree = AnalysisTree()
    .play(beforeAnyMove, whiteFirstMove)
    .play(afterWhiteFirstMove, blackFirstMove)
    .play(afterBlackFirstMove, whiteSecondMove)
    .play(afterWhiteSecondMove, blackSecondMove)

private val branchedLineTree = openingLineTree
    .play(afterWhiteFirstMove, blackAlternativeFirstMove)

private val evaluatedLineTree = openingLineTree
    .play(afterBlackSecondMove, whiteThirdMove)
    .play(afterWhiteThirdMove, blackThirdMove)
    .play(afterBlackThirdMove, whiteFourthMove)
    .play(afterWhiteFourthMove, blackFourthMove)
    .withEvaluations(
        generation = PreviewEvaluationGeneration,
        evaluations = mapOf(
            afterWhiteFirstMove to BLACK_BETTER,
            afterBlackFirstMove to EQUAL,
            afterWhiteSecondMove to WHITE_BETTER,
            afterBlackSecondMove to BLACK_BETTER,
            afterWhiteThirdMove to EQUAL,
            afterBlackThirdMove to WHITE_BETTER,
            afterWhiteFourthMove to BLACK_BETTER,
            afterBlackFourthMove to EQUAL
        )
    )

internal val PreviewOpeningLineWithLastMoveSelected = AnalysisState(
    tree = openingLineTree,
    moves = afterBlackSecondMove
)

internal val PreviewOpeningLineWithStartSelected = AnalysisState(
    tree = openingLineTree,
    moves = beforeAnyMove
)

internal val PreviewBranchedLineWithAlternativeSelected = AnalysisState(
    tree = branchedLineTree,
    moves = afterBlackAlternativeFirstMove
)

internal val PreviewEvaluatedLineWithLastMoveSelected = AnalysisState(
    tree = evaluatedLineTree,
    moves = afterBlackFourthMove
)

internal fun previewHandle(analysis: AnalysisState): SavedStateHandle {
    val handle = SavedStateHandle()
    analysis.saveTo(handle)

    return handle
}
