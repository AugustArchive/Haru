package dev.floofy.haru.examples.jvm

import dev.floofy.haru.Scheduler
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.minutes

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        println("Hello, world!")
        val scheduler = Scheduler {
            onError { job, throwable ->
                println("[${job.name}] Unable to execute job: [$throwable]")
            }
        }

        var count = 0
        scheduler.schedule {
            name = "my scheduler"
            expression = "* * * * *"

            execute { _ ->
                if ((count % 2) == 0) {
                    count++
                    println("updated count to $count")

                    throw IllegalStateException("It's even!")
                } else {
                    count++
                    println("updated count to $count")
                }
            }
        }

        runBlocking {
            delay(5.minutes)
            scheduler.unschedule("my scheduler")
        }

        println("done~")
    }
}
