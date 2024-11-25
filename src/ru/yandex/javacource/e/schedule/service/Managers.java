package ru.yandex.javacource.e.schedule.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.javacource.e.schedule.server.adapter.DurationAdapter;
import ru.yandex.javacource.e.schedule.server.adapter.LocalDateTimeAdapter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

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

    public static Gson getDefaultGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting()
                .create();
    }
}
