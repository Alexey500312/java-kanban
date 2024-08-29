package ru.yandex.model;

import java.util.Objects;

public abstract class BaseTask {
    private Integer id;
    private String name;
    private String description;
    private TaskStatus status;

    private BaseTask() {
        id = Integer.valueOf(SequenceTask.generateId());
    }

    public BaseTask(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    protected BaseTask(String name, String description, TaskStatus status) {
        this(name, description);
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    protected void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public final String toString() {
        return this.getClass().getSimpleName()
                + "{"
                + "id=" + id + ", "
                + "name='" + name + '\'' + ", "
                + "description='" + description + '\'' + ", "
                + "status='" + status.getStatusText() + '\''
                + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (this == null || this.getClass() != obj.getClass()) return false;
        BaseTask baseTask = (BaseTask) obj;
        return this.id.equals(baseTask.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
