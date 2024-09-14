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

        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
