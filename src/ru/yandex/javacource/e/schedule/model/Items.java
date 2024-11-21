package ru.yandex.javacource.e.schedule.model;

import java.util.List;

public class Items<T> {
    List<T> items;

    public Items(List<T> items) {
        this.items = items;
    }
}
