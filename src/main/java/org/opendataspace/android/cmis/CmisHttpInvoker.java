package org.opendataspace.android.cmis;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import okio.BufferedSink;
import org.apache.chemistry.opencmis.client.bindings.impl.CmisBindingsHelper;
import org.apache.chemistry.opencmis.client.bindings.spi.BindingSession;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpInvoker;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Output;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.spi.AuthenticationProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CmisHttpInvoker implements HttpInvoker {

    private static final OkHttpClient client = new OkHttpClient();

    static {
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        client.setReadTimeout(90, TimeUnit.SECONDS);
    }

    @Override
    public Response invokeGET(UrlBuilder url, BindingSession session) {
        return invokeGET(url, session, null, null);
    }

    @Override
    public Response invokeGET(UrlBuilder url, BindingSession session, BigInteger offset, BigInteger length) {
        Request.Builder req = new Request.Builder();

        if (offset != null || length != null) {
            StringBuilder sb = new StringBuilder("bytes=");

            if ((offset == null) || (offset.signum() == -1)) {
                offset = BigInteger.ZERO;
            }

            sb.append(offset.toString());
            sb.append("-");

            if ((length != null) && (length.signum() == 1)) {
                sb.append(offset.add(length.subtract(BigInteger.ONE)).toString());
            }

            req.addHeader("Range", sb.toString());
        }

        return createResponce(req, url, session);
    }

    @Override
    public Response invokePOST(UrlBuilder url, final String contentType, final Output writer, BindingSession session) {
        Request.Builder req = new Request.Builder().post(new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse(contentType);
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                try {
                    writer.write(sink.outputStream());
                } catch (Exception ex) {
                    throw new IOException(ex);
                }
            }
        });

        return createResponce(req, url, session);
    }

    @Override
    public Response invokePUT(UrlBuilder url, String contentType, Map<String, String> headers, Output writer,
                              BindingSession session) {
        Request.Builder req = new Request.Builder().put(new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType != null ? MediaType.parse(contentType) : null;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                try {
                    writer.write(sink.outputStream());
                } catch (Exception ex) {
                    throw new IOException(ex);
                }
            }
        });

        if (headers != null) {
            for (Map.Entry<String, String> cur : headers.entrySet()) {
                req.addHeader(cur.getKey(), cur.getValue());
            }
        }

        return createResponce(req, url, session);
    }

    @Override
    public Response invokeDELETE(UrlBuilder url, BindingSession session) {
        return createResponce(new Request.Builder().delete(), url, session);
    }

    private Response createResponce(Request.Builder req, UrlBuilder url, BindingSession session) {
        try {
            AuthenticationProvider provider = CmisBindingsHelper.getAuthenticationProvider(session);
            OkHttpClient http = client.clone();

            if (provider != null) {
                Map<String, List<String>> httpHeaders = provider.getHTTPHeaders(url.toString());

                if (httpHeaders != null) {
                    //noinspection Convert2streamapi
                    for (Map.Entry<String, List<String>> header : httpHeaders.entrySet()) {
                        if (header.getValue() != null) {
                            for (String value : header.getValue()) {
                                req.addHeader(header.getKey(), value);
                            }
                        }
                    }
                }

                http.setSocketFactory(provider.getSSLSocketFactory());
            }

            com.squareup.okhttp.Response call = http.newCall(req.url(url.toString()).build()).execute();
            Headers headers = call.headers();
            Map<String, List<String>> data = new HashMap<>();

            for (String cur : headers.names()) {
                data.put(cur, headers.values(cur));
            }

            if (provider != null) {
                provider.putResponseHeaders(call.request().urlString(), call.code(), data);
            }

            return new Response(call.code(), call.message(), data, call.body().byteStream(), call.body().byteStream());
        } catch (Exception ex) {
            throw new CmisConnectionException(ex.getMessage(), ex);
        }
    }
}
