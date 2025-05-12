package controller;

import exceptions.NotFoundException;
import exceptions.OverlapException;
import models.Task;
import models.Epic;
import models.Subtask;
import models.StatusTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();                    // список простых задач
    private final HashMap<Integer, Epic> epics = new HashMap<>();                    // список epic задач
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();              // список подзадач epic(а)
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasksList = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private int idCounter = 1;

    //Получение списка по типу задачи
    @Override
    public List<Task> getListAllTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getListAllEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getListAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    //Добавление новых задач
    @Override
    public void add(Task task) {
        if (task.getStartTime() == null || (tasksNotOverlap(task) && task.getDuration() != null)) {
            if (task.getId() < idCounter) {
                task.setId(idCounter++);
            } else {
                idCounter = task.getId() + 1;
            }
            tasks.put(task.getId(), task);
            if (task.getStartTime() != null) {
                prioritizedTasksList.add(task);
            }
        } else {
            throw new OverlapException("Перекрытие задач по времени выполнения");
        }
    }

    @Override
    public void add(Epic epic) {
        epic.setStatus(StatusTask.NEW);
        if (epic.getId() < idCounter) {
            epic.setId(idCounter++);
        } else {
            idCounter = epic.getId() + 1;
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void add(Subtask subtask) {
        if (subtask.getStartTime() == null || (tasksNotOverlap(subtask) && subtask.getDuration() != null)) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                if (subtask.getId() < idCounter) {
                    subtask.setId(idCounter++);
                } else {
                    idCounter = subtask.getId() + 1;
                }
                epic.linkSubtask(subtask.getId());
                subtasks.put(subtask.getId(), subtask);
                checkStatusEpic(epic);
                if (subtask.getStartTime() != null) {
                    prioritizedTasksList.add(subtask);
                    setEpicTimes(epic);
                }
            } else {
                throw new NotFoundException("Связанный эпик не найден");
            }
        } else {
            throw new OverlapException("Перекрытие задач по времени выполнения");
        }
    }

    //Удаление всего списка по типу задачи
    @Override
    public void removeAllTask() {
        tasks.keySet().forEach(historyManager::remove);
        tasks.values().stream()
                .filter(task -> task.getStartTime() != null)
                .forEach(prioritizedTasksList::remove);
        tasks.clear();
    }

    @Override
    public void removeAllEpic() {
        epics.keySet().forEach(historyManager::remove);
        epics.clear();
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.values().stream()
                .filter(task -> task.getStartTime() != null)
                .forEach(prioritizedTasksList::remove);
        subtasks.clear();
    }

    @Override
    public void removeAllSubtask() {
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.values().stream()
                .filter(task -> task.getStartTime() != null)
                .forEach(prioritizedTasksList::remove);
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.clearSubtask();
            epic.setStatus(StatusTask.NEW);
            setEpicTimes(epic);
        });
    }

    //Поиск по ID
    @Override
    public Task getTaskById(int id) {
        if (tasks.get(id) != null) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        }
        throw new NotFoundException("Задача не найдена");
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.get(id) != null) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        }
        throw new NotFoundException("Задача не найдена");
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.get(id) != null) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        }
        throw new NotFoundException("Задача не найдена");
    }

    //Перезапись данных
    @Override
    public void update(Task task) {
        if (tasks.get(task.getId()) != null) {
            if (task.getStartTime() == null || (task.getDuration() != null && tasksNotOverlap(task))) {
                if (task.getStartTime() != null) {
                    prioritizedTasksList.remove(tasks.get(task.getId()));
                    prioritizedTasksList.add(task);
                }
                tasks.put(task.getId(), task);
            } else {
                throw new OverlapException("Перекрытие задач по времени выполнения");
            }
        } else {
            throw new NotFoundException("Задача не найдена");
        }
    }

    @Override
    public void update(Epic newEpic) {
        Epic oldEpic = epics.get(newEpic.getId());
        if (oldEpic != null) {
            newEpic.clearSubtask();
            oldEpic.getSubtaskList().forEach(newEpic::linkSubtask);
            checkStatusEpic(newEpic);
            setEpicTimes(newEpic);
            epics.put(newEpic.getId(), newEpic);
        } else {
            throw new NotFoundException("Задача не найдена");
        }
    }

    @Override
    public void update(Subtask newSubtask) {
        Subtask oldSubtask = subtasks.get(newSubtask.getId());
        if (oldSubtask != null) {
            if (newSubtask.getStartTime() == null || (newSubtask.getDuration() != null && tasksNotOverlap(newSubtask))) {
                if (oldSubtask.getEpicId() != newSubtask.getEpicId()) {
                    Epic oldEpic = epics.get(oldSubtask.getEpicId());
                    oldEpic.unlinkSubtask(oldSubtask.getId());
                    checkStatusEpic(oldEpic);
                    setEpicTimes(oldEpic);
                }
                Epic epic = epics.get(newSubtask.getEpicId());
                if (newSubtask.getStartTime() != null) {
                    prioritizedTasksList.remove(subtasks.get(newSubtask.getId()));
                    prioritizedTasksList.add(newSubtask);
                }
                subtasks.put(newSubtask.getId(), newSubtask);
                epic.unlinkSubtask(newSubtask.getId());
                epic.linkSubtask(newSubtask.getId());
                checkStatusEpic(epic);
                setEpicTimes(epic);
            } else {
                throw new OverlapException("Перекрытие задач по времени выполнения");
            }
        } else {
            throw new NotFoundException("Задача не найдена");
        }
    }

    //Удаление по ID
    @Override
    public void removeTaskById(int taskId) {
        if (tasks.get(taskId) != null) {
            if (tasks.get(taskId).getStartTime() != null) {
                prioritizedTasksList.remove(tasks.get(taskId));
            }
            historyManager.remove(taskId);
            tasks.remove(taskId);
        } else {
            throw new NotFoundException("Задача не найдена");
        }
    }

    @Override
    public void removeEpicById(int epicId) {
        if (epics.get(epicId) != null) {
            epics.get(epicId).getSubtaskList()
                    .forEach(id -> {
                        if (subtasks.get(id).getStartTime() != null) {
                            prioritizedTasksList.remove(subtasks.get(id));
                        }
                        historyManager.remove(id);
                        subtasks.remove(id);
                    });
            epics.remove(epicId);
        } else {
            throw new NotFoundException("Задача не найдена");
        }
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());
        if (epic != null) {
            if (subtasks.get(subtaskId).getStartTime() != null) {
                prioritizedTasksList.remove(subtasks.get(subtaskId));
            }
            historyManager.remove(subtaskId);
            subtasks.remove(subtaskId);
            epic.unlinkSubtask(subtaskId);
            checkStatusEpic(epic);
            setEpicTimes(epic);
        } else {
            throw new NotFoundException("Задача не найдена");
        }
    }

    //Получение списка всех подзадач определенного эпика
    @Override
    public List<Subtask> getAllSubtaskOfEpic(Epic epic) {
        return epic.getSubtaskList().stream().map(subtasks::get).collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasksList);
    }

    private boolean tasksNotOverlap(Task newTask) {
        return prioritizedTasksList.stream()
                .allMatch(task -> newTask.getStartTime().isAfter(task.getEndTime())
                        || newTask.getEndTime().isBefore(task.getStartTime()));
    }

    //Проверка статуса EPIC
    private void checkStatusEpic(Epic epic) {
        epic.setStatus(StatusTask.NEW);
        if (!epic.getSubtaskList().isEmpty()) {
            epic.setStatus(StatusTask.IN_PROGRESS);
            if (epic.getSubtaskList().stream()
                    .map(subtasks::get)
                    .allMatch(sub -> sub.getStatus() == StatusTask.DONE)) {
                epic.setStatus(StatusTask.DONE);
            } else if (epic.getSubtaskList().stream()
                    .map(subtasks::get)
                    .allMatch(sub -> sub.getStatus() == StatusTask.NEW)) {
                epic.setStatus(StatusTask.NEW);
            }
        }
    }

    private void setEpicTimes(Epic epic) {
        epic.setStartTime(epic.getSubtaskList().stream()
                .map(subtasks::get)
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo).orElse(null));
        epic.setEndTime(epic.getSubtaskList().stream()
                .map(subtasks::get)
                .filter(sub -> sub.getStartTime() != null)
                .map(Task::getEndTime)
                .max(LocalDateTime::compareTo).orElse(null));
        epic.setDuration(epic.getSubtaskList().stream()
                .map(subtasks::get)
                .map(Task::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus));
    }
}