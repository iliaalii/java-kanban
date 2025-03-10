package controller;

import models.Epic;
import models.StatusTask;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

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
    void checkingHistoryOutput() {
        manager.add(task1);
        manager.add(task2);
        manager.add(epic);
        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getEpicById(3);
        ArrayList<Task> checklist = new ArrayList<>();

        checklist.add(task1);
        checklist.add(task2);
        checklist.add(epic);
        assertEquals(3, manager.getHistory().size());
        assertEquals(manager.getHistory(), checklist);
        for (int i = 0; i < 10; i++) {
            manager.getTaskById(1);
        }
        checklist.remove(0);
        checklist.add(task1);
        assertEquals(3, manager.getHistory().size(), "Превышено количество, должно быть 3");
        assertEquals(manager.getHistory(), checklist, "Не правильный порядок, либо количество");
    }

    @Test
    void checkingHistorySize(){
        for (int i = 0; i < 20; i++) {
            task1 = new Task("задача" + i, "тестируем");
            manager.add(task1);
            manager.getTaskById(task1.getId());
        }
        assertEquals(20, manager.getHistory().size(), "Превышено количество, должно быть 20");
    }

    @Test
    void updateTask() {
        manager.add(epic);
        assertEquals(StatusTask.NEW, manager.getEpicById(1).getStatus());
        Subtask subtask = new Subtask("подзадача", "уже выполнена", 1);
        manager.add(subtask);
        subtask.setStatus(StatusTask.DONE);
        manager.update(subtask);
        assertEquals(StatusTask.DONE, manager.getEpicById(1).getStatus());
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