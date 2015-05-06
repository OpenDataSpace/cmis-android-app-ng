package org.opendataspace.android.cmis;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectFactory;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.spi.ObjectService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public class CmisSession {

    private final Session session;
    private Folder root;

    public CmisSession(Session session) {
        this.session = session;
    }

    public CmisObject getObject(String path) {
        OperationContext context = session.getDefaultContext();
        ObjectService objectService = session.getBinding().getObjectService();
        ObjectFactory objectFactory = session.getObjectFactory();
        String root = getRoot().getPath();

        try {
            ObjectData objectData = objectService
                    .getObjectByPath(session.getRepositoryInfo().getId(), root + "/" + path, context.getFilterString(),
                            context.isIncludeAllowableActions(), context.getIncludeRelationships(),
                            context.getRenditionFilterString(), context.isIncludePolicies(), context.isIncludeAcls(),
                            null);

            return objectFactory.convertObject(objectData, context);
        } catch (CmisObjectNotFoundException ex) {
            return null;
        }
    }

    public Folder getRoot() {
        if (root == null) {
            root = session.getRootFolder();
        }

        return root;
    }

    public void save(CmisObject cmo, File f) throws IOException {
        OutputStream os = null;
        InputStream src = null;

        try {
            long downloaded = 0, sz = size(cmo);
            os = new BufferedOutputStream(new FileOutputStream(f));
            src = session.getBinding().getObjectService()
                    .getContentStream(session.getRepositoryInfo().getId(), cmo.getId(), null, null, null, null)
                    .getStream();

            byte[] buffer = new byte[1024];

            while (sz - downloaded > 0) {
                int read = src.read(buffer);

                if (read == -1) {
                    break;
                }

                os.write(buffer, 0, read);
                downloaded += read;
            }
        } finally {
            if (os != null) {
                os.close();
            }
            if (src != null) {
                src.close();
            }
        }
    }

    public long size(CmisObject obj) {
        return obj.<BigInteger>getPropertyValue(PropertyIds.CONTENT_STREAM_LENGTH).longValue();
    }
}
