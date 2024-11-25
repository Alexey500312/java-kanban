package ru.yandex.javacource.e.schedule.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Item {
    private Integer id;
    private String name;
    private String description;
    private TaskStatus status;
    private Duration duration;
    private LocalDateTime startTime;
    private Integer epicId;

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

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Integer getEpicId() {
        return epicId;
    }
}
