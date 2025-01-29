package controller;

import models.Task;
import models.Epic;
import models.Subtask;
import models.StatusTask;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();                    // список простых задач
    private final HashMap<Integer, Epic> epics = new HashMap<>();                    // список epic задач
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();              // список подзадач epic(а)
    private int idCounter = 1;

    //Получение списка по типу задачи
    public ArrayList<Task> getListAllTask() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getListAllEpic() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getListAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    //Добавление новых задач
    public void addTask(Task task) {
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epic.setId(idCounter++);
        epic.setStatus(StatusTask.NEW);
        epics.put(epic.getId(), epic);
    }

    public void addSubtask(Subtask subtask) {
        if (epics.get(subtask.getEpicId()) != null) {
            subtask.setId(idCounter++);
            subtasks.put(subtask.getId(), subtask);
            checkStatusEpic(epics.get(subtask.getEpicId()));
        }
    }

    //Удаление всего списка по типу задачи
    public void removeAllTask() {
        tasks.clear();
    }

    public void removeAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    public void removeAllSubtask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.setStatus(StatusTask.NEW);
        }
    }

    //Поиск по ID
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Task getEpicById(int id) {
        return epics.get(id);
    }

    public Task getSubtaskById(int id) {
        return subtasks.get(id);
    }

    //Перезапись данных
    public void updateTask(Task task) {
        if (tasks.get(task.getId()) != null) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.get(epic.getId()) != null) {
            checkStatusEpic(epic);
            epics.put(epic.getId(), epic);
        }
    }

    public void updateSubtask(Subtask newSubtask) {
        Subtask oldSubtask = subtasks.get(newSubtask.getId());
        if (oldSubtask != null) {
            if (oldSubtask.getEpicId() == newSubtask.getEpicId()) {
                subtasks.put(newSubtask.getId(), newSubtask);
                checkStatusEpic(epics.get(newSubtask.getEpicId()));
            } else {
                Epic oldEpic = epics.get(oldSubtask.getEpicId());
                subtasks.put(newSubtask.getId(), newSubtask);
                checkStatusEpic(epics.get(newSubtask.getEpicId()));
                checkStatusEpic(oldEpic);
            }
        }
    }

    //Удаление по ID
    public void removeTaskById(int taskId) {
        tasks.remove(taskId);
    }

    public void removeEpicById(int epicId) {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                subtasks.remove(subtask.getId());
            }
        }
        epics.remove(epicId);
    }

    public void removeSubtaskById(int subtaskId) {
        Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());
        subtasks.remove(subtaskId);
        checkStatusEpic(epic);
    }

    //Проверка статуса EPIC
    private void checkStatusEpic(Epic epic) {
        epic.setStatus(StatusTask.NEW);
        int counterSubtasksEpic = 0;
        int counterDoneSubtasks = 0;
        for (Subtask subtask : subtasks.values()) {
            if (epic.getId() == subtask.getEpicId()) {
                if (subtask.getStatus().equals(StatusTask.IN_PROGRESS)) {
                    epic.setStatus(StatusTask.IN_PROGRESS);
                    return;
                } else if (subtask.getStatus().equals(StatusTask.DONE)) {
                    counterDoneSubtasks++;
                }
                counterSubtasksEpic++;
            }
        }
        if (counterSubtasksEpic == counterDoneSubtasks && counterSubtasksEpic != 0) {
            epic.setStatus(StatusTask.DONE);
        }
    }

    //Получение списка всех ID подзадач определенного эпика
    public ArrayList<Subtask> getAllSubtaskOfEpic(Epic epic) {
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (epic.getId() == subtask.getEpicId()) {
                subtaskList.add(subtask);
            }
        }
        return subtaskList;
    }
}