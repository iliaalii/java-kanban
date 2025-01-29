package models;

import java.util.Objects;

public class Task {
    private String title;           // название задачи
    private String description;     // описание задачи
    private StatusTask status;      // статус задачи
    private int id;                 // id задачи

    public Task(String title, String description, StatusTask status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(String title, String description){
        this.title = title;
        this.description = description;
        this.status = StatusTask.NEW;
    }

    public void setId(int tag) {
        this.id = tag;
    }

    public void setStatus(StatusTask status) {
        this.status = status;
    }

    public StatusTask getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(title, task.title) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (title != null) {
            hash = hash + title.hashCode();
        }
        hash = hash * 31;

        if (description != null) {
            hash = hash + description.hashCode();
        }
        return hash;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", ID=" + id +
                ", status=" + status +
                '}';
    }
}