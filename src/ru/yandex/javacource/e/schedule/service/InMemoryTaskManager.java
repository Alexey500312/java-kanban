package ru.yandex.javacource.e.schedule.service;

import ru.yandex.javacource.e.schedule.model.Epic;
import ru.yandex.javacource.e.schedule.model.SubTask;
import ru.yandex.javacource.e.schedule.model.Task;
import ru.yandex.javacource.e.schedule.model.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int sequenceTask;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, SubTask> subTasks;
    private final HistoryManager historyManager;


    public InMemoryTaskManager(HistoryManager historyManager) {
        sequenceTask = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        this.historyManager = historyManager;
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public Task getTask(Integer taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public void removeTask(Integer taskId) {
        tasks.remove(taskId);
    }

    @Override
    public Task updateTask(Task task) {
        if (tasks.get(task.getId()) == null) {
            return null;
        }

        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Integer> getAllSubTasksInEpic(Epic epic) {
        return epic.getSubTasks();
    }

    @Override
    public void removeAllEpics() {
        subTasks.clear();
        epics.clear();
    }

    @Override
    public Epic getEpic(Integer epicId) {
        Epic epic = epics.get(epicId);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void removeEpic(Integer epicId) {
        Epic epic = epics.get(epicId);
        for (Integer subTaskId : epic.getSubTasks()) {
            subTasks.remove(subTaskId);
        }
        epics.remove(epicId);
    }

    @Override
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

    @Override
    public SubTask addNewSubTask(SubTask subTask) {
        Epic epic = getEpic(subTask.getEpicId());
        if (epic == null) {
            return null;
        }

        subTask.setId(generateId());
        if (epic.addSubTask(subTask)) {
            subTasks.put(subTask.getId(), subTask);
            updateEpicStatus(epic);
            return subTask;
        } else {
            return null;
        }
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void removeAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.removeAllSubTask();
            updateEpicStatus(epic);
        }
        subTasks.clear();
    }

    @Override
    public SubTask getSubTask(Integer subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public void removeSubTask(Integer subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        if (subTask != null) {
            subTasks.remove(subTask.getId());
            Epic epic =  getEpic(subTask.getEpicId());
            epic.removeSubTask(subTask.getId());
            updateEpicStatus(epic);
        }
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        SubTask savedSubTask = subTasks.get(subTask.getId());
        if (savedSubTask == null) {
            return null;
        }

        Integer savedSubTaskEpicId = savedSubTask.getEpicId();
        if (savedSubTaskEpicId == null) {
            return null;
        }

        savedSubTask.setName(subTask.getName());
        savedSubTask.setDescription(subTask.getDescription());
        savedSubTask.setStatus(subTask.getStatus());
        updateEpicStatus(getEpic(savedSubTaskEpicId));
        subTasks.put(savedSubTask.getId(), savedSubTask);
        return savedSubTask;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    private int generateId() {
        return ++sequenceTask;
    }

    private void updateEpicStatus(Epic epic) {
        if (epic == null) {
            return;
        }

        if (epic.getSubTasks().size() == 0) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        int[] countStatus = {0, 0, 0};
        for (Integer subTaskId : epic.getSubTasks()) {
            switch (getSubTask(subTaskId).getStatus()) {
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
}