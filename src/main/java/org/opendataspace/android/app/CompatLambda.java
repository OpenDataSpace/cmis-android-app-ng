package org.opendataspace.android.app;

public class CompatLambda {

    public interface Supplier<T> {
        T get();
    }

    public interface Consumer<T> {
        void accept(T t) throws Exception;
    }

    public interface Predicate<T> {
        boolean test(T t);
    }
}
