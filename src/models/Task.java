package models;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private String title;               // название задачи
    private String description;         // описание задачи
    private StatusTask status;          // статус задачи
    private int id;                     // id задачи
    protected Duration duration;          // продолжительность выполнения в минутах
    protected LocalDateTime startTime;    // время начала выполнения

    public Task(String[] data) {
        this.title = data[2];
        this.description = data[3];
        this.status = StatusTask.valueOf(data[4]);
        this.id = Integer.parseInt(data[0]);
        if (!data[5].equals("null")) {
            this.duration = Duration.parse(data[5]);
            this.startTime = LocalDateTime.parse(data[6]);
        }
    }

    public Task(String title, String description, StatusTask status, int id, Duration duration, LocalDateTime start) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = id;
        this.duration = duration;
        this.startTime = start;
    }

    public Task(String title, String description, StatusTask status, int id) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public Task(String title, String description, StatusTask status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(String title, String description) {
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

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
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
                "title=" + title +
                ", description=" + description +
                ", ID=" + id +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}