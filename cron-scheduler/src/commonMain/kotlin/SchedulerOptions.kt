package dev.floofy.haru

import kotlinx.coroutines.CoroutineScope

/**
 * Represents the scheduler's options, this is platform dependent.
 */
public expect class SchedulerOptions {
    /**
     * Sets the coroutine scope to use. By default, it'll use [GlobalScope][kotlinx.coroutines.GlobalScope].
     */
    public var coroutineScope: CoroutineScope

    /**
     * Sets the context object to use in the scheduler. Will default to [EmptyHaruContext] if
     * none was provided.
     */
    public var context: HaruContext

    /**
     * Adds an exception handler on when a job has failed to execute.
     * @param callable The callable function to call.
     */
    public fun onError(callable: (Job<*>, Throwable) -> Unit = { _, _ -> })
}
