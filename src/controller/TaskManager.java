package controller;

import java.util.List;
import java.util.ArrayList;

import models.Epic;
import models.Subtask;
import models.Task;

public interface TaskManager {
    //Получение списка по типу задачи
    ArrayList<Task> getListAllTask();
    ArrayList<Epic> getListAllEpic();
    ArrayList<Subtask> getListAllSubtask();

    //Добавление новых задач
    void add(Task task);
    void add(Epic epic);
    void add(Subtask subtask);

    //Удаление всего списка по типу задачи
    void removeAllTask();
    void removeAllEpic();
    void removeAllSubtask();

    //Поиск по ID
    Task getTaskById(int id);
    Task getEpicById(int id);
    Task getSubtaskById(int id);

    //Перезапись данных
    void update(Task task);
    void update(Epic epic);
    void update(Subtask newSubtask);

    //Удаление по ID
    void removeTaskById(int taskId);
    void removeEpicById(int epicId);
    void removeSubtaskById(int subtaskId);

    //Получение списка всех ID подзадач определенного эпика
    ArrayList<Subtask> getAllSubtaskOfEpic(Epic epic);

    //Вывод списка истории поиска
    List<Task> getHistory();
}
