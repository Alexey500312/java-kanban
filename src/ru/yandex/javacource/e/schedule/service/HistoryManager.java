package ru.yandex.javacource.e.schedule.service;

import ru.yandex.javacource.e.schedule.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();

    int getHistoryMaxSize();
}
