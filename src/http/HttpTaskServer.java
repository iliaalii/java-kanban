package http;


import com.sun.net.httpserver.HttpServer;
import controller.Managers;
import controller.TaskManager;
import http.handler.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.httpServer = HttpServer.create();
        this.httpServer.bind(new InetSocketAddress(PORT), 0);
        this.httpServer.createContext("/tasks", new TaskHandler(manager));
        this.httpServer.createContext("/subtasks", new SubtaskHandler(manager));
        this.httpServer.createContext("/epics", new EpicHandler(manager));
        this.httpServer.createContext("/history", new HistoryHandler(manager));
        this.httpServer.createContext("/prioritized", new PrioritizedHandler(manager));
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer(Managers.getDefaultFileBackedTaskManager());
        server.start();
    }

    public void start() {
        httpServer.start();
        System.out.println("Сервер запущен, порт: " + PORT);
    }

    public void stop() {
        System.out.println("Остановка сервера");
        httpServer.stop(1);
    }
}