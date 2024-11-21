package ru.yandex.javacource.e.schedule.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.e.schedule.exception.EndpointException;
import ru.yandex.javacource.e.schedule.exception.RequestException;
import ru.yandex.javacource.e.schedule.model.SubTask;
import ru.yandex.javacource.e.schedule.server.Item;
import ru.yandex.javacource.e.schedule.server.Items;
import ru.yandex.javacource.e.schedule.service.TaskManager;

import java.io.IOException;

public class SubTaskHandler extends BaseHttpHandler<SubTask> implements HttpHandler {
    public SubTaskHandler(TaskManager manager) {
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
        if (path.matches("^/subtasks/\\d+$")) {
            sendResponseItem(exchange, HTTP_OK, manager.getSubTask(getId(path)));
        } else if (path.matches("^/subtasks$")) {
            sendResponseItems(exchange, new Items<>(manager.getAllSubTasks()));
        } else {
            throw new EndpointException("Вызван несуществующий метод " + method + " " + path);
        }
    }

    private void handlePost(HttpExchange exchange, String method, String path) throws IOException {
        if (path.matches("^/subtasks$")) {
            SubTask subTask = getSubTask(exchange);
            if (subTask.getId() == null) {
                sendResponseItem(exchange, HTTP_CREATED, manager.addNewSubTask(subTask));
            } else {
                sendResponseItem(exchange, HTTP_OK, manager.updateSubTask(subTask));
            }
        } else {
            throw new EndpointException("Вызван несуществующий метод " + method + " " + path);
        }
    }

    private void handleDelete(HttpExchange exchange, String method, String path) throws IOException {
        if (path.matches("^/subtasks/\\d+$")) {
            manager.removeSubTask(getId(path));
            sendResponse(exchange, HTTP_NO_CONTENT, "");
        } else if (path.matches("^/subtasks$")) {
            manager.removeAllSubTasks();
            sendResponse(exchange, HTTP_NO_CONTENT, "");
        } else {
            throw new EndpointException("Вызван несуществующий метод " + method + " " + path);
        }
    }

    private SubTask getSubTask(HttpExchange exchange) {
        Item item = null;
        try {
            String body = readRequestBody(exchange);
            item = gson.fromJson(body, Item.class);
        } catch (Exception e) {
            throw new RequestException("Ошибка чтения тела запроса");
        }
        if (item.getName() == null || item.getDescription() == null || item.getStatus() == null ||
                item.getDuration() == null || item.getStartTime() == null || item.getEpicId() == null) {
            throw new RequestException("Ошибка в теле запроса");
        }
        SubTask subTask = new SubTask(item.getName(), item.getDescription(), item.getDuration(),
                item.getStartTime(), item.getStatus(), item.getEpicId());
        if (item.getId() != null && item.getId() > 0) {
            subTask.setId(item.getId());
        }
        return subTask;
    }
}