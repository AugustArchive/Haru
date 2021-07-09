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

package dev.floofy.haru.abstractions

import kotlinx.coroutines.Job

/**
 * Represents an abstraction for constructing jobs. This is the Java-style
 * of way to create a Job without using the DSL if you wish.
 *
 * @param name The name of the job
 * @param expression The expression to use when scheduling
 */
abstract class AbstractJob(val name: String, val expression: String) {
    /**
     * Represents the coroutine job for this [AbstractJob], returns `null`
     * if it hasn't been scheduled.
     */
    var coroutineJob: Job? = null

    /**
     * Executes this [AbstractJob] in a separate thread-pool.
     * @param ctx The context to use when executing.
     */
    abstract suspend fun execute()
}
