package ru.yandex.javacource.e.schedule.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.e.schedule.exception.EndpointException;
import ru.yandex.javacource.e.schedule.model.Task;
import ru.yandex.javacource.e.schedule.server.Items;
import ru.yandex.javacource.e.schedule.service.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler<Task> implements HttpHandler {
    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            if (method.equals("GET")) {
                handleGet(exchange, method, path);
            } else {
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
        if (path.matches("^/history$")) {
            sendResponseItems(exchange, new Items<>(manager.getHistory()));
        } else {
            throw new EndpointException("Вызван несуществующий метод " + method + " " + path);
        }
    }
}
