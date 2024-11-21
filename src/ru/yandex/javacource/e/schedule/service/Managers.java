package ru.yandex.javacource.e.schedule.service;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Path getDefaultFilePath() {
        return Paths.get("tasks.csv");
    }
}
