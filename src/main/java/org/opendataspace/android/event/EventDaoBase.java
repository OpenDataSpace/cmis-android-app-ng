package org.opendataspace.android.event;

import org.opendataspace.android.object.ObjectBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventDaoBase<T extends ObjectBase> {

    public enum Operation {INSERT, DELETE, UPDATE, RESET}

    public static class Event<T> {
        private final T object;
        private final Operation operation;
        private long extra;

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

        public long getExtra() {
            return extra;
        }

        public void setExtra(long extra) {
            this.extra = extra;
        }
    }

    private final List<Event<T>> data = new ArrayList<>();

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

    public void addReset(long extra) {
        Event<T> ev = new Event<>(null, Operation.RESET);
        ev.setExtra(extra);
        data.add(ev);
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}
