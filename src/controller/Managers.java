package controller;

import java.io.File;
import java.nio.file.Files;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getDefaultFileBackedTaskManager() {
        File file = new File("src/resources/TasksData");
        if (!Files.exists(file.toPath())) {
            try {
                Files.createFile(file.toPath());
            } catch (Exception exp) {
                System.out.println("Не удалось создать файл");
            }
        }
        return FileBackedTaskManager.loadFromFile(file);
    }
}