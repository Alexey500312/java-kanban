package ru.yandex.javacource.e.schedule.service;

import ru.yandex.javacource.e.schedule.exception.NullTaskException;
import ru.yandex.javacource.e.schedule.exception.TaskValidationException;
import ru.yandex.javacource.e.schedule.model.Epic;
import ru.yandex.javacource.e.schedule.model.SubTask;
import ru.yandex.javacource.e.schedule.model.Task;
import ru.yandex.javacource.e.schedule.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int sequenceTask;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, SubTask> subTasks;
    protected final TreeSet<Task> prioritizedTasks;
    private final HistoryManager historyManager;


    public InMemoryTaskManager(HistoryManager historyManager) {
        sequenceTask = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        this.historyManager = historyManager;
        prioritizedTasks = new TreeSet<>((Task task1, Task task2) -> {
            LocalDateTime startTime1 = task1.getStartTime() != null ? task1.getStartTime() : LocalDateTime.MAX;
            LocalDateTime startTime2 = task2.getStartTime() != null ? task2.getStartTime() : LocalDateTime.MAX;
            int compareStartTime = startTime1.compareTo(startTime2);
            if (compareStartTime == 0) {
                return task1.getId() - task2.getId();
            }
            return compareStartTime;
        });
    }

    @Override
    public Task createTask(Task task) throws NullTaskException, TaskValidationException {
        if (task == null) {
            throw new NullTaskException("Задача не передана");
        }
        if (!checkTaskTime(task)) {
            throw new TaskValidationException("Задачи пересекаются");
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.values().forEach(prioritizedTasks::remove);
        tasks.clear();
    }

    @Override
    public Task getTask(Integer taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public void removeTask(Integer taskId) throws NullTaskException {
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new NullTaskException("Задача не найдена");
        }
        prioritizedTasks.remove(task);
        tasks.remove(taskId);
    }

    @Override
    public Task updateTask(Task task) throws NullTaskException, TaskValidationException {
        if (task == null) {
            throw new NullTaskException("Задача не передана");
        }
        Task savedTask = tasks.get(task.getId());
        if (savedTask == null) {
            throw new NullTaskException("Задача не найдена");
        }
        if (!checkTaskTime(task)) {
            throw new TaskValidationException("Задачи пересекаются");
        }
        prioritizedTasks.remove(savedTask);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) throws NullTaskException {
        if (epic == null) {
            throw new NullTaskException("Эпик не передан");
        }
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
        subTasks.values().forEach(prioritizedTasks::remove);
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
    public void removeEpic(Integer epicId) throws NullTaskException {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NullTaskException("Эпик не найден");
        }
        epic.getSubTasks().forEach(subTaskId -> {
            prioritizedTasks.remove(subTasks.get(subTaskId));
            subTasks.remove(subTaskId);
        });
        epics.remove(epicId);
    }

    @Override
    public Epic updateEpic(Epic epic) throws NullTaskException {
        if (epic == null) {
            throw new NullTaskException("Эпик не передан");
        }
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            throw new NullTaskException("Эпик не найден");
        }
        epic.setStatus(savedEpic.getStatus());
        epic.setSubTasks(savedEpic.getSubTasks());
        epic.setDuration(savedEpic.getDuration());
        epic.setStartTime(savedEpic.getStartTime());
        epic.setEndTime(savedEpic.getEndTime());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public SubTask addNewSubTask(SubTask subTask) throws NullTaskException, TaskValidationException {
        if (subTask == null) {
            throw new NullTaskException("Подзадача не передана");
        }
        Epic epic = epics.get(subTask.getEpicId());
        if (epic == null) {
            throw new NullTaskException("Эпик переданной подзадачи не найден");
        }
        if (!checkTaskTime(subTask)) {
            throw new TaskValidationException("Задачи пересекаются");
        }
        subTask.setId(generateId());
        if (epic.addSubTask(subTask)) {
            subTasks.put(subTask.getId(), subTask);
            updateEpicData(epic);
            prioritizedTasks.add(subTask);
            return subTask;
        } else {
            throw new NullTaskException("Ошибка добавления подзадачи к эпику");
        }
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void removeAllSubTasks() {
        subTasks.values().forEach(prioritizedTasks::remove);
        for (Epic epic : epics.values()) {
            epic.removeAllSubTask();
            updateEpicData(epic);
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
    public void removeSubTask(Integer subTaskId) throws NullTaskException {
        SubTask subTask = subTasks.get(subTaskId);
        if (subTask == null) {
            throw new NullTaskException("Подзадача не найдена");
        }
        prioritizedTasks.remove(subTask);
        subTasks.remove(subTask.getId());
        Epic epic = epics.get(subTask.getEpicId());
        epic.removeSubTask(subTask.getId());
        updateEpicData(epic);
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) throws NullTaskException, TaskValidationException {
        if (subTask == null) {
            throw new NullTaskException("Подзадача не передана");
        }
        SubTask savedSubTask = subTasks.get(subTask.getId());
        if (savedSubTask == null) {
            throw new NullTaskException("Подзадача не найдена");
        }
        Integer savedSubTaskEpicId = savedSubTask.getEpicId();
        if (savedSubTaskEpicId == null) {
            throw new NullTaskException("Эпик подзадачи не найден");
        }
        if (!checkTaskTime(subTask)) {
            throw new TaskValidationException("Задачи пересекаются");
        }
        prioritizedTasks.remove(savedSubTask);
        subTask.setEpicId(savedSubTaskEpicId);
        updateEpicData(epics.get(subTask.getEpicId()));
        subTasks.put(subTask.getId(), subTask);
        prioritizedTasks.add(subTask);
        return subTask;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    protected void updateEpicData(Epic epic) throws NullTaskException {
        if (epic == null) {
            throw new NullTaskException("Эпик не передан");
        }
        updateEpicStatus(epic);
        updateEpicDuration(epic);
    }

    protected void updateEpicStatus(Epic epic) throws NullTaskException {
        if (epic == null) {
            throw new NullTaskException("Эпик не передан");
        }
        if (epic.getSubTasks().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        int[] countStatus = {0, 0, 0};

        for (Integer subTaskId : epic.getSubTasks()) {
            SubTask subTask = subTasks.get(subTaskId);
            //Статус эпика
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

    protected void updateEpicDuration(Epic epic) throws NullTaskException {
        if (epic == null) {
            throw new NullTaskException("Эпик не передан");
        }
        Duration duration = Duration.ZERO;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        for (Integer subTaskId : epic.getSubTasks()) {
            SubTask subTask = subTasks.get(subTaskId);
            //Длительность выполнения всех подзадач эпика
            duration = duration.plus(subTask.getDuration() != null ? subTask.getDuration() : Duration.ZERO);
            //Минимальная дата и время начала подзадач эпика
            if (subTask.getStartTime() != null && (startTime == null || subTask.getStartTime().isBefore(startTime))) {
                startTime = subTask.getStartTime();
            }
            //Дата и время завершения подзадачи эпика с максимальной датой и временем начала
            if (subTask.getEndTime() != null && (endTime == null || subTask.getEndTime().isAfter(endTime))) {
                endTime = subTask.getEndTime();
            }
        }
        epic.setDuration(duration);
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
    }

    private int generateId() {
        return ++sequenceTask;
    }

    private boolean checkTaskTime(Task task) {
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();
        if (startTime == null || endTime == null) {
            return true;
        }
        Task findTask = prioritizedTasks.stream()
                .filter(t -> !(t instanceof Epic) && !t.getId().equals(task.getId()) &&
                        t.getStartTime() != null && t.getEndTime() != null &&
                        (t.getStartTime().compareTo(endTime) <= 0 && t.getEndTime().compareTo(startTime) >= 0))
                .findFirst()
                .orElse(null);
        return findTask == null;
    }
}