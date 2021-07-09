package dev.floofy.haru

import java.util.*

object HaruInfo {
    val BUILT_AT: String
    val VERSION: String

    init {
        val reader = HaruInfo::class.java.getResourceAsStream("/metadata.properties")
        val props = Properties().apply { load(reader) }

        BUILT_AT = props.getProperty("built.at")
        VERSION = props.getProperty("app.version")
    }
}
