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

package dev.floofy.haru.builders

/**
 * Type-alias to indicate a schedule executor
 */
internal typealias ScheduleExecutor = suspend () -> Unit

/**
 * Represents a builder class to construct [AbstractJob][dev.floofy.haru.abstractions.AbstractJob]s.
 */
class ScheduleBuilder {
    /**
     * The expression to use when scheduling this job. This supports
     * full Unix CRON expressions like `@hourly`!
     */
    var expression: String = ""

    /**
     * Returns the executor to execute this scheduler.
     */
    var executor: ScheduleExecutor = {}

    /**
     * If the job should be started automatically or not.
     */
    var start: Boolean = true

    /**
     * The name of the job
     */
    var name: String = ""
}
