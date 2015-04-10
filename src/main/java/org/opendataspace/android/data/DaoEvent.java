package org.opendataspace.android.data;

import org.opendataspace.android.objects.ObjectBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DaoEvent<T extends ObjectBase> {

    public enum Operation {INSERT, DELETE, UPDATE}

    public static class Event<T> {
        private final T object;
        private final Operation operation;

        public Event(T object, Operation operation) {
            this.object = object;
            this.operation = operation;
        }

        public T getObject() {
            return object;
        }

        public Operation getOperation() {
            return operation;
        }
    }

    private final List<Event<T>> data = new ArrayList<Event<T>>();

    public DaoEvent() {
    }

    public List<Event<T>> getEvents() {
        return Collections.unmodifiableList(data);
    }

    public void addInsert(T val) {
        data.add(new Event<>(val, Operation.INSERT));
    }

    public void addUpdate(T val) {
        data.add(new Event<>(val, Operation.UPDATE));
    }

    public void addDelete(T val) {
        data.add(new Event<>(val, Operation.DELETE));
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}
