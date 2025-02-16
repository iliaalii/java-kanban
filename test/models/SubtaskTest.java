package models;

import controller.Managers;
import controller.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    TaskManager manager;
    Epic epic;
    Subtask task;
    Subtask task2;

    @BeforeEach
    void beforeEach() {
        manager = Managers.getDefault();
        epic = new Epic("эпик", "основной эпик");
        task = new Subtask("задача1", "автоматический ID", 1);
        task2 = new Subtask("задача2", "ручной ID", 1);
    }

    @Test
    void taskEqualityCheck() {
        task2.setId(2);
        manager.add(epic);
        manager.add(task);
        assertEquals(task, task2, "Задачи считаются разными, так как не совпадает ID");
        assertTrue(task.getClass() == Subtask.class);
    }

    @Test
    void checkForAddingASubtaskToItself() {
        Subtask task3 = new Subtask("задача3", "копия первой подзадачи, с привязкой к ней", 2);
        manager.add(epic);
        manager.add(task);
        manager.add(task3);
        assertEquals(1, manager.getListAllSubtask().size(),
                "Не должен превышать 1 (количество актуальных подзадач");
    }
}