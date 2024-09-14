package ru.yandex.javacource.e.schedule.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Эпик")
class EpicTest {
    private Epic epic;
    private Epic epicCopy;

    @BeforeEach
    public void init() {
        //Подготовленный эпик
        epic = new Epic("Эпик 1", "Описание 1");
        epic.setId(1);
        SubTask subTask = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW);
        subTask.setId(2);
        subTask.setEpicId(epic.getId());
        epic.addSubTask(subTask);

        //Копия эпика
        epicCopy = new Epic("Эпик 1", "Описание 1");
        epicCopy.setId(1);
        SubTask subTaskCopy = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW);
        subTaskCopy.setId(2);
        subTaskCopy.setEpicId(epicCopy.getId());
        epicCopy.addSubTask(subTaskCopy);
    }

    @Test
    @DisplayName("Должен полностью совпадать")
    public void shouldEqualsEpic() {
        assertEquals(epic.toString(), epicCopy.toString());
        assertArrayEquals(epic.getSubTasks().toArray(), epicCopy.getSubTasks().toArray());
    }

    @Test
    @DisplayName("Должен отличаться ИД")
    public void shouldNegativeId() {
        epicCopy.setId(2);

        assertNotEquals(epic.getId(), epicCopy.getId(), "ИД не должен совпадать");
    }

    @Test
    @DisplayName("Должно отличаться имя")
    public void shouldNegativeName() {
        epicCopy.setName("Эпик 2");

        assertNotEquals(epic.getName(), epicCopy.getName(), "Имя не должно совпадть");
    }

    @Test
    @DisplayName("Должно отличаться описание")
    public void shouldNegativeDescription() {
        epicCopy.setDescription("Описание 2");

        assertNotEquals(epic.getDescription(), epicCopy.getDescription(), "Описание не должно совпадать");
    }

    @Test
    @DisplayName("Должен отличаться статус")
    public void shouldNegativeStatus() {
        epicCopy.setStatus(TaskStatus.IN_PROGRESS);

        assertNotEquals(epic.getStatus().name(), epicCopy.getStatus().name(), "Статус не должен совпадать");
    }

    @Test
    @DisplayName("Должны отличаться подзадачи")
    public void shouldNegativeSubTasks() {
        SubTask subTask = new SubTask("Подзадача 2", "Описание 2", TaskStatus.IN_PROGRESS);
        subTask.setId(3);
        subTask.setEpicId(epicCopy.getId());
        epicCopy.addSubTask(subTask);

        assertFalse(epic.getSubTasks().equals(epicCopy.getSubTasks()), "Подзадачи не должны совпадать");
    }

    @Test
    @DisplayName("Добавление подзадачи")
    public void shouldAddSubTask() {
        assertEquals(epic.getSubTasks().size(), 1);

        SubTask subTask = new SubTask("Подзадача 2", "Описание 2", TaskStatus.IN_PROGRESS);
        subTask.setId(3);
        subTask.setEpicId(epic.getId());
        epic.addSubTask(subTask);

        assertEquals(epic.getSubTasks().size(), 2, "Количество подзадач не увеличелось");
    }

    @Test
    @DisplayName("Удаление подзадачи")
    public void shouldRemoveSubTask() {
        assertEquals(epic.getSubTasks().size(), 1);

        epic.removeSubTask(  epic.getSubTasks().get(0));

        assertEquals(epic.getSubTasks().size(), 0, "Количество подзадач не уменьшилось");
    }

    @Test
    @DisplayName("Удаление всех подзадач")
    public void shouldRemoveAllSubTasks() {
        assertEquals(epic.getSubTasks().size(), 1);

        SubTask subTask = new SubTask("Подзадача 2", "Описание 2", TaskStatus.IN_PROGRESS);
        subTask.setId(3);
        subTask.setEpicId(epic.getId());
        epic.addSubTask(subTask);

        assertEquals(epic.getSubTasks().size(), 2);

        epic.removeAllSubTask();

        assertEquals(epic.getSubTasks().size(), 0, "Не удалены все подзадачи");
    }
}