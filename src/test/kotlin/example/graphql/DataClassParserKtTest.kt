package example.graphql

import example.Model
import graphql.Scalars.*
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

internal class DataClassParserKtTest {

    @Test
    fun parse() {
        val parser = DataClassParser()
        val actual = parser.parse(arrayOf(Type::class))

        assertEquals(1, actual.size)


        assertEquals(
            Parser.Data(
                "Type", listOf(
                    Parser.Field("id", GraphQLString, true),
                    Parser.Field("string_value", GraphQLString, false),
                    Parser.Field("long_value", GraphQLInt, false),
                    Parser.Field("double_value", GraphQLFloat, false),
                    Parser.Field("boolean_value", GraphQLBoolean, false),
                ).sortedBy { it.name }
            ),
            actual[0]
        )
    }

    private data class Type(
        override var id: UUID?,
        val string_value: String,
        val long_value: Long,
        val double_value: Double,
        val boolean_value: Boolean,
    ) : Model

}