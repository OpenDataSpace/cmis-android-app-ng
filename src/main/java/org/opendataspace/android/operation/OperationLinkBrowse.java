package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Link;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.view.ViewLink;

public class OperationLinkBrowse extends OperationBase {

    @Expose
    private final Node node;

    @Expose
    private final Link.Type type;

    @Expose
    private final CmisSession session;

    private transient ViewLink view;

    public OperationLinkBrowse(final CmisSession session, final Node node, final Link.Type type) {
        this.node = node;
        this.type = type;
        this.session = session;
        view = OdsApp.get().getViewManager().createLinkView();
    }

    @Override
    protected void doExecute(final OperationResult result) throws Exception {
        final OdsApp app = OdsApp.get();
        view.sync(app.getDatabase().getLinks());

        if (!isCancel()) {
            app.getPool().execute(new OperationLinkFetch(session, node, type));
        }

        result.setOk();
    }

    public ViewLink getView() {
        return view;
    }

    public Link.Type getType() {
        return type;
    }

    public Node getNode() {
        return node;
    }

    public CmisSession getSession() {
        return session;
    }
}
