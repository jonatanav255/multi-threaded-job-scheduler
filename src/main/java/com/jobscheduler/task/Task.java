package com.jobscheduler.task;

/**
 * Represents a unit of work that can be executed by the scheduler.
 *
 * @param <T> the type of result this task produces
 */
@FunctionalInterface
public interface Task<T> {

    /**
     * Executes the task and returns a result. This method will be called by a
     * worker thread when the task is scheduled.
     *
     * @return the result of the task execution
     * @throws Exception if the task execution fails
     */
    T execute() throws Exception;
}
