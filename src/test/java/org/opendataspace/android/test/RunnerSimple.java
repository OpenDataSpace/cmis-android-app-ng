package org.opendataspace.android.test;

import org.junit.runners.model.InitializationError;
import org.robolectric.TestLifecycle;

public class RunnerSimple extends RunnerDefault {

    public RunnerSimple(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected Class<? extends TestLifecycle> getTestLifecycleClass() {
        return LifecycleSimple.class;
    }
}
