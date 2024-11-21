package ru.yandex.javacource.e.schedule.server;

import ru.yandex.javacource.e.schedule.model.Task;

import java.util.List;

public class Items<T> {
    List<T> items;

    public Items(List<T> items) {
        this.items = items;
    }
}
