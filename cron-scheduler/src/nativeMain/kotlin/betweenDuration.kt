package dev.floofy.haru

import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration.Companion.nanoseconds

actual fun betweenDuration(start: LocalDateTime, end: LocalDateTime): Long =
    (start.nanosecond - end.nanosecond).nanoseconds.inWholeMilliseconds
