package ru.yandex.javacource.e.schedule.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.e.schedule.model.Epic;
import ru.yandex.javacource.e.schedule.model.SubTask;
import ru.yandex.javacource.e.schedule.model.Task;
import ru.yandex.javacource.e.schedule.model.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Менеджер задач")
class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void init() {
        HistoryManager historyManager = new EmptyHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
        Task task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        taskManager.createTask(task);
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic);
        taskManager.addNewSubTask(subTask);
    }

    @Test
    @DisplayName("Добавление задачи")
    public void shouldCreateTask() {
        assertEquals(1, taskManager.getAllTasks().size());

        Task task = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW);
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
        SubTask subTask = new SubTask("Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, epic);
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
                TaskStatus.NEW,
                taskManager.getAllEpics().get(0));
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
        Task task = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW);
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
                TaskStatus.NEW,
                taskManager.getAllEpics().get(0));
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

        Task task = taskManager.getAllTasks().get(0);
        taskManager.removeTask(task.getId());

        assertEquals(0, taskManager.getAllTasks().size(), "Задача не удалена");
    }

    @Test
    @DisplayName("Удаление эпика")
    public void shouldRemoveEpic() {
        assertEquals(1, taskManager.getAllEpics().size());

        Epic epic = taskManager.getAllEpics().get(0);
        taskManager.removeEpic(epic.getId());

        assertEquals(0, taskManager.getAllEpics().size(), "Эпик не удален");
    }

    @Test
    @DisplayName("Удаление подзадачи")
    public void shouldRemoveSubTask() {
        assertEquals(1, taskManager.getAllSubTasks().size());

        SubTask subTask = taskManager.getAllSubTasks().get(0);
        taskManager.removeSubTask(subTask.getId());

        assertEquals(0, taskManager.getAllSubTasks().size(), "Подзадача не удалена");
    }

    @Test
    @DisplayName("Удаление всех задач")
    public void shouldRemoveAllTasks() {
        Task task = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW);
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
        SubTask subTask = new SubTask("Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, epic);
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
                TaskStatus.NEW,
                taskManager.getAllEpics().get(0));
        taskManager.addNewSubTask(subTask);

        assertEquals(2, taskManager.getAllSubTasks().size());

        taskManager.removeAllSubTasks();

        assertEquals(0, taskManager.getAllSubTasks().size(), "Подзадачи остались");
    }

    @Test
    @DisplayName("Обновление задачи")
    public void shouldUpdateTask() {
        Task task = taskManager.getAllTasks().get(0);
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
        Epic epic = taskManager.getAllEpics().get(0);
        Epic epicCopy = new Epic(epic.getName(), epic.getDescription());
        epicCopy.setId(epic.getId());
        epicCopy.addSubTask(taskManager.getSubTask(epic.getSubTasks().get(0)));
        epicCopy.setName("Измененное имя");

        taskManager.updateEpic(epicCopy);

        assertEquals(
                epicCopy.toString(),
                taskManager.getEpic(epicCopy.getId()).toString(),
                "После обновления, эпики не изменились");

        epicCopy.removeAllSubTask();

        taskManager.updateEpic(epicCopy);

        assertNotEquals(epicCopy.getSubTasks().size(),
                taskManager.getEpic(epicCopy.getId()).getSubTasks().size(),
                "После обновления изменилось количество подзадач");
    }

    @Test
    @DisplayName("Обновление подзадачи")
    public void shouldUpdateSubTask() {
        SubTask subTask = taskManager.getAllSubTasks().get(0);
        SubTask subTaskCopy = new SubTask(subTask.getName(), subTask.getDescription(), subTask.getStatus());
        subTaskCopy.setId(subTask.getId());
        subTaskCopy.setEpicId(subTask.getEpicId());
        subTaskCopy.setDescription("Измененное описание");

        assertNotEquals(
                subTaskCopy.toString(),
                taskManager.getSubTask(subTaskCopy.getId()).toString(),
                "После изменения, подзадачи совпадают");

        taskManager.updateSubTask(subTaskCopy);

        assertEquals(
                subTaskCopy.toString(),
                taskManager.getSubTask(subTaskCopy.getId()).toString(),
                "После обновления, подзадачи отличаются");

        Epic epic = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.createEpic(epic);
        subTaskCopy.setEpicId(epic.getId());
        taskManager.updateSubTask(subTaskCopy);

        assertNotEquals(subTaskCopy.getEpicId(),
                taskManager.getSubTask(subTaskCopy.getId()).getEpicId(),
                "После обновления изменился эпик");
    }

    @Test
    @DisplayName("Изменение статуса эпика")
    public void shouldChangeEpicStatus() {
        Epic epic = taskManager.getAllEpics().get(0);
        SubTask subTask1 = taskManager.getAllSubTasks().get(0);
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
        assertNotNull(taskManager.getHistoryManager(), "Нет истории");
    }

    private class EmptyHistoryManager implements HistoryManager {
        @Override
        public void add(Task task) {
        }

        @Override
        public List<Task> getHistory() {
            return null;
        }

        @Override
        public int getHistoryMaxSize() {
            return 0;
        }
    }
}