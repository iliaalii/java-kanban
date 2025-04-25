package http.handler;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;


public class BaseHttpHandler implements HttpHandler {
    protected enum Endpoint {
        GET_TASK,
        GET_ALL_TASKS,
        ADD_TASK,
        UPDATE_TASK,
        DELETE_TASK,
        DELETE_ALL_TASK,
        UNKNOWN
    }

    TaskManager manager;
    String modelTask;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
    }

    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals(modelTask)) {
            switch (requestMethod) {
                case "GET" -> {
                    return Endpoint.GET_ALL_TASKS;
                }
                case "POST" -> {
                    return Endpoint.ADD_TASK;
                }
                case "DELETE" -> {
                    return Endpoint.DELETE_ALL_TASK;
                }
            }
        }
        if (pathParts.length == 3 && pathParts[1].equals(modelTask)) {
            switch (requestMethod) {
                case "GET" -> {
                    return Endpoint.GET_TASK;
                }
                case "POST" -> {
                    return Endpoint.UPDATE_TASK;
                }
                case "DELETE" -> {
                    return Endpoint.DELETE_TASK;
                }
            }
        }
        return Endpoint.UNKNOWN;
    }

    protected void sendText(HttpExchange exchange, String text, int responseCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(responseCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "Not Found", 404);
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        sendText(exchange, "Not Acceptable", 406);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_ALL_TASKS -> getAllTask(exchange);
            case GET_TASK -> getTask(exchange);
            case ADD_TASK -> addTask(exchange);
            case UPDATE_TASK -> updateTask(exchange);
            case DELETE_TASK -> removeTask(exchange);
            case DELETE_ALL_TASK -> removeAllTask(exchange);
            default -> sendText(exchange, "Endpoint not found", 404);
        }
    }

    protected void getAllTask(HttpExchange exchange) throws IOException {
    }

    protected void getTask(HttpExchange exchange) throws IOException {
    }

    protected void addTask(HttpExchange exchange) throws IOException {
    }

    protected void updateTask(HttpExchange exchange) throws IOException {
    }

    protected void removeTask(HttpExchange exchange) throws IOException {
    }

    protected void removeAllTask(HttpExchange exchange) throws IOException {
    }

    protected Optional<Integer> getPostId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    protected boolean checkClassJSON(String str) {
        JsonElement body = JsonParser.parseString(str);
        JsonObject obj = body.getAsJsonObject();

        return body.isJsonObject()
                && obj.has("title")
                && obj.get("title").getAsJsonPrimitive().isString()
                && obj.has("description")
                && obj.get("description").getAsJsonPrimitive().isString();
    }
}