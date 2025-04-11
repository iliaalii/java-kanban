package controller;

import exceptions.ManagerReadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest {

    @BeforeEach
    void beforeEach() {
        try {
            File tempFile = File.createTempFile("TempDataTask", null);
            manager = FileBackedTaskManager.loadFromFile(tempFile);
        } catch (IOException e) {
            throw new ManagerReadException("Ошибка чтения: " + e.getMessage());
        }
        super.beforeEach();
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
        manager = FileBackedTaskManager.loadFromFile(new File("src/resources/TesterFile")); // файл из ручного теста
        assertNotNull(manager.getTaskById(1));
        assertNotNull(manager.getTaskById(4));
        assertNotNull(manager.getSubtaskById(3));
    }

    @Test
    public void testException() {
        assertThrows(ManagerReadException.class, () -> FileBackedTaskManager.loadFromFile(new File("error")));
    }
}