package example.graphql

import example.Model
import example.Repository
import graphql.GraphQL
import graphql.schema.idl.SchemaPrinter
import org.junit.jupiter.api.Test
import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.session.SessionFactory
import java.time.ZonedDateTime
import java.util.*
import kotlin.test.assertEquals

internal class __DataClassParserKtTest {

    @Test
    fun parse() {
        val configuration: Configuration = Configuration.Builder()
            .uri("bolt://dev")
            .credentials("neo4j", "password")
            .build()

        val sessionFactory = SessionFactory(configuration, "example.model")
        val ses = sessionFactory.openSession()
        ses.purgeDatabase()

        // test data
        val repo = Repository()
        val entity1 = repo.save(ses, ZonedDateTime.now(), Data(null, "string1", 123L, 456.789, true))
        repo.save(ses, ZonedDateTime.now(), Data(null, "string2", 123L, 456.789, false))
        repo.save(ses, ZonedDateTime.now(), Data(null, "string3", 123L, 456.789, false))
        repo.save(ses, ZonedDateTime.now(), Data(null, "string4", 123L, 456.789, false))

        repo.save(ses, ZonedDateTime.now(), DataOne(null, "value"))

        val schema = generateGraphQLSchema(ses, DataClassParser(), arrayOf(Data::class, DataOne::class))
        println(SchemaPrinter().print(schema))
        val executor = GraphQL.newGraphQL(schema).build()

        val raw = executor.execute("{data { string_value }}").getData<Map<String, String>>()
            .get("data") as List<Map<String, String>>
        val actual1 = raw.map { it.get("string_value") }.sortedBy { it }
        assertEquals(
            listOf("string1", "string2", "string3", "string4"),
            actual1
        )

        assertEquals(
            "{data=[{string_value=string1}]}",
            "${executor.execute("""{data(filter: {boolean_value: {eq: true}}) { string_value }}""").getData() as Any}"
        )
        assertEquals(
            "{data_one=[{value=value}]}",
            "${executor.execute("{data_one { value }}").getData() as Any}"
        )

        assertEquals(
            "{data={string_value=string, double_value=654.321}}",
            "${
                executor.execute("mutation {data(data: {string_value: \"string\", long_value: 987, double_value: 654.321, boolean_value: true}) {string_value double_value}}")
                    .getData() as Any
            }"
        )

        executor.execute("mutation {data(data: {id: \"${entity1.id}\", string_value: \"MUTATION_UPDATE\", long_value: 987, double_value: 11.11, boolean_value: true}) {id}}")

        assertEquals(
            "{data=[{double_value=11.11}]}",
            "${
                executor.execute("""{data(filter: {string_value: {eq: "MUTATION_UPDATE"}}) { double_value }}""")
                    .getData() as Any
            }"
        )

        executor.execute("mutation {data(delete: \"${entity1.id}\") {id}}")
        val raw2 = executor.execute("{data { string_value }}").getData<Map<String, String>>()
            .get("data") as List<Map<String, String>>
        assertEquals(
            listOf("string", "string2", "string3", "string4"),
            raw2.map { it.get("string_value") }.sortedBy { it }
        )
    }
}

data class Data(
    override var id: UUID?,
    val string_value: String,
    val long_value: Long,
    val double_value: Double,
    val boolean_value: Boolean,
) : Model

data class DataOne(
    override var id: UUID?,
    val value: String,
) : Model