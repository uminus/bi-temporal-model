package example.graphql

import example.Model
import example.Repository
import example.camel2snake
import example.graphql.filter.BooleanFilter
import example.graphql.filter.Filter
import example.graphql.filter.NumericFilter
import example.graphql.filter.StringFilter
import graphql.Scalars
import graphql.schema.DataFetcher
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLScalarType
import org.neo4j.ogm.session.Session
import java.util.*
import java.util.function.Predicate
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

class DataClassParser : Parser<Array<KClass<out Model>>>() {
    override fun parse(klasses: Array<KClass<out Model>>): Array<Data> {
        return klasses.map { klass ->
            Data(
                klass.simpleName!!,
                klass.memberProperties.map {
                    Field(
                        it.name.camel2snake(),
                        graphQLType(it.returnType),
                        it.returnType.isMarkedNullable
                    )
                }
            )
        }.toTypedArray()
    }

    override fun dataFetchers(ses: Session, klasses: Array<KClass<out Model>>): GraphQLCodeRegistry {
        val registry = GraphQLCodeRegistry.newCodeRegistry()
        klasses.map { klass ->
            registry.dataFetcher(
                FieldCoordinates.coordinates(QUERY, klass.simpleName?.camel2snake()),
                DataFetcher { env ->
                    val (time, version) = getTime(env)
                    val (id, ids) = getIds(env)

                    val data = if (id !== null) {
                        listOf(Repository().get(ses, time, version, UUID.fromString(id)))
                    } else if (ids !== null) {
                        ids.map {
                            Repository().get(ses, time, version, UUID.fromString(it))
                        }
                    } else {
                        Repository().getAll(ses, time, version, klass)
                    }

                    val filterArgs = env.getArgumentOrDefault<Map<String, Map<String, String>>>(FILTER, emptyMap())

                    val filters = filterArgs
                        .flatMap { f ->
                            val target = klass.memberProperties.first { it.name == f.key }
                            val ops = f.value
                            ops.entries.map { kv ->
                                Predicate<Model> { model ->
                                    val filter = when (graphQLType(target.returnType)) {
                                        Scalars.GraphQLString -> StringFilter::valueOf
                                        Scalars.GraphQLInt -> NumericFilter::valueOf
                                        Scalars.GraphQLFloat -> NumericFilter::valueOf
                                        Scalars.GraphQLBoolean -> BooleanFilter::valueOf
                                        else -> StringFilter::valueOf
                                    }

                                    val op = filter(kv.key.uppercase()) as Filter<Any>
                                    op.test(kv.value, target.getter.call(model))
                                }
                            }
                        }
                    data.filter { t -> filters.all { it.test(t) } }
                }
            )

            registry.dataFetcher(
                FieldCoordinates.coordinates(MUTATION, klass.simpleName?.camel2snake()),
                DataFetcher { env ->
                    val (time, version) = getTime(env)

                    if (env.containsArgument(klass.simpleName?.camel2snake())) {
                        val argsJson = env.arguments[klass.simpleName?.camel2snake()] as Map<String, Any>
                        val constructor = klass.primaryConstructor!!
                        val args = mutableListOf<Any?>()
                        for (param in constructor.parameters) {
                            if (param.name == "id") {
                                val id = argsJson["id"] as String?
                                args.add(if (id != null) UUID.fromString(id) else null)
                            } else {
                                argsJson.entries.firstOrNull { it.key == param.name }?.let {
                                    args.add(it.value)
                                }
                            }
                        }
                        val model = constructor.call(*args.toTypedArray()) as Model
                        Repository().save(ses, time, model)
                    } else if (env.containsArgument(DELETE)) {
                        val delete = env.getArgument<String>(DELETE)
                        val deleted = Repository().get(ses, time, null, UUID.fromString(delete)) as Model
                        Repository().delete(ses, time, deleted.id!!)
                        deleted
                    } else {
                        throw IllegalArgumentException()
                    }
                }
            )
        }

        return registry.build()
    }


    private fun graphQLType(ktype: KType): GraphQLScalarType {
        return when (ktype.classifier) {
            String::class -> Scalars.GraphQLString
            Int::class, Long::class -> Scalars.GraphQLInt
            Float::class, Double::class -> Scalars.GraphQLFloat
            Boolean::class -> Scalars.GraphQLBoolean
            else -> Scalars.GraphQLString
        }
    }
}