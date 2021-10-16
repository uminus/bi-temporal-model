package example

import example.model.Entity
import example.model.Version
import org.neo4j.ogm.session.Session
import java.time.ZonedDateTime
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.*

class Repository {
    fun <T : Model> save(ses: Session, time: ZonedDateTime, obj: T): T {
        if (!obj::class.isData) {
            throw IllegalArgumentException()
        }

        val kvs = collectKVs("", obj)
        val (_, entity) = Service().save(ses, time, obj.id, obj::class.qualifiedName!!, *kvs.toTypedArray())

        val copy = obj::class.memberFunctions.first { it.name == "copy" }
        val params = copy.instanceParameter!!
        val idParam = copy.parameters.first { it.name == "id" }

        @Suppress("UNCHECKED_CAST")
        return copy.callBy(mapOf(params to obj, idParam to entity.id)) as T
    }

    private fun collectKVs(root: String, obj: Any): List<Pair<String, Any>> {
        val kvs = obj::class.memberProperties
            .filter { it.name != "id" }
            .flatMap {
                val key = if (root.isEmpty()) it.name else "${root}.${it.name}"
                val value = it.getter.call(obj) as Any?
                if (value == null) {
                    listOf()
                } else if (value::class.isData) {
                    collectKVs(key, value)
                } else {
                    listOf(Pair(key, it.getter.call(obj) as Any))
                }
            }
        return kvs
    }

    fun delete(ses: Session, time: ZonedDateTime, id: UUID): Version {
        return Service().delete(ses, time, id)
    }

    fun <T : Model> get(ses: Session, time: ZonedDateTime?, version: Long?, id: UUID): T {
        val (entity, kvs) = Service().get(ses, time ?: ZonedDateTime.now(), version ?: Long.MAX_VALUE, id)
        return toModel(entity, kvs)
    }

    fun <T : Model> getAll(ses: Session, time: ZonedDateTime?, version: Long?, klass: KClass<T>): List<T> {
        if (!klass.isData) {
            throw IllegalArgumentException()
        }

        val entities =
            Service().getAll(ses, time ?: ZonedDateTime.now(), version ?: Long.MAX_VALUE, klass.qualifiedName!!)

        return entities
            .map { toModel(it.first, it.second) }
    }

    private fun <T : Model> toModel(entity: Entity, kvs: Array<Pair<String, Any?>>): T {
        val klass = Class.forName(entity.type).kotlin
        val constructor = klass.primaryConstructor!!
        val args = mutableListOf<Any?>()
        for (param in constructor.parameters) {
            val type = param.type.classifier as KClass<*>
            if (type.isSubclassOf(Ref::class)) {
                println("Reference type is not implemented. type: $type")
                args.add(null)
            } else if (param.name == "id") {
                args.add(entity.id)
            } else if (type.isData) {

                // TODO duplicated code
                val kvs = kvs.filter { it.first.startsWith("${param.name}.") }
                args.add(
                    if (kvs.isNotEmpty())
                        toNestedProps(
                            param.name!!,
                            param.type.classifier as KClass<*>,
                            kvs
                        )
                    else
                        null
                )
            } else {
                args.add(kvs.firstOrNull { it.first == param.name }?.second)
            }
        }

        @Suppress("UNCHECKED_CAST")
        return constructor.call(*args.toTypedArray()) as T
    }

    private fun <NEST : Any> toNestedProps(prefix: String, klass: KClass<NEST>, kvs: List<Pair<String, Any?>>): NEST {
        val constructor = klass.primaryConstructor!!
        val args = mutableListOf<Any?>()
        for (param in constructor.parameters) {
            val type = param.type.classifier as KClass<*>
            val name = param.name!!
            if (type.isData) {
                val next = "${prefix}.${name}"
                val kvs = kvs.filter { it.first.startsWith(next) }

                args.add(
                    if (kvs.isNotEmpty())
                        toNestedProps(next, type, kvs)
                    else
                        null
                )
            } else {
                args.add(kvs.firstOrNull { it.first == "${prefix}.${name}" }?.second)
            }
        }
        return constructor.call(*args.toTypedArray())
    }
}