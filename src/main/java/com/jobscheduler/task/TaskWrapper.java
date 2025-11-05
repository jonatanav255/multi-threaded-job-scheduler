package com.jobscheduler.task;

import com.jobscheduler.model.TaskPriority;
import com.jobscheduler.model.TaskStatus;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Wraps a Task with metadata needed for scheduling and execution. This class is
 * thread-safe for status updates.
 *
 * @param <T> the type of result the task produces
 */
public class TaskWrapper<T> {

    private final String id;
    private final String name;
    private final Task<T> task;
    private final TaskPriority priority;
    private final Instant creationTime;
    private final Set<String> dependencies;

    // Thread-safe status using AtomicReference
    private final AtomicReference<TaskStatus> status;

    // Execution tracking
    private Instant startTime;
    private Instant endTime;
    private T result;
    private Exception exception;
    private int retryCount;

    // Package-private constructor for builder
    TaskWrapper(Task<T> task, String id, String name, TaskPriority priority, Set<String> dependencies) {
        this.task = task;
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.creationTime = Instant.now();
        this.dependencies = new HashSet<>(dependencies);
        this.status = new AtomicReference<>(TaskStatus.PENDING);
        this.retryCount = 0;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Task<T> getTask() {
        return task;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public TaskStatus getStatus() {
        return status.get();
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public T getResult() {
        return result;
    }

    public Exception getException() {
        return exception;
    }

    public Set<String> getDependencies() {
        return new HashSet<>(dependencies);
    }

    public int getRetryCount() {
        return retryCount;
    }

    // Status management (thread-safe)
    public boolean updateStatus(TaskStatus expectedStatus, TaskStatus newStatus) {
        return status.compareAndSet(expectedStatus, newStatus);
    }

    public void setStatus(TaskStatus newStatus) {
        status.set(newStatus);
    }

    // Execution tracking
    public void markStarted() {
        this.startTime = Instant.now();
        setStatus(TaskStatus.RUNNING);
    }

    public void markCompleted(T result) {
        this.endTime = Instant.now();
        this.result = result;
        setStatus(TaskStatus.COMPLETED);
    }

    public void markFailed(Exception exception) {
        this.endTime = Instant.now();
        this.exception = exception;
        setStatus(TaskStatus.FAILED);
    }

    public void markCancelled() {
        this.endTime = Instant.now();
        setStatus(TaskStatus.CANCELLED);
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    @Override
    public String toString() {
        return String.format("Task[id=%s, name=%s, priority=%s, status=%s]",
                id, name, priority, status.get());
    }
}
