package ru.yandex.javacource.e.schedule.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Подзадача")
class SubTaskTest {
    private SubTask subTask;

    @BeforeEach
    public void init() {  //Подготовленная подзадача
        Epic epic = new Epic("Эпик 1", "Описание 1");
        epic.setId(1);

        subTask = new SubTask("Задача 1", "Описание 1", TaskStatus.NEW);
        subTask.setId(1);
        subTask.setEpicId(epic.getId());
    }

    @Test
    @DisplayName("Должна полностью совпадать")
    public void shouldEqualsSubTask() {
        Epic epic = new Epic("Эпик 1", "Описание 1");
        epic.setId(1);
        SubTask subTaskCopy = new SubTask("Задача 1", "Описание 1", TaskStatus.NEW);
        subTaskCopy.setId(1);
        subTaskCopy.setEpicId(epic.getId());

        assertEquals(subTask.toString(), subTaskCopy.toString());
    }

    @Test
    @DisplayName("Должен отличаться ИД")
    public void shouldNegativeId() {
        SubTask subTaskCopy = new SubTask("Задача 1", "Описание 1", TaskStatus.NEW);
        subTaskCopy.setId(2);

        assertNotEquals(subTask.getId(), subTaskCopy.getId(), "ИД не должен совпадать");
    }

    @Test
    @DisplayName("Должно отличаться имя")
    public void shouldNegativeName() {
        SubTask subTaskCopy = new SubTask("Задача 2", "Описание 1", TaskStatus.NEW);
        subTaskCopy.setId(1);

        assertNotEquals(subTask.getName(), subTaskCopy.getName(), "Имя не должно совпадть");
    }

    @Test
    @DisplayName("Должно отличаться описание")
    public void shouldNegativeDescription() {
        SubTask subTaskCopy = new SubTask("Задача 1", "Описание 2", TaskStatus.NEW);
        subTaskCopy.setId(1);

        assertNotEquals(subTask.getDescription(), subTaskCopy.getDescription(), "Описание не должно совпадать");
    }

    @Test
    @DisplayName("Должен отличаться статус")
    public void shouldNegativeStatus() {
        SubTask subTaskCopy = new SubTask("Задача 1", "Описание 1", TaskStatus.IN_PROGRESS);
        subTaskCopy.setId(1);

        assertNotEquals(subTask.getStatus().name(), subTaskCopy.getStatus().name(), "Статус не должен совпадать");
    }

    @Test
    @DisplayName("Должен отличаться эпик")
    public void shouldNegativeEpic() {
        Epic epic = new Epic("Эпик 2", "Описание 2");
        epic.setId(2);
        SubTask subTaskCopy = new SubTask("Задача 1", "Описание 1", TaskStatus.NEW);
        subTaskCopy.setId(1);
        subTaskCopy.setEpicId(epic.getId());
        epic.addSubTask(subTaskCopy);

        assertNotEquals(subTask.getEpicId(), subTaskCopy.getEpicId(), "Эпик не должен совпадать");
    }
}