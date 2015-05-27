package org.opendataspace.android.cmis;

import com.google.gson.annotations.Expose;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectFactory;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.client.runtime.OperationContextImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderList;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.spi.NavigationService;
import org.apache.chemistry.opencmis.commons.spi.ObjectService;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.object.Repo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CmisSession {

    private transient Session session;
    private transient Folder root;

    @Expose
    private final Map<String, String> config;

    @Expose
    private final Repo repo;

    public CmisSession(Account account, Repo repo) {
        config = Cmis.createSessionSettings(account, repo);
        this.repo = repo;
    }

    public CmisObject getObject(String path) {
        Session session = getSession();
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
            root = getSession().getRootFolder();
        }

        return root;
    }

    public void save(CmisObject cmo, File f) throws IOException {
        OutputStream os = null;
        InputStream src = null;

        try {
            long downloaded = 0, sz = size(cmo);
            Session session = getSession();
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

    private Session getSession() {
        if (session == null) {
            session = Cmis.factory.createSession(config);
        }

        return session;
    }

    public boolean same(Repo repo) {
        return this.repo.equals(repo);
    }

    public Repo getRepo() {
        return repo;
    }

    public List<CmisObject> children(Node folder) {
        Session session = getSession();
        NavigationService ns = session.getBinding().getNavigationService();
        ObjectFactory objectFactory = session.getObjectFactory();
        OperationContext context = new OperationContextImpl(session.getDefaultContext());
        ObjectInFolderList children;
        List<CmisObject> res = new ArrayList<>();
        long pos = 0;

        do {
            children =
                    ns.getChildren(repo.getUuid(), folder != null ? folder.getUuid() : repo.getRootFolderUuid(), null,
                            null, true, IncludeRelationships.NONE, null, false, BigInteger.valueOf(50),
                            BigInteger.valueOf(pos), null);

            List<ObjectInFolderData> ls = children.getObjects();

            if (ls == null) {
                break;
            }

            for (ObjectInFolderData cur : ls) {
                res.add(objectFactory.convertObject(cur.getObject(), context));
            }

            pos += ls.size();
        } while (children.hasMoreItems());

        return res;
    }

    public InputStream getRendition(String nodeId) {
        Session session = getSession();
        OperationContext context = session.createOperationContext();
        context.setRenditionFilterString("image/*");

        List<Rendition> renditions = session.getObject(nodeId, context).getRenditions();
        Rendition r = null;

        for (Rendition cur : renditions) {
            if (r == null || r.getWidth() * r.getHeight() < cur.getHeight() * cur.getWidth()) {
                r = cur;
            }
        }

        return r != null ? r.getContentStream().getStream() : null;
    }

    public CmisObject createFolder(Node parent, String name) {
        Session session = getSession();
        Map<String, Serializable> properties = new HashMap<>();

        properties.put(PropertyIds.OBJECT_TYPE_ID, BaseTypeId.CMIS_FOLDER.value());
        properties.put(PropertyIds.NAME, name);
        return session.getObject(session.createFolder(properties,
                new ObjectIdImpl(parent != null ? parent.getUuid() : repo.getRootFolderUuid())));
    }

    public void delete(String id) {
        getSession().delete(new ObjectIdImpl(id));
    }
}
