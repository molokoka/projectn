package molokoka.project.n

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform