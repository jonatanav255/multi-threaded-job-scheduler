package com.jobscheduler;

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

        // TODO: Initialize scheduler and console interface

        logger.info("Application ready. Type 'help' for available commands.");
    }
}
