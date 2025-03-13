package controller;

import models.Epic;
import models.StatusTask;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    InMemoryTaskManager manager;
    Task task1;
    Task task2;
    Epic epic;

    @BeforeEach
    void beforeEach() {
        manager = new InMemoryTaskManager();
        task1 = new Task("задача один", "простая задача");
        task2 = new Task("задача два", "простая задача, чуть другая");
        epic = new Epic("эпик", "ЭПИЧЕСКОЕ ЗАДАНИЕ");
    }

    @Test
    void addingAndSearchingById() {
        manager.add(task1);
        manager.add(task2);
        manager.add(epic);
        assertNotNull(manager.getTaskById(1));
        assertNotNull(manager.getTaskById(2));
        assertNotNull(manager.getEpicById(3));
    }

    @Test
    void thereIsNoConflictWithAddingATaskWithTheSameId() {
        manager.add(task1);
        task2.setId(1);
        manager.add(task2);
        assertEquals(2, manager.getListAllTask().size());
        assertNotEquals(manager.getTaskById(1), manager.getTaskById(2));
    }

    @Test
    void updateTask() {
        manager.add(epic);
        assertEquals(StatusTask.NEW, manager.getEpicById(1).getStatus());
        Subtask subtask1 = new Subtask("подзадача 1", "статус станет done", 1);
        Subtask subtask2 = new Subtask("подзадача 2", "статус new", 1);
        manager.add(subtask1);
        manager.add(subtask2);
        assertEquals(StatusTask.NEW, manager.getEpicById(1).getStatus(), "статус должен быть NEW");
        subtask1.setStatus(StatusTask.DONE);
        manager.update(subtask1);
        assertEquals(StatusTask.IN_PROGRESS, manager.getEpicById(1).getStatus(), "статус должен быть IN_PROGRESS");
        subtask2.setStatus(StatusTask.DONE);
        manager.update(subtask2);
        assertEquals(StatusTask.DONE, manager.getEpicById(1).getStatus(), "статус должен быть DONE");

    }

    @Test
    void remove() {
        manager.add(task1);
        manager.add(task2);
        assertEquals(2, manager.getListAllTask().size());
        manager.removeTaskById(1);
        assertEquals(1, manager.getListAllTask().size());
        for (int i = 0; i < 5; i++) {
            manager.add(task1);
        }
        manager.removeAllTask();
        assertEquals(0, manager.getListAllTask().size());
    }
}