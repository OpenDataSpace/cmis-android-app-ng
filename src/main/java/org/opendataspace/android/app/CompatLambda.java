package org.opendataspace.android.app;

public class CompatLambda {

    private CompatLambda() {
    }

    public interface Supplier<T> {
        T get();
    }

    public interface Consumer<T> {
        void accept(T t) throws Exception;
    }

    public interface Predicate<T> {
        boolean test(T t);
    }

    public interface Checker {
        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        boolean test();
    }

    public interface BiConsumer<T, U> {
        @SuppressWarnings("RedundantThrows")
        void accept(T t, U u) throws Exception;
    }
}
