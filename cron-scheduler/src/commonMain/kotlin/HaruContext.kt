package dev.floofy.haru

/**
 * Represents the "context" object that can be used when scheduling a job.
 */
public interface HaruContext

/**
 * Represents an empty [HaruContext], the default context unless a user-provided one
 * is set.
 */
public object EmptyHaruContext: HaruContext
