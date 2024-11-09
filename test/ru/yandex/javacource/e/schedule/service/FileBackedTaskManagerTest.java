package ru.yandex.javacource.e.schedule.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.e.schedule.model.Epic;
import ru.yandex.javacource.e.schedule.model.SubTask;
import ru.yandex.javacource.e.schedule.model.Task;
import ru.yandex.javacource.e.schedule.model.TaskStatus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private Path path = null;

    @BeforeEach
    public void init() {
        try {
            path = Files.createTempFile("temp", ".csv");
        } catch (IOException ignored) {
        }
    }

    @AfterEach
    public void delete() {
        if (path != null) {
            path.toFile().deleteOnExit();
        }
    }

    @Test
    @DisplayName("Сохранение без задач")
    public void shouldSaveWithoutTasks() {
        assertEquals(path.toFile().length(), 0, "Файл не пустой");

        FileBackedTaskManager manager = new FileBackedTaskManager(path);
        manager.save();
        int lines = getCountFileLines(path);

        assertEquals(lines, 1, "Количество строк в файле не равно 1");
    }

    @Test
    @DisplayName("Сохранение нескольких задач")
    public void shouldSaveTasks() {
        assertEquals(path.toFile().length(), 0, "Файл не пустой");

        FileBackedTaskManager manager = new FileBackedTaskManager(path);
        manager.createTask(new Task("Task 1", "Task description", TaskStatus.NEW));
        Epic epic = manager.createEpic(new Epic("Epic 1", "Epic description"));
        manager.addNewSubTask(new SubTask("SubTask 1", "SubTask description", TaskStatus.NEW, epic));
        int lines = getCountFileLines(path);

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
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(path);

        assertEquals(manager.tasks.size(), 0, "Колиичество задач больше 0");
        assertEquals(manager.epics.size(), 0, "Колиичество эпиков больше 0");
        assertEquals(manager.subTasks.size(), 0, "Колиичество подзадач больше 0");
    }

    @Test
    @DisplayName("Загрузка из заполненного файла")
    public void shouldLoadFromFile() {
        FileBackedTaskManager manager = new FileBackedTaskManager(path);
        manager.createTask(new Task("Task 1", "Task description", TaskStatus.NEW));
        Epic epic = manager.createEpic(new Epic("Epic 1", "Epic description"));
        manager.addNewSubTask(new SubTask("SubTask 1", "SubTask description", TaskStatus.NEW, epic));

        assertNotEquals(path.toFile().length(), 0, "Файл пустой");

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(path);

        assertNotEquals(newManager.tasks.size(), 0, "Колиичество задач 0");
        assertNotEquals(newManager.epics.size(), 0, "Колиичество эпиков 0");
        assertNotEquals(newManager.subTasks.size(), 0, "Колиичество подзадач 0");
    }
}
