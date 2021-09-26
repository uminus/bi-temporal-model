package example.model

import org.neo4j.ogm.annotation.GeneratedValue
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship
import org.neo4j.ogm.annotation.typeconversion.Convert
import org.neo4j.ogm.id.UuidStrategy
import org.neo4j.ogm.typeconversion.UuidStringConverter
import java.util.*

@NodeEntity("Entity")
data class Entity constructor(
    @Id @GeneratedValue(strategy = UuidStrategy::class) @Convert(UuidStringConverter::class) var id: UUID? = null,
    var type: String = "Object",
    @Relationship(type = "fields") var fields: Array<Field> = emptyArray(),
) {
    @Relationship(type = "deleted")
    var created: Change? = null
    var deleted: Change? = null
}

@NodeEntity("Field")
data class Field constructor(
    @Id @GeneratedValue(strategy = UuidStrategy::class) @Convert(UuidStringConverter::class) val id: UUID? = null,
    val name: String = "_",
//    val first: Change,
//    val last: Change,
    @Relationship(type = "changes") var changes: Array<Change> = emptyArray(),
)

@NodeEntity("Change")
data class Change constructor(
    @Id @GeneratedValue(strategy = UuidStrategy::class) @Convert(UuidStringConverter::class) val id: UUID? = null,
    @Relationship(type = "value") val value: Value? = null,
    @Relationship(type = "version", direction = Relationship.INCOMING) val version: Version? = null,
    @Relationship(type = "timeline", direction = Relationship.INCOMING) val timeline: Timeline? = null,
)

@NodeEntity("Value")
data class Value constructor(
    @Id @GeneratedValue(strategy = UuidStrategy::class) @Convert(UuidStringConverter::class) val id: UUID? = null,
    val value: String? = null,
)