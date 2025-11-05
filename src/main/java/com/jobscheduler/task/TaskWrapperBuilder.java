package com.jobscheduler.task;

import com.jobscheduler.model.TaskPriority;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Builder for creating TaskWrapper instances with fluent API. Provides
 * convenient way to configure task metadata.
 *
 * @param <T> the type of result the task produces
 */
public class TaskWrapperBuilder<T> {

    private final Task<T> task;
    private String id;
    private String name;
    private TaskPriority priority = TaskPriority.MEDIUM;
    private final Set<String> dependencies = new HashSet<>();

    public TaskWrapperBuilder(Task<T> task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        this.task = task;
        this.id = UUID.randomUUID().toString();
        this.name = "Task-" + this.id.substring(0, 8);
    }

    public TaskWrapperBuilder<T> id(String id) {
        this.id = id;
        return this;
    }

    public TaskWrapperBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    public TaskWrapperBuilder<T> priority(TaskPriority priority) {
        this.priority = priority;
        return this;
    }

    public TaskWrapperBuilder<T> dependsOn(String taskId) {
        this.dependencies.add(taskId);
        return this;
    }

    public TaskWrapperBuilder<T> dependsOn(Set<String> taskIds) {
        this.dependencies.addAll(taskIds);
        return this;
    }

    public TaskWrapper<T> build() {
        return new TaskWrapper<>(task, id, name, priority, dependencies);
    }
}
