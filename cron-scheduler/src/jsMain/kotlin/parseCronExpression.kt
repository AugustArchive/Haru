package dev.floofy.haru

import kotlinx.datetime.LocalDateTime

public actual fun parseCronExpression(expression: String): LocalDateTime {
    return LocalDateTime.parse("abc")
}
