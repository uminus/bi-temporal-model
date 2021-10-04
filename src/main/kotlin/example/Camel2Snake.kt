package example

import java.util.*

val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()
val snakeRegex = "_[a-zA-Z]".toRegex()

fun String.camel2snake(): String {
    return camelRegex.replace(this) {
        "_${it.value}"
    }.lowercase(Locale.getDefault())
}

fun String.snake2camel(): String {
    return snakeRegex.replace(this) {
        it.value.replace("_", "")
            .uppercase(Locale.getDefault())
    }
}