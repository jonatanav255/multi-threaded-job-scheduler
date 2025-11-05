package com.jobscheduler.model;

/**
 * Represents the lifecycle states of a task. Tasks transition through these
 * states as they are processed by the scheduler.
 */
public enum TaskStatus {
    /**
     * Task has been created but not yet submitted to the scheduler
     */
    PENDING,
    /**
     * Task is currently being executed by a worker thread
     */
    RUNNING,
    /**
     * Task finished successfully without errors
     */
    COMPLETED,
    /**
     * Task threw an exception or encountered an error during execution
     */
    FAILED,
    /**
     * Task was cancelled before or during execution
     */
    CANCELLED
}
