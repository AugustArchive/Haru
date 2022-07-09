package dev.floofy.haru

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.Duration

public actual fun betweenDuration(start: LocalDateTime, end: LocalDateTime): Long {
    val duration = Duration.between(start.toJavaLocalDateTime(), end.toJavaLocalDateTime())
    return duration.toMillis()
}
