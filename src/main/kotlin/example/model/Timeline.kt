package example.model

import example.model.util.ZonedDateTimeStrategy
import org.neo4j.ogm.annotation.GeneratedValue
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.typeconversion.Convert
import java.time.ZonedDateTime

data class Timeline(
    @Id @GeneratedValue(strategy = ZonedDateTimeStrategy::class) @Convert(ZonedDateTimeStrategy::class) val id: ZonedDateTime? = ZonedDateTime.now(),
)