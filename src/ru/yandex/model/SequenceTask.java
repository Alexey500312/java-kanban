package ru.yandex.model;

public class SequenceTask {
    private static int sequenceTask = 0;

    public static int generateId() {
        return ++sequenceTask;
    }
}
