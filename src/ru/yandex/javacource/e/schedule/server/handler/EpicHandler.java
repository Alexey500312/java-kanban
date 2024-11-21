package ru.yandex.javacource.e.schedule.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.e.schedule.exception.EndpointException;
import ru.yandex.javacource.e.schedule.exception.RequestException;
import ru.yandex.javacource.e.schedule.model.Epic;
import ru.yandex.javacource.e.schedule.model.Item;
import ru.yandex.javacource.e.schedule.model.Items;
import ru.yandex.javacource.e.schedule.service.TaskManager;

import java.io.IOException;

public class EpicHandler extends BaseHttpHandler<Epic> implements HttpHandler {
    public EpicHandler(TaskManager manager) {
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
        if (path.matches("^/epics/\\d+$")) {
            sendResponseItem(exchange, HTTP_OK, manager.getEpic(getId(path)));
        } else if (path.matches("^/epics$")) {
            sendResponseItems(exchange, new Items<>(manager.getAllEpics()));
        } else {
            throw new EndpointException("Вызван несуществующий метод " + method + " " + path);
        }
    }

    private void handlePost(HttpExchange exchange, String method, String path) throws IOException {
        if (path.matches("^/epics$")) {
            Epic epic = getEpic(exchange);
            if (epic.getId() == null) {
                sendResponseItem(exchange, HTTP_CREATED, manager.createEpic(epic));
            } else {
                sendResponseItem(exchange, HTTP_OK, manager.updateEpic(epic));
            }
        } else {
            throw new EndpointException("Вызван несуществующий метод " + method + " " + path);
        }
    }

    private void handleDelete(HttpExchange exchange, String method, String path) throws IOException {
        if (path.matches("^/epics/\\d+$")) {
            manager.removeEpic(getId(path));
            sendResponse(exchange, HTTP_NO_CONTENT, "");
        } else if (path.matches("^/epics$")) {
            manager.removeAllEpics();
            sendResponse(exchange, HTTP_NO_CONTENT, "");
        } else {
            throw new EndpointException("Вызван несуществующий метод " + method + " " + path);
        }
    }

    private Epic getEpic(HttpExchange exchange) {
        Item item = null;
        try {
            String body = readRequestBody(exchange);
            item = gson.fromJson(body, Item.class);
        } catch (Exception e) {
            throw new RequestException("Ошибка чтения тела запроса");
        }
        if (item.getName() == null || item.getDescription() == null) {
            throw new RequestException("Ошибка в теле запроса");
        }
        Epic epic = new Epic(item.getName(), item.getDescription());
        if (item.getId() != null && item.getId() > 0) {
            epic.setId(item.getId());
        }
        return epic;
    }
}
