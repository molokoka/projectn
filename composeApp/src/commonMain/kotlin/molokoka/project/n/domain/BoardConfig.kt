package molokoka.project.n.domain

const val FIRST_FILE = 'a'
const val BOARD_SIZE = 8

const val INITIAL_POSITION = "Ra1 Rc1 Re1 Rg1 Qb2 Qd2 Qf2 Qh2 qa7 qc7 qe7 qg7 rb8 rd8 rf8 rh8"

val FILE_RANGE = FIRST_FILE..(FIRST_FILE + BOARD_SIZE - 1)
val RANK_RANGE = 1..BOARD_SIZE
