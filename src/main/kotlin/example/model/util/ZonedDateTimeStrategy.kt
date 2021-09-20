package example.model.util

import org.neo4j.ogm.id.IdStrategy
import org.neo4j.ogm.typeconversion.AttributeConverter
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ZonedDateTimeStrategy : IdStrategy, AttributeConverter<ZonedDateTime, Long> {
    override fun generateId(entity: Any?): Any {
        return ZonedDateTime.now()
    }

    override fun toGraphProperty(value: ZonedDateTime?): Long? {
        return value?.toInstant()?.toEpochMilli()
    }

    override fun toEntityAttribute(value: Long?): ZonedDateTime {
        return ZonedDateTime.ofInstant(value?.let { Instant.ofEpochMilli(it) }, ZoneId.systemDefault())
    }
}