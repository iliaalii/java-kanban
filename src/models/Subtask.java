package models;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String[] data) {
        super(data);
        this.epicId = Integer.parseInt(data[5]);
    }

    public Subtask(String title, String description, StatusTask status, int id, int epicId) {
        super(title, description, status, id);
        this.epicId = epicId;
    }

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


    @Override
    public String toString() {
        return "Subtask{" +
                "title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", ID=" + this.getId() + '\'' +
                ", status=" + this.getStatus() + '\'' +
                ", epicId=" + epicId +
                '}';
    }
}
