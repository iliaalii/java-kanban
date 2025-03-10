package controller;

import models.StatusTask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void add() {
        Task task = new Task("Test add", "Test add description", StatusTask.NEW);
        task.setId(1);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
        assertEquals(task, history.get(0), "Задача должна быть добавлена в историю.");
    }

    @Test
    void checkingHistorySize() {
        for (int i = 1; i <= 11; i++) {
            Task task = new Task("Test removeOldestTask " + i, "Test removeOldestTask " + i + " description", StatusTask.NEW);
            task.setId(i);
            historyManager.add(task);
        }
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(11, history.size(), "История должна содержать 10 задач.");
        assertEquals(1, history.get(0).getId(), "Первая задача должна быть первой добавленной задачей.");
        assertEquals(11, history.get(history.size() - 1).getId(), "Последняя задача должна быть одиннадцатой добавленной задачей.");
    }

    @Test
    void checkForDuplicates() {
        ArrayList<Task> checklist = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            Task task = new Task("Test task " + i, "Test removeDuplicates " + i + " description", StatusTask.NEW);
            task.setId(i);
            historyManager.add(task);
            checklist.add(task);
        }
        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size());
        assertEquals(checklist, history);
        Task task = history.get(0);
        historyManager.add(task);
        checklist.remove(0);
        checklist.add(task);
        history = historyManager.getHistory();

        assertEquals(3, history.size(), "Превышено количество, должно быть 3");
        assertEquals(checklist, history, "Не правильный порядок, либо количество");
    }

    @Test
    void getHistory() {
        Task task1 = new Task("Test getHistory 1", "Test getHistory 1 description", StatusTask.NEW);
        Task task2 = new Task("Test getHistory 2", "Test getHistory 2 description", StatusTask.NEW);
        task1.setId(1);
        task2.setId(2);
        historyManager.add(task1);
        historyManager.add(task2);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "История должна содержать две задачи.");
        assertEquals(task1, history.get(0), "Первая задача должна быть добавлена в историю.");
        assertEquals(task2, history.get(1), "Вторая задача должна быть добавлена в историю.");
    }
}