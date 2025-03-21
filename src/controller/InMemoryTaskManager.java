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
        if (task.getId() == 0) {
            task.setId(idCounter++);
        } else if (tasks.get(task.getId()) != null) {
            task.setId(idCounter++);
        } else {
            idCounter = task.getId() + 1;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void add(Epic epic) {
        epic.setStatus(StatusTask.NEW);
        if (epic.getId() == 0) {
            epic.setId(idCounter++);
        } else if (tasks.get(epic.getId()) != null) {
            epic.setId(idCounter++);
        } else {
            idCounter = epic.getId() + 1;
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void add(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            if (subtask.getId() == 0) {
                subtask.setId(idCounter++);
            } else if (tasks.get(subtask.getId()) != null) {
                subtask.setId(idCounter++);
            } else {
                idCounter = subtask.getId() + 1;
            }
            epic.linkSubtask(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            checkStatusEpic(epic);
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
            epic.clearSubtask();
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
    public void update(Epic newEpic) {
        Epic oldEpic = epics.get(newEpic.getId());
        if (oldEpic != null) {
            newEpic.clearSubtask();
            for (Integer idSubtask : oldEpic.getSubtaskList()) {
                newEpic.linkSubtask(idSubtask);
            }
            checkStatusEpic(newEpic);
            epics.put(newEpic.getId(), newEpic);
        }
    }

    @Override
    public void update(Subtask newSubtask) {
        Subtask oldSubtask = subtasks.get(newSubtask.getId());
        if (oldSubtask != null) {
            if (oldSubtask.getEpicId() != newSubtask.getEpicId()) {
                Epic oldEpic = epics.get(oldSubtask.getEpicId());
                oldEpic.unlinkSubtask(oldSubtask.getId());
                checkStatusEpic(oldEpic);
            }
            Epic epic = epics.get(newSubtask.getEpicId());
            subtasks.put(newSubtask.getId(), newSubtask);
            epic.linkSubtask(newSubtask.getId());
            checkStatusEpic(epic);
        }
    }

    //Удаление по ID
    @Override
    public void removeTaskById(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void removeEpicById(int epicId) {
        if (epics.get(epicId) != null) {
            Epic epic = epics.get(epicId);
            for (Integer idSubtask : epic.getSubtaskList()) {
                subtasks.remove(idSubtask);
            }
            epics.remove(epicId);
        }
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        if (epics.get(subtasks.get(subtaskId).getEpicId()) != null) {
            Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());
            subtasks.remove(subtaskId);
            epic.unlinkSubtask(subtaskId);
            checkStatusEpic(epic);
        }
    }

    //Проверка статуса EPIC
    private void checkStatusEpic(Epic epic) {
        epic.setStatus(StatusTask.NEW);
        if (!epic.getSubtaskList().isEmpty()) {
            int counterDoneSubtasks = 0;
            for (Integer idSubtask : epic.getSubtaskList()) {
                Subtask subtask = subtasks.get(idSubtask);
                if (subtask.getStatus().equals(StatusTask.IN_PROGRESS)) {
                    epic.setStatus(StatusTask.IN_PROGRESS);
                    return;
                } else if (subtask.getStatus().equals(StatusTask.DONE)) {
                    counterDoneSubtasks++;
                }
            }
            if (epic.getSubtaskList().size() == counterDoneSubtasks) {
                epic.setStatus(StatusTask.DONE);
            } else if (counterDoneSubtasks != 0) {
                epic.setStatus(StatusTask.IN_PROGRESS);
            }
        }
    }

    //Получение списка всех подзадач определенного эпика
    @Override
    public ArrayList<Subtask> getAllSubtaskOfEpic(Epic epic) {
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Integer idSubtask : epic.getSubtaskList()) {
            subtaskList.add(subtasks.get(idSubtask));
        }
        return subtaskList;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}