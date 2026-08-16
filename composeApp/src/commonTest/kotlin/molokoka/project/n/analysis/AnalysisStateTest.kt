package molokoka.project.n.analysis

import molokoka.project.n.analysis.move_evaluation.MoveEvaluation
import molokoka.project.n.domain.Coordinates
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.Side
import molokoka.project.n.domain.play
import molokoka.project.n.analysis.util.moveTreeDiagram
import molokoka.project.n.domain.util.positionDiagram
import molokoka.project.n.ui.BoardOrientation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AnalysisStateTest {

    class TheStartingState {

        @Test
        fun `the board starts from the white orientation`() {
            assertEquals(BoardOrientation.WHITE, AnalysisState().orientation)
        }

        @Test
        fun `the board starts from the initial position`() {
            assertEquals(Position.INITIAL, AnalysisState().position)
        }
    }

    class FlippingTheBoard {

        @Test
        fun `flipping the board switches to the black orientation`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.FlipBoard).first

            assertEquals(BoardOrientation.BLACK, state.orientation)
        }

        @Test
        fun `flipping the board twice returns to the white orientation`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.FlipBoard).first
                .reduce(AnalysisIntent.FlipBoard).first

            assertEquals(BoardOrientation.WHITE, state.orientation)
        }
    }

    class SelectingASquare {

        @Test
        fun `clicking a piece of the side to move selects it`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first

            assertEquals(Coordinates.parse("a1"), state.selected)
        }

        @Test
        fun `clicking an opposing piece selects nothing`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("b8"))).first

            assertNull(state.selected)
        }

        @Test
        fun `clicking an empty square selects nothing`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first

            assertNull(state.selected)
        }

        @Test
        fun `clicking the selected square deselects it`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first

            assertNull(state.selected)
        }

        @Test
        fun `clicking an empty square the piece cannot reach deselects it`() {
            // the rook on a1 moves along rank one and file a, so e5 is not a move
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("e5"))).first

            assertNull(state.selected)
        }

        @Test
        fun `clicking an empty square the piece cannot reach plays nothing`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("e5"))).first

            assertEquals(emptyList(), state.moves)
        }

        @Test
        fun `clicking an opposing piece the piece cannot reach deselects it`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("b8"))).first

            assertNull(state.selected)
        }

        @Test
        fun `clicking another piece of the side to move reselects it`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("c1"))).first

            assertEquals(Coordinates.parse("c1"), state.selected)
            assertEquals(emptyList(), state.moves)
        }
    }

    class PlayingMoves {

        @Test
        fun `clicking a legal target plays the move`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4

            assertEquals(listOf(Move.parse("a1a4")), state.moves)
            assertEquals(
                """
                8 . r . r . r . r
                7 q . q . q . q .
                6 . . . . . . . .
                5 . . . . . . . .
                4 R . . . . . . .
                3 . . . . . . . .
                2 . Q . Q . Q . Q
                1 . . R . R . R .
                  a b c d e f g h
                """.trimIndent(),
                state.position.positionDiagram()
            )
        }

        @Test
        fun `playing a move deselects the piece`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4

            assertNull(state.selected)
        }

        @Test
        fun `playing a move gives the other side the move`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4

            assertEquals(Side.BLACK, state.sideToMove)
        }

        @Test
        fun `playing a move creates a child node`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4

            assertEquals(listOf(listOf(Move.parse("a1a4"))), state.tree.paths())
        }

        @Test
        fun `playing moves builds up the list of moves`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("b8"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("b5"))).first // black b8b5

            assertEquals(listOf(Move.parse("a1a4"), Move.parse("b8b5")), state.moves)
            assertEquals(
                """
                8 . . . r . r . r
                7 q . q . q . q .
                6 . . . . . . . .
                5 . r . . . . . .
                4 R . . . . . . .
                3 . . . . . . . .
                2 . Q . Q . Q . Q
                1 . . R . R . R .
                  a b c d e f g h
                """.trimIndent(),
                state.position.positionDiagram()
            )
        }

        @Test
        fun `playing four moves builds up the whole line`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("b8"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("b5"))).first // black b8b5
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("c1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("c4"))).first // white c1c4
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("d8"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("d5"))).first // black d8d5

            assertEquals(
                listOf(
                    Move.parse("a1a4"),
                    Move.parse("b8b5"),
                    Move.parse("c1c4"),
                    Move.parse("d8d5")
                ),
                state.moves
            )
            assertEquals(
                """
                8 . . . . . r . r
                7 q . q . q . q .
                6 . . . . . . . .
                5 . r . r . . . .
                4 R . R . . . . .
                3 . . . . . . . .
                2 . Q . Q . Q . Q
                1 . . . . R . R .
                  a b c d e f g h
                """.trimIndent(),
                state.position.positionDiagram()
            )
            assertEquals(Side.WHITE, state.sideToMove)
        }

        @Test
        fun `playing a move that already exists selects it instead of duplicating it`() {
            val move = Move.parse("a1a4")

            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(move.from)).first
                .reduce(AnalysisIntent.OnSquareClick(move.to)).first // white a1a4
                .reduce(AnalysisIntent.SelectNode(emptyList())).first
                .reduce(AnalysisIntent.OnSquareClick(move.from)).first
                .reduce(AnalysisIntent.OnSquareClick(move.to)).first // white a1a4 again

            assertEquals(
                """
                Start
                └── $move
                """.trimIndent(),
                state.tree.moveTreeDiagram()
            )
            assertEquals(listOf(move), state.moves)
        }

        @Test
        fun `playing from an earlier node creates a variation`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4
                .reduce(AnalysisIntent.SelectNode(emptyList())).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("c1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("c4"))).first // white c1c4, from the root again

            assertEquals(
                """
                Start
                ├── a1a4
                └── c1c4
                """.trimIndent(),
                state.tree.moveTreeDiagram()
            )
        }

        @Test
        fun `playing from a node at depth creates a variation there`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("b8"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("b5"))).first // black b8b5
                .reduce(AnalysisIntent.SelectNode(listOf(Move.parse("a1a4")))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("d8"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("d5"))).first // black d8d5, instead of b8b5

            assertEquals(
                """
                Start
                └── a1a4
                    ├── b8b5
                    └── d8d5
                """.trimIndent(),
                state.tree.moveTreeDiagram()
            )
            assertEquals(listOf(Move.parse("a1a4"), Move.parse("d8d5")), state.moves)
        }
    }

    class SelectingANode {

        @Test
        fun `selecting the start node shows the initial position`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4
                .reduce(AnalysisIntent.SelectNode(emptyList())).first

            assertEquals(Position.INITIAL, state.position)
            assertEquals(emptyList(), state.moves)
        }

        @Test
        fun `selecting a node shows the position at that node`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("b8"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("b5"))).first // black b8b5
                .reduce(AnalysisIntent.SelectNode(listOf(Move.parse("a1a4")))).first

            assertEquals(listOf(Move.parse("a1a4")), state.moves)
            // b8b5 is undone: the black rook is back on b8
            assertEquals(
                """
                8 . r . r . r . r
                7 q . q . q . q .
                6 . . . . . . . .
                5 . . . . . . . .
                4 R . . . . . . .
                3 . . . . . . . .
                2 . Q . Q . Q . Q
                1 . . R . R . R .
                  a b c d e f g h
                """.trimIndent(),
                state.position.positionDiagram()
            )
        }

        @Test
        fun `selecting a node deep in the tree shows every move up to it`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("b8"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("b5"))).first // black b8b5
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("c1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("c4"))).first // white c1c4
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("d8"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("d5"))).first // black d8d5
                .reduce(AnalysisIntent.SelectNode(emptyList())).first
                .reduce(
                    AnalysisIntent.SelectNode(listOf(Move.parse("a1a4"), Move.parse("b8b5")))
                ).first

            assertEquals(listOf(Move.parse("a1a4"), Move.parse("b8b5")), state.moves)
            // the last two moves are undone: c1 and d8 are occupied again
            assertEquals(
                """
                8 . . . r . r . r
                7 q . q . q . q .
                6 . . . . . . . .
                5 . r . . . . . .
                4 R . . . . . . .
                3 . . . . . . . .
                2 . Q . Q . Q . Q
                1 . . R . R . R .
                  a b c d e f g h
                """.trimIndent(),
                state.position.positionDiagram()
            )
        }

        @Test
        fun `selecting a second node shows the position at the one selected last`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4
                .reduce(AnalysisIntent.SelectNode(emptyList())).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("c1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("c4"))).first // white c1c4, a second branch
                .reduce(AnalysisIntent.SelectNode(listOf(Move.parse("a1a4")))).first

            assertEquals(listOf(Move.parse("a1a4")), state.moves)
            // the c1c4 branch is not replayed: c1 still holds its rook
            assertEquals(
                """
                8 . r . r . r . r
                7 q . q . q . q .
                6 . . . . . . . .
                5 . . . . . . . .
                4 R . . . . . . .
                3 . . . . . . . .
                2 . Q . Q . Q . Q
                1 . . R . R . R .
                  a b c d e f g h
                """.trimIndent(),
                state.position.positionDiagram()
            )
        }

        @Test
        fun `selecting a node gives the move back to the side that follows`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("b8"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("b5"))).first // black b8b5
                .reduce(AnalysisIntent.SelectNode(listOf(Move.parse("a1a4")))).first

            assertEquals(Side.BLACK, state.sideToMove)
        }

        @Test
        fun `selecting a node that is not in the tree changes nothing`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.SelectNode(listOf(Move.parse("a1a4")))).first

            assertEquals(emptyList(), state.moves)
        }
    }

    class Resetting {

        @Test
        fun `resetting a flipped board returns to the white orientation`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.FlipBoard).first
                .reduce(AnalysisIntent.Reset).first

            assertEquals(BoardOrientation.WHITE, state.orientation)
        }

        @Test
        fun `resetting clears the tree`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4
                .reduce(AnalysisIntent.Reset).first

            assertEquals(AnalysisTree(), state.tree)
        }

        @Test
        fun `resetting clears the selected square`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.Reset).first

            assertNull(state.selected)
        }

        @Test
        fun `resetting clears the moves played`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4
                .reduce(AnalysisIntent.Reset).first

            assertEquals(emptyList(), state.moves)
            assertEquals(Position.INITIAL, state.position)
        }
    }

    class RequestingAComputerMove {

        @Test
        fun `requesting a computer move shows loading`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.RequestComputerMove).first

            assertTrue(state.isComputerMovePending)
        }

        @Test
        fun `requesting a computer move starts one for the selected node`() {
            val effect = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4
                .reduce(AnalysisIntent.RequestComputerMove).second

            assertEquals(
                listOf(
                    AnalysisEffect.StartComputerMove(
                        Position.INITIAL.play(Move.parse("a1a4"), Side.WHITE),
                        Side.BLACK,
                        listOf(Move.parse("a1a4"))
                    )
                ),
                effect
            )
        }
    }

    class CancellingAPendingComputerMove {

        @Test
        fun `changing the visible position cancels the computer move`() {
            val effect = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4
                .reduce(AnalysisIntent.RequestComputerMove).first
                .reduce(AnalysisIntent.SelectNode(emptyList())).second

            assertEquals(listOf(AnalysisEffect.CancelComputerMove), effect)
        }

        @Test
        fun `changing the visible position hides the computer move loading`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4
                .reduce(AnalysisIntent.RequestComputerMove).first
                .reduce(AnalysisIntent.SelectNode(emptyList())).first

            assertFalse(state.isComputerMovePending)
        }

        @Test
        fun `selecting the position already shown keeps the computer move loading`() {
            val update = AnalysisState()
                .reduce(AnalysisIntent.RequestComputerMove).first
                .reduce(AnalysisIntent.SelectNode(emptyList()))

            assertTrue(update.first.isComputerMovePending)
            assertTrue(update.second.isEmpty())
        }

        @Test
        fun `playing a move hides the computer move loading and cancels it`() {
            val update = AnalysisState()
                .reduce(AnalysisIntent.RequestComputerMove).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("c1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("c4"))) // white c1c4

            assertFalse(update.first.isComputerMovePending)
            assertEquals(listOf(AnalysisEffect.CancelComputerMove), update.second)
        }

        @Test
        fun `picking up a piece keeps the computer move loading`() {
            val update = AnalysisState()
                .reduce(AnalysisIntent.RequestComputerMove).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("c1")))

            assertTrue(update.first.isComputerMovePending)
            assertTrue(update.second.isEmpty())
        }

        @Test
        fun `resetting cancels the computer move`() {
            val effects = AnalysisState()
                .reduce(AnalysisIntent.RequestComputerMove).first
                .reduce(AnalysisIntent.Reset).second

            assertTrue(effects.contains(AnalysisEffect.CancelComputerMove))
        }
    }

    class ReceivingAComputerMove {

        @Test
        fun `receiving a computer move adds it when the visible position has not changed`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.RequestComputerMove).first
                .reduce(AnalysisIntent.ComputerMoveReady(emptyList(), Move.parse("a1a4"))).first

            assertEquals(
                """
                Start
                └── a1a4
                """.trimIndent(),
                state.tree.moveTreeDiagram()
            )
        }

        @Test
        fun `receiving a computer move selects the child it added`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.RequestComputerMove).first
                .reduce(AnalysisIntent.ComputerMoveReady(emptyList(), Move.parse("a1a4"))).first

            assertEquals(listOf(Move.parse("a1a4")), state.moves)
        }

        @Test
        fun `a computer move for a position no longer visible is not added to the tree`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4
                .reduce(AnalysisIntent.RequestComputerMove).first
                .reduce(AnalysisIntent.SelectNode(emptyList())).first
                .reduce(
                    AnalysisIntent.ComputerMoveReady(
                        listOf(Move.parse("a1a4")),
                        Move.parse("b8b5")
                    )
                ).first

            // b8b5 would have gone under a1a4
            assertEquals(
                """
                Start
                └── a1a4
                """.trimIndent(),
                state.tree.moveTreeDiagram()
            )
        }

        @Test
        fun `a computer move for a position no longer visible does not change the selected node`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4
                .reduce(AnalysisIntent.RequestComputerMove).first
                .reduce(AnalysisIntent.SelectNode(emptyList())).first
                .reduce(
                    AnalysisIntent.ComputerMoveReady(
                        listOf(Move.parse("a1a4")),
                        Move.parse("b8b5")
                    )
                ).first

            assertEquals(emptyList(), state.moves)
        }

        @Test
        fun `a computer move arriving after reset cannot alter the reset state`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4
                .reduce(AnalysisIntent.RequestComputerMove).first
                .reduce(AnalysisIntent.Reset).first
                .reduce(
                    AnalysisIntent.ComputerMoveReady(
                        listOf(Move.parse("a1a4")),
                        Move.parse("b8b5")
                    )
                ).first

            assertEquals(AnalysisTree(), state.tree)
        }

        @Test
        fun `receiving an invalid computer move plays nothing`() {
            // a1a8 is blocked: a7 holds a black queen
            val state = AnalysisState()
                .reduce(AnalysisIntent.RequestComputerMove).first
                .reduce(AnalysisIntent.ComputerMoveReady(emptyList(), Move.parse("a1a8"))).first

            assertEquals(AnalysisTree(), state.tree)
        }

        @Test
        fun `receiving an invalid computer move hides the loading`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.RequestComputerMove).first
                .reduce(AnalysisIntent.ComputerMoveReady(emptyList(), Move.parse("a1a8"))).first

            assertFalse(state.isComputerMovePending)
        }

        @Test
        fun `receiving a computer move hides the loading`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.RequestComputerMove).first
                .reduce(AnalysisIntent.ComputerMoveReady(emptyList(), Move.parse("a1a4"))).first

            assertFalse(state.isComputerMovePending)
        }

        @Test
        fun `finding no computer move hides the loading`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.RequestComputerMove).first
                .reduce(AnalysisIntent.ComputerMoveNotFound(emptyList())).first

            assertFalse(state.isComputerMovePending)
        }

    }

    class RequestingAMoveEvaluation {

        @Test
        fun `requesting a move evaluation shows loading`() {
            val state = AnalysisState()
                .reduce(AnalysisIntent.RequestMovesEvaluation).first

            assertTrue(state.isMoveEvaluationPending)
        }

        @Test
        fun `requesting a move evaluation analyses every move node in the tree`() {
            val played = AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4

            val effects = played.reduce(AnalysisIntent.RequestMovesEvaluation).second

            assertEquals(listOf(AnalysisEffect.StartMovesEvaluation(1, played.tree)), effects)
        }

        @Test
        fun `a move evaluation may be started again while an earlier one is running`() {
            val effects = AnalysisState()
                .reduce(AnalysisIntent.RequestMovesEvaluation).first
                .reduce(AnalysisIntent.RequestMovesEvaluation).second

            assertEquals(listOf(AnalysisEffect.StartMovesEvaluation(2, AnalysisTree())), effects)
        }
    }

    class CancellingAPendingMoveEvaluation {

        @Test
        fun `resetting cancels the move evaluation`() {
            val effects = AnalysisState()
                .reduce(AnalysisIntent.RequestMovesEvaluation).first
                .reduce(AnalysisIntent.Reset).second

            assertTrue(effects.contains(AnalysisEffect.CancelMoveEvaluation))
        }
    }

    class ReceivingAMoveEvaluation {

        @Test
        fun `receiving evaluations shows one beside every move`() {
            val move = Move.parse("a1a4")
            val answer = MoveEvaluation.WHITE_BETTER

            val state = evaluationRequestedAfter(move)
                .reduce(AnalysisIntent.MovesEvaluationReady(1, mapOf(listOf(move) to answer))).first

            assertEquals(
                """
                Start
                └── $move$answer
                """.trimIndent(),
                state.tree.moveTreeDiagram()
            )
        }

        @Test
        fun `receiving evaluations hides the loading`() {
            val move = Move.parse("a1a4")

            val state = evaluationRequestedAfter(move)
                .reduce(
                    AnalysisIntent.MovesEvaluationReady(
                        1,
                        mapOf(listOf(move) to MoveEvaluation.WHITE_BETTER)
                    )
                ).first

            assertFalse(state.isMoveEvaluationPending)
        }

        @Test
        fun `an older request may not overwrite the newer results`() {
            val move = Move.parse("a1a4")
            val olderAnswer = MoveEvaluation.WHITE_BETTER
            val newerAnswer = MoveEvaluation.BLACK_BETTER

            val state = evaluationRequestedAfter(move)
                .reduce(AnalysisIntent.RequestMovesEvaluation).first
                .reduce(AnalysisIntent.MovesEvaluationReady(2, mapOf(listOf(move) to newerAnswer)))
                .first
                .reduce(AnalysisIntent.MovesEvaluationReady(1, mapOf(listOf(move) to olderAnswer)))
                .first

            assertEquals(
                """
                Start
                └── $move$newerAnswer
                """.trimIndent(),
                state.tree.moveTreeDiagram()
            )
        }

        @Test
        fun `evaluations still arrive after the visible position changed`() {
            val move = Move.parse("a1a4")
            val answer = MoveEvaluation.WHITE_BETTER

            val state = evaluationRequestedAfter(move)
                .reduce(AnalysisIntent.SelectNode(emptyList())).first
                .reduce(AnalysisIntent.MovesEvaluationReady(1, mapOf(listOf(move) to answer))).first

            assertEquals(
                """
                Start
                └── $move$answer
                """.trimIndent(),
                state.tree.moveTreeDiagram()
            )
        }

        @Test
        fun `only the moves in the snapshot are evaluated`() {
            val opening = Move.parse("a1a4")
            val reply = Move.parse("b8b5")
            val answer = MoveEvaluation.WHITE_BETTER

            val state = evaluationRequestedAfter(opening)
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("b8"))).first
                .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("b5"))).first // black b8b5
                .reduce(AnalysisIntent.MovesEvaluationReady(1, mapOf(listOf(opening) to answer)))
                .first

            assertEquals(
                """
                Start
                └── $opening$answer
                    └── $reply
                """.trimIndent(),
                state.tree.moveTreeDiagram()
            )
        }

        @Test
        fun `evaluations arriving after reset cannot alter the reset state`() {
            val move = Move.parse("a1a4")

            val state = evaluationRequestedAfter(move)
                .reduce(AnalysisIntent.Reset).first
                .reduce(
                    AnalysisIntent.MovesEvaluationReady(
                        1,
                        mapOf(listOf(move) to MoveEvaluation.WHITE_BETTER)
                    )
                ).first

            assertEquals(AnalysisTree(), state.tree)
        }

        @Test
        fun `replaying a move keeps the evaluation already attached to it`() {
            val move = Move.parse("a1a4")
            val answer = MoveEvaluation.WHITE_BETTER

            val state = evaluationRequestedAfter(move)
                .reduce(AnalysisIntent.MovesEvaluationReady(1, mapOf(listOf(move) to answer))).first
                .reduce(AnalysisIntent.SelectNode(emptyList())).first
                .reduce(AnalysisIntent.OnSquareClick(move.from)).first
                .reduce(AnalysisIntent.OnSquareClick(move.to)).first // white a1a4 again

            assertEquals(
                """
                Start
                └── $move$answer
                """.trimIndent(),
                state.tree.moveTreeDiagram()
            )
        }

        private fun evaluationRequestedAfter(move: Move): AnalysisState =
            AnalysisState()
                .reduce(AnalysisIntent.OnSquareClick(move.from)).first
                .reduce(AnalysisIntent.OnSquareClick(move.to)).first
                .reduce(AnalysisIntent.RequestMovesEvaluation).first
    }
}
