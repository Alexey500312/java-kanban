package ru.yandex.javacource.e.schedule.model;

public enum TaskStatus {
    NEW("To do"),
    IN_PROGRESS("In progress"),
    DONE("Done");

    private String statusText;

    TaskStatus(String statusText) {
        this.statusText = statusText;
    }

    public String getStatusText() {
        return statusText;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + "{"
                + "status=" + this.name() + ", "
                + "statusText='" + this.getStatusText() + '\''
                + "}";
    }
}
