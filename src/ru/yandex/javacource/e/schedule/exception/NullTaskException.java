package ru.yandex.javacource.e.schedule.exception;

public class NullTaskException extends RuntimeException {
    public NullTaskException() {
        super();
    }

    public NullTaskException(String message) {
        super(message);
    }

    public NullTaskException(String message, final Throwable cause) {
        super(message, cause);
    }

    public NullTaskException(Throwable cause) {
        super(cause);
    }

    protected NullTaskException(String message, Throwable cause,
                                boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
