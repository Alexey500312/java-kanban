package ru.yandex.javacource.e.schedule.service;

import ru.yandex.javacource.e.schedule.model.Epic;
import ru.yandex.javacource.e.schedule.model.SubTask;
import ru.yandex.javacource.e.schedule.model.Task;

import java.util.List;

public interface TaskManager {
    Task createTask(Task task);

    List<Task> getAllTasks();

    void removeAllTasks();

    Task getTask(Integer taskId);

    void removeTask(Integer taskId);

    Task updateTask(Task task);

    Epic createEpic(Epic epic);

    List<Epic> getAllEpics();

    List<Integer> getAllSubTasksInEpic(Epic epic);

    void removeAllEpics();

    Epic getEpic(Integer epicId);

    void removeEpic(Integer epicId);

    Epic updateEpic(Epic epic);

    SubTask addNewSubTask(SubTask subTask);

    List<SubTask> getAllSubTasks();

    void removeAllSubTasks();

    SubTask getSubTask(Integer subTaskId);

    void removeSubTask(Integer subTaskId);

    SubTask updateSubTask(SubTask subTask);

    List<Task> getHistory();
}
