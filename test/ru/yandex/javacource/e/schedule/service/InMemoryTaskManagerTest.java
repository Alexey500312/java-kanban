package ru.yandex.javacource.e.schedule.service;

import org.junit.jupiter.api.DisplayName;

@DisplayName("Менеджер задач")
class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    public InMemoryTaskManager createTaskManager() {
        HistoryManager historyManager = new EmptyHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
        return taskManager;
    }
}