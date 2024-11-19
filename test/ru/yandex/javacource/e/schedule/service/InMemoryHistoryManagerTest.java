package ru.yandex.javacource.e.schedule.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.e.schedule.model.Epic;
import ru.yandex.javacource.e.schedule.model.SubTask;
import ru.yandex.javacource.e.schedule.model.Task;
import ru.yandex.javacource.e.schedule.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Менеджер истории")
class InMemoryHistoryManagerTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    public void init() {
        historyManager = Managers.getDefaultHistory();
        taskManager = new InMemoryTaskManager(historyManager);
        Task task = new Task(
                "Задача 1",
                "Описание задачи 1",
                Duration.ofMinutes(10),
                LocalDateTime.parse("01.01.2025 09:00:00", Task.FORMATTER),
                TaskStatus.NEW);
        taskManager.createTask(task);
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask(
                "Подзадача 1",
                "Описание подзадачи 1",
                Duration.ofMinutes(10),
                LocalDateTime.parse("01.01.2025 10:00:00", Task.FORMATTER),
                TaskStatus.NEW,
                epic);
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

        countHistory = historyManager.getHistory().size();
        assertEquals(
                task.toString(),
                historyManager.getHistory().get(countHistory - 1).toString(),
                "Задача не соответствует последней записи в истории");

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
    @DisplayName("Удаление из истории")
    void shouldLinkHistory() {
        taskManager.getTask(1);
        taskManager.getEpic(2);
        taskManager.getSubTask(3);

        historyManager.remove(3);

        assertEquals(2, historyManager.getHistory().size(), "Запись из истории не удалена");
    }

    @Test
    @DisplayName("Повторение задач")
    void shouldRepeatTasksHistory() {
        taskManager.getTask(1);
        taskManager.getEpic(2);
        taskManager.getSubTask(3);

        int historySize = historyManager.getHistory().size();

        taskManager.getTask(1);
        taskManager.getSubTask(3);
        taskManager.getEpic(2);
        taskManager.getTask(1);
        taskManager.getSubTask(3);
        taskManager.getEpic(2);

        int newHistorySize = historyManager.getHistory().size();

        assertEquals(historySize, newHistorySize, "Задачи в истории повторяются");
    }

    @Test
    @DisplayName("Получение истории")
    void shouldGetHistory() {
        assertNotNull(historyManager.getHistory(), "Нет истории");
    }
}