package example

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.session.SessionFactory
import java.util.*

internal class RepositoryTest {

    @Test
    fun save() {
        val configuration: Configuration = Configuration.Builder()
            .uri("bolt://192.168.10.101")
            .credentials("neo4j", "password")
            .build()

        val sessionFactory = SessionFactory(configuration, "example.model")
        val ses = sessionFactory.openSession()

        val d = Data(null, "a", "b")
        val d2 = Repository().save(ses, null, d)
        assertEquals(d, d2)

        val d3: Data = Repository().get(ses, null, null, d2.id!!)

        assertEquals(d, d3)

    }
}

data class Data(override var id: UUID?, val name: String, val value: String) : Model
