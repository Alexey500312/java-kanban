package ru.yandex.javacource.e.schedule.exception;

public class TaskValidationException extends RuntimeException {
    public TaskValidationException() {
        super();
    }

    public TaskValidationException(String message) {
        super(message);
    }

    public TaskValidationException(String message, final Throwable cause) {
        super(message, cause);
    }

    public TaskValidationException(Throwable cause) {
        super(cause);
    }

    protected TaskValidationException(String message, Throwable cause,
                                      boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
