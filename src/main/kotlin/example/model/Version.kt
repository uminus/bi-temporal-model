package example.model

import org.neo4j.ogm.annotation.GeneratedValue
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.Relationship

data class Version(
    @Id @GeneratedValue val id: Long? = null,
) {
    @Relationship(type = "created") var created: Array<Entity> = emptyArray()
    @Relationship(type = "deleted") var deleted: Array<Entity> = emptyArray()
    @Relationship(type = "changes") var changes: Array<Change> = emptyArray()
}