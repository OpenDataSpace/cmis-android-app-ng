package org.opendataspace.android.app;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.test.TestRunner;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class WeakCallbackTest {

    private static class Base {

        public int value = 0;

        public void doIt(final Integer arg) {
            value = arg;
        }
    }

    private static class Devivied extends Base {

        @Override
        public void doIt(final Integer arg) {
            value = 2 * arg;
        }
    }

    @Test
    public void checkOverride() throws Exception {
        Base obj = new Devivied();
        WeakCallback<Base, Integer> callback = new WeakCallback<>(obj, Base::doIt);
        callback.call(2);
        Assert.assertEquals(4, obj.value);
        callback.callThrows(3);
        Assert.assertEquals(6, obj.value);
    }

    @Test
    public void checkGc() throws Exception {
        Base obj = new Base();
        WeakCallback<Base, Integer> callback = new WeakCallback<>(obj, Base::doIt);
        callback.call(2);
        Assert.assertEquals(2, obj.value);
        //noinspection UnusedAssignment
        obj = null;
        System.gc();
        Assert.assertEquals(null, callback.dereference());
        callback.callThrows(2);
    }
}