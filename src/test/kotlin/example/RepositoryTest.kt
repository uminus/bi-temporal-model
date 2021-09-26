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

        val d = Data(null, "a", "b")
        val d2 = Repository().save(ses, ZonedDateTime.now(), d)
        assertEquals(d.name, d2.name)
        assertEquals(d.name, d2.name)

        val d3: Data = Repository().get(ses, null, null, d2.id!!)
        assertEquals(d2, d3)

        val d4 = Repository().save(ses, ZonedDateTime.now(), d3.copy(name = "updated"))
        assertEquals("updated", d4.name)

        val d5 = Repository().getAll(ses, ZonedDateTime.now(), null, Data::class)
        assertEquals(1, d5.size)

        Repository().save(ses, ZonedDateTime.now(), Data(null, "c", "d"))
        val d6 = Repository().getAll(ses, ZonedDateTime.now(), null, Data::class)
        assertEquals(2, d6.size)
    }
}

data class Data(override var id: UUID?, val name: String, val value: String) : Model
