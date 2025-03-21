package controller;

import java.io.*;

import exceptions.ManagerSaveException;
import exceptions.ManagerReadException;
import models.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File tasksData;

    public FileBackedTaskManager(File file) {
        this.tasksData = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerReadException {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                String[] taskData = br.readLine().split(",");
                switch (TypesOfTasks.valueOf(taskData[1])) {
                    case TASK:
                        Task task = new Task(taskData);
                        taskManager.add(task);
                        break;
                    case EPIC:
                        Epic epic = new Epic(taskData);
                        taskManager.add(epic);
                        break;
                    case SUBTASK:
                        Subtask subtask = new Subtask(taskData);
                        taskManager.add(subtask);
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerReadException("Ошибка чтения: " + e.getMessage());
        }
        return taskManager;
    }

    private void save() throws ManagerSaveException {
        try (Writer writer = new FileWriter(tasksData)) {
            for (Task task : super.getListAllTask()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic task : super.getListAllEpic()) {
                writer.write(toString(task) + "\n");
            }
            for (Subtask task : super.getListAllSubtask()) {
                writer.write(toString(task) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения: " + e.getMessage());
        }
    }

    private String toString(Task task) {
        return String.format("%s,%s,%s,%s,%s",
                task.getId(),
                TypesOfTasks.TASK,
                task.getTitle(),
                task.getDescription(),
                task.getStatus());
    }

    private String toString(Epic task) {
        return String.format("%s,%s,%s,%s,%s",
                task.getId(), TypesOfTasks.EPIC,
                task.getTitle(),
                task.getDescription(),
                task.getStatus());
    }

    private String toString(Subtask task) {
        return String.format("%s,%s,%s,%s,%s,%s",
                task.getId(),
                TypesOfTasks.SUBTASK,
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getEpicId());
    }

    @Override
    public void add(Task task) {
        super.add(task);
        save();
    }

    @Override
    public void add(Epic epic) {
        super.add(epic);
        save();
    }

    @Override
    public void add(Subtask subtask) {
        super.add(subtask);
        save();
    }

    @Override
    public void removeAllTask() {
        super.removeAllTask();
        save();
    }

    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        save();
    }

    @Override
    public void removeAllSubtask() {
        super.removeAllSubtask();
        save();
    }

    @Override
    public void update(Task task) {
        super.update(task);
        save();
    }

    @Override
    public void update(Epic newEpic) {
        super.update(newEpic);
        save();
    }

    @Override
    public void update(Subtask newSubtask) {
        super.update(newSubtask);
        save();
    }

    @Override
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);
        save();
    }

    @Override
    public void removeEpicById(int epicId) {
        super.removeEpicById(epicId);
        save();
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        super.removeSubtaskById(subtaskId);
        save();
    }
}