package ru.yandex.model;

public class Task extends BaseTask {
    public Task(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    @Override
    public void setStatus(TaskStatus status) {
        super.setStatus(status);
    }
}
