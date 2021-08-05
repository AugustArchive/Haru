/**
 * Copyright (c) 2021 Noel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.floofy.haru

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import dev.floofy.haru.abstractions.AbstractJob
import dev.floofy.haru.abstractions.toJob
import dev.floofy.haru.builders.ScheduleBuilder
import dev.floofy.haru.exceptions.UnknownJobException
import dev.floofy.haru.extensions.*
import java.util.concurrent.CancellationException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlinx.coroutines.*

/**
 * Creates a new [Scheduler] as a DSL function with the [block] being
 * a [Scheduler.Options] initializer.
 *
 * @param block The block to create this [Scheduler].
 * @return This [Scheduler] object.
 */
@OptIn(ExperimentalContracts::class)
fun Scheduler(block: Scheduler.Options.() -> Unit): Scheduler {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val options = Scheduler.Options().apply(block)
    return Scheduler(options)
}

/**
 * Represents a [Scheduler] class to handle scheduling within this project.
 * @param options Any additional options or it'll use the [default options][Options.Default]
 */
class Scheduler(private val options: Options = Options.Default) {
    private val scope: SchedulerScope = SchedulerScope(options)
    private val jobs: MutableList<AbstractJob> = mutableListOf()
    private val cron: CronParser = CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX))

    private val AT_EXPRESSIONS: Map<String, String> = mapOf(
        "@daily" to "0 0 * * *",
        "@yearly" to "0 0 1 1 *",
        "@annually" to "0 0 1 1 *",
        "@monthly" to "0 0 1 * *",
        "@weekly" to "0 0 * * 0",
        "@hourly" to "0 * * * *"
    )

    /**
     * Schedules the job and if [start] is true, starts it
     * otherwise, it'll have to be started programmatically
     * using [Scheduler#start(AbstractJob)][Scheduler.schedule].
     *
     * @param job The job to use
     */
    fun schedule(job: AbstractJob, start: Boolean = true): Scheduler {
        val useThis = if (AT_EXPRESSIONS.containsKey(job.expression))
            AT_EXPRESSIONS[job.expression]
        else
            null

        val coroutineJob = scope.launch(job, cron, useThis)
        job.executionTime = ExecutionTime.forCron(cron.parse(useThis ?: job.expression))
        job.coroutineJob = coroutineJob

        if (start)
            job.coroutineJob!!.start()

        return this
    }

    /**
     * Schedules the job by a [builder][dev.floofy.haru.builders.ScheduleBuilder].
     * @param block The builder to construct this job.
     */
    @OptIn(ExperimentalContracts::class)
    fun schedule(block: ScheduleBuilder.() -> Unit): Scheduler {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }

        val builder = ScheduleBuilder().apply(block)
        val job = builder.toJob()

        return schedule(job, start = builder.start)
    }

    /**
     * Unschedule and cancels the coroutine job attached to the [AbstractJob], if it was scheduled.
     * @param name The name of the job
     */
    @Suppress("UNUSED")
    fun unschedule(name: String): Scheduler {
        val job = jobs.find { it.name == name } ?: throw UnknownJobException(name)
        return unschedule(job)
    }

    /**
     * Unschedule and cancels the coroutine job attached to the [AbstractJob], if it was scheduled.
     * @param name The name of the job
     */
    fun unschedule(job: AbstractJob): Scheduler {
        job.coroutineJob?.cancel(CancellationException("Job was cancelled by Scheduler#unschedule(AbstractJob)"))
        jobs.remove(job)
        return this
    }

    /**
     * Unschedules all jobs in this scheduler.
     */
    @Suppress("UNUSED")
    fun unschedule(): Scheduler {
        for (job in jobs) {
            job.coroutineJob?.cancel(CancellationException("Job was cancelled by Scheduler#unschedule()"))
            jobs.remove(job)
        }

        return this
    }

    /**
     * Any additional options to extend this [Scheduler]
     */
    data class Options(
        /**
         * Returns a error handler for all scheduled jobs if that specific job doesn't
         * have a `jobOnError` executor.
         */
        var errorHandler: ((AbstractJob, Throwable) -> Unit)? = null
    ) {
        companion object {
            val Default: Options = Options(
                errorHandler = null
            )
        }

        /**
         * Attaches a [handler] to the current [errorHandler] in this embedded
         * [Options] context.
         *
         * @param handler The handler function to call
         * @returns This [Options] object.
         */
        @Suppress("UNUSED")
        fun handleError(handler: (AbstractJob, Throwable) -> Unit): Options {
            errorHandler = handler
            return this
        }
    }
}
