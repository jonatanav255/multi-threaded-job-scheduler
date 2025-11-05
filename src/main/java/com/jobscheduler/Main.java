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

        // Create a scheduler with 3 threads
        SimpleScheduler scheduler = new SimpleScheduler(3);

        // Submit 5 tasks - watch them run concurrently!
        logger.info("Submitting 5 tasks...");

        for (int i = 1; i <= 5; i++) {
            final int taskNum = i;

            Task<String> task = () -> {
                logger.info("Task {} starting...", taskNum);
                Thread.sleep(2000);  // Each task takes 2 seconds
                logger.info("Task {} finished!", taskNum);
                return "Result from task " + taskNum;
            };

            scheduler.submit(task);
        }

        logger.info("All 5 tasks submitted!");
        logger.info("Main thread: Waiting for tasks to complete...");

        // Give tasks time to finish
        Thread.sleep(5000);

        scheduler.shutdown();
        logger.info("Application finished.");
    }
}
