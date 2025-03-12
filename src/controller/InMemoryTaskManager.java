package controller;

import models.Task;
import models.Epic;
import models.Subtask;
import models.StatusTask;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();                    // список простых задач
    private final HashMap<Integer, Epic> epics = new HashMap<>();                    // список epic задач
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();              // список подзадач epic(а)
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int idCounter = 1;

    //Получение списка по типу задачи
    @Override
    public ArrayList<Task> getListAllTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getListAllEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getListAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    //Добавление новых задач
    @Override
    public void add(Task task) {
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
    }

    @Override
    public void add(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void add(Subtask subtask) {
        if (epics.get(subtask.getEpicId()) != null) {
            subtask.setId(idCounter++);
            subtasks.put(subtask.getId(), subtask);
            checkStatusEpic(epics.get(subtask.getEpicId()));
        }
    }

    //Удаление всего списка по типу задачи
    @Override
    public void removeAllTask() {
        tasks.clear();
    }

    @Override
    public void removeAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.setStatus(StatusTask.NEW);
        }
    }

    //Поиск по ID
    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Task getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Task getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    //Перезапись данных
    @Override
    public void update(Task task) {
        if (tasks.get(task.getId()) != null) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void update(Epic epic) {
        if (epics.get(epic.getId()) != null) {
            checkStatusEpic(epic);
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void update(Subtask newSubtask) {
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
    @Override
    public void removeTaskById(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void removeEpicById(int epicId) {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                subtasks.remove(subtask.getId());
            }
        }
        epics.remove(epicId);
    }

    @Override
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
        } else if (counterSubtasksEpic != 0 && counterDoneSubtasks != 0) {
            epic.setStatus(StatusTask.IN_PROGRESS);
        }
    }

    //Получение списка всех ID подзадач определенного эпика
    @Override
    public ArrayList<Subtask> getAllSubtaskOfEpic(Epic epic) {
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (epic.getId() == subtask.getEpicId()) {
                subtaskList.add(subtask);
            }
        }
        return subtaskList;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}