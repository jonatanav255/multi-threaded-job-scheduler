package com.jobscheduler.scheduler;

import com.jobscheduler.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Simple scheduler with background thread execution.
 */
public class SimpleScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SimpleScheduler.class);
    private final ExecutorService executor;

    public SimpleScheduler(int numThreads) {
        this.executor = Executors.newFixedThreadPool(numThreads);
        logger.info("SimpleScheduler created with {} threads", numThreads);
    }

    public SimpleScheduler() {
        this(3);  // Default: 3 threads
    }

    /**
     * Execute a task synchronously (blocks until complete).
     */
    public <T> T execute(Task<T> task) {
        logger.info("Starting task execution (synchronous)...");

        try {
            T result = task.execute();
            logger.info("Task completed successfully");
            return result;

        } catch (Exception e) {
            logger.error("Task failed", e);
            throw new RuntimeException("Task execution failed", e);
        }
    }

    /**
     * Submit a task for background execution (returns immediately).
     */
    public <T> Future<T> submit(Task<T> task) {
        logger.info("Submitting task for background execution...");

        Future<T> future = executor.submit(() -> {
            logger.info("[Background Thread] Starting task...");
            T result = task.execute();
            logger.info("[Background Thread] Task completed");
            return result;
        });

        logger.info("Task submitted (not waiting for result)");
        return future;
    }

    /**
     * Shutdown the scheduler.
     */
    public void shutdown() {
        logger.info("Shutting down scheduler...");
        executor.shutdown();
    }
}
