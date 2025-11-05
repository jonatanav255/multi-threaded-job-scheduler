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

        // Wrap it using the NEW separate builder
        TaskWrapper<String> wrapper = new TaskWrapperBuilder<>(task)
                .name("My First Task")
                .priority(TaskPriority.HIGH)
                .build();

        logger.info("Created task wrapper: {}", wrapper);

        // Execute the task
        String result = scheduler.execute(task);
        logger.info("Result: {}", result);

        logger.info("Application finished.");
    }
}
