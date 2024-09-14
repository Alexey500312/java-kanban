package ru.yandex.javacource.e.schedule.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.e.schedule.model.Epic;
import ru.yandex.javacource.e.schedule.model.SubTask;
import ru.yandex.javacource.e.schedule.model.Task;
import ru.yandex.javacource.e.schedule.model.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Менеджер истории")
class InMemoryHistoryManagerTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    public void init() {
        taskManager = Managers.getDefault();
        historyManager = taskManager.getHistoryManager();
        Task task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        taskManager.createTask(task);
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic);
        taskManager.addNewSubTask(subTask);
    }

    @Test
    @DisplayName("Добавление в историю")
    void shouldAdd() {
        int countHistory = historyManager.getHistory().size();

        Task task = taskManager.getTask(1);

        assertEquals(
                countHistory + 1,
                historyManager.getHistory().size(),
                "Количество записей в истории не увеличелось");

        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);
        taskManager.getTask(1);
        countHistory = historyManager.getHistory().size();

        assertNotEquals(
                historyManager.getHistory().get(countHistory - 2).toString(),
                historyManager.getHistory().get(countHistory - 1).toString(),
                "После изменения задачи в истории совпадают");

        for (int i = 0; i < 20; i++) {
            taskManager.getTask(1);
        }

        assertEquals(
                historyManager.getHistoryMaxSize(),
                historyManager.getHistory().size(),
                "Размер истории превышает максимум");

        Epic epic = taskManager.getEpic(2);
        countHistory = historyManager.getHistory().size();

        assertEquals(
                epic.toString(),
                historyManager.getHistory().get(countHistory - 1).toString(),
                "Эпик не соответствует последней записи в истории");

        SubTask subTask = taskManager.getSubTask(3);
        countHistory = historyManager.getHistory().size();

        assertEquals(
                subTask.toString(),
                historyManager.getHistory().get(countHistory - 1).toString(),
                "Подзадача не соответствует последней записи в истории");
    }

    @Test
    @DisplayName("Получение истории")
    void shouldGetHistory() {
        assertNotNull(historyManager.getHistory(), "Нет истории");
    }
}