package org.opendataspace.android.app;

import com.crashlytics.android.Crashlytics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OdsLog {

    private static final Logger logger = LoggerFactory.getLogger("ods");

    public static void ex(Class<?> cls, Throwable ex) {
        logger.warn("Caught at " + cls.getSimpleName(), ex);
        Crashlytics.logException(ex);
    }
}
