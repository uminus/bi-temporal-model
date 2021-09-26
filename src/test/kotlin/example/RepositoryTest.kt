package example

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.session.SessionFactory
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
        val d2 = Repository().save(ses, null, d)
        assertEquals(d.name, d2.name)
        assertEquals(d.name, d2.name)

        val d3: Data = Repository().get(ses, null, null, d2.id!!)
        assertEquals(d2, d3)

        val d4 = Repository().save(ses, null, d3.copy(name = "updated"))
        assertEquals("updated", d4.name)
    }
}

data class Data(override var id: UUID?, val name: String, val value: String) : Model
