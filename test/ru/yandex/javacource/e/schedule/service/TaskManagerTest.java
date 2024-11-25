package ru.yandex.javacource.e.schedule.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.e.schedule.exception.TaskNotFoundException;
import ru.yandex.javacource.e.schedule.exception.TaskValidationException;
import ru.yandex.javacource.e.schedule.model.Epic;
import ru.yandex.javacource.e.schedule.model.SubTask;
import ru.yandex.javacource.e.schedule.model.Task;
import ru.yandex.javacource.e.schedule.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    public void init() {
        taskManager = createTaskManager();
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
    @DisplayName("Добавление задачи")
    public void shouldCreateTask() {
        assertEquals(1, taskManager.getAllTasks().size());

        Task task = new Task(
                "Задача 2",
                "Описание задачи 2",
                Duration.ofMinutes(10),
                LocalDateTime.parse("01.01.2025 11:00:00", Task.FORMATTER),
                TaskStatus.NEW);
        task = taskManager.createTask(task);

        assertEquals(2, taskManager.getAllTasks().size(), "Количество задач не увеличилось");
        assertEquals(task.toString(), taskManager.getTask(task.getId()).toString(), "Задача не совпадает");
    }

    @Test
    @DisplayName("Добавление эпика")
    public void shouldCreateEpic() {
        assertEquals(1, taskManager.getAllEpics().size());

        Epic epic = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask(
                "Подзадача 2",
                "Описание подзадачи 2",
                Duration.ofMinutes(10),
                LocalDateTime.parse("01.01.2025 11:00:00", Task.FORMATTER),
                TaskStatus.NEW,
                epic);
        taskManager.addNewSubTask(subTask);

        assertEquals(2, taskManager.getAllEpics().size(), "Количество эпиков не увеличилось");
        assertEquals(epic.toString(), taskManager.getEpic(epic.getId()).toString(), "Эпик не совпадает");
        assertArrayEquals(
                epic.getSubTasks().toArray(),
                taskManager.getAllEpics().get(1).getSubTasks().toArray(),
                "Подзадачи эпика не совпадают");
    }

    @Test
    @DisplayName("Добавление подзадачи")
    public void shouldAddSubTask() {
        assertEquals(1, taskManager.getAllSubTasks().size());

        SubTask subTask = new SubTask(
                "Подзадача 2",
                "Описание подзадачи 2",
                Duration.ofMinutes(10),
                LocalDateTime.parse("01.01.2025 11:00:00", Task.FORMATTER),
                TaskStatus.NEW,
                taskManager.getAllEpics().getFirst());
        taskManager.addNewSubTask(subTask);

        assertEquals(2, taskManager.getAllSubTasks().size(), "Количество подзадач не увеличилось");
        assertEquals(
                subTask.toString(),
                taskManager.getSubTask(subTask.getId()).toString(),
                "Подзадача не совпадает");
    }

    @Test
    @DisplayName("Получение задачи")
    public void shouldGetTask() {
        Task task = new Task(
                "Задача 2",
                "Описание задачи 2",
                Duration.ofMinutes(10),
                LocalDateTime.parse("01.01.2025 11:00:00", Task.FORMATTER),
                TaskStatus.NEW);
        task = taskManager.createTask(task);

        assertNotNull(taskManager.getTask(task.getId()), "Задача не получена");
        assertEquals(task.toString(), taskManager.getTask(task.getId()).toString(), "Задачи не совпадают");
    }

    @Test
    @DisplayName("Получение эпика")
    public void shouldGetEpic() {
        Epic epic = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.createEpic(epic);

        assertNotNull(taskManager.getEpic(epic.getId()), "Эпик не получен");
        assertEquals(epic.toString(), taskManager.getEpic(epic.getId()).toString(), "Эпики не совпадают");
    }

    @Test
    @DisplayName("Получение подзадачи")
    public void shouldGetSubTask() {
        SubTask subTask = new SubTask(
                "Подзадача 2",
                "Описание подзадачи 2",
                Duration.ofMinutes(10),
                LocalDateTime.parse("01.01.2025 11:00:00", Task.FORMATTER),
                TaskStatus.NEW,
                taskManager.getAllEpics().getFirst());
        taskManager.addNewSubTask(subTask);

        assertNotNull(taskManager.getSubTask(subTask.getId()), "Подзадача не получен");
        assertEquals(subTask.toString(), taskManager.getSubTask(subTask.getId()).toString(), "Подзадачи не совпадают");
    }

    @Test
    @DisplayName("Получение всех задач")
    public void shouldGetAllTasks() {
        assertNotNull(taskManager.getAllTasks(), "Нет списка задач");
        assertNotEquals(0, taskManager.getAllTasks().size(), "Список задач пустой");
    }

    @Test
    @DisplayName("Получение всех эпиков")
    public void shouldGetAllEpics() {
        assertNotNull(taskManager.getAllEpics(), "Нет списка эпиков");
        assertNotEquals(0, taskManager.getAllEpics().size(), "Список эпиков пустой");
    }

    @Test
    @DisplayName("Получение всех подзадач")
    public void shouldGetAllSubTasks() {
        assertNotNull(taskManager.getAllSubTasks(), "Нет списка подзадач");
        assertNotEquals(0, taskManager.getAllSubTasks().size(), "Список подзадач пустой");
    }

    @Test
    @DisplayName("Удаление задачи")
    public void shouldRemoveTask() {
        assertEquals(1, taskManager.getAllTasks().size());

        Task task = taskManager.getAllTasks().getFirst();
        taskManager.removeTask(task.getId());

        assertEquals(0, taskManager.getAllTasks().size(), "Задача не удалена");
    }

    @Test
    @DisplayName("Удаление эпика")
    public void shouldRemoveEpic() {
        assertEquals(1, taskManager.getAllEpics().size());

        Epic epic = taskManager.getAllEpics().getFirst();
        taskManager.removeEpic(epic.getId());

        assertEquals(0, taskManager.getAllEpics().size(), "Эпик не удален");
    }

    @Test
    @DisplayName("Удаление подзадачи")
    public void shouldRemoveSubTask() {
        assertEquals(1, taskManager.getAllSubTasks().size());

        SubTask subTask = taskManager.getAllSubTasks().getFirst();
        Epic epic = taskManager.getEpic(subTask.getEpicId());
        int epicCountSubTask = epic.getSubTasks().size();
        taskManager.removeSubTask(subTask.getId());
        epicCountSubTask--;

        assertEquals(0, taskManager.getAllSubTasks().size(), "Подзадача не удалена");
        assertEquals(epicCountSubTask, epic.getSubTasks().size(), "Подзадача не удалена из эпика");
    }

    @Test
    @DisplayName("Удаление всех задач")
    public void shouldRemoveAllTasks() {
        Task task = new Task(
                "Задача 2",
                "Описание задачи 2",
                Duration.ofMinutes(10),
                LocalDateTime.parse("01.01.2025 11:00:00", Task.FORMATTER),
                TaskStatus.NEW);
        taskManager.createTask(task);

        assertEquals(2, taskManager.getAllTasks().size());

        taskManager.removeAllTasks();

        assertEquals(0, taskManager.getAllTasks().size(), "Задачи остались");
    }

    @Test
    @DisplayName("Удаление всех эпиков")
    public void shouldRemoveAllEpics() {
        Epic epic = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask(
                "Подзадача 2",
                "Описание подзадачи 2",
                Duration.ofMinutes(10),
                LocalDateTime.parse("01.01.2025 11:00:00", Task.FORMATTER),
                TaskStatus.NEW,
                epic);
        taskManager.addNewSubTask(subTask);

        assertEquals(2, taskManager.getAllEpics().size());
        assertEquals(2, taskManager.getAllSubTasks().size());

        taskManager.removeAllEpics();

        assertEquals(0, taskManager.getAllSubTasks().size(), "Подзадачи остались");
        assertEquals(0, taskManager.getAllEpics().size(), "Эпики остались");
    }

    @Test
    @DisplayName("Удаление всех подзадач")
    public void shouldRemoveAllSubTasks() {
        SubTask subTask = new SubTask(
                "Подзадача 2",
                "Описание подзадачи 2",
                Duration.ofMinutes(10),
                LocalDateTime.parse("01.01.2025 11:00:00", Task.FORMATTER),
                TaskStatus.NEW,
                taskManager.getAllEpics().getFirst());
        taskManager.addNewSubTask(subTask);

        assertEquals(2, taskManager.getAllSubTasks().size());

        taskManager.removeAllSubTasks();

        assertEquals(0, taskManager.getAllSubTasks().size(), "Подзадачи остались");
    }

    @Test
    @DisplayName("Обновление задачи")
    public void shouldUpdateTask() {
        Task task = taskManager.getAllTasks().getFirst();
        task.setStatus(TaskStatus.DONE);

        taskManager.updateTask(task);

        assertEquals(
                task.toString(),
                taskManager.getTask(task.getId()).toString(),
                "После обновления, задачи отличаются");
    }

    @Test
    @DisplayName("Обновление эпика")
    public void shouldUpdateEpic() {
        Epic epic = taskManager.getAllEpics().getFirst();
        Epic newEpic = new Epic(epic.getName(), epic.getDescription());
        newEpic.setId(epic.getId());
        newEpic.addSubTask(taskManager.getSubTask(epic.getSubTasks().getFirst()));
        newEpic.setName("Измененное имя");

        taskManager.updateEpic(newEpic);

        assertEquals(
                newEpic.toString(),
                taskManager.getEpic(newEpic.getId()).toString(),
                "После обновления, эпики не изменились");

        Epic newEpicEmptySubTasks = new Epic(newEpic.getName(), newEpic.getDescription());
        newEpicEmptySubTasks.setId(newEpic.getId());
        newEpicEmptySubTasks.setStatus(newEpic.getStatus());

        taskManager.updateEpic(newEpicEmptySubTasks);

        assertEquals(newEpicEmptySubTasks.getSubTasks().size(),
                newEpic.getSubTasks().size(),
                "После обновления изменилось количество подзадач");
    }

    @Test
    @DisplayName("Обновление подзадачи")
    public void shouldUpdateSubTask() {
        SubTask subTask = taskManager.getAllSubTasks().getFirst();
        SubTask newSubTask = new SubTask(subTask.getName(), subTask.getDescription(), subTask.getStatus());
        newSubTask.setId(subTask.getId());
        newSubTask.setEpicId(subTask.getEpicId());
        newSubTask.setDescription("Измененное описание");

        assertNotEquals(
                newSubTask.toString(),
                taskManager.getSubTask(newSubTask.getId()).toString(),
                "После изменения, подзадачи совпадают");

        taskManager.updateSubTask(newSubTask);

        assertEquals(
                newSubTask.toString(),
                taskManager.getSubTask(newSubTask.getId()).toString(),
                "После обновления, подзадачи отличаются");

        Epic epic = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.createEpic(epic);
        SubTask newSubTaskChangeEpic = new SubTask(
                newSubTask.getName(),
                newSubTask.getDescription(),
                newSubTask.getStatus(),
                epic);
        newSubTaskChangeEpic.setId(newSubTask.getId());

        taskManager.updateSubTask(newSubTaskChangeEpic);

        assertEquals(
                newSubTaskChangeEpic.getEpicId(),
                newSubTask.getEpicId(),
                "После обновления изменился эпик");
    }

    @Test
    @DisplayName("Изменение статуса эпика")
    public void shouldChangeEpicStatus() {
        Epic epic = taskManager.getAllEpics().getFirst();
        SubTask subTask1 = taskManager.getAllSubTasks().getFirst();
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, epic);
        taskManager.addNewSubTask(subTask2);

        assertEquals(
                TaskStatus.NEW.name(),
                epic.getStatus().name(),
                "Статус != NEW, когда у всех подзадач статус = NEW");

        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubTask(subTask1);

        assertEquals(
                TaskStatus.IN_PROGRESS.name(),
                epic.getStatus().name(),
                "Статус != IN_PROGRESS, когда у subTask1 статус = IN_PROGRESS");

        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubTask(subTask2);

        assertEquals(
                TaskStatus.IN_PROGRESS.name(),
                epic.getStatus().name(),
                "Статус != IN_PROGRESS, когда у всех подзадач статус = IN_PROGRESS");

        subTask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask1);

        assertEquals(
                TaskStatus.IN_PROGRESS.name(),
                epic.getStatus().name(),
                "Статус != IN_PROGRESS, когда у subTask1 статус = DONE, subTask2 статус = IN_PROGRESS");

        subTask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask2);

        assertEquals(
                TaskStatus.DONE.name(),
                epic.getStatus().name(),
                "Статус != DONE, когда у всех подзадач статус = DONE");

        taskManager.removeAllSubTasks();

        assertEquals(
                TaskStatus.NEW.name(),
                epic.getStatus().name(),
                "Статус != NEW, когда все подзадачи удалены");
    }

    @Test
    @DisplayName("Получение истории")
    public void shouldGetHistory() {
        assertNotNull(taskManager.getHistory(), "Нет истории");
    }

    @Test
    @DisplayName("Добавление пустой задачи")
    public void shouldCreateNullTask() {
        Task task = null;
        assertThrows(
                TaskNotFoundException.class,
                () -> {
                    taskManager.createTask(task);
                },
                "Задача добавлена");
    }

    @Test
    @DisplayName("Пересечение интервалов")
    public void shouldIntersectionOfIntervals() {
        Task task = new Task(
                "Задача 1",
                "Описание задачи 1",
                Duration.ofMinutes(10),
                LocalDateTime.parse("01.01.2025 09:10:00", Task.FORMATTER),
                TaskStatus.NEW);
        assertThrows(
                TaskValidationException.class, () -> {
                    taskManager.createTask(task);
                },
                "Задача не пересекается про времени выполнения");
    }

    protected class EmptyHistoryManager implements HistoryManager {

        @Override
        public void add(Task task) {
        }

        @Override
        public void remove(int id) {

        }

        @Override
        public List<Task> getHistory() {
            return new ArrayList<>();
        }
    }
}
