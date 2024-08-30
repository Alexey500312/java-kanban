package ru.yandex.javacource.e.schedule.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description);
        setStatus(TaskStatus.NEW);
        subTasks = new ArrayList<>();
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public boolean addSubTask(SubTask subTask) {
        if (subTasks.contains(subTask))
            return false;

        if (subTask.getEpic() == null)
            return false;

        subTasks.add(subTask);
        return true;
    }

    public boolean removeSubTask(SubTask subTask) {
        int id = subTasks.indexOf(subTask);
        return id >= 0 && subTasks.remove(id) != null;
    }

    public void removeAllSubTask() {
        subTasks.clear();
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
