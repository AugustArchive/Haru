package dev.floofy.haru

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import java.time.ZonedDateTime

public actual fun parseCronExpression(expression: String): LocalDateTime {
    val parser = CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX))
    val optional = ExecutionTime.forCron(parser.parse(expression)).nextExecution(ZonedDateTime.now())
    if (!optional.isPresent)
        throw IllegalStateException("Unable to fetch next execution.")

    return optional.get().toInstant().toKotlinInstant().toLocalDateTime(TimeZone.currentSystemDefault())
}
