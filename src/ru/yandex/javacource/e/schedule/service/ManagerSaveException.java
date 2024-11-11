package ru.yandex.javacource.e.schedule.service;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException() {
        super();
    }

    public ManagerSaveException(String message) {
        super(message);
    }

    public ManagerSaveException(String message, final Throwable cause) {
        super(message, cause);
    }

    public ManagerSaveException(Throwable cause) {
        super(cause);
    }

    protected ManagerSaveException(String message, Throwable cause,
                                   boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
