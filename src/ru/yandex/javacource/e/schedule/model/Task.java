package ru.yandex.javacource.e.schedule.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private static final Duration DURATION_DEFAULT = Duration.ofMinutes(10);
    private static final LocalDateTime START_TIME_DEFAULT = LocalDateTime.now();
    public static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private Integer id;
    private String name;
    private String description;
    private TaskStatus status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.duration = DURATION_DEFAULT;
        this.startTime = START_TIME_DEFAULT;
    }

    public Task(String name, String description, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.duration = duration != null ? duration : DURATION_DEFAULT;
        this.startTime = startTime != null ? startTime : START_TIME_DEFAULT;
    }

    public Task(String name, String description, TaskStatus status) {
        this(name, description);
        this.status = status;
    }

    public Task(String name, String description, Duration duration, LocalDateTime startTime, TaskStatus status) {
        this(name, description, duration, startTime);
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

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return duration != null && startTime != null ? startTime.plus(duration) : null;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + "{"
                + "id=" + this.getId() + ", "
                + "name='" + this.getName() + '\'' + ", "
                + "description='" + this.getDescription() + '\'' + ", "
                + "status='" + this.getStatus().getStatusText() + '\'' + ", "
                + "duration=" + (this.duration != null ? this.duration.toMinutes() : "") + ", "
                + "startTime='" + (this.startTime != null ? this.startTime.format(FORMATTER) : "") + '\''
                + "}";
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (this.getClass() != obj.getClass()) return false;
        Task baseTask = (Task) obj;
        return this.id.equals(baseTask.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(id);
    }
}
