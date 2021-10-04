package example

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.session.SessionFactory
import java.time.ZonedDateTime
import java.util.*

internal class RepositoryTest {

    @Test
    fun dataclass() {
        val configuration: Configuration = Configuration.Builder()
            .uri("bolt://dev")
            .credentials("neo4j", "password")
            .build()

        val sessionFactory = SessionFactory(configuration, "example.model")
        val ses = sessionFactory.openSession()
        ses.purgeDatabase()

        val d = Data(null, "str", 123L, 456.789, true)
        val d2 = Repository().save(ses, ZonedDateTime.now(), d)
        assertEquals(d.string_value, d2.string_value)
        assertEquals(d.long_value, d2.long_value)
        assertEquals(d.double_value, d2.double_value)
        assertEquals(d.boolean_value, d2.boolean_value)

        val d3: Data = Repository().get(ses, null, null, d2.id!!)
        assertEquals(d2, d3)

        val d4 = Repository().save(
            ses,
            ZonedDateTime.now(),
            d3.copy(string_value = "updated", long_value = 456L, double_value = 789.123, boolean_value = false)
        )
        assertEquals("updated", d4.string_value)
        assertEquals(456L, d4.long_value)
        assertEquals(789.123, d4.double_value)
        assertEquals(false, d4.boolean_value)

        val d5 = Repository().getAll(ses, ZonedDateTime.now(), null, Data::class)
        assertEquals(1, d5.size)

        Repository().save(
            ses,
            ZonedDateTime.now(),
            Data(null, "STR", 987L, 654.321, true)
        )
        val d6 = Repository().getAll(ses, ZonedDateTime.now(), null, Data::class)
        assertEquals(2, d6.size)

        Repository().delete(ses, ZonedDateTime.now(), d2.id!!)
        val d7 = Repository().getAll(ses, ZonedDateTime.now(), null, Data::class)
        assertEquals(1, d7.size)
    }
}

data class Data(
    override var id: UUID?,
    val string_value: String,
    val long_value: Long,
    val double_value: Double,
    val boolean_value: Boolean,
) : Model
