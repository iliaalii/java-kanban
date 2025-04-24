package http.handler;

import com.sun.net.httpserver.HttpExchange;
import controller.TaskManager;
import exceptions.NotFoundException;
import exceptions.OverlapException;
import models.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler {
    public SubtaskHandler(TaskManager manager) {
        super(manager);
        this.modelTask = "subtasks";
    }

    protected void getAllTask(HttpExchange exchange) throws IOException {
        try {
            String list = gson.toJson(manager.getListAllSubtask());
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
            sendText(exchange, gson.toJson(manager.getSubtaskById(postIdOpt.get())), 200);
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
                Subtask task = gson.fromJson(body, Subtask.class);
                manager.add(task);
                sendText(exchange, "задача добавлена под номером: " + task.getId(), 201);
            } else {
                sendText(exchange, "Не подходящий json", 400);
            }
        } catch (OverlapException e) {
            sendHasInteractions(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
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
                Subtask task = gson.fromJson(body, Subtask.class);
                if (task.getId() != postIdOpt.get()) {
                    sendNotFound(exchange);
                    return;
                }
                manager.update(task);
                sendText(exchange, "Задача обновлена", 201);
            } else {
                sendText(exchange, "Не подходящий json ", 400);
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
            manager.removeSubtaskById(postIdOpt.get());
            sendText(exchange, "Удаление проведено", 201);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendText(exchange, "Внутренняя ошибка сервера", 500);
        }
    }

    protected void removeAllTask(HttpExchange exchange) throws IOException {
        try {
            manager.removeAllSubtask();
            sendText(exchange, "Удаление проведено", 201);
        } catch (Exception e) {
            sendText(exchange, "Внутренняя ошибка сервера", 500);
        }
    }
}