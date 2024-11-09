package ru.yandex.javacource.e.schedule.service;

import ru.yandex.javacource.e.schedule.model.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private Path path;

    public FileBackedTaskManager(Path path) {
        super(Managers.getDefaultHistory());
        this.path = path;
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        if (path == null) {
            return null;
        }
        int sequenceTask = 0;
        FileBackedTaskManager manager = new FileBackedTaskManager(path);
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
                switch (task.getClass().getSimpleName()) {
                    case "Task":
                        manager.tasks.put(task.getId(), task);
                        break;
                    case "Epic":
                        manager.epics.put(task.getId(), (Epic) task);
                        break;
                    case "SubTask":
                        manager.subTasks.put(task.getId(), (SubTask) task);
                        break;
                }
                sequenceTask = task.getId() > sequenceTask ? task.getId() : sequenceTask;
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
            epic.addSubTask(subTask);
        }
        return manager;
    }

    public void save() {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            bufferedWriter.write("id,type,name,status,description,epic\n");
            for (Task task : getAllTasks()) {
                bufferedWriter.write(toString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                bufferedWriter.write(toString(epic) + "\n");
            }
            for (SubTask subTask : getAllSubTasks()) {
                bufferedWriter.write(toString(subTask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл:\n" + e.getMessage());
        }
    }

    public String toString(Task task) {
        TaskType taskType = TaskType.getTaskType(task);
        String taskTypeName = taskType == null ? "" : taskType.name();
        return task.getId()
                + ","
                + taskTypeName
                + ","
                + task.getStatus().name()
                + ","
                + task.getName()
                + ","
                + task.getDescription()
                + ","
                + (task.getEpicId() == null ? "" : task.getEpicId().toString());
    }

    public Task fromString(String value) {
        String[] taskData = value.split(",");
        TaskType taskType = TaskType.valueOf(taskData[1]);
        switch (taskType) {
            case TASK:
                Task task = new Task(taskData[3], taskData[4], TaskStatus.valueOf(taskData[2]));
                task.setId(Integer.valueOf(taskData[0]));
                return task;
            case EPIC:
                Epic epic = new Epic(taskData[3], taskData[4]);
                epic.setId(Integer.valueOf(taskData[0]));
                epic.setStatus(TaskStatus.valueOf(taskData[2]));
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
