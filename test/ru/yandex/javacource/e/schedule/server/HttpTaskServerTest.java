package ru.yandex.javacource.e.schedule.server;

import com.google.gson.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.e.schedule.model.Epic;
import ru.yandex.javacource.e.schedule.model.SubTask;
import ru.yandex.javacource.e.schedule.model.Task;
import ru.yandex.javacource.e.schedule.model.TaskStatus;
import ru.yandex.javacource.e.schedule.service.Managers;
import ru.yandex.javacource.e.schedule.service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private TaskManager manager;
    private HttpTaskServer server;
    private Gson gson;

    @BeforeEach
    public void start() {
        manager = Managers.getDefault();
        server = new HttpTaskServer(manager);
        gson = Managers.getDefaultGson();
        server.start();
    }

    @AfterEach
    public void close() {
        server.stop();
    }

    @Test
    @DisplayName("Получение всех задач")
    public void shouldGetAllTask() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task description",
                Duration.ofMinutes(10), LocalDateTime.now(), TaskStatus.NEW);
        manager.createTask(task);
        task = new Task("Task 2", "Task 2 description",
                Duration.ofMinutes(10), LocalDateTime.now().plusDays(1), TaskStatus.NEW);
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray jsonArray = jsonObject.get("items").getAsJsonArray();

        List<Task> tasks = manager.getAllTasks();

        assertEquals(tasks.size(), jsonArray.size(), "Некорректное количество задач");
    }

    @Test
    @DisplayName("Получение задачи")
    public void shouldGetTask() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task description",
                Duration.ofMinutes(10), LocalDateTime.now(), TaskStatus.NEW);
        manager.createTask(task);
        task = new Task("Task 2", "Task 2 description",
                Duration.ofMinutes(10), LocalDateTime.now().plusDays(1), TaskStatus.NEW);
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int id = jsonObject.get("id").getAsInt();

        assertEquals(id, task.getId(), "Некорректное количество задач");
    }

    @Test
    @DisplayName("Добавление задачи")
    public void shouldCreateTask() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task description",
                Duration.ofMinutes(10), LocalDateTime.now(), TaskStatus.NEW);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals("Task", tasks.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    @DisplayName("Обновление задачи")
    public void shouldUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task description",
                Duration.ofMinutes(10), LocalDateTime.now(), TaskStatus.NEW);
        int id = manager.createTask(task).getId();

        task = new Task("Task update", task.getDescription(),
                task.getDuration(), task.getStartTime(), task.getStatus());
        task.setId(id);

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasks = manager.getAllTasks();

        assertEquals("Task update", tasks.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    @DisplayName("Удаление задачи")
    public void shouldDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task description",
                Duration.ofMinutes(10), LocalDateTime.now(), TaskStatus.NEW);
        task = manager.createTask(task);
        List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        tasks = manager.getAllTasks();

        assertEquals(0, tasks.size(), "Задача не удалена");
    }

    @Test
    @DisplayName("Удаление всех задач")
    public void shouldDeleteAllTask() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task description",
                Duration.ofMinutes(10), LocalDateTime.now(), TaskStatus.NEW);
        manager.createTask(task);
        task = new Task("Task 2", "Task 2 description",
                Duration.ofMinutes(10), LocalDateTime.now().plusDays(1), TaskStatus.NEW);
        manager.createTask(task);

        List<Task> tasks = manager.getAllTasks();

        assertEquals(2, tasks.size(), "Некорретное кол-во задач");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        tasks = manager.getAllTasks();

        assertEquals(0, tasks.size(), "Задачи не удалены");
    }

    @Test
    @DisplayName("Получение всех эпиков")
    public void shouldGetAllEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);
        epic = new Epic("Epic 2", "Epic 2 description");
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray jsonArray = jsonObject.get("items").getAsJsonArray();

        List<Epic> epics = manager.getAllEpics();

        assertEquals(epics.size(), jsonArray.size(), "Некорректное количество эпиков");
    }

    @Test
    @DisplayName("Получение эпика")
    public void shouldGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);
        epic = new Epic("Epic 2", "Epic 2 description");
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int id = jsonObject.get("id").getAsInt();

        assertEquals(id, epic.getId(), "Некорректное количество эпика");
    }

    @Test
    @DisplayName("Добавление эпика")
    public void shouldCreateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> epics = manager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Некорректное количество эпиков");
        assertEquals("Epic", epics.getFirst().getName(), "Некорректное имя эпика");
    }

    @Test
    @DisplayName("Обновление эпика")
    public void shouldUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        int id = manager.createEpic(epic).getId();

        epic = new Epic("Epic update", epic.getDescription());
        epic.setId(id);

        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> epics = manager.getAllEpics();

        assertEquals("Epic update", epics.getFirst().getName(), "Некорректное имя эпика");
    }

    @Test
    @DisplayName("Удаление эпика")
    public void shouldDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);
        List<Epic> epics = manager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        epics = manager.getAllEpics();

        assertEquals(0, epics.size(), "Эпик не удален");
    }

    @Test
    @DisplayName("Удаление всех эпиков")
    public void shouldDeleteAllEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);
        epic = new Epic("Epic 2", "Epic 2 description");
        manager.createEpic(epic);

        List<Epic> epics = manager.getAllEpics();

        assertEquals(2, epics.size(), "Некорретное кол-во эпикоа");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        epics = manager.getAllEpics();

        assertEquals(0, epics.size(), "Эпики не удалены");
    }

    @Test
    @DisplayName("Получение всех подзадач")
    public void shouldGetAllSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask", "SubTask description",
                Duration.ofMinutes(10), LocalDateTime.now(), TaskStatus.NEW, epic);
        manager.addNewSubTask(subTask);
        subTask = new SubTask("SubTask 2", "SubTask 2 description",
                Duration.ofMinutes(10), LocalDateTime.now().plusDays(1), TaskStatus.NEW, epic);
        manager.addNewSubTask(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray jsonArray = jsonObject.get("items").getAsJsonArray();

        List<SubTask> subTasks = manager.getAllSubTasks();

        assertEquals(subTasks.size(), jsonArray.size(), "Некорректное количество подзадач");
    }

    @Test
    @DisplayName("Получение подзадачи")
    public void shouldGetSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask", "SubTask description",
                Duration.ofMinutes(10), LocalDateTime.now(), TaskStatus.NEW, epic);
        manager.addNewSubTask(subTask);
        subTask = new SubTask("SubTask 2", "SubTask 2 description",
                Duration.ofMinutes(10), LocalDateTime.now().plusDays(1), TaskStatus.NEW, epic);
        manager.addNewSubTask(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subTask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int id = jsonObject.get("id").getAsInt();

        assertEquals(id, subTask.getId(), "Некорректное количество подзадач");
    }

    @Test
    @DisplayName("Добавление подзадачи")
    public void shouldCreateSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask", "SubTask description",
                Duration.ofMinutes(10), LocalDateTime.now(), TaskStatus.NEW, epic);
        String taskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<SubTask> subTasks = manager.getAllSubTasks();

        assertNotNull(subTasks, "Подзадачи не возвращаются");
        assertEquals(1, subTasks.size(), "Некорректное количество подзадач");
        assertEquals("SubTask", subTasks.getFirst().getName(), "Некорректное имя подзадачи");
    }

    @Test
    @DisplayName("Обновление подзадачи")
    public void shouldUpdateSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask", "SubTask description",
                Duration.ofMinutes(10), LocalDateTime.now(), TaskStatus.NEW, epic);
        int id = manager.addNewSubTask(subTask).getId();

        subTask = new SubTask("SubTask update", subTask.getDescription(),
                subTask.getDuration(), subTask.getStartTime(), subTask.getStatus(), subTask.getEpicId());
        subTask.setId(id);

        String subTaskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<SubTask> subTasks = manager.getAllSubTasks();

        assertEquals("SubTask update", subTasks.getFirst().getName(), "Некорректное имя подзадачи");
    }

    @Test
    @DisplayName("Удаление подзадачи")
    public void shouldDeleteSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask", "SubTask description",
                Duration.ofMinutes(10), LocalDateTime.now(), TaskStatus.NEW, epic);
        manager.addNewSubTask(subTask);
        List<SubTask> subTasks = manager.getAllSubTasks();

        assertNotNull(subTasks, "Подзадачи не возвращаются");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subTask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        subTasks = manager.getAllSubTasks();

        assertEquals(0, subTasks.size(), "Подзадача не удалена");
    }

    @Test
    @DisplayName("Удаление всех подзадач")
    public void shouldDeleteAllSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask", "SubTask description",
                Duration.ofMinutes(10), LocalDateTime.now(), TaskStatus.NEW, epic);
        manager.addNewSubTask(subTask);
        subTask = new SubTask("SubTask 2", "SubTask 2 description",
                Duration.ofMinutes(10), LocalDateTime.now().plusDays(1), TaskStatus.NEW, epic);
        manager.addNewSubTask(subTask);

        List<SubTask> subTasks = manager.getAllSubTasks();

        assertEquals(2, subTasks.size(), "Некорретное кол-во подзадач");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        subTasks = manager.getAllSubTasks();

        assertEquals(0, subTasks.size(), "Подзадачи не удалены");
    }

    @Test
    @DisplayName("Пересечение задач")
    public void shouldValidationTask() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task description",
                Duration.ofMinutes(10), LocalDateTime.now(), TaskStatus.NEW);
        manager.createTask(task).getId();
        task = new Task(task.getName(), task.getDescription(),
                task.getDuration(), task.getStartTime(), task.getStatus());

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    @DisplayName("Получение истории")
    public void shouldGetHistory() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task description",
                Duration.ofMinutes(10), LocalDateTime.now(), TaskStatus.NEW);
        manager.createTask(task);
        manager.getTask(task.getId());
        task = new Task("Task 2", "Task 2 description",
                Duration.ofMinutes(10), LocalDateTime.now().plusDays(1), TaskStatus.NEW);
        manager.createTask(task);
        manager.getTask(task.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray jsonArray = jsonObject.get("items").getAsJsonArray();

        List<Task> tasks = manager.getHistory();

        assertEquals(tasks.size(), jsonArray.size(), "Некорректное количество задач");
    }

    @Test
    @DisplayName("Получение задач в порядке приоритета")
    public void shouldGetPrioritized() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task description",
                Duration.ofMinutes(10), LocalDateTime.now(), TaskStatus.NEW);
        manager.createTask(task);
        task = new Task("Task 2", "Task 2 description",
                Duration.ofMinutes(10), LocalDateTime.now().minusDays(1), TaskStatus.NEW);
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray jsonArray = jsonObject.get("items").getAsJsonArray();

        List<Task> tasks = manager.getPrioritizedTasks();

        assertEquals(tasks.size(), jsonArray.size(), "Некорректное количество задач");
    }
}