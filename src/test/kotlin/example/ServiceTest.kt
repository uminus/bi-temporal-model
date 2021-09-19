package example

import org.junit.jupiter.api.Test
import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.session.SessionFactory
import kotlin.test.assertContentEquals

internal class ServiceTest {

    @Test
    fun versioning() {
        val configuration: Configuration = Configuration.Builder()
            .uri("bolt://dev")
            .credentials("neo4j", "password")
            .build()

        val sessionFactory = SessionFactory(configuration, "example.model")
        val ses = sessionFactory.openSession()
        val service = Service()

        val entity1 = service.save(ses, null, "UNIT_TEST", Pair("key1", "value1"), Pair("key2", "value2"))

        val actual1 = service.get(ses, entity1.id)
        actual1.sortBy { it.first }
        assertContentEquals(arrayOf(Pair("key1", "value1"), Pair("key2", "value2")), actual1)

        val entity2 = service.save(ses, entity1.id, "UNIT_TEST", Pair("key1", "updated"), Pair("KEY1", "VALUE1"))
        val actual2 = service.get(ses, entity2.id)
        actual2.sortBy { it.first }
        assertContentEquals(arrayOf(Pair("KEY1", "VALUE1"), Pair("key1", "updated"), Pair("key2", "value2")), actual2)
    }
}