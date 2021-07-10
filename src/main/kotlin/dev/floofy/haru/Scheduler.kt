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
import dev.floofy.haru.builders.ScheduleBuilder
import dev.floofy.haru.exceptions.UnknownJobException
import dev.floofy.haru.extensions.*
import java.util.concurrent.CancellationException
import kotlinx.coroutines.*

/**
 * Represents a [Scheduler] class to handle scheduling within this project.
 */
class Scheduler {
    private val scope: SchedulerScope = SchedulerScope()
    private val jobs: MutableList<AbstractJob> = mutableListOf()
    private val cron: CronParser = CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX))

    /**
     * Schedules the job and if [start] is true, starts it
     * otherwise, it'll have to be started programmatically
     * using [Scheduler#start(AbstractJob)][Scheduler.schedule].
     *
     * @param job The job to use
     */
    fun schedule(job: AbstractJob, start: Boolean = true): Scheduler {
        val coroutineJob = scope.launch(job, cron)

        job.executionTime = ExecutionTime.forCron(cron.parse(job.expression))
        job.coroutineJob = coroutineJob

        if (start)
            job.coroutineJob!!.start()

        return this
    }

    /**
     * Schedules the job by a [builder][dev.floofy.haru.builders.ScheduleBuilder].
     * @param block The builder to construct this job.
     */
    fun schedule(block: ScheduleBuilder.() -> Unit): Scheduler {
        val builder = ScheduleBuilder().apply(block)
        val job = object: AbstractJob(name = builder.name, expression = builder.expression) {
            override suspend fun execute() = builder.executor()
        }

        return schedule(job, start = builder.start)
    }

    /**
     * Unschedule and cancels the coroutine job attached to the [AbstractJob], if it was scheduled.
     * @param name The name of the job
     */
    fun unschedule(name: String): Scheduler {
        val job = jobs.find { it.name == name } ?: throw UnknownJobException(name)
        return unschedule(job)
    }

    /**
     * Unschedule and cancels the coroutine job attached to the [AbstractJob], if it was scheduled.
     * @param name The name of the job
     */
    fun unschedule(job: AbstractJob): Scheduler {
        jobs.find { it.name == job.name } ?: throw UnknownJobException(job.name) // shouldn't get here but :shrug:

        job.coroutineJob?.cancel(CancellationException("Job was cancelled by Scheduler#unschedule(AbstractJob)"))
        jobs.remove(job)
        return this
    }

    /**
     * Unschedules all jobs in this scheduler.
     */
    fun unschedule(): Scheduler {
        for (job in jobs) {
            job.coroutineJob?.cancel(CancellationException("Job was cancelled by Scheduler#unschedule()"))
            jobs.remove(job)
        }

        return this
    }
}
