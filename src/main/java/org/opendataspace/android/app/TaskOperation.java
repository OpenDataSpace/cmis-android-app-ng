package org.opendataspace.android.app;

import org.opendataspace.android.operation.OperationBase;
import org.opendataspace.android.operation.OperationBaseCmis;

public class TaskOperation<T extends OperationBase> extends Task {

    private final T operation;
    private final WeakCallback<?, T> callback;

    public TaskOperation(final T operation, final WeakCallback<?, T> callback) {
        this.operation = operation;
        this.callback = callback;
    }

    @Override
    public void onExecute() throws Exception {
        operation.execute();
    }

    @Override
    public void onDone() throws Exception {
        callback.call(operation);
    }

    public void start() {
        OdsApp.get().getPool().execute(this, operation instanceof OperationBaseCmis);
    }
}
