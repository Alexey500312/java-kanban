package ru.yandex.javacource.e.schedule.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.e.schedule.exception.EndpointException;
import ru.yandex.javacource.e.schedule.exception.RequestException;
import ru.yandex.javacource.e.schedule.model.Task;
import ru.yandex.javacource.e.schedule.server.Item;
import ru.yandex.javacource.e.schedule.server.Items;
import ru.yandex.javacource.e.schedule.service.TaskManager;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler<Task> implements HttpHandler {
    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            switch (method) {
                case "GET":
                    handleGet(exchange, method, path);
                    break;
                case "POST":
                    handlePost(exchange, method, path);
                    break;
                case "DELETE":
                    handleDelete(exchange, method, path);
                    break;
                default:
                    throw new EndpointException("Вызван несуществующий метод " + method + " " + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponseError(exchange, e);
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange, String method, String path) throws IOException {
        if (path.matches("^/tasks/\\d+$")) {
            sendResponseItem(exchange, HTTP_OK, manager.getTask(getId(path)));
        } else if (path.matches("^/tasks$")) {
            sendResponseItems(exchange, new Items<>(manager.getAllTasks()));
        } else {
            throw new EndpointException("Вызван несуществующий метод " + method + " " + path);
        }
    }

    private void handlePost(HttpExchange exchange, String method, String path) throws IOException {
        if (path.matches("^/tasks$")) {
            Task task = getTask(exchange);
            if (task.getId() == null) {
                sendResponseItem(exchange, HTTP_CREATED, manager.createTask(task));
            } else {
                sendResponseItem(exchange, HTTP_OK, manager.updateTask(task));
            }
        } else {
            throw new EndpointException("Вызван несуществующий метод " + method + " " + path);
        }
    }

    private void handleDelete(HttpExchange exchange, String method, String path) throws IOException {
        if (path.matches("^/tasks/\\d+$")) {
            manager.removeTask(getId(path));
            sendResponse(exchange, HTTP_NO_CONTENT, "");
        } else if (path.matches("^/tasks$")) {
            manager.removeAllTasks();
            sendResponse(exchange, HTTP_NO_CONTENT, "");
        } else {
            throw new EndpointException("Вызван несуществующий метод " + method + " " + path);
        }
    }

    private Task getTask(HttpExchange exchange) {
        Item item = null;
        try {
            String body = readRequestBody(exchange);
            item = gson.fromJson(body, Item.class);
        } catch (Exception e) {
            throw new RequestException("Ошибка чтения тела запроса");
        }
        if (item.getName() == null || item.getDescription() ==null || item.getStatus() == null ||
                item.getDuration() == null || item.getStartTime() == null) {
            throw new RequestException("Ошибка в теле запроса");
        }
        Task task = new Task(item.getName(), item.getDescription(), item.getDuration(),
                item.getStartTime(), item.getStatus());
        if (item.getId() != null && item.getId() > 0) {
            task.setId(item.getId());
        }
        return task;
    }
}
