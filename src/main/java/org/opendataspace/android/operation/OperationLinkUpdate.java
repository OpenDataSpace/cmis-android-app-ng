package org.opendataspace.android.operation;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;

import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.cmis.CmisDefinitions;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Link;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OperationLinkUpdate extends OperationBaseCmis {

    @Expose
    private final Link link;

    @Expose
    private final CmisSession session;

    public OperationLinkUpdate(final Link link, final CmisSession session) {
        this.link = link;
        this.session = session;
    }

    @Override
    protected void doExecute(final OperationResult result) throws Exception {
        final Map<String, Object> properties = new HashMap<>();
        properties.put(PropertyIds.EXPIRATION_DATE, link.getExpires());
        properties.put(CmisDefinitions.SUBJECT_PROP_ID, link.getName());
        properties.put(CmisDefinitions.MESSAGE_PROP_ID, link.getMessage());
        properties.put(CmisDefinitions.EMAIL_PROP_ID, Arrays.asList(link.getEmail().split(",")));

        if (!TextUtils.isEmpty(link.getPassword())) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(link.getPassword().getBytes("UTF-8"));
            properties.put(CmisDefinitions.PASSWORD_PROP_ID, new BigInteger(1, hash).toString(16));
        }

        session.getObjectById(link.getObjectId(), getStatus()).updateProperties(properties);
        OdsApp.get().getDatabase().getLinks().update(link);
    }
}
