package example.graphql

import example.Model
import example.Repository
import example.camel2snake
import graphql.Scalars
import graphql.schema.*
import graphql.schema.GraphQLCodeRegistry.newCodeRegistry
import org.neo4j.ogm.session.Session
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties

fun parse(ses: Session, klasses: Array<KClass<out Model>>): GraphQLSchema {
    val type = GraphQLObjectType.newObject()
        .name("Query")
        .fields(klasses.map {
            GraphQLFieldDefinition.newFieldDefinition()
                .name(it.simpleName?.camel2snake())
                .type(GraphQLList(parse(it)))
                .build()
        })

    val registry = newCodeRegistry()
    klasses.map {
        registry.dataFetcher(
            FieldCoordinates.coordinates("Query", it.simpleName?.camel2snake()),
            DataFetcher { env ->
                // TODO get time, version from env
                // FIXME hard coding
                Repository().getAll(ses, null, null, it)
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