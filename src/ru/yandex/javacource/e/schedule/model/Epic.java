package ru.yandex.javacource.e.schedule.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTasks;

    public Epic(String name, String description) {
        super(name, description);
        setStatus(TaskStatus.NEW);
        subTasks = new ArrayList<>();
    }

    public List<Integer> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<Integer> subTasks) {
        this.subTasks = subTasks;
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
                + "countSubTasks = " + this.getSubTasks().size()
                + "}";
    }
}
