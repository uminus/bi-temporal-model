package example

import example.model.*
import org.neo4j.ogm.session.Session
import java.lang.IllegalStateException
import java.util.*

class Service {
    fun save(ses: Session, id: UUID?, type: String, vararg kvs: Pair<String, String>): Entity {
        lateinit var entity: Entity
        ses.beginTransaction().use { tx ->
            val version = Version()
            val created = mutableListOf<Entity>()
            val deleted = mutableListOf<Entity>()
            val changes = mutableListOf<Change>()

            entity = if (id == null) {
                val e = Entity(id = null, type = type, fields = emptyArray())
                created.add(e)
                e
            } else ses.load(Entity::class.java, id, 3)

            checkEntity(entity)

            val fields = entity.fields.toMutableList()

            for (kv: Pair<String, String> in kvs) {
                val key = kv.first
                val value = kv.second
                val v = Value(id = null, value = value)
                val c = Change(id = null, value = v, version = version)
                changes.add(c)

                val fieldExists = fields.any() { it.name == key }

                val f = fields.find { it.name == key } ?: Field(id = null, key, changes = emptyArray())
                f.changes = f.changes + c

                if (!fieldExists) {
                    fields.add(f)
                }
            }

            entity.fields = fields.toTypedArray()
            ses.save(with(version) {
                this.created = created.toTypedArray()
                this.deleted = deleted.toTypedArray()
                this.changes = changes.toTypedArray()
                this
            })
            ses.save(entity)
            tx.commit()
        }

        return entity
    }

    fun delete(ses: Session, id: UUID): Version {
        lateinit var version: Version
        ses.beginTransaction().use { tx ->
            val entity = ses.load(Entity::class.java, id, 3)
            checkEntity(entity)

            version = Version()
            entity.deleted = version
            version.deleted += entity

            ses.save(version)
            ses.save(entity)

            tx.commit()
        }
        return version
    }

    fun get(ses: Session, id: UUID?, version: Long = Long.MAX_VALUE): Array<Pair<String, String?>> {
        val entity = ses.load(Entity::class.java, id, 3)
        return entity.fields.map { f ->
            f.changes.sortBy { it.version?.id }
            val value = f.changes.last { it.version?.id!! <= version }
            Pair(f.name, value.value?.value)
        }.toTypedArray()
    }

    private fun checkEntity(entity: Entity) {
        if (entity.deleted != null) {
            throw IllegalStateException("Entity is deleted. id: ${entity.id}")
        }
    }
}