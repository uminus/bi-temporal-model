package example

import example.model.*
import org.neo4j.ogm.cypher.ComparisonOperator
import org.neo4j.ogm.cypher.Filter
import org.neo4j.ogm.session.Session
import java.time.ZonedDateTime
import java.util.*


class Service {
    fun save(
        ses: Session,
        time: ZonedDateTime,
        id: UUID?,
        type: String,
        vararg kvs: Pair<String, Any>
    ): Pair<Version, Entity> {
        lateinit var version: Version
        lateinit var entity: Entity
        ses.beginTransaction().use { tx ->
            version = Version()
            val t = Timeline(id = time)
            val created = mutableListOf<Entity>()
            val deleted = mutableListOf<Entity>()
            val changes = mutableListOf<Change>()

            entity = if (id == null) {
                val e = Entity(id = null, type = type, fields = emptyArray())
                e.created = Change(null, null, version, t)
                created.add(e)
                e
            } else ses.load(Entity::class.java, id, 3)

            checkEntity(entity)

            val fields = entity.fields.toMutableList()

            for (kv: Pair<String, Any> in kvs) {
                val key = kv.first
                val value = kv.second
                val v = Value(id = null, value = value)
                val c = Change(id = null, value = v, version = version, timeline = t)
                changes.add(c)

                val fieldExists = fields.any() { it.name == key }

                val f = fields.find { it.name == key } ?: Field(id = null, key, changes = emptyArray())
                f.changes += c

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

        return Pair(version, entity)
    }

    fun delete(ses: Session, time: ZonedDateTime, id: UUID): Version {
        lateinit var version: Version
        ses.beginTransaction().use { tx ->
            val entity = ses.load(Entity::class.java, id, 3)
            checkEntity(entity)

            version = Version()
            val timeline = Timeline(id = time)
            entity.deleted = Change(id = null, value = null, version = version, timeline = timeline)
            version.deleted += entity

            ses.save(version)
            ses.save(entity)

            tx.commit()
        }
        return version
    }

    fun get(ses: Session, time: ZonedDateTime, version: Long?, id: UUID?): Pair<Entity, Array<Pair<String, Any?>>> {
        val entity = ses.load(Entity::class.java, id)
        return Pair(entity, toKVs(time, version ?: Long.MAX_VALUE, entity))
    }

    fun getAll(
        ses: Session,
        time: ZonedDateTime,
        version: Long?,
        type: String
    ): Array<Pair<Entity, Array<Pair<String, Any?>>>> {
        return ses.loadAll(Entity::class.java, Filter("type", ComparisonOperator.EQUALS, type))
            .filter { it.created?.version?.id!! <= (version ?: Long.MAX_VALUE) }
            .filter { it.created?.timeline?.id!! <= time }
            .map { Pair(it, toKVs(time, version ?: Long.MAX_VALUE, it)) }
            .toTypedArray()
    }

    private fun toKVs(time: ZonedDateTime, version: Long, entity: Entity): Array<Pair<String, Any?>> {
        return entity.fields.map { f ->
            val versioned = f.changes
                .filter { it.version?.id!! <= (version) }
                .sortedBy { it.timeline?.id }
            val value = versioned.lastOrNull { it.timeline?.id!! <= time }
            Pair(f.name, value?.value?.value)
        }
            .filter { it.second != null }
            .toTypedArray()
    }

    private fun checkEntity(entity: Entity) {
        if (entity.deleted != null) {
            throw IllegalStateException("Entity is deleted. id: ${entity.id}")
        }
    }
}