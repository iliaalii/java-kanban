package http.handler;

import com.sun.net.httpserver.HttpExchange;
import controller.TaskManager;
import exceptions.NotFoundException;
import exceptions.OverlapException;
import models.Task;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager manager) {
        super(manager);
        this.modelTask = "tasks";
    }

    protected void getAllTask(HttpExchange exchange) throws IOException {
        try {
            String list = gson.toJson(manager.getListAllTask());
            sendText(exchange, list, 200);
        } catch (Exception e) {
            sendText(exchange, "Внутренняя ошибка сервера", 500);
        }
    }

    protected void getTask(HttpExchange exchange) throws IOException {
        Optional<Integer> postIdOpt = getPostId(exchange);

        if (postIdOpt.isEmpty()) {
            sendNotFound(exchange);
            return;
        }
        try {
            sendText(exchange, gson.toJson(manager.getTaskById(postIdOpt.get())), 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendText(exchange, "Внутренняя ошибка сервера", 500);
        }
    }

    protected void addTask(HttpExchange exchange) throws IOException {
        try {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes());

            if (checkClassJSON(body)) {
                Task task = gson.fromJson(body, Task.class);
                manager.add(task);
                sendText(exchange, "Задача добавлена под номером: " + task.getId(), 201);
            } else {
                sendText(exchange, "Не подходящий json ", 400);
            }
        } catch (OverlapException e) {
            sendHasInteractions(exchange);
        } catch (Exception e) {
            sendText(exchange, "Внутренняя ошибка сервера", 500);
        }
    }

    protected void updateTask(HttpExchange exchange) throws IOException {
        Optional<Integer> postIdOpt = getPostId(exchange);

        if (postIdOpt.isEmpty()) {
            sendText(exchange, "Некорректный идентификатор", 400);
            return;
        }
        try {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes());
            if (checkClassJSON(body)) {
                Task task = gson.fromJson(body, Task.class);
                if (task.getId() != postIdOpt.get()) {
                    sendNotFound(exchange);
                    return;
                }
                manager.update(task);
                sendText(exchange, "Задача обновлена", 201);
            } else {
                sendText(exchange, "Не подходящий json", 400);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendText(exchange, "Внутренняя ошибка сервера", 500);
        }
    }

    protected void removeTask(HttpExchange exchange) throws IOException {
        Optional<Integer> postIdOpt = getPostId(exchange);
        if (postIdOpt.isEmpty()) {
            sendText(exchange, "Некорректный идентификатор", 400);
            return;
        }
        try {
            manager.removeTaskById(postIdOpt.get());
            sendText(exchange, "Удаление проведено", 201);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendText(exchange, "Внутренняя ошибка сервера", 500);
        }
    }

    protected void removeAllTask(HttpExchange exchange) throws IOException {
        try {
            manager.removeAllTask();
            sendText(exchange, "Удаление проведено", 201);
        } catch (Exception e) {
            sendText(exchange, "Внутренняя ошибка сервера", 500);
        }
    }
}