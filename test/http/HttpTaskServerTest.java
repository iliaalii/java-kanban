package http;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import controller.Managers;
import controller.TaskManager;
import models.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    TaskManager manager;
    Task task1;
    Task task2;
    Epic epic;
    Subtask subtask1;
    Subtask subtask2;

    HttpTaskServer server;
    HttpClient client = HttpClient.newHttpClient();
    URI url;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    HttpResponse<String> response;
    HttpRequest request;

    @BeforeEach
    void beforeEach() throws IOException {
        task1 = new Task("задача один", "простая задача");
        task2 = new Task("задача два", "простая задача, чуть другая");
        epic = new Epic("эпик", "ЭПИЧЕСКОЕ ЗАДАНИЕ");
        subtask1 = new Subtask("подзадача 1", "связан с эпик N1", 1);
        subtask2 = new Subtask("подзадача 2", "связан с эпик N1", 1);
        manager = Managers.getDefault();
        server = new HttpTaskServer(manager);
        server.start();
    }

    @AfterEach
    void afterEach() {
        server.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getListAllEpic();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("эпик", epicsFromManager.get(0).getTitle(), "Некорректное имя задачи");


        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1))).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getListAllSubtask();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("подзадача 1", subtasksFromManager.get(0).getTitle(), "Некорректное имя задачи");


        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1))).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getListAllTask();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("задача один", tasksFromManager.get(0).getTitle(), "Некорректное имя задачи");
    }

    @Test
    void errorAddTask() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString("")).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(500, response.statusCode());

        List<Task> tasksFromManager = manager.getListAllTask();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");

        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1))).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getListAllSubtask();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(0, subtasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void getTasksList() throws IOException, InterruptedException {
        class TaskListTypeToken extends TypeToken<List<Task>> {

        }

        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1))).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2))).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        List<Task> tasks = gson.fromJson(jsonArray, new TaskListTypeToken().getType());

        assertEquals(200, response.statusCode());
        assertEquals(2, tasks.size(), "Некорректное количество задач");
        assertEquals(tasks, manager.getListAllTask());
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1))).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        url = URI.create("http://localhost:8080/tasks/1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Не удалось получить задачу по ID");

        Task task = gson.fromJson(response.body(), Task.class);

        assertEquals(task1.getTitle(), task.getTitle());
        assertEquals(1, task.getId());
    }

    @Test
    void overlapTask() throws IOException, InterruptedException {
        task1 = new Task("Task test1", "проверка создания", StatusTask.NEW, 1, Duration.parse("PT30M"), LocalDateTime.parse("2025-04-10T12:00"));
        task2 = new Task("Task test2", "проверка создания", StatusTask.NEW, 1, Duration.parse("PT30M"), LocalDateTime.parse("2025-04-10T12:00"));

        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1))).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2))).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
        assertEquals(1, manager.getListAllTask().size(), "Некорректное количество задач");


        task2 = new Task("Task test2", "проверка создания", StatusTask.NEW, 1, Duration.parse("PT30M"), LocalDateTime.parse("2025-04-10T13:00"));
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2))).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(2, manager.getListAllTask().size(), "Некорректное количество задач");
    }

    @Test
    void updateTask() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1))).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        url = URI.create("http://localhost:8080/tasks/1");

        assertEquals(StatusTask.NEW, manager.getTaskById(1).getStatus());

        task1.setStatus(StatusTask.DONE);
        task1.setId(1);

        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1))).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getListAllTask().size());
        assertEquals(StatusTask.DONE, manager.getTaskById(1).getStatus());
    }
}