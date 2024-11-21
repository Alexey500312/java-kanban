package ru.yandex.javacource.e.schedule.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.e.schedule.exception.EndpointException;
import ru.yandex.javacource.e.schedule.exception.NullTaskException;
import ru.yandex.javacource.e.schedule.exception.RequestException;
import ru.yandex.javacource.e.schedule.exception.TaskValidationException;
import ru.yandex.javacource.e.schedule.model.Task;
import ru.yandex.javacource.e.schedule.model.Items;
import ru.yandex.javacource.e.schedule.service.Managers;
import ru.yandex.javacource.e.schedule.service.TaskManager;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class BaseHttpHandler<T extends Task> implements HttpHandler {
    protected static final int HTTP_OK = 200;
    protected static final int HTTP_CREATED = 201;
    protected static final int HTTP_NO_CONTENT = 204;
    protected static final int HTTP_BAD_REQUEST = 400;
    protected static final int HTTP_NOT_FOUND = 404;
    protected static final int HTTP_NOT_ACCEPTABLE = 406;
    protected static final int HTTP_INTERNAL_SERVER_ERROR = 500;

    protected TaskManager manager;
    protected Gson gson;

    public BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = Managers.getDefaultGson();
    }

    @Override
    public abstract void handle(HttpExchange exchange) throws IOException;

    protected String readRequestBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendResponse(HttpExchange exchange, int code, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        int respLength = code != 204 ? resp.length : -1;
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(code, respLength);
        exchange.getResponseBody().write(resp);
    }

    protected void sendResponseItem(HttpExchange exchange, int code, T item) throws IOException {
        String response = gson.toJson(item);
        sendResponse(exchange, code, response);
    }

    protected void sendResponseItems(HttpExchange exchange, Items<T> items) throws IOException {
        String response = gson.toJson(items);
        sendResponse(exchange, HTTP_OK, response);
    }

    protected void sendResponseError(HttpExchange exchange, Exception e) {
        try {
            if (e instanceof NullTaskException || e instanceof EndpointException) {
                sendResponse(exchange, HTTP_NOT_FOUND, e.getMessage());
            } else if (e instanceof TaskValidationException) {
                sendResponse(exchange, HTTP_NOT_ACCEPTABLE, e.getMessage());
            } else if (e instanceof RequestException) {
                sendResponse(exchange, HTTP_BAD_REQUEST, e.getMessage());
            } else {
                sendResponse(exchange, HTTP_INTERNAL_SERVER_ERROR, "Ошибка сервера");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected int getId(String path) {
        String id = path.replaceAll("^.+/", "");
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
