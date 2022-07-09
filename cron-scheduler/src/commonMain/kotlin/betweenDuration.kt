package dev.floofy.haru

import kotlinx.datetime.LocalDateTime

/**
 * Calculates the duration from [start] to [end].
 * @param start The start date
 * @param end The end date
 * @return The in-between time in milliseconds
 */
public expect fun betweenDuration(start: LocalDateTime, end: LocalDateTime): Long
