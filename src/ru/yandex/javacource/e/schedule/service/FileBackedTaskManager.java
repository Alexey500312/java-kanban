package ru.yandex.javacource.e.schedule.service;

import ru.yandex.javacource.e.schedule.exception.ManagerSaveException;
import ru.yandex.javacource.e.schedule.model.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String HEADER = "id,type,name,status,description,epic,duration,startTime";
    private final Path path;

    public FileBackedTaskManager(HistoryManager historyManager, Path path) {
        super(historyManager);
        this.path = path;
    }

    public FileBackedTaskManager(Path path) {
        this(Managers.getDefaultHistory(), path);
    }

    public static FileBackedTaskManager loadFromFile(HistoryManager historyManager, Path path) {
        if (path == null) {
            return null;
        }
        int sequenceTask = 0;
        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, path);
        try (BufferedReader bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            int i = 1;
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (i == 1) {
                    i++;
                    continue;
                }
                Task task = manager.fromString(line);
                if (task == null) {
                    continue;
                }
                switch (task.getType()) {
                    case TASK:
                        manager.tasks.put(task.getId(), task);
                        break;
                    case EPIC:
                        manager.epics.put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        manager.subTasks.put(task.getId(), (SubTask) task);
                        break;
                }
                sequenceTask = task.getId() > sequenceTask ? task.getId() : sequenceTask;
                manager.prioritizedTasks.add(task);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла:\n" + e.getMessage());
        }
        manager.sequenceTask = sequenceTask;
        for (SubTask subTask : manager.subTasks.values()) {
            Epic epic = manager.epics.get(subTask.getEpicId());
            if (epic == null) {
                continue;
            }
            manager.prioritizedTasks.remove(epic);
            epic.addSubTask(subTask);
            manager.updateEpicData(epic);
            manager.prioritizedTasks.add(epic);
        }
        return manager;
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        HistoryManager historyManager = Managers.getDefaultHistory();
        return loadFromFile(historyManager, path);
    }

    public void save() {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            bufferedWriter.write(HEADER);
            bufferedWriter.newLine();
            for (Task task : getAllTasks()) {
                bufferedWriter.write(toString(task));
                bufferedWriter.newLine();
            }
            for (Epic epic : getAllEpics()) {
                bufferedWriter.write(toString(epic));
                bufferedWriter.newLine();
            }
            for (SubTask subTask : getAllSubTasks()) {
                bufferedWriter.write(toString(subTask));
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл:\n" + e.getMessage());
        }
    }

    public String toString(Task task) {
        return task.getId()
                + ","
                + task.getType()
                + ","
                + task.getStatus().name()
                + ","
                + task.getName()
                + ","
                + task.getDescription()
                + ","
                + (task.getType().equals(TaskType.SUBTASK) ? ((SubTask) task).getEpicId() : "")
                + ","
                + (task.getDuration() != null ? task.getDuration().toMinutes() : "")
                + ","
                + (task.getStartTime() != null ? task.getStartTime().format(Task.FORMATTER) : "");
    }

    public Task fromString(String value) {
        String[] taskData = value.split(",");
        TaskType taskType = TaskType.valueOf(taskData[1]);
        switch (taskType) {
            case TASK:
                Task task = new Task(
                        taskData[3],
                        taskData[4],
                        Duration.ofMinutes(Integer.parseInt(taskData[6])),
                        LocalDateTime.parse(taskData[7], Task.FORMATTER),
                        TaskStatus.valueOf(taskData[2]));
                task.setId(Integer.valueOf(taskData[0]));
                return task;
            case EPIC:
                Epic epic = new Epic(taskData[3], taskData[4]);
                epic.setId(Integer.valueOf(taskData[0]));
                epic.setStatus(TaskStatus.valueOf(taskData[2]));
                epic.setDuration(Duration.ofMinutes(Integer.parseInt(taskData[6])));
                epic.setStartTime(LocalDateTime.parse(taskData[7], Task.FORMATTER));
                return epic;
            case SUBTASK:
                SubTask subTask = new SubTask(
                        taskData[3],
                        taskData[4],
                        TaskStatus.valueOf(taskData[2]),
                        Integer.valueOf(taskData[5]));
                subTask.setId(Integer.valueOf(taskData[0]));
                return subTask;
            default:
                return null;
        }
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeTask(Integer taskId) {
        super.removeTask(taskId);
        save();
    }

    @Override
    public Task updateTask(Task task) {
        Task updateTask = super.updateTask(task);
        save();
        return updateTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeEpic(Integer epicId) {
        super.removeEpic(epicId);
        save();
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updateEpic = super.updateEpic(epic);
        save();
        return updateEpic;
    }

    @Override
    public SubTask addNewSubTask(SubTask subTask) {
        SubTask newSubTask = super.addNewSubTask(subTask);
        save();
        return newSubTask;
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void removeSubTask(Integer subTaskId) {
        super.removeSubTask(subTaskId);
        save();
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        SubTask updateSubTask = super.updateSubTask(subTask);
        save();
        return updateSubTask;
    }
}
