package ru.yandex.javacource.e.schedule.service;

import ru.yandex.javacource.e.schedule.model.Epic;
import ru.yandex.javacource.e.schedule.model.SubTask;
import ru.yandex.javacource.e.schedule.model.Task;
import ru.yandex.javacource.e.schedule.model.TaskStatus;

import java.util.*;

public class TaskManager {
    private int sequenceTask;
    private Map<Integer, Task> tasks;
    private Map<Integer, Epic> epics;
    private Map<Integer, SubTask> subTasks;

    public TaskManager() {
        sequenceTask = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
    }

    private int generateId() {
        return ++sequenceTask;
    }

    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
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
        if (tasks.get(task.getId()) == null) {
            return null;
        }

        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
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
            subTasks.remove(subTask.getId());
        }
        epics.remove(epicId);
    }

    public Epic updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return null;
        }

        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
        epics.put(savedEpic.getId(), savedEpic);
        return savedEpic;
    }

    public void updateEpicStatus(Epic epic) {
        if (epic == null) {
            return;
        }

        if (epic.getSubTasks().size() == 0) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        int[] countStatus = {0, 0, 0};
        for (SubTask subTask : epic.getSubTasks()) {
            switch (subTask.getStatus()) {
                case NEW:
                    countStatus[0]++;
                    break;
                case IN_PROGRESS:
                    countStatus[1]++;
                    break;
                case DONE:
                    countStatus[2]++;
                    break;
            }
        }
        if (countStatus[0] > 0 && countStatus[1] == 0 && countStatus[2] == 0) {
            epic.setStatus(TaskStatus.NEW);
        } else if (countStatus[0] == 0 && countStatus[1] == 0 && countStatus[2] > 0) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public SubTask addNewSubTask(SubTask subTask) {
        Epic epic = subTask.getEpic();
        if (epic == null) {
            return null;
        }

        subTask.setId(generateId());
        if (epic.addSubTask(subTask)) {
            updateEpicStatus(epic);
            subTasks.put(subTask.getId(), subTask);
            return subTask;
        } else {
            return null;
        }
    }

    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public void removeAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.removeAllSubTask();
            updateEpicStatus(epic);
        }
        subTasks.clear();
    }

    public SubTask getSubTask(Integer subTaskId) {
        return subTasks.get(subTaskId);
    }

    public void removeSubTask(Integer subTaskId) {
        SubTask subTask = getSubTask(subTaskId);
        if (subTask != null) {
            subTasks.remove(subTask.getId());
            Epic epic =  subTask.getEpic();
            epic.removeSubTask(subTask);
            updateEpicStatus(epic);
        }
    }

    public SubTask updateSubTask(SubTask subTask) {
        SubTask savedSubTask = subTasks.get(subTask.getId());
        if (savedSubTask == null) {
            return null;
        }

        Epic savedSubTaskEpic = savedSubTask.getEpic();
        if (savedSubTaskEpic == null) {
            return null;
        }

        savedSubTask.setName(subTask.getName());
        savedSubTask.setDescription(subTask.getDescription());
        savedSubTask.setStatus(subTask.getStatus());
        updateEpicStatus(savedSubTaskEpic);
        subTasks.put(savedSubTask.getId(), savedSubTask);
        return savedSubTask;
    }
}