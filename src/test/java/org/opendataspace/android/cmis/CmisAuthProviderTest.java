package org.opendataspace.android.cmis;

import android.text.TextUtils;

import org.apache.chemistry.opencmis.client.bindings.impl.SessionImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsPreferences;
import org.opendataspace.android.test.TestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class CmisAuthProviderTest {

    @Test
    public void checkHeaders() {
        CmisAuthProvider provider = new CmisAuthProvider();
        SessionImpl ses = new SessionImpl();

        ses.put(SessionParameter.USER, "user");
        ses.put(SessionParameter.PASSWORD, "password");
        provider.setSession(ses);

        Map<String, List<String>> data = provider.getHTTPHeaders("https://demo.dataspace.cc");

        Assert.assertEquals(true, data.containsKey("Authorization"));
        Assert.assertEquals(true, data.containsKey("User-Agent"));
        Assert.assertEquals(true, data.containsKey("Device-ID"));

        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        OdsPreferences pref = app.getPrefs();
        String agent = data.get("User-Agent").get(0);
        String deviceid = data.get("Device-ID").get(0);
        String auth = data.get("Authorization").get(0);

        Assert.assertEquals(false, TextUtils.isEmpty(auth));
        Assert.assertEquals(false, TextUtils.isEmpty(deviceid));
        Assert.assertEquals(pref.getInstallId(), deviceid);
        Assert.assertEquals(true, agent.contains(pref.version()));
        Assert.assertEquals(true, agent.contains("Android"));
    }
}
