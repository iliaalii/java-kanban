package controller;

import models.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);                    //добавление просмотренной задачи в историю

    void remove(int id);                    //удаление задачи из истории

    List<Task> getHistory();                //вывод списка истории
}