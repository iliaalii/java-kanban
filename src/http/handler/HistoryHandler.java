package http.handler;

import com.sun.net.httpserver.HttpExchange;
import controller.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();
        String[] pathParts = requestPath.split("/");
        try {
            if (pathParts.length == 2 && pathParts[1].equals("history") && requestMethod.equals("GET")) {
                sendText(exchange, gson.toJson(manager.getHistory()), 200);
            } else {
                sendText(exchange, "Метод не поддерживается ", 405);
            }
        } catch (Exception e) {
            sendText(exchange, "Внутренняя ошибка сервера", 500);
        }
    }
}
