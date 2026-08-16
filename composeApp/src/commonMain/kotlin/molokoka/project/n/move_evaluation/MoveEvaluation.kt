package molokoka.project.n.move_evaluation

// `+`, `-` and `=` name a side by chess convention (Chess Informant; PGN NAGs 14-19):
// the sign is always White-relative, never relative to whoever just moved.
enum class MoveEvaluation(private val symbol: String) {
    WHITE_BETTER("+"),
    BLACK_BETTER("-"),
    EQUAL("=");

    override fun toString(): String = symbol
}
