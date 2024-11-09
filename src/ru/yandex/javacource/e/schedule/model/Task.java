package ru.yandex.javacource.e.schedule.model;

import java.util.Objects;

public class Task {
    private Integer id;
    private String name;
    private String description;
    private TaskStatus status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, TaskStatus status) {
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

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Integer getEpicId() {
        return null;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + "{"
                + "id=" + this.getId() + ", "
                + "name='" + this.getName() + '\'' + ", "
                + "description='" + this.getDescription() + '\'' + ", "
                + "status='" + this.getStatus().getStatusText() + '\''
                + "}";
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (this == null || this.getClass() != obj.getClass()) return false;
        Task baseTask = (Task) obj;
        return this.id.equals(baseTask.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(id);
    }
}
