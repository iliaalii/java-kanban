package models;

import controller.Managers;
import controller.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void taskEqualityCheck() {
        TaskManager manager = Managers.getDefault();
        Task task = new Task("задача1", "автоматический ID");
        Task task2 = new Task("задача2", "ручной ID");
        task2.setId(1);
        manager.add(task);
        assertEquals(task, task2, "Задачи считаются разными, так как не совпадает ID");
        assertTrue(task.getClass() == Task.class);
    }
}