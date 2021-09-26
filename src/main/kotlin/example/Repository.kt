package example

import example.model.Entity
import org.neo4j.ogm.session.Session
import java.time.ZonedDateTime
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

class Repository {
    fun <T : Model> save(ses: Session, time: ZonedDateTime, obj: T): T {
        if (!obj::class.isData) {
            throw IllegalArgumentException()
        }

        val kvs = obj::class.memberProperties
            .filter { it.name != "id" }
            .map { Pair(it.name, it.getter.call(obj) as String) }
        val (_, entity) = Service().save(ses, time, obj.id, obj::class.qualifiedName!!, *kvs.toTypedArray())

//        val idProp = obj::class.memberProperties
//            .first { it.name == "id" } as KMutableProperty<*>
//        idProp.isAccessible = true
//        idProp.setter.call(obj, entity.id)

        val copy = obj::class.memberFunctions.first { it.name == "copy" }
        val params = copy.instanceParameter!!
        val idParam = copy.parameters.first { it.name == "id" }

        @Suppress("UNCHECKED_CAST")
        return copy.callBy(mapOf(params to obj, idParam to entity.id)) as T
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

    private fun <T : Model> toModel(entity: Entity, kvs: Array<Pair<String, String?>>): T {
        val klass = Class.forName(entity.type).kotlin
        val constructor = klass.primaryConstructor!!
        val args = mutableListOf<Any?>()
        for (param in constructor.parameters) {
            if (param.name == "id") {
                args.add(entity.id)
            } else {
                args.add(kvs.first { it.first == param.name }.second)
            }
        }
        return constructor.call(*args.toTypedArray()) as T
    }
}