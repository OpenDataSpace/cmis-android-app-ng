package org.opendataspace.android.operation;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Link;

public class OperationLinkDelete extends OperationBaseCmis {

    @Expose
    private final Link link;

    @Expose
    private final CmisSession session;

    public OperationLinkDelete(final Link link, final CmisSession session) {
        this.link = link;
        this.session = session;
    }

    @Override
    protected void doExecute(final OperationResult result) throws Exception {
        final String relId = link.getRelationId();

        if (!TextUtils.isEmpty(relId)) {
            session.delete(relId, getStatus());
            link.setRelationId(null);
        }

        session.delete(link.getObjectId(), getStatus());
        OdsApp.get().getDatabase().getLinks().delete(link);
    }
}
