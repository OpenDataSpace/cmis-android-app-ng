package org.opendataspace.android.app;

import org.opendataspace.android.operation.OperationBase;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskPool {

    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private final ThreadPoolExecutor service =
            new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, 1, TimeUnit.MINUTES,
                    new ArrayBlockingQueue<>(100, true), new ThreadPoolExecutor.CallerRunsPolicy());

    private AtomicInteger counter = new AtomicInteger();

    public void execute(final Task task) {
        counter.incrementAndGet();
        task.setDecrement(counter);
        service.execute(task);
    }

    public void stop() {
        service.shutdown();
        counter.set(0);
    }

    public void cancel(Task task) {
        if (service.remove(task)) {
            task.setDecrement(null);
            counter.decrementAndGet();
        }
    }

    public boolean hasTasks() {
        return counter.get() > 0;
    }

    public void execute(final OperationBase op) {
        execute(new Task() {

            @Override
            public void onExecute() throws Exception {
                op.execute();
            }
        });
    }
}
