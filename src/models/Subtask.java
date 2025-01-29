package models;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, StatusTask status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
