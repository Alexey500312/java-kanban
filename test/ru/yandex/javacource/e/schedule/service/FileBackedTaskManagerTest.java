package ru.yandex.javacource.e.schedule.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.e.schedule.exception.ManagerSaveException;
import ru.yandex.javacource.e.schedule.model.Epic;
import ru.yandex.javacource.e.schedule.model.SubTask;
import ru.yandex.javacource.e.schedule.model.Task;
import ru.yandex.javacource.e.schedule.model.TaskStatus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private HistoryManager historyManager;
    private Path path;
    private Path emptyPath;

    @Override
    public FileBackedTaskManager createTaskManager() {
        try {
            path = Files.createTempFile("temp", ".csv");
            emptyPath = Files.createTempFile("tempEmpty", ".csv");
        } catch (IOException ignored) {
        }
        historyManager = new EmptyHistoryManager();
        taskManager = new FileBackedTaskManager(historyManager, path);
        return taskManager;
    }

    @AfterEach
    public void delete() {
        if (path != null) {
            path.toFile().deleteOnExit();
        }
        if (emptyPath != null) {
            emptyPath.toFile().deleteOnExit();
        }
    }

    @Test
    @DisplayName("Сохранение без задач")
    public void shouldSaveWithoutTasks() {
        assertEquals(emptyPath.toFile().length(), 0, "Файл не пустой");

        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, emptyPath);
        manager.save();
        int lines = getCountFileLines(emptyPath);

        assertEquals(lines, 1, "Количество строк в файле не равно 1");
    }

    @Test
    @DisplayName("Сохранение нескольких задач")
    public void shouldSaveTasks() {
        assertEquals(emptyPath.toFile().length(), 0, "Файл не пустой");

        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, emptyPath);
        manager.createTask(new Task(
                "Task 1",
                "Task description",
                Duration.ofMinutes(10),
                LocalDateTime.parse("01.01.2025 10:00:00", Task.FORMATTER),
                TaskStatus.NEW));
        Epic epic = manager.createEpic(new Epic("Epic 1", "Epic description"));
        manager.addNewSubTask(new SubTask(
                "SubTask 1",
                "SubTask description",
                Duration.ofMinutes(10),
                LocalDateTime.parse("01.01.2025 11:00:00", Task.FORMATTER),
                TaskStatus.NEW,
                epic));
        int lines = getCountFileLines(emptyPath);

        assertEquals(lines, 4, "Количество строк в файле не равно 4");
    }

    private int getCountFileLines(Path path) {
        int lines = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            while (br.readLine() != null) {
                lines++;
            }
        } catch (IOException ignored) {
        }

        return lines;
    }

    @Test
    @DisplayName("Загрузка из пустого файла")
    public void shouldLoadFromEmptyFile() {

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(historyManager, emptyPath);

        assertEquals(manager.tasks.size(), 0, "Колиичество задач больше 0");
        assertEquals(manager.epics.size(), 0, "Колиичество эпиков больше 0");
        assertEquals(manager.subTasks.size(), 0, "Колиичество подзадач больше 0");
    }

    @Test
    @DisplayName("Загрузка из заполненного файла")
    public void shouldLoadFromFile() {
        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, emptyPath);
        manager.createTask(new Task(
                "Task 1",
                "Task description",
                Duration.ofMinutes(10),
                LocalDateTime.parse("01.01.2025 10:00:00", Task.FORMATTER),
                TaskStatus.NEW));
        Epic epic = manager.createEpic(new Epic("Epic 1", "Epic description"));
        manager.addNewSubTask(new SubTask(
                "SubTask 1",
                "SubTask description",
                Duration.ofMinutes(10),
                LocalDateTime.parse("01.01.2025 11:00:00", Task.FORMATTER),
                TaskStatus.NEW,
                epic));

        assertNotEquals(path.toFile().length(), 0, "Файл пустой");

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(emptyPath);

        assertNotEquals(newManager.tasks.size(), 0, "Колиичество задач 0");
        assertNotEquals(newManager.epics.size(), 0, "Колиичество эпиков 0");
        assertNotEquals(newManager.subTasks.size(), 0, "Колиичество подзадач 0");
    }

    @Test
    @DisplayName("Проверка исключения")
    public void shouldException() {
        assertThrows(
                ManagerSaveException.class,
                () -> {
                    Path noFile = Paths.get("/NoDirectory/NoFile.csv");
                    FileBackedTaskManager.loadFromFile(historyManager, noFile);
                },
                "Ошибка чтения, файл не существует!");

        assertThrows(
                ManagerSaveException.class,
                () -> {
                    Path noFile = Paths.get("/NoDirectory/NoFile.csv");
                    FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, noFile);
                    manager.save();
                },
                "Ошибка записи, файл не существует!");
    }
}
