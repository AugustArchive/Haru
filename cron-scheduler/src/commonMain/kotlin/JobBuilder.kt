package dev.floofy.haru

import kotlin.properties.Delegates

public class JobBuilder<C: HaruContext> {
    private var _executor: (suspend (C) -> Unit)? = null

    /**
     * Returns the expression to use when scheduling the job.
     */
    public var expression: String by Delegates.notNull()

    /**
     * If the Job is supposed to be a one-time job, it'll be removed from the job queue. You will
     * have to set this to false OR keep re-enqueueing it.
     */
    public var isOneTime: Boolean = false

    /**
     * The name of the job.
     */
    public var name: String by Delegates.notNull()

    /**
     * Sets the executor function when executing this job.
     * @param callable The callable function
     * @throws IllegalArgumentException If the executor function was already set.
     */
    public fun execute(callable: (suspend (C) -> Unit)? = {}) {
        if (_executor != null)
            throw IllegalArgumentException("Executor was already set.")

        _executor = callable
    }

    /**
     * Creates the [Job] from this builder.
     */
    public fun build(): Job<C> = object: Job<C>() {
        override val expression: String = this@JobBuilder.expression
        override val isOneTime: Boolean = this@JobBuilder.isOneTime
        override val name: String = this@JobBuilder.name

        override suspend fun execute(context: C) {
            _executor?.invoke(context)
        }
    }
}
