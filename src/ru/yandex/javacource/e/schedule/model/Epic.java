package ru.yandex.javacource.e.schedule.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTasks;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        setStatus(TaskStatus.NEW);
        setDuration(null);
        setStartTime(null);
        subTasks = new ArrayList<>();
    }

    public List<Integer> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<Integer> subTasks) {
        this.subTasks = subTasks;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean addSubTask(SubTask subTask) {
        if (subTasks.contains(subTask.getId()))
            return false;

        if (subTask.getEpicId() == null)
            return false;

        subTasks.add(subTask.getId());
        return true;
    }

    public boolean removeSubTask(Integer subTaskId) {
        int id = subTasks.indexOf(subTaskId);
        return id >= 0 && subTasks.remove(id) != null;
    }

    public void removeAllSubTask() {
        subTasks.clear();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + "{"
                + "id=" + this.getId() + ", "
                + "name='" + this.getName() + '\'' + ", "
                + "description='" + this.getDescription() + '\'' + ", "
                + "status='" + this.getStatus().getStatusText() + '\'' + ", "
                + "countSubTasks = " + this.getSubTasks().size() + ", "
                + "duration=" + (this.getDuration() != null ? this.getDuration().toMinutes() : "") + ", "
                + "startTime='" + (this.getStartTime() != null ? this.getStartTime().format(FORMATTER) : "") + '\''
                + "}";
    }
}
