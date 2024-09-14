package ru.yandex.javacource.e.schedule.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Задача")
class TaskTest {
    private Task task;

    @BeforeEach
    public void init() {  //Подготовленная задача
        task = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        task.setId(1);
    }

    @Test
    @DisplayName("Должна полностью совпадать")
    public void shouldEqualsTask() {
        Task taskCopy = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        taskCopy.setId(1);

        assertEquals(task.toString(), taskCopy.toString());
    }

    @Test
    @DisplayName("Должен отличаться ИД")
    public void shouldNegativeId() {
        Task taskCopy = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        taskCopy.setId(2);

        assertNotEquals(task.getId(), taskCopy.getId(), "ИД не должен совпадать");
    }

    @Test
    @DisplayName("Должно отличаться имя")
    public void shouldNegativeName() {
        Task taskCopy = new Task("Задача 2", "Описание 1", TaskStatus.NEW);
        taskCopy.setId(1);

        assertNotEquals(task.getName(), taskCopy.getName(), "Имя не должно совпадть");
    }

    @Test
    @DisplayName("Должно отличаться описание")
    public void shouldNegativeDescription() {
        Task taskCopy = new Task("Задача 1", "Описание 2", TaskStatus.NEW);
        taskCopy.setId(1);

        assertNotEquals(task.getDescription(), taskCopy.getDescription(), "Описание не должно совпадать");
    }

    @Test
    @DisplayName("Должен отличаться статус")
    public void shouldNegativeStatus() {
        Task taskCopy = new Task("Задача 1", "Описание 1", TaskStatus.IN_PROGRESS);
        taskCopy.setId(1);

        assertNotEquals(task.getStatus().name(), taskCopy.getStatus().name(), "Статус не должен совпадать");
    }
}