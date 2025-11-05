package com.jobscheduler.task;

import com.jobscheduler.model.TaskPriority;
import com.jobscheduler.model.TaskStatus;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Wraps a Task with metadata needed for scheduling and execution.
 * This class is thread-safe for status updates.
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

    private TaskWrapper(Builder<T> builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.task = builder.task;
        this.priority = builder.priority;
        this.creationTime = Instant.now();
        this.dependencies = new HashSet<>(builder.dependencies);
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

    // Builder pattern for flexible task creation
    public static <T> Builder<T> builder(Task<T> task) {
        return new Builder<>(task);
    }

    public static class Builder<T> {
        private final Task<T> task;
        private String id;
        private String name;
        private TaskPriority priority = TaskPriority.MEDIUM;
        private final Set<String> dependencies = new HashSet<>();

        private Builder(Task<T> task) {
            if (task == null) {
                throw new IllegalArgumentException("Task cannot be null");
            }
            this.task = task;
            this.id = UUID.randomUUID().toString();
            this.name = "Task-" + this.id.substring(0, 8);
        }

        public Builder<T> id(String id) {
            this.id = id;
            return this;
        }

        public Builder<T> name(String name) {
            this.name = name;
            return this;
        }

        public Builder<T> priority(TaskPriority priority) {
            this.priority = priority;
            return this;
        }

        public Builder<T> dependsOn(String taskId) {
            this.dependencies.add(taskId);
            return this;
        }

        public Builder<T> dependsOn(Set<String> taskIds) {
            this.dependencies.addAll(taskIds);
            return this;
        }

        public TaskWrapper<T> build() {
            return new TaskWrapper<>(this);
        }
    }
}
