package ru.yandex.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends BaseTask {
    private List<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description);
        this.setStatus(TaskStatus.NEW);
        subTasks = new ArrayList<>();
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public boolean addSubtask(SubTask subTask) {
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

    public void calcEpicStatus() {
        if (subTasks.size() == 0) {
            this.setStatus(TaskStatus.NEW);
            return;
        }

        int[] countStatus = {0, 0, 0};
        for (SubTask subTask : subTasks) {
            switch (subTask.getStatus()) {
                case NEW:
                    countStatus[0]++;
                    break;
                case IN_PROGRESS:
                    countStatus[1]++;
                    break;
                case DONE:
                    countStatus[2]++;
                    break;
            }
        }
        if (countStatus[0] > 0 && countStatus[1] == 0 && countStatus[2] == 0) {
            this.setStatus(TaskStatus.NEW);
        } else if (countStatus[0] == 0 && countStatus[1] == 0 && countStatus[2] > 0) {
            this.setStatus(TaskStatus.DONE);
        } else {
            this.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
