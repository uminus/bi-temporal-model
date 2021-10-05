package example.graphql

import example.DummyNeo4jSession
import example.Model
import graphql.schema.idl.SchemaPrinter
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

internal class GraphQLSchemaGeneratorKtTest {

    @Test
    fun generateGraphQLSchema_dataclass() {
        val schema = generateGraphQLSchema(DummyNeo4jSession(), DataClassParser(), arrayOf(Example::class))

        val type = schema.getType(Example::class.simpleName!!)
        assertEquals(
            """
            type Example {
              b: Boolean
              d: Float
              id: String
              l: Int
              s: String
            }
        """.trimIndent(),
            SchemaPrinter().print(type).trim()
        )

        val filterType = schema.getType("${Example::class.simpleName!!}Filter")
        assertEquals(
            """
            "Filter for Example"
            input ExampleFilter {
              b: BooleanFilter
              d: FloatFilter
              id: StringFilter
              l: IntegerFilter
              s: StringFilter
            }
        """.trimIndent(),
            SchemaPrinter().print(filterType).trim()
        )

        val mutationType = schema.getType("${Example::class.simpleName!!}Args")
        assertEquals(
            """
            "Arguments for Example"
            input ExampleArgs {
              b: Boolean!
              d: Float!
              id: String
              l: Int!
              s: String!
            }
        """.trimIndent(),
            SchemaPrinter().print(mutationType).trim()
        )
    }
}

data class Example(
    override var id: UUID?,
    val s: String,
    val l: Long,
    val d: Double,
    val b: Boolean,
) : Model
