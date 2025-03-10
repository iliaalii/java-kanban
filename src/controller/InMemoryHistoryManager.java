package controller;

import models.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> searchHistory = new ArrayList<>();
    private final int LIMIT_HISTORY = 10;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (searchHistory.size() == LIMIT_HISTORY) {
            searchHistory.removeFirst();
        }
        searchHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(searchHistory);
    }
}
