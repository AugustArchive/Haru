package dev.floofy.haru

import kotlinx.datetime.LocalDateTime

/**
 * Parses a cron [expression] and returns the [LocalDateTime] object that it'll be scheduled.
 * @param expression The expression to parse.
 */
public expect fun parseCronExpression(expression: String): LocalDateTime
