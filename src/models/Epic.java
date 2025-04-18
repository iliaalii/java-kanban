package models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskList = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String[] data) {
        super(data);
    }

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(String title, String description, StatusTask status, int id) {
        super(title, description, status, id);
    }

    public void linkSubtask(Integer idSubtask) {
        subtaskList.add(idSubtask);
    }

    public void unlinkSubtask(Integer idSubtask) {
        subtaskList.remove(idSubtask);
    }

    public void clearSubtask() {
        subtaskList.clear();
    }

    public ArrayList<Integer> getSubtaskList() {
        return subtaskList;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime start) {
        this.startTime = start;
    }

    public void setEndTime(LocalDateTime end) {
        this.endTime = end;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "EPIC{" +
                "title='" + this.getTitle() +
                ", description=" + this.getDescription() +
                ", ID=" + this.getId() +
                ", status=" + this.getStatus() +
                ", duration=" + this.getDuration() +
                ", startTime=" + this.getStartTime() +
                '}';
    }
}