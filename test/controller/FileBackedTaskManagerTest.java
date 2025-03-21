package controller;

import exceptions.ManagerReadException;
import models.Epic;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    FileBackedTaskManager manager;
    Task task1;
    Task task2;
    Epic epic;

    @BeforeEach
    void beforeEach() throws ManagerReadException {
        try {
            File tempFile = File.createTempFile("TempDataTask", null);
            manager = FileBackedTaskManager.loadFromFile(tempFile);
        } catch (IOException e) {
            throw new ManagerReadException("Ошибка чтения: " + e.getMessage());
        }
        task1 = new Task("задача один", "простая задача");
        task2 = new Task("задача два", "простая задача, чуть другая");
        epic = new Epic("эпик", "ЭПИЧЕСКОЕ ЗАДАНИЕ");
    }

    @Test
    void uploadFromEmptyFile() {
        assertTrue(manager.getListAllTask().isEmpty());
        assertTrue(manager.getListAllEpic().isEmpty());
        assertTrue(manager.getListAllSubtask().isEmpty());
        manager.add(task1);
        assertFalse(manager.getListAllTask().isEmpty());
        assertNotNull(manager.getTaskById(1));
    }

    @Test
    void uploadingANonEmptyFile() {
        manager = FileBackedTaskManager.loadFromFile(new File("src/util/TesterFile")); // файл из ручного теста
        assertNotNull(manager.getTaskById(1));
        assertNotNull(manager.getTaskById(4));
        assertNotNull(manager.getSubtaskById(3));
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
}