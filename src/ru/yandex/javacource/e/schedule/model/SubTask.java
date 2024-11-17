package ru.yandex.javacource.e.schedule.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public SubTask(String name, String description, Duration duration, LocalDateTime startTime, TaskStatus status) {
        super(name, description, duration, startTime, status);
    }

    public SubTask(String name, String description, TaskStatus status, Epic epic) {
        this(name, description, status);
        this.epicId = epic.getId();
    }

    public SubTask(String name, String description, Duration duration, LocalDateTime startTime, TaskStatus status, Epic epic) {
        this(name, description, duration, startTime, status);
        this.epicId = epic.getId();
    }

    public SubTask(String name, String description, TaskStatus status, Integer epicId) {
        this(name, description, status);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, Duration duration, LocalDateTime startTime, TaskStatus status, Integer epicId) {
        this(name, description, duration, startTime, status);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        if (epicId == null)
            return;

        this.epicId = epicId;
    }

    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + "{"
                + "id=" + this.getId() + ", "
                + "name='" + this.getName() + '\'' + ", "
                + "description='" + this.getDescription() + '\'' + ", "
                + "status='" + this.getStatus().getStatusText() + '\'' + ", "
                + "epicId = " + (this.getEpicId() != null ? this.epicId : null) + ", "
                + "duration=" + (this.getDuration() != null ? this.getDuration().toMinutes() : "") + ", "
                + "startTime='" + (this.getStartTime() != null ? this.getStartTime().format(FORMATTER) : "") + '\''
                + "}";
    }
}
