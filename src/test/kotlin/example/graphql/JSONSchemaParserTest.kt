package example.graphql

import graphql.Scalars
import org.junit.jupiter.api.Test
import java.io.File

internal class JSONSchemaParserTest {

    @Test
    fun parse() {
        val parser = JSONSchemaParser()
        val actual = parser.parse(File("./src/test/resources/simple.schema.json").inputStream())

        kotlin.test.assertEquals(1, actual.size)

        kotlin.test.assertEquals(
            Parser.Data(
                "Simple", listOf(
                    Parser.Field("id", Scalars.GraphQLString, false),
                    Parser.Field("string_value", Scalars.GraphQLString, false),
                    Parser.Field("long_value", Scalars.GraphQLInt, false),
                    Parser.Field("double_value", Scalars.GraphQLFloat, false),
                    Parser.Field("boolean_value", Scalars.GraphQLBoolean, false),
                )
            ),
            actual[0]
        )
    }

}