package molokoka.project.n.domain

data class Move(val from: Coordinates, val to: Coordinates) {

    override fun toString(): String =
        "$from$to"

    companion object {

        fun parse(lan: String): Move {
            require(lan.length == 4) { "Move must be two squares, like 'b2b4', was '$lan'" }

            return Move(Coordinates.parse(lan.take(2)), Coordinates.parse(lan.drop(2)))
        }
    }
}
