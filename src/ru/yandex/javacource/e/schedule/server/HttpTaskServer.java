package ru.yandex.javacource.e.schedule.server;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacource.e.schedule.exception.HttpCreateException;
import ru.yandex.javacource.e.schedule.server.handler.*;
import ru.yandex.javacource.e.schedule.service.FileBackedTaskManager;
import ru.yandex.javacource.e.schedule.service.Managers;
import ru.yandex.javacource.e.schedule.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    public static final String HTTP_ADDRESS = "localhost";
    public static final int HTTP_PORT = 8080;

    private TaskManager manager;
    private HttpServer server;

    public HttpTaskServer() throws HttpCreateException {
        this(FileBackedTaskManager.loadFromFile(Managers.getDefaultFilePath()));
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
