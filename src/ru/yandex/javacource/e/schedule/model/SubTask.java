package ru.yandex.javacource.e.schedule.model;

public class SubTask extends Task {
    private Epic epic;

    public SubTask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public SubTask(String name, String description, TaskStatus status, Epic epic) {
        this(name, description, status);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        if (epic == null)
            return;

        this.epic = epic;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + "{"
                + "id=" + this.getId() + ", "
                + "name='" + this.getName() + '\'' + ", "
                + "description='" + this.getDescription() + '\'' + ", "
                + "status='" + this.getStatus().getStatusText() + '\'' + ", "
                + "epicId = " + (this.getEpic() != null ? this.getEpic().getId() : null)
                + "}";
    }
}
