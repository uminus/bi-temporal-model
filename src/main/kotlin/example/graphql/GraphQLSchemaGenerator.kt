package example.graphql

import example.camel2snake
import example.graphql.filter.BooleanFilter
import example.graphql.filter.NumericFilter
import example.graphql.filter.StringFilter
import graphql.Scalars
import graphql.schema.*
import org.neo4j.ogm.session.Session
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime


const val TIME = "time"
const val VERSION = "version"
const val ID = "id"
const val IDs = "ids"
const val FILTER = "filter"
const val DATA = "data"
const val DELETE = "delete"
const val QUERY = "query"
const val MUTATION = "mutation"


fun <SOURCE> generateGraphQLSchema(ses: Session, parser: Parser<SOURCE>, source: SOURCE): GraphQLSchema {
    val types = parser.parse(source)
    val defs = types.map { toGraphQLObjectType(it) }

    val type = GraphQLObjectType.newObject()
        .name(QUERY)
        .fields(defs.map {
            GraphQLFieldDefinition.newFieldDefinition()
                .name(it.first.name.camel2snake())
                .type(GraphQLList(it.first))
                .argument(
                    GraphQLArgument.newArgument()
                        .name(TIME)
                        .type(Scalars.GraphQLInt)
                )
                .argument(
                    GraphQLArgument.newArgument()
                        .name(VERSION)
                        .type(Scalars.GraphQLInt)
                )
                .argument(
                    GraphQLArgument.newArgument()
                        .name(ID)
                        .type(Scalars.GraphQLString)
                )
                .argument(
                    GraphQLArgument.newArgument()
                        .name(IDs)
                        .type(GraphQLList(Scalars.GraphQLString))
                )
                .argument(
                    GraphQLArgument.newArgument()
                        .name(FILTER)
                        .type(it.second)
                )
                .build()
        })

    val mutation = GraphQLObjectType.newObject()
        .name(MUTATION)
        .fields(defs.map {
            GraphQLFieldDefinition.newFieldDefinition()
                .name(it.first.name.camel2snake())
                .type(it.first)
                .argument(
                    GraphQLArgument.newArgument()
                        .name(TIME)
                        .type(Scalars.GraphQLInt)
                )
                .argument(
                    GraphQLArgument.newArgument()
                        .name(DATA)
                        .type(it.third)
                        .build()
                )
                .argument(
                    GraphQLArgument.newArgument()
                        .name(DELETE)
                        .description("Object id to delete")
                        .type(Scalars.GraphQLString)
                )
                .build()
        })

    val registry = parser.dataFetchers(ses, source)

    return GraphQLSchema.newSchema()
        .query(type)
        .mutation(mutation)
        .codeRegistry(registry)
        .build()
}


fun toGraphQLObjectType(data: Parser.Data): Triple<GraphQLObjectType, GraphQLInputObjectType, GraphQLInputObjectType> {
    val type = GraphQLObjectType.newObject()
        .name(data.name)
        .fields(data.fields.map {
            GraphQLFieldDefinition.newFieldDefinition()
                .name(it.name)
                .type(it.type as GraphQLOutputType)
                .build()
        }.toList())
        .build()

    val filterType = GraphQLInputObjectType.newInputObject()
        .name("${data.name}Filter")
        .description("Filter for ${data.name}")
        .fields(data.fields
            .filter {
                when (it.type) {
                    Scalars.GraphQLString -> true
                    Scalars.GraphQLInt -> true
                    Scalars.GraphQLFloat -> true
                    Scalars.GraphQLBoolean -> true
                    else -> false
                }
            }
            .map {
                val f = GraphQLInputObjectField.newInputObjectField()
                    .name(it.name)
                val filter = when (it.type) {
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
        .name("${data.name}Args")
        .description("Arguments for ${data.name}")
        .fields(data.fields.map {
            GraphQLInputObjectField.newInputObjectField()
                .name(it.name.camel2snake())
                .type(
                    if (it.nullable)
                        it.type as GraphQLInputType
                    else
                        GraphQLNonNull.nonNull(it.type)
                )
                .build()
        })
        .build()

    return Triple(type, filterType, mutationArgsType)
}

fun getIds(env: DataFetchingEnvironment): Pair<String?, List<String>?> {
    return Pair(env.getArgument(ID), env.getArgument(IDs))
}

fun getTime(env: DataFetchingEnvironment): Pair<ZonedDateTime, Long?> {
    return Pair(
        Instant.ofEpochMilli(env.getArgumentOrDefault(TIME, System.currentTimeMillis()))
            .atZone(ZoneId.systemDefault()),
        env.getArgument(VERSION)
    )
}