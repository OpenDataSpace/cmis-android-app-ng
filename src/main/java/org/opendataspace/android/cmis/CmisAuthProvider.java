package org.opendataspace.android.cmis;

import android.util.Base64;

import org.apache.chemistry.opencmis.client.bindings.spi.AbstractAuthenticationProvider;
import org.apache.chemistry.opencmis.client.bindings.spi.BindingSession;
import org.apache.chemistry.opencmis.client.bindings.spi.cookies.CmisCookieManager;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.opendataspace.android.app.OdsLog;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CmisAuthProvider extends AbstractAuthenticationProvider {

    private CmisCookieManager cookieManager;
    private final Map<String, List<String>> fixedHeaders = new HashMap<>();
    private SSLSocketFactory factory;

    @Override
    public Map<String, List<String>> getHTTPHeaders(String url) {
        Map<String, List<String>> result = new HashMap<>(fixedHeaders);

        if (cookieManager != null) {
            result.putAll(cookieManager.get(url, result));
        }

        return result.isEmpty() ? null : result;
    }

    @Override
    public SSLSocketFactory getSSLSocketFactory() {
        if (factory == null) {
            try {
                X509TrustManager customManager = new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws
                            CertificateException {
                        // nothing
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws
                            CertificateException {
                        // nothing
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                };

                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, new X509TrustManager[] {customManager}, new SecureRandom());
                factory = context.getSocketFactory();
            } catch (Exception ex) {
                OdsLog.ex(getClass(), ex);
            }
        }

        return factory;
    }

    @Override
    public void setSession(BindingSession session) {
        super.setSession(session);
        cookieManager = new CmisCookieManager(session.getSessionId());
        fixedHeaders.put("Authorization", Collections
                .singletonList("Basic " + Base64.encodeToString((session.get(SessionParameter.USER).toString() + ":" +
                        session.get(SessionParameter.PASSWORD).toString()).getBytes(), Base64.NO_WRAP)));
    }
}
