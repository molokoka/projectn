package molokoka.project.n.analysis

import molokoka.project.n.domain.AnalyticsTree
import molokoka.project.n.domain.Coordinates
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.Side
import molokoka.project.n.domain.util.moveTreeDiagram
import molokoka.project.n.domain.util.positionDiagram
import molokoka.project.n.ui.BoardOrientation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AnalysisStateTest {

    // starting state

    @Test
    fun `the board starts from the white orientation`() {
        assertEquals(BoardOrientation.WHITE, AnalysisState().orientation)
    }

    @Test
    fun `the board starts from the initial position`() {
        assertEquals(Position.INITIAL, AnalysisState().position)
    }

    // flipping the board

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

    // selecting a square

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
    fun `clicking another piece of the side to move reselects it`() {
        val state = AnalysisState()
            .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
            .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("c1"))).first

        assertEquals(Coordinates.parse("c1"), state.selected)
        assertEquals(emptyList(), state.moves)
    }

    // playing moves

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
    fun `playing a move records it in the tree`() {
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
    fun `plays a line four moves long`() {
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

    // selecting a node

    @Test
    fun `selecting an initial node shows initial state`() {
        val state = AnalysisState()
            .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
            .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4
            .reduce(AnalysisIntent.SelectNode(emptyList())).first

        assertEquals(Position.INITIAL, state.position)
        assertEquals(emptyList(), state.moves)
    }

    @Test
    fun `selecting an node shows list of moves and position for that node`() {
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
    fun `selecting multiple nodes shows list of moves and position for last selected node`() {
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

    // resetting

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

        assertEquals(AnalyticsTree(), state.tree)
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

    // requesting a computer move

    @Test
    fun `requesting a computer move marks it pending`() {
        val state = AnalysisState()
            .reduce(AnalysisIntent.RequestComputerMove).first

        assertTrue(state.computerMovePending)
    }

    @Test
    fun `requesting a computer move starts one for the selected node`() {
        val effect = AnalysisState()
            .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
            .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4
            .reduce(AnalysisIntent.RequestComputerMove).second

        assertEquals(
            AnalysisEffect.StartComputerMove(
                AnalyticsTree().play(emptyList(), Move.parse("a1a4")),
                listOf(Move.parse("a1a4"))
            ),
            effect
        )
    }

    // cancelling a pending computer move

    @Test
    fun `selecting another node cancels the computer move`() {
        val effect = AnalysisState()
            .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
            .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4
            .reduce(AnalysisIntent.RequestComputerMove).first
            .reduce(AnalysisIntent.SelectNode(emptyList())).second

        assertEquals(AnalysisEffect.CancelComputerMove, effect)
    }

    @Test
    fun `selecting another node stops waiting for the computer`() {
        val state = AnalysisState()
            .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a1"))).first
            .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("a4"))).first // white a1a4
            .reduce(AnalysisIntent.RequestComputerMove).first
            .reduce(AnalysisIntent.SelectNode(emptyList())).first

        assertFalse(state.computerMovePending)
    }

    @Test
    fun `selecting the node already shown keeps waiting for the computer`() {
        val update = AnalysisState()
            .reduce(AnalysisIntent.RequestComputerMove).first
            .reduce(AnalysisIntent.SelectNode(emptyList()))

        assertTrue(update.first.computerMovePending)
        assertNull(update.second)
    }

    @Test
    fun `playing a move cancels the computer move`() {
        val effect = AnalysisState()
            .reduce(AnalysisIntent.RequestComputerMove).first
            .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("c1"))).first
            .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("c4"))).second // white c1c4

        assertEquals(AnalysisEffect.CancelComputerMove, effect)
    }

    @Test
    fun `playing a move stops waiting for the computer`() {
        val state = AnalysisState()
            .reduce(AnalysisIntent.RequestComputerMove).first
            .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("c1"))).first
            .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("c4"))).first // white c1c4

        assertFalse(state.computerMovePending)
    }

    @Test
    fun `picking up a piece keeps waiting for the computer`() {
        val update = AnalysisState()
            .reduce(AnalysisIntent.RequestComputerMove).first
            .reduce(AnalysisIntent.OnSquareClick(Coordinates.parse("c1")))

        assertTrue(update.first.computerMovePending)
        assertNull(update.second)
    }

    @Test
    fun `resetting cancels the computer move`() {
        val effect = AnalysisState()
            .reduce(AnalysisIntent.RequestComputerMove).first
            .reduce(AnalysisIntent.Reset).second

        assertEquals(AnalysisEffect.CancelComputerMove, effect)
    }

    // receiving a computer move

    @Test
    fun `plays the computer move at the node that asked for it`() {
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
    fun `selects the computer move it played`() {
        val state = AnalysisState()
            .reduce(AnalysisIntent.RequestComputerMove).first
            .reduce(AnalysisIntent.ComputerMoveReady(emptyList(), Move.parse("a1a4"))).first

        assertEquals(listOf(Move.parse("a1a4")), state.moves)
    }

    @Test
    fun `ignores a computer move for a node the user has left`() {
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
        assertEquals(emptyList(), state.moves)
    }

    @Test
    fun `ignores a computer move for a node no longer in the tree`() {
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

        assertEquals(AnalyticsTree(), state.tree)
    }

    @Test
    fun `plays nothing when the computer has no move`() {
        val state = AnalysisState()
            .reduce(AnalysisIntent.RequestComputerMove).first
            .reduce(AnalysisIntent.ComputerMoveReady(emptyList(), null)).first

        assertEquals(AnalyticsTree(), state.tree)
    }

    @Test
    fun `plays nothing when the computer move is illegal`() {
        // a1a8 is blocked: a7 holds a black queen
        val state = AnalysisState()
            .reduce(AnalysisIntent.RequestComputerMove).first
            .reduce(AnalysisIntent.ComputerMoveReady(emptyList(), Move.parse("a1a8"))).first

        assertEquals(AnalyticsTree(), state.tree)
    }

    @Test
    fun `an illegal computer move stops waiting for it`() {
        val state = AnalysisState()
            .reduce(AnalysisIntent.RequestComputerMove).first
            .reduce(AnalysisIntent.ComputerMoveReady(emptyList(), Move.parse("a1a8"))).first

        assertFalse(state.computerMovePending)
    }

    @Test
    fun `playing the computer move stops waiting for it`() {
        val state = AnalysisState()
            .reduce(AnalysisIntent.RequestComputerMove).first
            .reduce(AnalysisIntent.ComputerMoveReady(emptyList(), Move.parse("a1a4"))).first

        assertFalse(state.computerMovePending)
    }

    @Test
    fun `an empty computer move stops waiting for it`() {
        val state = AnalysisState()
            .reduce(AnalysisIntent.RequestComputerMove).first
            .reduce(AnalysisIntent.ComputerMoveReady(emptyList(), null)).first

        assertFalse(state.computerMovePending)
    }
}
