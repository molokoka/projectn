package molokoka.project.n.log

actual fun log(tag: String, message: String) {
    println("$tag: $message")
}
