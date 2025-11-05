package com.jobscheduler.scheduler;

import com.jobscheduler.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The simplest possible scheduler - just runs one task.
 * We'll build on this incrementally.
 */
public class SimpleScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SimpleScheduler.class);

    /**
     * Execute a task right now (synchronously).
     */
    public <T> T execute(Task<T> task) {
        logger.info("Starting task execution...");

        try {
            T result = task.execute();
            logger.info("Task completed successfully");
            return result;

        } catch (Exception e) {
            logger.error("Task failed", e);
            throw new RuntimeException("Task execution failed", e);
        }
    }
}
