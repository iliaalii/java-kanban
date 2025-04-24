package http.handler;

import com.sun.net.httpserver.HttpExchange;
import controller.TaskManager;
import exceptions.NotFoundException;
import exceptions.OverlapException;
import models.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {
    public EpicHandler(TaskManager manager) {
        super(manager);
        this.modelTask = "epics";
    }

    protected void getAllTask(HttpExchange exchange) throws IOException {
        try {
            String list = gson.toJson(manager.getListAllEpic());
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
            sendText(exchange, gson.toJson(manager.getEpicById(postIdOpt.get())), 200);
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
                Epic task = gson.fromJson(body, Epic.class);
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
                Epic task = gson.fromJson(body, Epic.class);
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
            manager.removeEpicById(postIdOpt.get());
            sendText(exchange, "Удаление проведено", 201);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendText(exchange, "Внутренняя ошибка сервера", 500);
        }
    }

    protected void removeAllTask(HttpExchange exchange) throws IOException {
        try {
            manager.removeAllEpic();
            sendText(exchange, "Удаление проведено", 201);
        } catch (Exception e) {
            sendText(exchange, "Внутренняя ошибка сервера", 500);
        }
    }
}