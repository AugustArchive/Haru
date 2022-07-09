package dev.floofy.haru

import kotlinx.coroutines.CancellationException
import kotlin.jvm.JvmName

/**
 * Represents the cron scheduler itself. This is where you can execute actions based off a specific time, and
 * it'll execute on every iteration unless the job is a one-time job.
 */
public class Scheduler(internal val options: SchedulerOptions) {
    private val _jobs: MutableList<Job<*>> = mutableListOf()
    private val queue: SchedulerQueue = SchedulerQueue(options.coroutineScope, this)

    /**
     * Returns all the expression attributes that you can use for scheduling jobs.
     */
    public val expressions: Map<String, String> = mapOf(
        "@daily" to "0 0 * * *",
        "@yearly" to "0 0 1 1 *",
        "@annually" to "0 0 1 1 *",
        "@monthly" to "0 0 1 * *",
        "@weekly" to "0 0 * * 0",
        "@hourly" to "0 * * * *"
    )

    /**
     * Returns all the jobs this scheduler has queued up.
     */
    public val jobs: List<Job<*>>
        get() = _jobs.toList()

    /**
     * Operator overload to retrieve a Job that was queued by its identifier, or `null`
     * if it was never scheduled.
     *
     * @param key The job's identifier
     */
    public operator fun get(key: String): Job<*>? = jobs.singleOrNull { it.name == key }

    /**
     * Schedules the job and automatically starts the job.
     * @param job The job to use when executing.
     */
    public fun schedule(job: Job<*>): Scheduler {
        val expression = if (expressions.containsKey(job.expression)) expressions[job.expression]!! else job.expression
        val coroutineJob = queue.launch(job, expression)

        job.coroutineJob = coroutineJob
        coroutineJob.start()

        return this
    }

    /**
     * Schedules the job based off a Kotlin DSL builder. This will use the [EmptyHaruContext] type rather than
     * your custom context, if you ever set one.
     *
     * @param builder The builder to create the job.
     */
    @JvmName("scheduleWithEmptyContext")
    public fun schedule(builder: JobBuilder<EmptyHaruContext>.() -> Unit = {}): Scheduler = schedule<EmptyHaruContext>(builder)

    /**
     * Schedules the job based off a Kotlin DSL builder. This will use your custom context type for type-safety.
     * @param builder The builder to create the job.
     */
    public fun <C: HaruContext> schedule(builder: JobBuilder<C>.() -> Unit = {}): Scheduler {
        val job = JobBuilder<C>().apply(builder).build()
        return schedule(job)
    }

    /**
     * Bulk schedules a list of jobs and enqueues it into the scheduler queue. This will use the [EmptyHaruContext] context
     * type rather a user-provided one.
     *
     * @param jobs The jobs to bulk schedule.
     */
    @JvmName("scheduleAllWithEmptyContext")
    public fun scheduleAll(vararg jobs: Job<EmptyHaruContext>): Scheduler = scheduleAll(*jobs)

    /**
     * Bulk schedules a list of jobs and enqueues it into the scheduler queue. This will use your context type
     * for type-safety!
     *
     * @param jobs The jobs to bulk schedule.
     */
    public fun <C: HaruContext> scheduleAll(vararg jobs: Job<C>): Scheduler {
        for (job in jobs) schedule(job)
        return this
    }

    /**
     * Unschedules a job by its identifier.
     * @param name The name of the job.
     * @return If the job was found or not.
     */
    public fun unschedule(name: String): Boolean {
        val job = this[name] ?: return false
        job.coroutineJob?.cancel(CancellationException("Job was cancelled manually via #unschedule(String)"))
        _jobs.remove(job)

        return true
    }

    /**
     * Unschedules a job.
     * @param job The job to unschedule
     * @return If the job was found or not.
     */
    public fun unschedule(job: Job<*>): Boolean = unschedule(job.name)

    /**
     * Unschedules all jobs
     * @return If the job was found or not.
     */
    public fun unschedule(): Boolean {
        for (job in jobs) {
            unschedule(job)
        }

        return true
    }
}

internal expect fun Scheduler.runOnError(job: Job<*>, throwable: Throwable)

/**
 * Creates a new [Scheduler] object via a Kotlin DSL object.
 * @param options The options DSL object to create the [Scheduler].
 */
public expect fun Scheduler(options: SchedulerOptions.() -> Unit = {}): Scheduler
