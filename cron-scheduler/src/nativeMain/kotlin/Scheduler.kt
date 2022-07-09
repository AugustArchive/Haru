package dev.floofy.haru

internal actual fun Scheduler.runOnError(job: Job<*>, throwable: Throwable) {
    options._onError?.invoke(job, throwable)
}

actual fun Scheduler(options: SchedulerOptions.() -> Unit): Scheduler =
    Scheduler(SchedulerOptions().apply(options))
