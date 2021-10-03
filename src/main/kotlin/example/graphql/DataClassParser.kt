package example.graphql

import example.Model
import example.Repository
import example.camel2snake
import graphql.Scalars
import graphql.schema.*
import graphql.schema.GraphQLArgument.newArgument
import graphql.schema.GraphQLCodeRegistry.newCodeRegistry
import org.neo4j.ogm.session.Session
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties

const val TIME = "time"
const val VERSION = "version"
const val ID = "id"
const val IDs = "ids"


fun parse(ses: Session, klasses: Array<KClass<out Model>>): GraphQLSchema {
    val type = GraphQLObjectType.newObject()
        .name("Query")
        .fields(klasses.map {
            GraphQLFieldDefinition.newFieldDefinition()
                .name(it.simpleName?.camel2snake())
                .type(GraphQLList(parse(it)))
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
                    listOf(Repository().get<Model>(ses, time, version, UUID.fromString(id)))
                } else if (ids !== null) {
                    ids.map {
                        Repository().get<Model>(ses, time, version, UUID.fromString(it))
                    }
                } else {
                    Repository().getAll(ses, time, version, klass)
                }

                data
            }
        )
    }

    return GraphQLSchema.newSchema()
        .query(type)
        .codeRegistry(registry.build())
        .build()
}


fun <T : Model> parse(klass: KClass<T>): GraphQLObjectType {
    return GraphQLObjectType.newObject()
        .name(klass.simpleName)
        .description(klass.qualifiedName)
        .fields(klass.memberProperties.map {
            GraphQLFieldDefinition.newFieldDefinition()
                .name(it.name.camel2snake())
                .type(graphQLType(it.returnType))
                .build()
        }.toList())
        .build()
}

fun graphQLType(ktype: KType): GraphQLOutputType {
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