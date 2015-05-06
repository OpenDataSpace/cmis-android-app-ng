package org.opendataspace.android.app;

import org.opendataspace.android.operation.OperationBase;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskPool {

    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private final ThreadPoolExecutor service =
            new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, 1, TimeUnit.MINUTES,
                    new ArrayBlockingQueue<>(100, true), new ThreadPoolExecutor.CallerRunsPolicy());

    public void execute(final Task task) {
        service.execute(task);
    }

    public void stop() {
        service.shutdown();
    }

    public void cancel(Task task) {
        service.remove(task);
    }

    public boolean hasTasks() {
        return service.getActiveCount() > 0;
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
