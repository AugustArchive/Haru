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

import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import dev.floofy.haru.abstractions.AbstractJob
import dev.floofy.haru.internal.HaruThreadFactory
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.*

internal class SchedulerScope(private val options: Scheduler.Options): CoroutineScope {
    private val executor: ExecutorService = Executors.newCachedThreadPool(HaruThreadFactory())
    private val job = Job()

    override val coroutineContext: CoroutineContext =
        job + executor.asCoroutineDispatcher()

    fun launch(job: AbstractJob, parser: CronParser, expression: String? = null): Job {
        val now = ZonedDateTime.now()
        val nextSchedule = ExecutionTime.forCron(parser.parse(expression ?: job.expression)).nextExecution(now).get()
        val delayInMillis = Duration.between(now, nextSchedule)

        return launch(start = CoroutineStart.LAZY) {
            delay(delayInMillis.toMillis())
            while (isActive) {
                try {
                    job.execute()
                } catch (e: Exception) {
                    job.jobOnError(e)
                    options.errorHandler?.invoke(e)
                }

                val nextDelay = job.getAndUpdateNextDelay()
                delay(nextDelay)
            }
        }
    }
}
