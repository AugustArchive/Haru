package dev.floofy.haru

/**
 * Represents a job that can be executed by the scheduler.
 * @param C The context object, will default to [EmptyHaruContext].
 */
public abstract class Job<C: HaruContext> {
    /**
     * Represents the CRON expression this Job is using.
     */
    public abstract val expression: String

    /**
     * If the Job is executed only once, then it's popped off the scheduler's queue.
     */
    public abstract val isOneTime: Boolean

    /**
     * Represents the identifier of this [Job].
     */
    public abstract val name: String

    /**
     * Returns the coroutine job that this job is being held on by the scheduler queue.
     */
    public var coroutineJob: kotlinx.coroutines.Job? = null

    /**
     * Executes this job once the scheduler has queued it.
     * @param context The context object.
     */
    public abstract suspend fun execute(context: C)
}
