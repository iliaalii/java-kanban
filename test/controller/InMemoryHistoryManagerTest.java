package controller;

import models.StatusTask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void removeDuplicates() {
        for (int i = 1; i <= 3; i++) {
            Task task = new Task("Test removeDuplicates " + i, "Test removeDuplicates " + i + " description", StatusTask.NEW);
            task.setId(i);
            historyManager.add(task);
        }
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.getFirst().getId(), "первой задача должна быть под номером 1");
        assertEquals(3, history.size(), "в истории должно быть 3 задачи");
        historyManager.add(history.getFirst());
        history = historyManager.getHistory();
        assertEquals(2, history.getFirst().getId(), "первой задачей становиться под номером 2");
        assertEquals(3, history.size(), "в истории должно быть 3 задачи");
    }

    @Test
    void remove() {
        for (int i = 1; i <= 3; i++) {
            Task task = new Task("Test removeDuplicates " + i, "Test removeDuplicates " + i + " description", StatusTask.NEW);
            task.setId(i);
            historyManager.add(task);
        }
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.getFirst().getId(), "первая задача под номером 1");
        assertEquals(3, history.size(), "в истории должно быть 3 задачи");
        historyManager.remove(1);
        history = historyManager.getHistory();
        assertEquals(2, history.getFirst().getId(), "первой должна стать 2я задача");
        assertEquals(2, history.size(), "в истории должно быть 2 задачи");

    }

    @Test
    void checkSize() {
        for (int i = 1; i <= 20; i++) {
            Task task = new Task("Test removeOldestTask " + i, "Test removeOldestTask " + i + " description", StatusTask.NEW);
            task.setId(i);
            historyManager.add(task);
        }
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(20, history.size(), "История должна содержать 20 задач.");
        assertEquals(1, history.getFirst().getId(), "Первая задача должна быть первой добавленной задачей.");
        assertEquals(20, history.getLast().getId(), "Последняя задача должна быть двадцатой добавленной задачей.");
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