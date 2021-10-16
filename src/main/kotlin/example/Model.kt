package example

import java.util.*

interface Model {
    var id: UUID?
}

data class Ref<T : Model>(val model: T)
