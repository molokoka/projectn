package molokoka.project.n.domain

fun Position.asDiagram(): String {
    val ranks = RANK_RANGE.reversed().map { rank ->
        "$rank " + FILE_RANGE.joinToString(" ") { file ->
            pieces[Coordinates(file, rank)]?.symbol?.toString() ?: "."
        }
    }
    val files = "  " + FILE_RANGE.joinToString(" ")

    return (ranks + files).joinToString("\n")
}
