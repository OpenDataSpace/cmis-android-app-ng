package org.opendataspace.android.app;

import java.lang.ref.WeakReference;

public class WeakCallback<T, U> {

    private final WeakReference<T> reference;
    private final CompatLambda.BiConsumer<T, U> consumer;

    public WeakCallback(final T object, final CompatLambda.BiConsumer<T, U> consumer) {
        reference = new WeakReference<>(object);
        this.consumer = consumer;
    }

    public void call(final U operation) {
        final T object = dereference();

        try {
            if (object != null) {
                consumer.accept(object, operation);
            }
        } catch (Exception ex) {
            OdsLog.ex(object.getClass(), ex);
        }
    }

    public void callThrows(final U operation) throws Exception {
        final T object = dereference();

        if (object != null) {
            consumer.accept(object, operation);
        }
    }

    public T dereference() {
        return reference.get();
    }
}
