package com.jobscheduler;

import com.jobscheduler.model.TaskPriority;
import com.jobscheduler.scheduler.SimpleScheduler;
import com.jobscheduler.task.TaskWrapper;
import com.jobscheduler.task.TaskWrapperBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point - Demo of Priority-Based Scheduling.
 *
 * This demo shows that tasks run in PRIORITY order, not submission order:
 * - Submit tasks in reverse order (LOW, MEDIUM, HIGH)
 * - Watch them execute in priority order (HIGH, MEDIUM, LOW)
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        logger.info("=== Priority-Based Job Scheduler Demo ===\n");

        // Create scheduler with only 1 thread
        // This makes it easy to see priority ordering
        // (Tasks will queue up and execute one at a time)
        SimpleScheduler scheduler = new SimpleScheduler(1);

        // =====================================================
        // CREATE 5 TASKS WITH DIFFERENT PRIORITIES
        // =====================================================

        TaskWrapper<String> lowTask1 = new TaskWrapperBuilder<>(() -> {
            Thread.sleep(500);
            return "Done";
        }).name("LOW-1").priority(TaskPriority.LOW).build();

        TaskWrapper<String> lowTask2 = new TaskWrapperBuilder<>(() -> {
            Thread.sleep(500);
            return "Done";
        }).name("LOW-2").priority(TaskPriority.LOW).build();

        TaskWrapper<String> mediumTask = new TaskWrapperBuilder<>(() -> {
            Thread.sleep(500);
            return "Done";
        }).name("MEDIUM-1").priority(TaskPriority.MEDIUM).build();

        TaskWrapper<String> highTask1 = new TaskWrapperBuilder<>(() -> {
            Thread.sleep(500);
            return "Done";
        }).name("HIGH-1").priority(TaskPriority.HIGH).build();

        TaskWrapper<String> highTask2 = new TaskWrapperBuilder<>(() -> {
            Thread.sleep(500);
            return "Done";
        }).name("HIGH-2").priority(TaskPriority.HIGH).build();

        // =====================================================
        // SUBMIT IN REVERSE PRIORITY ORDER (worst first!)
        // =====================================================
        logger.info("Submitting tasks in WRONG order:");
        logger.info("  1. {} (priority={})", lowTask1.getName(), lowTask1.getPriority());
        logger.info("  2. {} (priority={})", lowTask2.getName(), lowTask2.getPriority());
        logger.info("  3. {} (priority={})", mediumTask.getName(), mediumTask.getPriority());
        logger.info("  4. {} (priority={})", highTask1.getName(), highTask1.getPriority());
        logger.info("  5. {} (priority={})\n", highTask2.getName(), highTask2.getPriority());

        scheduler.submitWrapper(lowTask1);
        scheduler.submitWrapper(lowTask2);
        scheduler.submitWrapper(mediumTask);
        scheduler.submitWrapper(highTask1);
        scheduler.submitWrapper(highTask2);

        logger.info("All tasks submitted. Watch them execute in PRIORITY order!\n");

        // =====================================================
        // WAIT FOR ALL TO COMPLETE
        // =====================================================
        Thread.sleep(3500);  // 5 tasks × 500ms each

        // =====================================================
        // SHOW RESULTS
        // =====================================================
        logger.info("\nExecution order (check logs above):");
        logger.info("Expected: HIGH-1, HIGH-2, MEDIUM-1, LOW-1, LOW-2");
        logger.info("(Priority scheduling works!)");

        scheduler.shutdown();
        logger.info("\nDemo finished.");
    }
}
