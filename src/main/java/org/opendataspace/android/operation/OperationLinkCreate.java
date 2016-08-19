package org.opendataspace.android.operation;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.cmis.CmisDefinitions;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Link;
import org.opendataspace.android.object.Node;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OperationLinkCreate extends OperationBaseCmis {

    @Expose
    private final Link link;

    @Expose
    private final CmisSession session;

    public OperationLinkCreate(final Link link, final CmisSession session) {
        this.link = link;
        this.session = session;
    }

    @Override
    protected void doExecute(final OperationResult result) throws Exception {
        final Map<String, Object> properties = new HashMap<>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, BaseTypeId.CMIS_ITEM.value());
        properties.put(PropertyIds.EXPIRATION_DATE, link.getExpires());
        properties.put(CmisDefinitions.SUBJECT_PROP_ID, link.getName());
        properties.put(CmisDefinitions.MESSAGE_PROP_ID, link.getMessage());

        properties.put(CmisDefinitions.EMAIL_PROP_ID, Arrays.asList(link.getEmail().split(",")));
        properties.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, Collections.singletonList(CmisDefinitions.LINK_TYPE_ID));
        properties.put(CmisDefinitions.LTYPE_PROP_ID,
                link.getType() == Link.Type.DOWNLOAD ? CmisDefinitions.LINK_TYPE_DOWNLAOD :
                        CmisDefinitions.LINK_TYPE_UPLOAD);

        if (!TextUtils.isEmpty(link.getPassword())) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(link.getPassword().getBytes("UTF-8"));
            properties.put(CmisDefinitions.PASSWORD_PROP_ID, new BigInteger(1, hash).toString(16));
        }

        final Node node = OdsApp.get().getDatabase().getNodes().get(link.getNodeId());
        final CmisObject item = session.createItem(properties, node.getParentUuid(), getStatus());
        link.setUrl(item.<String>getProperty(CmisDefinitions.URL_PROP_ID).getFirstValue());
        link.setObjectId(item.getId());
        link.setRelationId(session.createRelationship(link.getObjectId(), node.getUuid(), getStatus()));
        OdsApp.get().getDatabase().getLinks().create(link);
    }
}
