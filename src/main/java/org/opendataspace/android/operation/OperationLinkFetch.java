package org.opendataspace.android.operation;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.dao.CloseableIterator;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Relationship;
import org.apache.chemistry.opencmis.client.api.SecondaryType;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.cmis.CmisDefinitions;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.event.EventLinkUpdate;
import org.opendataspace.android.object.Link;
import org.opendataspace.android.object.Node;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OperationLinkFetch extends OperationBaseFetch<Link, Link> {

    @Expose
    private final CmisSession session;

    @Expose
    private final Node folder;

    @Expose
    private final Link.Type type;

    public OperationLinkFetch(CmisSession session, Node folder, final Link.Type type) {
        this.session = session;
        this.folder = folder;
        this.type = type;
    }


    @Override
    protected void doExecute(OperationResult result) throws Exception {
        final String name = folder.getName();
        getStatus().postMessage(R.string.status_synclink, TextUtils.isEmpty(name) ? "/" : name);
        process(this, this);

        if (isCancel()) {
            throw new InterruptedException();
        }

        OdsApp.bus.post(new EventLinkUpdate(folder.getUuid(), type));
        result.setOk();
    }

    @Override
    protected CloseableIterator<Link> localObjects() throws SQLException {
        return OdsApp.get().getDatabase().getLinks().getLinksByNode(folder, type);
    }

    @Override
    protected List<Link> fetch() {
        final List<Link> data = new ArrayList<>();
        final List<Relationship> relationships = session.getRelations(folder, getStatus());

        if (relationships == null) {
            return data;
        }

        for (final Relationship relationship : relationships) {
            final CmisObject cmo;

            try {
                cmo = relationship.getSource();
            } catch (Exception ex) {
                OdsLog.ex(getClass(), ex);
                continue;
            }

            boolean found = false;

            if (cmo == null || cmo.getSecondaryTypes() == null) {
                continue;
            }

            for (final SecondaryType secondaryType : cmo.getSecondaryTypes()) {
                if (CmisDefinitions.LINK_TYPE_ID.equals(secondaryType.getId())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                continue;
            }

            final String ltype = cmo.getPropertyValue(CmisDefinitions.LTYPE_PROP_ID);

            if ((CmisDefinitions.LINK_TYPE_UPLOAD.equals(ltype) && type == Link.Type.UPLOAD) ||
                    (CmisDefinitions.LINK_TYPE_DOWNLAOD.equals(ltype) && type == Link.Type.DOWNLOAD)) {
                final Link link = new Link();
                final List<String> emails = cmo.getPropertyValue(CmisDefinitions.EMAIL_PROP_ID);
                Calendar expires = cmo.getPropertyValue(PropertyIds.EXPIRATION_DATE);

                if (expires == null) {
                    expires = Calendar.getInstance();
                }

                link.setType(type);
                link.setEmail(TextUtils.join(", ", emails));
                link.setExpires(expires);
                link.setMessage(cmo.getPropertyValue(CmisDefinitions.MESSAGE_PROP_ID));
                link.setName(cmo.getPropertyValue(CmisDefinitions.SUBJECT_PROP_ID));
                link.setNodeId(folder.getId());
                link.setObjectId(cmo.getId());
                link.setUrl(cmo.getPropertyValue(CmisDefinitions.URL_PROP_ID));
                link.setRelationId(relationship.getId());

                if (link.isValid()) {
                    data.add(link);
                }
            }
        }

        return data;
    }

    @Override
    protected Link find(final Link val, final List<Link> ls) {
        for (final Link cur : ls) {
            if (cur.match(val)) {
                return cur;
            }
        }

        return null;
    }

    @Override
    protected void create(final Link val) throws SQLException {
        OdsApp.get().getDatabase().getLinks().create(val);
    }

    @Override
    protected void merge(final Link obj, final Link val) throws SQLException {
        if (obj.merge(val)) {
            OdsApp.get().getDatabase().getLinks().update(obj);
        }
    }

    @Override
    protected void delete(final Link obj) throws SQLException {
        OdsApp.get().getDatabase().getLinks().delete(obj);
    }

    @Override
    protected void cleanup(final List<Link> ls) throws SQLException {
        // nothing
    }
}
