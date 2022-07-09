package dev.floofy.haru

import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

internal class SchedulerQueue(private val scope: CoroutineScope, private val scheduler: Scheduler): CoroutineScope by scope {
    @Suppress("UNCHECKED_CAST")
    fun launch(job: Job<*>, expression: String): kotlinx.coroutines.Job {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val nextSchedule = parseCronExpression(expression)
        val delayInMillis = betweenDuration(now, nextSchedule)

        return launch(start = CoroutineStart.LAZY) {
            delay(delayInMillis)
            while (isActive) {
                println("job executed ^_^")
                try {
                    (job as Job<HaruContext>).execute(scheduler.options.context)
                } catch (e: Throwable) {
                    scheduler.runOnError(job, e)
                }

                if (!job.isOneTime) {
                    val nextDelay = parseCronExpression(expression)
                    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    val delayInMills = betweenDuration(now, nextDelay)

                    delay(delayInMills)
                } else {
                    scheduler.unschedule(job)
                    cancel()
                }
            }
        }
    }
}

