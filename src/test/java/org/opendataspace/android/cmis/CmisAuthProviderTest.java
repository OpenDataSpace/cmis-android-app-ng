package org.opendataspace.android.cmis;

import android.text.TextUtils;

import junit.framework.Assert;
import org.apache.chemistry.opencmis.client.bindings.impl.SessionImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsPreferences;
import org.opendataspace.android.test.OdsRunner;

import java.util.List;
import java.util.Map;

@RunWith(OdsRunner.class)
public class CmisAuthProviderTest {

    @Test
    public void checkHeaders() throws Exception {
        CmisAuthProvider provider = new CmisAuthProvider();
        SessionImpl ses = new SessionImpl();

        ses.put(SessionParameter.USER, "user");
        ses.put(SessionParameter.PASSWORD, "password");
        provider.setSession(ses);

        Map<String, List<String>> data = provider.getHTTPHeaders("https://demo.dataspace.cc");

        Assert.assertTrue(data.containsKey("Authorization"));
        Assert.assertTrue(data.containsKey("User-Agent"));
        Assert.assertTrue(data.containsKey("Device-ID"));

        String agent = data.get("User-Agent").get(0);
        String deviceid = data.get("Device-ID").get(0);
        String auth = data.get("Authorization").get(0);
        OdsPreferences pref = OdsApp.get().getPrefs();

        Assert.assertFalse(TextUtils.isEmpty(auth));
        Assert.assertFalse(TextUtils.isEmpty(deviceid));
        Assert.assertEquals(pref.getInstallId(), deviceid);
        Assert.assertTrue(agent.contains(pref.version()));
        Assert.assertTrue(agent.contains("Android"));
    }
}
