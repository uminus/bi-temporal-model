package example.graphql

import example.Model
import example.Repository
import example.camel2snake
import example.graphql.filter.BooleanFilter
import example.graphql.filter.Filter
import example.graphql.filter.NumericFilter
import example.graphql.filter.StringFilter
import graphql.Scalars
import graphql.schema.*
import graphql.schema.GraphQLArgument.newArgument
import graphql.schema.GraphQLCodeRegistry.newCodeRegistry
import org.neo4j.ogm.session.Session
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import java.util.function.Predicate
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor


const val TIME = "time"
const val VERSION = "version"
const val ID = "id"
const val IDs = "ids"
const val FILTER = "filter"

/**
 * TODO refactoring
 */
fun parse(ses: Session, klasses: Array<KClass<out Model>>): GraphQLSchema {
    val defs = klasses.map { parse(it) }


    val type = GraphQLObjectType.newObject()
        .name("Query")
        .fields(defs.map {
            GraphQLFieldDefinition.newFieldDefinition()
                .name(it.first.name.camel2snake())
                .type(GraphQLList(it.first))
                .argument(
                    newArgument()
                        .name(TIME)
                        .type(Scalars.GraphQLInt)
                )
                .argument(
                    newArgument()
                        .name(VERSION)
                        .type(Scalars.GraphQLInt)
                )
                .argument(
                    newArgument()
                        .name(ID)
                        .type(Scalars.GraphQLString)
                )
                .argument(
                    newArgument()
                        .name(IDs)
                        .type(GraphQLList(Scalars.GraphQLString))
                )
                .argument(
                    newArgument()
                        .name(FILTER)
                        .type(it.second)
                )
                .build()
        })

    val mutation = GraphQLObjectType.newObject()
        .name("Mutation")
        .fields(defs.map {
            GraphQLFieldDefinition.newFieldDefinition()
                .name(it.first.name.camel2snake())
                .type(it.first)
                .argument(
                    newArgument()
                        .name(TIME)
                        .type(Scalars.GraphQLInt)
                )
                .argument(
                    newArgument()
                        .name("data")
                        .type(it.third)
                        .build()
                )
                .build()
        })

    val registry = newCodeRegistry()
    klasses.map { klass ->
        registry.dataFetcher(
            FieldCoordinates.coordinates("Query", klass.simpleName?.camel2snake()),
            DataFetcher { env ->
                val (time, version) = getTime(env)
                val (id, ids) = getIds(env)

                // FIXME hard coding
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
            FieldCoordinates.coordinates("Mutation", klass.simpleName?.camel2snake()),
            DataFetcher { env ->
                val (time, version) = getTime(env)
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
            }
        )
    }

    return GraphQLSchema.newSchema()
        .query(type)
        .mutation(mutation)
        .codeRegistry(registry.build())
        .build()
}

fun <T : Model> parse(klass: KClass<T>): Triple<GraphQLObjectType, GraphQLInputObjectType, GraphQLInputObjectType> {
    val props = klass.memberProperties
    val type = GraphQLObjectType.newObject()
        .name(klass.simpleName)
        .description(klass.qualifiedName)
        .fields(props.map {
            GraphQLFieldDefinition.newFieldDefinition()
                .name(it.name.camel2snake())
                .type(graphQLType(it.returnType))
                .build()
        }.toList())
        .build()

    val filterType = GraphQLInputObjectType.newInputObject()
        .name("${klass.simpleName}Filter")
        .description("Filter for ${klass.qualifiedName}")
        .fields(props
            .filter {
                when (graphQLType(it.returnType)) {
                    Scalars.GraphQLString -> true
                    Scalars.GraphQLInt -> true
                    Scalars.GraphQLFloat -> true
                    Scalars.GraphQLBoolean -> true
                    else -> false
                }
            }
            .map {
                val f = GraphQLInputObjectField.newInputObjectField()
                    .name(it.name.camel2snake())

                val filter = when (graphQLType(it.returnType)) {
                    Scalars.GraphQLString -> StringFilter.FILTERS
                    Scalars.GraphQLInt -> NumericFilter.INTEGER_FILTERS
                    Scalars.GraphQLFloat -> NumericFilter.FLOAT_FILTERS
                    Scalars.GraphQLBoolean -> BooleanFilter.FILTERS
                    else -> Scalars.GraphQLString
                }

                f.type(filter)
                    .build()
            }.toList()
        )
        .build()

    val mutationArgsType = GraphQLInputObjectType.newInputObject()
        .name("${klass.simpleName}Args")
        .description("Arguments for ${klass.qualifiedName}")
        .fields(props.map {
            GraphQLInputObjectField.newInputObjectField()
                .name(it.name.camel2snake())
                .type(
                    if (it.returnType.isMarkedNullable)
                        graphQLType(it.returnType)
                    else
                        GraphQLNonNull.nonNull(graphQLType(it.returnType))
                )
                .build()
        })
        .build()

    return Triple(type, filterType, mutationArgsType)
}

fun graphQLType(ktype: KType): GraphQLScalarType {
    return when (ktype.classifier) {
        String::class -> Scalars.GraphQLString
        Int::class, Long::class -> Scalars.GraphQLInt
        Float::class, Double::class -> Scalars.GraphQLFloat
        Boolean::class -> Scalars.GraphQLBoolean
        else -> Scalars.GraphQLString
    }
}

fun getIds(env: DataFetchingEnvironment): Pair<String?, List<String>?> {
    return Pair(env.getArgument(ID), env.getArgument(IDs))
}

fun getTime(env: DataFetchingEnvironment): Pair<ZonedDateTime, Long?> {
    return Pair(
        Instant.ofEpochMilli(env.getArgumentOrDefault(TIME, System.currentTimeMillis())).atZone(ZoneId.systemDefault()),
        env.getArgument(VERSION)
    )
}