package models;

public class Node {

    public Task data;           //ссылка на задачу
    public Node next;           //ссылка на следующий узел
    public Node prev;           //ссылка на предыдущий узел

    public Node(Task data) {
        this.data = data;
    }
}
