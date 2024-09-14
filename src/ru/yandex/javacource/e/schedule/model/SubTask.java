package ru.yandex.javacource.e.schedule.model;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public SubTask(String name, String description, TaskStatus status, Epic epic) {
        this(name, description, status);
        this.epicId = epic.getId();
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        if (epicId == null)
            return;

        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + "{"
                + "id=" + this.getId() + ", "
                + "name='" + this.getName() + '\'' + ", "
                + "description='" + this.getDescription() + '\'' + ", "
                + "status='" + this.getStatus().getStatusText() + '\'' + ", "
                + "epicId = " + (this.getEpicId() != null ? this.epicId : null)
                + "}";
    }
}
