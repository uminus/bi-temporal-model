package example

import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.session.SessionFactory

fun main() {
    val configuration: Configuration = Configuration.Builder()
        .uri("bolt://192.168.10.101")
        .credentials("neo4j", "password")
        .build()

    val sessionFactory = SessionFactory(configuration, "example.model")

    val ses = sessionFactory.openSession()


//    val e = Service().save(ses, UUID.fromString("199d200c-4524-4c86-9432-d3e28c0c873c"), "EXAMPLE", Pair("key", "vak"))
    val e = Service().save(ses, null, "EXAMPLE", Pair("key", "value"))
    println(e)

//    val entity = Entity(id = null, "TYPE", fields = emptyArray())
//    ses.beginTransaction().use {
//        println(entity)
//        ses.save(entity)
//        it.commit()
//    }
//
//    ses.beginTransaction().use {
//        val e = ses.load(Entity::class.java, UUID.fromString(entity.id.toString()))
//        println(e)
//        e.type = "abbb"
//
//        ses.save(e)
//        it.commit()
//    }

    sessionFactory.close()
}