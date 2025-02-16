package models;

import controller.Managers;
import controller.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void taskEqualityCheck() {
        TaskManager manager = Managers.getDefault();
        Epic task = new Epic("задача1", "автоматический ID");
        Epic task2 = new Epic("задача2", "ручной ID");
        task2.setId(1);
        manager.add(task);
        assertEquals(task, task2, "Задачи считаются разными, так как не совпадает ID");
        assertTrue(task.getClass() == Epic.class);
    }
}