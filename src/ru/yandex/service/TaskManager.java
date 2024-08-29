package ru.yandex.service;

import ru.yandex.model.Epic;
import ru.yandex.model.SubTask;
import ru.yandex.model.Task;
import ru.yandex.model.TaskStatus;

import javax.xml.parsers.SAXParser;
import java.util.*;

public class TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Epic> epics;
    private Map<Integer, SubTask> subTasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
    }

    public Task createTask(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    public Map<Integer, Task> getAllTasks() {
        return tasks;
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public Task getTask(Integer taskId) {
        return tasks.get(taskId);
    }

    public void removeTask(Integer taskId) {
        tasks.remove(taskId);
    }

    public Task updateTask(Task task) {
        if (tasks.get(task.getId()) == null)
            return null;

        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Map<Integer, Epic> getAllEpics() {
        return epics;
    }

    public List<SubTask> getAllSubTasksInEpic(Epic epic) {
        return epic.getSubTasks();
    }

    public void removeAllEpics() {
        subTasks.clear();
        epics.clear();
    }

    public Epic getEpic(Integer epicId) {
        return epics.get(epicId);
    }

    public void removeEpic(Integer epicId) {
        Epic epic = getEpic(epicId);
        for (SubTask subTask : epic.getSubTasks()) {
            removeSubTask(subTask.getId());
        }
        epics.remove(epicId);
    }

    public Epic updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null)
            return null;

        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
        epics.put(savedEpic.getId(), savedEpic);
        return savedEpic;
    }

    public SubTask createSubTask(SubTask subTask) {
        if (subTask.getEpic() == null) {
            return null;
        }

        Epic epic = subTask.getEpic();
        if (epic.addSubtask(subTask)) {
            epic.calcEpicStatus();
            subTasks.put(subTask.getId(), subTask);
            return subTask;
        } else {
            return null;
        }
    }

    public Map<Integer, SubTask> getAllSubTasks() {
        return subTasks;
    }

    public void removeAllSubTasks() {
        subTasks.clear();
    }

    public SubTask getSubTask(Integer subTaskId) {
        return subTasks.get(subTaskId);
    }

    public void removeSubTask(Integer subTaskId) {
        subTasks.remove(subTaskId);
    }

    public SubTask updateSubTask(SubTask subTask) {
        SubTask savedSubTask = subTasks.get(subTask.getId());
        if (savedSubTask == null)
            return null;

        Epic savedSubTaskEpic = savedSubTask.getEpic();
        Epic subTaskEpic = subTask.getEpic();
        if (!savedSubTaskEpic.getId().equals(subTaskEpic.getId())) {
            savedSubTaskEpic.removeSubTask(subTask);
            savedSubTaskEpic.calcEpicStatus();
            subTaskEpic.addSubtask(subTask);
        }
        subTaskEpic.calcEpicStatus();
        subTasks.put(subTask.getId(), subTask);
        return subTask;
    }
}