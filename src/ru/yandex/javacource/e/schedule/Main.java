package ru.yandex.javacource.e.schedule;

import ru.yandex.javacource.e.schedule.model.Epic;
import ru.yandex.javacource.e.schedule.model.SubTask;
import ru.yandex.javacource.e.schedule.model.Task;
import ru.yandex.javacource.e.schedule.model.TaskStatus;
import ru.yandex.javacource.e.schedule.service.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager manager = Managers.getDefault();

        Task task = new Task(
                "Замена лампочки",
                "Поменять испорченную лампочку в люстре",
                Duration.ofMinutes(60),
                LocalDateTime.parse("10.11.2024 12:00:00", Task.FORMATTER),
                TaskStatus.NEW);
        manager.createTask(task);
        task = new Task(
                "Пересечение",
                "Проверка на пересечение",
                Duration.ofMinutes(120),
                LocalDateTime.parse("10.11.2024 12:30:00", Task.FORMATTER),
                TaskStatus.NEW);
        manager.createTask(task);
        task = new Task(
                "Помыть посуду",
                "Помыть посуду",
                Duration.ofMinutes(60),
                LocalDateTime.parse("11.11.2024 12:00:00", Task.FORMATTER),
                TaskStatus.NEW);
        manager.createTask(task);

        Epic epic = new Epic("Поход за покупками", "Купить все необходимое по списку");
        manager.createEpic(epic);
        SubTask subTask = new SubTask(
                "Составить список",
                "Составить список покупок",
                Duration.ofMinutes(60),
                LocalDateTime.parse("12.11.2024 12:00:00", Task.FORMATTER),
                TaskStatus.NEW,
                epic);
        manager.addNewSubTask(subTask);
        subTask = new SubTask(
                "Сходить в магазин",
                "Сходить в магазин за покупками",
                Duration.ofMinutes(60),
                LocalDateTime.parse("13.11.2024 12:00:00", Task.FORMATTER),
                TaskStatus.NEW,
                epic);
        manager.addNewSubTask(subTask);

        epic = new Epic("Собрать мебель", "Собрать мебель");
        manager.createEpic(epic);
        subTask = new SubTask(
                "Прочитать инструкцию",
                "Прочитать инструкцию по сборке",
                Duration.ofMinutes(60),
                LocalDateTime.parse("14.11.2024 12:00:00", Task.FORMATTER),
                TaskStatus.NEW,
                epic);
        manager.addNewSubTask(subTask);

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());

        task.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task);
        System.out.println(manager.getTask(task.getId()));

        subTask.setStatus(TaskStatus.DONE);
        manager.updateSubTask(subTask);
        System.out.println(manager.getSubTask(subTask.getId()));
        System.out.println(manager.getEpic(epic.getId()));

        manager.removeTask(task.getId());
        manager.removeEpic(epic.getId());

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());
        System.out.println(manager.getHistory());
        System.out.println(manager.getPrioritizedTasks());

        System.out.println("\n----------------------------------------------\n");

        Path path = Paths.get("tasks.csv");
        TaskManager fileManager = new FileBackedTaskManager(path);

        task = new Task(
                "Замена лампочки",
                "Поменять испорченную лампочку в люстре",
                Duration.ofMinutes(60),
                LocalDateTime.parse("10.11.2024 12:00:00", Task.FORMATTER),
                TaskStatus.NEW);
        fileManager.createTask(task);
        task = new Task(
                "Помыть посуду",
                "Помыть посуду",
                Duration.ofMinutes(60),
                LocalDateTime.parse("11.11.2024 12:00:00", Task.FORMATTER),
                TaskStatus.NEW);
        fileManager.createTask(task);

        epic = new Epic("Поход за покупками", "Купить все необходимое по списку");
        fileManager.createEpic(epic);
        subTask = new SubTask(
                "Составить список",
                "Составить список покупок",
                Duration.ofMinutes(60),
                LocalDateTime.parse("12.11.2024 12:00:00", Task.FORMATTER),
                TaskStatus.NEW,
                epic);
        fileManager.addNewSubTask(subTask);
        subTask = new SubTask(
                "Сходить в магазин",
                "Сходить в магазин за покупками",
                Duration.ofMinutes(60),
                LocalDateTime.parse("13.11.2024 12:00:00", Task.FORMATTER),
                TaskStatus.NEW,
                epic);
        fileManager.addNewSubTask(subTask);

        epic = new Epic("Собрать мебель", "Собрать мебель");
        fileManager.createEpic(epic);
        subTask = new SubTask(
                "Прочитать инструкцию",
                "Прочитать инструкцию по сборке",
                Duration.ofMinutes(60),
                LocalDateTime.parse("14.11.2024 12:00:00", Task.FORMATTER),
                TaskStatus.NEW,
                epic);
        fileManager.addNewSubTask(subTask);

        System.out.println(fileManager.getAllTasks());
        System.out.println(fileManager.getAllEpics());
        System.out.println(fileManager.getAllSubTasks());

        task.setStatus(TaskStatus.IN_PROGRESS);
        fileManager.updateTask(task);
        System.out.println(fileManager.getTask(task.getId()));

        subTask.setStatus(TaskStatus.DONE);
        fileManager.updateSubTask(subTask);
        System.out.println(fileManager.getSubTask(subTask.getId()));
        System.out.println(fileManager.getEpic(epic.getId()));

        fileManager.removeTask(task.getId());
        fileManager.removeEpic(epic.getId());

        System.out.println(fileManager.getAllTasks());
        System.out.println(fileManager.getAllEpics());
        System.out.println(fileManager.getAllSubTasks());
        System.out.println(fileManager.getHistory());
        System.out.println(manager.getPrioritizedTasks());
    }
}
