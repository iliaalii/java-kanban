package controller;

import models.Epic;
import models.StatusTask;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    T manager;
    Task task1;
    Task task2;
    Epic epic;
    Subtask subtask1;
    Subtask subtask2;


    @BeforeEach
    void beforeEach() {
        task1 = new Task("задача один", "простая задача");
        task2 = new Task("задача два", "простая задача, чуть другая");
        epic = new Epic("эпик", "ЭПИЧЕСКОЕ ЗАДАНИЕ");
        subtask1 = new Subtask("подзадача 1", "связан с эпик N1", 1);
        subtask2 = new Subtask("подзадача 2", "связан с эпик N1", 1);
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

    @Test
    void thereIsNoConflictWithAddingATaskWithTheSameId() {
        manager.add(task1);
        task2.setId(1);
        manager.add(task2);
        assertEquals(2, manager.getListAllTask().size());
        assertNotEquals(manager.getTaskById(1), manager.getTaskById(2));
    }

    @Test
    void epicStatusCalculationAndUpdateTask() {
        manager.add(epic);
        manager.add(subtask1);
        manager.add(subtask2);

        //a. Все подзадачи со статусом NEW.
        assertEquals(StatusTask.NEW, epic.getStatus());

        //b. Все подзадачи со статусом DONE.
        subtask1.setStatus(StatusTask.DONE);
        subtask2.setStatus(StatusTask.DONE);
        manager.update(subtask1);
        manager.update(subtask2);
        assertEquals(StatusTask.DONE, epic.getStatus());

        //c. Подзадачи со статусами NEW и DONE.
        subtask1.setStatus(StatusTask.NEW);
        manager.update(subtask1);
        assertEquals(StatusTask.IN_PROGRESS, epic.getStatus());

        //d. Подзадачи со статусом IN_PROGRESS.
        subtask1.setStatus(StatusTask.IN_PROGRESS);
        manager.update(subtask1);
        assertEquals(StatusTask.IN_PROGRESS, epic.getStatus());

        subtask2.setStatus(StatusTask.NEW);
        manager.update(subtask2);
        assertEquals(StatusTask.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void tasksOverlap() {
        task1 = new Task("task", "", StatusTask.NEW, 0,
                Duration.ofMinutes(30), LocalDateTime.of(2025, Month.APRIL, 10, 12, 0));
        task2 = new Task("task", "", StatusTask.NEW, 0,
                Duration.ofMinutes(30), LocalDateTime.of(2025, Month.APRIL, 10, 12, 20));
        manager.add(task1);
        manager.add(task2);
        manager.add(epic);
        assertEquals(1, manager.getListAllTask().size(), "1 элемент в списке");
        task2 = new Task("task", "", StatusTask.NEW, 0,
                Duration.ofMinutes(30), LocalDateTime.of(2025, Month.APRIL, 10, 13, 20));
        manager.add(task2);
        assertEquals(2, manager.getListAllTask().size(), "2 элемента в списке");

        task2 = new Task("task", "", StatusTask.NEW, 0,
                Duration.ofMinutes(30), LocalDateTime.of(2025, Month.APRIL, 10, 9, 20));
        manager.add(task2);
        assertEquals(3, manager.getListAllTask().size(), "3 элемента в списке");
        assertEquals(4, manager.getPrioritizedTasks().stream().findFirst().get().getId(), "последняя добавленная задача, первая в приоритете");
    }

    @Test
    void removeHistory() {
        manager.add(task1);
        manager.add(task2);
        manager.getTaskById(1);
        manager.getTaskById(2);
        assertEquals(2, manager.getHistory().size());
        manager.removeTaskById(1);
        assertEquals(1, manager.getHistory().size());
        manager.removeAllTask();
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    void removePrioritizedTask() {
        task1 = new Task("task", "", StatusTask.NEW, 0,
                Duration.ofMinutes(30), LocalDateTime.of(2025, Month.APRIL, 10, 12, 0));
        task2 = new Task("task", "", StatusTask.NEW, 0,
                Duration.ofMinutes(30), LocalDateTime.of(2025, Month.APRIL, 10, 13, 20));
        manager.add(task1);
        manager.add(task2);
        assertEquals(2, manager.getPrioritizedTasks().size());
        manager.removeTaskById(1);
        assertEquals(1, manager.getPrioritizedTasks().size());
        manager.removeAllTask();
        assertEquals(0, manager.getPrioritizedTasks().size());
    }

    @Test
    void getAllSubtaskOfEpic() {
        manager.add(epic);
        manager.add(subtask1);
        manager.add(subtask2);
        assertEquals(2, manager.getAllSubtaskOfEpic(epic).size());
        manager.update(subtask2);
        assertEquals(2, manager.getAllSubtaskOfEpic(epic).size());
    }
}