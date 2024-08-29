package ru.yandex.model;

public class SubTask extends BaseTask {
    private Epic epic;

    public SubTask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public SubTask(String name, String description, TaskStatus status, Epic epic) {
        this(name, description, status);
        this.epic = epic;
    }

    @Override
    public void setStatus(TaskStatus status) {
        super.setStatus(status);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        if (epic == null)
            return;

        this.epic = epic;
    }
}
