package ru.yandex.javacource.e.schedule.service;

import ru.yandex.javacource.e.schedule.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> history;
    Node first;
    Node last;

    public InMemoryHistoryManager() {
        history = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        Node node = history.get(task.getId());
        removeNode(node);
        addNode(task);
    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyAll = new ArrayList<>();

        for(Node node = first; node != null; node = node.next) {
            historyAll.add(node.task);
        }

        return historyAll;
    }

    private void addNode(Task task) {
        final Node last = this.last;
        final Node newNode = new Node(task, last, null);
        this.last = newNode;
        if (last == null) {
            this.first = newNode;
        } else {
            last.next = newNode;
        }

        history.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        final Task task = node.task;
        final Node next = node.next;
        final Node prev = node.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }

        history.remove(task.getId());
    }

    private static class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }
    }
}
