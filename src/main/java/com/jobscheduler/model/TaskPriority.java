package com.jobscheduler.model;

/**
 * Priority levels for task execution. Higher priority tasks are executed before
 * lower priority tasks when multiple tasks are waiting.
 */
public enum TaskPriority {
    /**
     * Low priority - execute when system resources are available
     */
    LOW(1),
    /**
     * Normal priority - standard execution order
     */
    MEDIUM(5),
    /**
     * High priority - execute as soon as possible
     */
    HIGH(10);

    private final int weight;

    TaskPriority(int weight) {
        this.weight = weight;
    }

    /**
     * Get the numeric weight of this priority. Higher weight = higher priority.
     *
     * @return the priority weight
     */
    public int getWeight() {
        return weight;
    }
}
