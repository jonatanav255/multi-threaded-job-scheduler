package com.jobscheduler;

import com.jobscheduler.model.TaskPriority;
import com.jobscheduler.scheduler.SimpleScheduler;
import com.jobscheduler.task.Task;
import com.jobscheduler.task.TaskWrapper;
import com.jobscheduler.task.TaskWrapperBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the Multi-Threaded Job Scheduler application.
 *
 * This demo shows:
 * 1. Creating a thread pool scheduler
 * 2. Creating tasks with different priorities
 * 3. Tracking task status (PENDING -> RUNNING -> COMPLETED/FAILED)
 * 4. Handling task failures
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        logger.info("=== Multi-Threaded Job Scheduler ===\n");

        // Create a scheduler with 2 worker threads
        // This means maximum 2 tasks can run simultaneously
        SimpleScheduler scheduler = new SimpleScheduler(2);

        // =====================================================
        // CREATE TASKS
        // =====================================================

        // Task 1: Succeeds after 1 second
        TaskWrapper<String> task1 = new TaskWrapperBuilder<>(() -> {
            Thread.sleep(1000);  // Simulate work
            return "Result 1";
        }).name("Task-1").priority(TaskPriority.HIGH).build();

        // Task 2: Succeeds after 1 second
        TaskWrapper<String> task2 = new TaskWrapperBuilder<>(() -> {
            Thread.sleep(1000);  // Simulate work
            return "Result 2";
        }).name("Task-2").priority(TaskPriority.MEDIUM).build();

        // Task 3: Fails with an exception
        Task<String> failingTask = () -> {
            Thread.sleep(1000);  // Simulate work
            throw new RuntimeException("Simulated failure!");  // Then fail
        };
        TaskWrapper<String> task3 = new TaskWrapperBuilder<>(failingTask)
                .name("Task-3-Fails")
                .priority(TaskPriority.LOW)
                .build();

        // =====================================================
        // CHECK INITIAL STATUS (all should be PENDING)
        // =====================================================
        logger.info("Initial status:");
        logger.info("  {}", task1);
        logger.info("  {}", task2);
        logger.info("  {}\n", task3);

        // =====================================================
        // SUBMIT TASKS
        // =====================================================
        // All 3 tasks submitted, but only 2 can run at once (we have 2 threads)
        // Task 3 will wait in the queue until a thread is free
        scheduler.submitWrapper(task1);
        scheduler.submitWrapper(task2);
        scheduler.submitWrapper(task3);

        // =====================================================
        // CHECK STATUS WHILE RUNNING
        // =====================================================
        Thread.sleep(500);  // Wait half a second
        logger.info("\nAfter 500ms (tasks should be RUNNING):");
        logger.info("  {}", task1);
        logger.info("  {}\n", task2);

        // =====================================================
        // WAIT FOR ALL TASKS TO COMPLETE
        // =====================================================
        Thread.sleep(2000);  // Wait for all to finish

        // =====================================================
        // CHECK FINAL STATUS
        // =====================================================
        logger.info("\nFinal status:");
        logger.info("  {} → Result: {}", task1, task1.getResult());
        logger.info("  {} → Result: {}", task2, task2.getResult());
        logger.info("  {} → Error: {}\n", task3, task3.getException().getMessage());

        // =====================================================
        // CLEANUP
        // =====================================================
        scheduler.shutdown();
        logger.info("Application finished.");
    }
}
