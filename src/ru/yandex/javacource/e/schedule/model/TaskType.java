package ru.yandex.javacource.e.schedule.model;

public enum TaskType {
    TASK,
    EPIC,
    SUBTASK;

    public static TaskType getTaskType(Task task) {
        switch (task.getClass().getSimpleName()) {
            case "Task":
                return TASK;
            case "Epic":
                return EPIC;
            case "SubTask":
                return SUBTASK;
            default:
                return null;
        }
    }
}
