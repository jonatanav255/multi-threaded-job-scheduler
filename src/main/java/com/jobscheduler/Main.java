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
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        logger.info("=== Multi-Threaded Job Scheduler ===");
        logger.info("Starting application...");

        // Create a scheduler with 2 threads
        SimpleScheduler scheduler = new SimpleScheduler(2);

        // Create 3 wrapped tasks
        TaskWrapper<String> task1 = new TaskWrapperBuilder<>(() -> {
            Thread.sleep(1000);
            return "Result 1";
        }).name("Task-1").priority(TaskPriority.HIGH).build();

        TaskWrapper<String> task2 = new TaskWrapperBuilder<>(() -> {
            Thread.sleep(1000);
            return "Result 2";
        }).name("Task-2").priority(TaskPriority.MEDIUM).build();

        Task<String> failingTask = () -> {
            Thread.sleep(1000);
            throw new RuntimeException("Simulated failure!");
        };
        TaskWrapper<String> task3 = new TaskWrapperBuilder<>(failingTask)
                .name("Task-3-Fails")
                .priority(TaskPriority.LOW)
                .build();

        // Check initial status
        logger.info("Before submit - Task 1: {}", task1);
        logger.info("Before submit - Task 2: {}", task2);
        logger.info("Before submit - Task 3: {}", task3);

        // Submit tasks
        scheduler.submitWrapper(task1);
        scheduler.submitWrapper(task2);
        scheduler.submitWrapper(task3);

        // Wait a bit and check status
        Thread.sleep(500);
        logger.info("After 500ms - Task 1: {}", task1);
        logger.info("After 500ms - Task 2: {}", task2);

        // Wait for completion
        Thread.sleep(2000);
        logger.info("Final - Task 1: {} | Result: {}", task1, task1.getResult());
        logger.info("Final - Task 2: {} | Result: {}", task2, task2.getResult());
        logger.info("Final - Task 3: {} | Error: {}", task3, task3.getException().getMessage());

        scheduler.shutdown();
        logger.info("Application finished.");
    }
}
