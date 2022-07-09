package dev.floofy.haru

import kotlinx.datetime.LocalDateTime

actual fun parseCronExpression(expression: String): LocalDateTime {
    return LocalDateTime.parse("abcd")
}
