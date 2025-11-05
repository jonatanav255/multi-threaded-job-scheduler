package com.jobscheduler;

import com.jobscheduler.scheduler.SimpleScheduler;
import com.jobscheduler.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the Multi-Threaded Job Scheduler application.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("=== Multi-Threaded Job Scheduler ===");
        logger.info("Starting application...");

        // Create a simple scheduler
        SimpleScheduler scheduler = new SimpleScheduler();

        // Create a simple task using lambda
        Task<String> task = () -> {
            logger.info("Hello from inside the task!");
            return "Task completed successfully!";
        };

        // Execute the task
        String result = scheduler.execute(task);
        logger.info("Result: {}", result);

        logger.info("Application finished.");
    }
}
