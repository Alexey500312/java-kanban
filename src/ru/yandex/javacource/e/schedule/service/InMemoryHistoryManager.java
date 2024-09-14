package ru.yandex.javacource.e.schedule.service;

import ru.yandex.javacource.e.schedule.model.Epic;
import ru.yandex.javacource.e.schedule.model.SubTask;
import ru.yandex.javacource.e.schedule.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final int HISTORY_SIZE;
    private final List<Task> history;

    public InMemoryHistoryManager() {
        HISTORY_SIZE = 10;
        history = new ArrayList<>(HISTORY_SIZE);
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        if (history.size() == HISTORY_SIZE) {
            history.remove(0);
        }

        if (task.getClass().equals(Epic.class)) {
            task = cloneEpic((Epic) task);
        } else if (task.getClass().equals(SubTask.class)) {
            task = cloneSubTask((SubTask) task);
        } else {
            task = cloneTask(task);
        }

        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public int getHistoryMaxSize() {
        return HISTORY_SIZE;
    }

    private Task cloneTask(Task task) {
        Task clone = new Task(task.getName(), task.getDescription(), task.getStatus());
        clone.setId(task.getId());

        return clone;
    }

    private Epic cloneEpic(Epic epic) {
        Epic clone = new Epic(epic.getName(), epic.getDescription());
        clone.setId(epic.getId());
        clone.setStatus(epic.getStatus());
        clone.getSubTasks().addAll(epic.getSubTasks());

        return clone;
    }

    private SubTask cloneSubTask(SubTask subTask) {
        SubTask clone = new SubTask(subTask.getName(), subTask.getDescription(), subTask.getStatus());
        clone.setId(subTask.getId());
        clone.setEpicId(subTask.getEpicId());

        return clone;
    }
}
