package ru.yandex.javacource.e.schedule.exception;

public class EndpointException extends RuntimeException {
    public EndpointException() {
        super();
    }

    public EndpointException(String message) {
        super(message);
    }

    public EndpointException(String message, final Throwable cause) {
        super(message, cause);
    }

    public EndpointException(Throwable cause) {
        super(cause);
    }

    protected EndpointException(String message, Throwable cause,
                                boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
