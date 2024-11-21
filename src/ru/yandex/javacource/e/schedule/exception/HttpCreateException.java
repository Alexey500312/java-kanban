package ru.yandex.javacource.e.schedule.exception;

public class HttpCreateException extends RuntimeException {
    public HttpCreateException() {
        super();
    }

    public HttpCreateException(String message) {
        super(message);
    }

    public HttpCreateException(String message, final Throwable cause) {
        super(message, cause);
    }

    public HttpCreateException(Throwable cause) {
        super(cause);
    }

    protected HttpCreateException(String message, Throwable cause,
                                      boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
