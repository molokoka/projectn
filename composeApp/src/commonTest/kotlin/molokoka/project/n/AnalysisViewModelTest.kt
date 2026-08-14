package molokoka.project.n

import molokoka.project.n.analysis.AnalysisViewModel
import molokoka.project.n.domain.AnalyticsTree
import molokoka.project.n.domain.Coordinates
import molokoka.project.n.domain.Move
import molokoka.project.n.domain.Position
import molokoka.project.n.domain.Side
import molokoka.project.n.domain.util.moveTreeDiagram
import molokoka.project.n.domain.util.positionDiagram
import molokoka.project.n.ui.BoardOrientation.BLACK
import molokoka.project.n.ui.BoardOrientation.WHITE
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AnalysisViewModelTest {

    // starting state

    @Test
    fun `the board starts from the white orientation`() {
        assertEquals(WHITE, AnalysisViewModel().state.value.orientation)
    }

    @Test
    fun `the board starts from the initial position`() {
        assertEquals(Position.INITIAL, AnalysisViewModel().state.value.position)
    }

    // flipping the board

    @Test
    fun `flipping the board switches to the black orientation`() {
        val viewModel = AnalysisViewModel()

        viewModel.flipBoard()

        assertEquals(BLACK, viewModel.state.value.orientation)
    }

    @Test
    fun `flipping the board twice returns to the white orientation`() {
        val viewModel = AnalysisViewModel()

        viewModel.flipBoard()
        viewModel.flipBoard()

        assertEquals(WHITE, viewModel.state.value.orientation)
    }

    // selecting a square

    @Test
    fun `clicking a piece of the side to move selects it`() {
        val viewModel = AnalysisViewModel()

        viewModel.onSquareClicked(Coordinates.parse("a1"))

        assertEquals(Coordinates.parse("a1"), viewModel.state.value.selected)
    }

    @Test
    fun `clicking an opposing piece selects nothing`() {
        val viewModel = AnalysisViewModel()

        viewModel.onSquareClicked(Coordinates.parse("b8"))

        assertNull(viewModel.state.value.selected)
    }

    @Test
    fun `clicking an empty square selects nothing`() {
        val viewModel = AnalysisViewModel()

        viewModel.onSquareClicked(Coordinates.parse("a4"))

        assertNull(viewModel.state.value.selected)
    }

    @Test
    fun `clicking the selected square deselects it`() {
        val viewModel = AnalysisViewModel()

        viewModel.onSquareClicked(Coordinates.parse("a1"))
        viewModel.onSquareClicked(Coordinates.parse("a1"))

        assertNull(viewModel.state.value.selected)
    }

    @Test
    fun `clicking another piece of the side to move reselects it`() {
        val viewModel = AnalysisViewModel()

        viewModel.onSquareClicked(Coordinates.parse("a1"))
        viewModel.onSquareClicked(Coordinates.parse("c1"))

        assertEquals(Coordinates.parse("c1"), viewModel.state.value.selected)
        assertEquals(emptyList(), viewModel.state.value.moves)
    }

    // playing moves

    @Test
    fun `clicking a legal target plays the move`() {
        val viewModel = AnalysisViewModel()

        viewModel.onSquareClicked(Coordinates.parse("a1"))
        viewModel.onSquareClicked(Coordinates.parse("a4")) // white a1a4

        assertEquals(listOf(Move.parse("a1a4")), viewModel.state.value.moves)
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
            viewModel.state.value.position.positionDiagram()
        )
        assertNull(viewModel.state.value.selected)
    }

    @Test
    fun `playing a move gives the other side the move`() {
        val viewModel = AnalysisViewModel()

        viewModel.onSquareClicked(Coordinates.parse("a1"))
        viewModel.onSquareClicked(Coordinates.parse("a4")) // white a1a4

        assertEquals(Side.BLACK, viewModel.state.value.sideToMove)
    }

    @Test
    fun `playing a move records it in the tree`() {
        val viewModel = AnalysisViewModel()

        viewModel.onSquareClicked(Coordinates.parse("a1"))
        viewModel.onSquareClicked(Coordinates.parse("a4")) // white a1a4

        assertEquals(
            listOf(listOf(Move.parse("a1a4"))),
            viewModel.state.value.tree.paths()
        )
    }

    @Test
    fun `playing moves builds up the list of moves`() {
        val viewModel = AnalysisViewModel()

        viewModel.onSquareClicked(Coordinates.parse("a1"))
        viewModel.onSquareClicked(Coordinates.parse("a4")) // white a1a4
        viewModel.onSquareClicked(Coordinates.parse("b8"))
        viewModel.onSquareClicked(Coordinates.parse("b5")) // black b8b5

        assertEquals(
            listOf(Move.parse("a1a4"), Move.parse("b8b5")),
            viewModel.state.value.moves
        )
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
            viewModel.state.value.position.positionDiagram()
        )
    }

    @Test
    fun `plays a line four moves long`() {
        val viewModel = AnalysisViewModel()

        viewModel.onSquareClicked(Coordinates.parse("a1"))
        viewModel.onSquareClicked(Coordinates.parse("a4")) // white a1a4
        viewModel.onSquareClicked(Coordinates.parse("b8"))
        viewModel.onSquareClicked(Coordinates.parse("b5")) // black b8b5
        viewModel.onSquareClicked(Coordinates.parse("c1"))
        viewModel.onSquareClicked(Coordinates.parse("c4")) // white c1c4
        viewModel.onSquareClicked(Coordinates.parse("d8"))
        viewModel.onSquareClicked(Coordinates.parse("d5")) // black d8d5

        assertEquals(
            listOf(
                Move.parse("a1a4"),
                Move.parse("b8b5"),
                Move.parse("c1c4"),
                Move.parse("d8d5")
            ),
            viewModel.state.value.moves
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
            viewModel.state.value.position.positionDiagram()
        )
        assertEquals(Side.WHITE, viewModel.state.value.sideToMove)
    }

    @Test
    fun `playing from an earlier node creates a variation`() {
        val viewModel = AnalysisViewModel()

        viewModel.onSquareClicked(Coordinates.parse("a1"))
        viewModel.onSquareClicked(Coordinates.parse("a4")) // white a1a4
        viewModel.onAnalyticsNodeSelected(emptyList())
        viewModel.onSquareClicked(Coordinates.parse("c1"))
        viewModel.onSquareClicked(Coordinates.parse("c4")) // white c1c4, from the root again

        assertEquals(
            """
            Start
            ├── a1a4
            └── c1c4
            """.trimIndent(),
            viewModel.state.value.tree.moveTreeDiagram()
        )
    }

    @Test
    fun `playing from a node at depth creates a variation there`() {
        val viewModel = AnalysisViewModel()

        viewModel.onSquareClicked(Coordinates.parse("a1"))
        viewModel.onSquareClicked(Coordinates.parse("a4")) // white a1a4
        viewModel.onSquareClicked(Coordinates.parse("b8"))
        viewModel.onSquareClicked(Coordinates.parse("b5")) // black b8b5

        viewModel.onAnalyticsNodeSelected(listOf(Move.parse("a1a4")))

        viewModel.onSquareClicked(Coordinates.parse("d8"))
        viewModel.onSquareClicked(Coordinates.parse("d5")) // black d8d5, instead of b8b5

        assertEquals(
            """
            Start
            └── a1a4
                ├── b8b5
                └── d8d5
            """.trimIndent(),
            viewModel.state.value.tree.moveTreeDiagram()
        )
        assertEquals(
            listOf(Move.parse("a1a4"), Move.parse("d8d5")),
            viewModel.state.value.moves
        )
    }

    // selecting a node

    @Test
    fun `selecting an initial node shows initial state`() {
        val viewModel = AnalysisViewModel()

        viewModel.onSquareClicked(Coordinates.parse("a1"))
        viewModel.onSquareClicked(Coordinates.parse("a4")) // white a1a4

        viewModel.onAnalyticsNodeSelected(emptyList())

        assertEquals(Position.INITIAL, viewModel.state.value.position)
        assertEquals(emptyList(), viewModel.state.value.moves)
    }

    @Test
    fun `selecting an node shows list of moves and position for that node`() {
        val viewModel = AnalysisViewModel()

        viewModel.onSquareClicked(Coordinates.parse("a1"))
        viewModel.onSquareClicked(Coordinates.parse("a4")) // white a1a4
        viewModel.onSquareClicked(Coordinates.parse("b8"))
        viewModel.onSquareClicked(Coordinates.parse("b5")) // black b8b5

        viewModel.onAnalyticsNodeSelected(listOf(Move.parse("a1a4")))

        assertEquals(listOf(Move.parse("a1a4")), viewModel.state.value.moves)
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
            viewModel.state.value.position.positionDiagram()
        )
    }

    @Test
    fun `selecting a node deep in the tree shows every move up to it`() {
        val viewModel = AnalysisViewModel()

        viewModel.onSquareClicked(Coordinates.parse("a1"))
        viewModel.onSquareClicked(Coordinates.parse("a4")) // white a1a4
        viewModel.onSquareClicked(Coordinates.parse("b8"))
        viewModel.onSquareClicked(Coordinates.parse("b5")) // black b8b5
        viewModel.onSquareClicked(Coordinates.parse("c1"))
        viewModel.onSquareClicked(Coordinates.parse("c4")) // white c1c4
        viewModel.onSquareClicked(Coordinates.parse("d8"))
        viewModel.onSquareClicked(Coordinates.parse("d5")) // black d8d5
        viewModel.onAnalyticsNodeSelected(emptyList())

        viewModel.onAnalyticsNodeSelected(listOf(Move.parse("a1a4"), Move.parse("b8b5")))

        assertEquals(
            listOf(Move.parse("a1a4"), Move.parse("b8b5")),
            viewModel.state.value.moves
        )
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
            viewModel.state.value.position.positionDiagram()
        )
    }

    @Test
    fun `selecting multiple nodes shows list of moves and position for last selected node`() {
        val viewModel = AnalysisViewModel()

        viewModel.onSquareClicked(Coordinates.parse("a1"))
        viewModel.onSquareClicked(Coordinates.parse("a4")) // white a1a4
        viewModel.onAnalyticsNodeSelected(emptyList())
        viewModel.onSquareClicked(Coordinates.parse("c1"))
        viewModel.onSquareClicked(Coordinates.parse("c4")) // white c1c4, a second branch

        viewModel.onAnalyticsNodeSelected(listOf(Move.parse("a1a4")))

        assertEquals(listOf(Move.parse("a1a4")), viewModel.state.value.moves)
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
            viewModel.state.value.position.positionDiagram()
        )
    }

    @Test
    fun `selecting a node gives the move back to the side that follows`() {
        val viewModel = AnalysisViewModel()

        viewModel.onSquareClicked(Coordinates.parse("a1"))
        viewModel.onSquareClicked(Coordinates.parse("a4")) // white a1a4
        viewModel.onSquareClicked(Coordinates.parse("b8"))
        viewModel.onSquareClicked(Coordinates.parse("b5")) // black b8b5

        viewModel.onAnalyticsNodeSelected(listOf(Move.parse("a1a4")))

        assertEquals(Side.BLACK, viewModel.state.value.sideToMove)
    }

    @Test
    fun `selecting a node that is not in the tree changes nothing`() {
        val viewModel = AnalysisViewModel()

        viewModel.onAnalyticsNodeSelected(listOf(Move.parse("a1a4")))

        assertEquals(emptyList(), viewModel.state.value.moves)
    }

    // resetting

    @Test
    fun `resetting a flipped board returns to the white orientation`() {
        val viewModel = AnalysisViewModel()
        viewModel.flipBoard()

        viewModel.reset()

        assertEquals(WHITE, viewModel.state.value.orientation)
    }

    @Test
    fun `resetting clears the tree`() {
        val viewModel = AnalysisViewModel()
        viewModel.onSquareClicked(Coordinates.parse("a1"))
        viewModel.onSquareClicked(Coordinates.parse("a4")) // white a1a4

        viewModel.reset()

        assertEquals(AnalyticsTree(), viewModel.state.value.tree)
    }

    @Test
    fun `resetting clears the selected square`() {
        val viewModel = AnalysisViewModel()
        viewModel.onSquareClicked(Coordinates.parse("a1"))

        viewModel.reset()

        assertNull(viewModel.state.value.selected)
    }

    @Test
    fun `resetting clears the moves played`() {
        val viewModel = AnalysisViewModel()
        viewModel.onSquareClicked(Coordinates.parse("a1"))
        viewModel.onSquareClicked(Coordinates.parse("a4")) // white a1a4

        viewModel.reset()

        assertEquals(emptyList(), viewModel.state.value.moves)
        assertEquals(Position.INITIAL, viewModel.state.value.position)
    }
}
