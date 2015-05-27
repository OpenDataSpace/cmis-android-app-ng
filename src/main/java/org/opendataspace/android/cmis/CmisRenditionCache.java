package org.opendataspace.android.cmis;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;
import org.opendataspace.android.app.CompatDisposable;
import org.opendataspace.android.app.CompatLambda;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.app.beta.BuildConfig;
import org.opendataspace.android.object.Node;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class CmisRenditionCache implements CompatDisposable {

    private static final String SCHEME = "ren";

    private final Picasso picasso;
    private final Map<Uri, CmisSession> sessions = new HashMap<>();

    public CmisRenditionCache(Context context, ExecutorService service) {
        picasso = new Picasso.Builder(context).executor(service).addRequestHandler(new RequestHandler() {

            @Override
            public boolean canHandleRequest(Request data) {
                return SCHEME.equals(data.uri.getScheme());
            }

            @Override
            public Result load(Request request, int networkPolicy) throws IOException {
                CmisSession session = sessions.get(request.uri);
                return session != null ?
                        new Result(session.getRendition(request.uri.getAuthority()), Picasso.LoadedFrom.NETWORK) : null;
            }
        }).build();

        picasso.setLoggingEnabled(BuildConfig.DEBUG);
    }

    @Override
    public void dispose() {
        picasso.shutdown();
    }

    public void load(Node node, CmisSession session, ImageView iv, CompatLambda.Consumer<Boolean> callback) {
        if (node == null || session == null) {
            return;
        }

        Uri uri = new Uri.Builder().scheme(SCHEME).authority(node.getUuid()).build();
        sessions.put(uri, session);

        picasso.load(uri).noFade().into(iv, new Callback() {

            @Override
            public void onSuccess() {
                try {
                    sessions.remove(uri);
                    callback.accept(true);
                } catch (Exception ex) {
                    OdsLog.ex(getClass(), ex);
                }
            }

            @Override
            public void onError() {
                try {
                    sessions.remove(uri);
                    callback.accept(false);
                } catch (Exception ex) {
                    OdsLog.ex(getClass(), ex);
                }
            }
        });
    }

    public void cancel(ImageView iv) {
        picasso.cancelRequest(iv);
    }
}
