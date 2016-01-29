package org.opendataspace.android.app;

import org.opendataspace.android.operation.OperationBase;
import org.opendataspace.android.operation.OperationBaseCmis;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskPool {

    private static final int NUMBER_OF_CORES = Math.min(6, Math.max(4, Runtime.getRuntime().availableProcessors()));

    private final ThreadPoolExecutor common =
            new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, 1, TimeUnit.MINUTES,
                    new ArrayBlockingQueue<>(100, true), new ThreadPoolExecutor.CallerRunsPolicy());

    private final ThreadPoolExecutor cmis =
            new ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(100, true),
                    new ThreadPoolExecutor.CallerRunsPolicy());

    private final AtomicInteger counter = new AtomicInteger();

    public void execute(final Task task, boolean isCmis) {
        if (task == null) {
            return;
        }

        counter.incrementAndGet();
        task.setDecrement(counter);

        if (isCmis) {
            cmis.execute(task);
        } else {
            common.execute(task);
        }
    }

    public void stop() {
        common.shutdown();
        cmis.shutdown();
        counter.set(0);
    }

    public void cancel(Task task) {
        if (task != null && (common.remove(task) || cmis.remove(task))) {
            task.setDecrement(null);
            counter.decrementAndGet();
        }
    }

    public boolean hasTasks() {
        return counter.get() > 0;
    }

    public Task execute(final OperationBase op) {
        Task t = new Task() {

            @Override
            public void onExecute() throws Exception {
                op.execute();
            }
        };

        execute(t, op instanceof OperationBaseCmis);
        return t;
    }

    ExecutorService getCmisService() {
        return cmis;
    }
}
