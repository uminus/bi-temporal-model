package example

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.session.SessionFactory
import java.time.ZonedDateTime.now
import java.time.ZonedDateTime.parse
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

internal class ServiceTest {

    @Test
    fun test() {
        val configuration: Configuration = Configuration.Builder()
            .uri("bolt://dev")
            .credentials("neo4j", "password")
            .build()

        val sessionFactory = SessionFactory(configuration, "example.model")
        val ses = sessionFactory.openSession()
        ses.purgeDatabase()

        val service = Service()

        val (v1, entity1) = service.save(
            ses,
            parse("2021-01-01T00:00:00+09:00"),
            null,
            "UNIT_TEST",
            Pair("key1", "value1"),
            Pair("key2", "value2")
        )

        val (_, actual1) = service.get(ses, parse("2021-01-01T00:00:00+09:00"), null, entity1.id)
        actual1.sortBy { it.first }
        assertContentEquals(arrayOf(Pair("key1", "value1"), Pair("key2", "value2")), actual1)

        val (v2, entity2) = service.save(
            ses,
            parse("2021-06-01T00:00:00+09:00"),
            entity1.id,
            "UNIT_TEST",
            Pair("key1", "updated"),
            Pair("KEY1", "VALUE1")
        )
        val (_, actual2) = service.get(ses, parse("2021-06-01T00:00:00+09:00"), null, entity2.id)
        actual2.sortBy { it.first }
        assertContentEquals(arrayOf(Pair("KEY1", "VALUE1"), Pair("key1", "updated"), Pair("key2", "value2")), actual2)

        val (_, actual3) = service.get(ses, parse("2021-01-01T00:00:00+09:00"), null, entity2.id)
        actual3.sortBy { it.first }
        assertContentEquals(arrayOf(Pair("key1", "value1"), Pair("key2", "value2")), actual3)

        val (_, actual4) = service.get(ses, parse("2021-06-01T00:00:00+09:00"), v1.id, entity2.id)
        actual4.sortBy { it.first }
        assertContentEquals(arrayOf(Pair("key1", "value1"), Pair("key2", "value2")), actual4)

        val deleted = service.delete(ses, parse("2021-06-01T00:00:00+09:00"), entity2.id!!)
        assertEquals(entity2, deleted.deleted[0])

        val exception = assertThrows<IllegalStateException> {
            service.save(ses, now(), entity1.id, "UNIT_TEST", Pair("error", "error"))
        }
        assertEquals("Entity is deleted. id: ${entity1.id}", exception.message)

        val (v3, _) = service.save(
            ses,
            parse("2021-12-01T00:00:00+09:00"),
            null,
            "UNIT_TEST"
        )

        val entities1 = service.getAll(ses, parse("2021-06-01T00:00:00+09:00"), v2.id, "UNIT_TEST")
        assertEquals(0, entities1.size)

        val entities2 = service.getAll(ses, parse("2021-05-31T00:00:00+09:00"), v3.id, "UNIT_TEST")
        assertEquals(1, entities2.size)

        val entities3 = service.getAll(ses, parse("2021-12-01T00:00:00+09:00"), v3.id, "UNIT_TEST")
        assertEquals(1, entities3.size)
    }
}