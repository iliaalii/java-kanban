package controller;

import models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;                                          //начало списка
    private Node tail;                                          //конец списка
    private final Map<Integer, Node> searchHistory = new HashMap<>();       //таблица истории просмотров

    private Node linkLast(Task task) {                         //создает узел, и возвращает его
        Node newNode = new Node(task);
        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;
        return newNode;
    }

    private List<Task> getTasks() {                            // собрать все задачи из таблицы в обычный ArrayList
        List<Task> historyList = new ArrayList<>();
        Node node = head;
        while (node != null) {
            historyList.add(node.data);
            node = node.next;
        }
        return historyList;
    }

    private void removeNode(Node node) {                       //   удаление узла
        Node prevNode = node.prev;
        Node nextNode = node.next;
        if (node.prev != null && node.next != null) {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        } else if (node.prev != null) {
            prevNode.next = null;
            tail = prevNode;
        } else if (node.next != null) {
            nextNode.prev = null;
            head = nextNode;
        } else {
            head = null;
            tail = null;
        }
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (searchHistory.get(task.getId()) != null) {
                remove(task.getId());
            }
            searchHistory.put(task.getId(), linkLast(task));
        }
    }

    @Override
    public void remove(int id) {
        removeNode(searchHistory.get(id));
        searchHistory.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(getTasks());
    }
}

class Node {
    public Task data;           //ссылка на задачу
    public Node next;           //ссылка на следующий узел
    public Node prev;           //ссылка на предыдущий узел

    public Node(Task data) {
        this.data = data;
    }
}