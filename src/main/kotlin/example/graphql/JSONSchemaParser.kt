package example.graphql

import com.jayway.jsonpath.JsonPath
import graphql.Scalars
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLScalarType
import org.neo4j.ogm.session.Session
import java.io.InputStream

class JSONSchemaParser : Parser<InputStream>() {
    override fun parse(source: InputStream): Array<Data> {
        val context = JsonPath.parse(source)
        val title = context.read<String>("$.title")
        val props = context.read<Map<String, Map<String, String>>>("$.properties")

        return arrayOf(Data(title, props.entries.map {
            Field(
                it.key,
                graphQLType(it.value["type"] ?: "string"),
                false
            )
        }))
    }

    override fun dataFetchers(ses: Session, source: InputStream): GraphQLCodeRegistry {
        val registry = GraphQLCodeRegistry.newCodeRegistry()
        // TODO
        return registry.build()
    }


    private fun graphQLType(type: String): GraphQLScalarType {
        return when (type) {
            "string" -> Scalars.GraphQLString
            "integer" -> Scalars.GraphQLInt
            "number" -> Scalars.GraphQLFloat
            "boolean" -> Scalars.GraphQLBoolean
            else -> Scalars.GraphQLString
        }
    }
}