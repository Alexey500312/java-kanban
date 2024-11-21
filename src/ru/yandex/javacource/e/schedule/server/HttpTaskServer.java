package ru.yandex.javacource.e.schedule.server;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacource.e.schedule.exception.HttpCreateException;
import ru.yandex.javacource.e.schedule.model.Epic;
import ru.yandex.javacource.e.schedule.model.SubTask;
import ru.yandex.javacource.e.schedule.model.Task;
import ru.yandex.javacource.e.schedule.model.TaskStatus;
import ru.yandex.javacource.e.schedule.server.handler.*;
import ru.yandex.javacource.e.schedule.service.Managers;
import ru.yandex.javacource.e.schedule.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    public static final String HTTP_ADDRESS = "localhost";
    public static final int HTTP_PORT = 8080;

    private TaskManager manager;
    private HttpServer server;

    public HttpTaskServer() throws HttpCreateException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager) throws HttpCreateException {
        this.manager = taskManager;
        try {
            this.server = HttpServer.create(new InetSocketAddress(HTTP_ADDRESS, HTTP_PORT), 0);
            server.createContext("/tasks", new TaskHandler(manager));
            server.createContext("/epics", new EpicHandler(manager));
            server.createContext("/subtasks", new SubTaskHandler(manager));
            server.createContext("/history", new HistoryHandler(manager));
            server.createContext("/prioritized", new PrioritizedHandler(manager));
        } catch (IOException e) {
            throw new HttpCreateException("Ошибка создания сервера\n" + e);
        }
    }

    public static void main(String[] args) {
        try {
            HttpTaskServer httpTaskServer = new HttpTaskServer();
            Task task = new Task("Task", "Task description", Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 12:00:00", Task.FORMATTER), TaskStatus.NEW);
            Epic epic = new Epic("Epic", "Epic description");
            httpTaskServer.manager.createTask(task);
            epic = httpTaskServer.manager.createEpic(epic);
            SubTask subTask = new SubTask("SubTask", "SubTask description", Duration.ofMinutes(10), LocalDateTime.parse("01.01.2025 13:00:00", Task.FORMATTER), TaskStatus.NEW, epic);
            httpTaskServer.manager.addNewSubTask(subTask);
            httpTaskServer.start();
        } catch (HttpCreateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void start() {
        server.start();
        System.out.println("Запущен сервер по адресу " + HTTP_ADDRESS + ":" + HTTP_PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("Cервер по адресу " + HTTP_ADDRESS + ":" + HTTP_PORT + " остановлен");
    }
}
