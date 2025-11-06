package com.jobscheduler.scheduler;

import com.jobscheduler.task.Task;
import com.jobscheduler.task.TaskWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Simple scheduler with background thread execution.
 *
 * This scheduler manages a pool of worker threads that execute tasks concurrently.
 * Think of it as a manager with a fixed number of workers - when you submit a task,
 * an available worker picks it up and executes it.
 */
public class SimpleScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SimpleScheduler.class);

    // The thread pool that will execute our tasks
    // ExecutorService = service that manages threads for us
    private final ExecutorService executor;

    /**
     * Create a scheduler with a specific number of worker threads.
     *
     * @param numThreads how many tasks can run at the same time
     */
    public SimpleScheduler(int numThreads) {
        // Create a fixed thread pool:
        // - Creates exactly 'numThreads' worker threads
        // - Threads are reused (not created/destroyed per task)
        // - If all threads are busy, new tasks wait in a queue
        this.executor = Executors.newFixedThreadPool(numThreads);
        logger.info("Scheduler ready with {} worker threads", numThreads);
    }

    /**
     * Create a scheduler with default 3 worker threads.
     */
    public SimpleScheduler() {
        this(3);
    }

    /**
     * Execute a task synchronously (blocks until complete).
     *
     * This runs the task on the CURRENT thread (not in background).
     * The method doesn't return until the task finishes.
     *
     * @param task the task to execute
     * @return the result from the task
     */
    public <T> T execute(Task<T> task) {
        try {
            // Call task.execute() and wait for result
            T result = task.execute();
            return result;

        } catch (Exception e) {
            // If task throws exception, wrap it and re-throw
            throw new RuntimeException("Task execution failed", e);
        }
    }

    /**
     * Submit a task for background execution (returns immediately).
     *
     * This submits the task to the thread pool and returns a Future.
     * The task runs in the background while your code continues.
     *
     * @param task the task to execute in background
     * @return a Future you can use to get the result later
     */
    public <T> Future<T> submit(Task<T> task) {
        // Submit to thread pool - returns immediately
        // The executor will:
        // 1. Put task in queue
        // 2. When a worker thread is free, it picks up the task
        // 3. Worker thread calls task.execute()
        // 4. Result is stored in the Future
        Future<T> future = executor.submit(() -> {
            // This lambda runs in a BACKGROUND THREAD
            T result = task.execute();
            return result;
        });

        // Return the Future immediately (task may still be running)
        return future;
    }

    /**
     * Submit a TaskWrapper - automatically tracks status changes.
     *
     * This is the recommended way to submit tasks. The wrapper tracks:
     * - Status (PENDING -> RUNNING -> COMPLETED/FAILED)
     * - Result or exception
     * - Start and end times
     *
     * @param wrapper the wrapped task
     * @return the task ID
     */
    public <T> String submitWrapper(TaskWrapper<T> wrapper) {
        logger.info("Submitting: {}", wrapper);

        // Submit to thread pool (runs in background)
        executor.submit(() -> {
            try {
                // STEP 1: Mark task as started
                // Changes status from PENDING to RUNNING
                // Records start time
                wrapper.markStarted();

                // STEP 2: Execute the actual task
                // This is where the real work happens
                T result = wrapper.getTask().execute();

                // STEP 3: Mark as completed successfully
                // Changes status from RUNNING to COMPLETED
                // Stores the result
                // Records end time
                wrapper.markCompleted(result);
                logger.info("✓ Completed: {}", wrapper.getName());

            } catch (Exception e) {
                // STEP 3 (alternate): Mark as failed
                // Changes status from RUNNING to FAILED
                // Stores the exception
                // Records end time
                wrapper.markFailed(e);
                logger.error("✗ Failed: {} - {}", wrapper.getName(), e.getMessage());
            }
        });

        // Return the task ID so caller can track it
        return wrapper.getId();
    }

    /**
     * Shutdown the scheduler gracefully.
     *
     * This stops accepting new tasks and waits for running tasks to finish.
     */
    public void shutdown() {
        logger.info("Shutting down scheduler...");
        // Initiates orderly shutdown:
        // - No new tasks accepted
        // - Already submitted tasks continue
        // - Threads terminate when all tasks done
        executor.shutdown();
    }
}
