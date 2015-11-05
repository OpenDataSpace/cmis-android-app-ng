package org.opendataspace.android.cmis;

import com.google.gson.annotations.Expose;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.client.runtime.OperationContextImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderList;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.spi.NavigationService;
import org.apache.chemistry.opencmis.commons.spi.ObjectService;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.object.Repo;
import org.opendataspace.android.storage.FileInfo;
import org.opendataspace.android.storage.LimitInputStream;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class CmisSession {

    private static final Set<Updatability> CREATE_UPDATABILITY = new HashSet<>();
    private static final long CHUNK_SIZE = 4 * 1024 * 1024;

    static {
        CREATE_UPDATABILITY.add(Updatability.ONCREATE);
        CREATE_UPDATABILITY.add(Updatability.READWRITE);
    }


    @Expose
    private final Repo repo;

    @Expose
    private final Account account;

    private transient Session session;
    private transient Folder root;

    public CmisSession(Account account, Repo repo) {
        this.account = account;
        this.repo = repo;
    }

    public CmisObject getObjectByPath(String path) {
        Session session = getSession();
        OperationContext context = session.getDefaultContext();
        ObjectService objectService = session.getBinding().getObjectService();
        ObjectFactory objectFactory = session.getObjectFactory();
        String root = getRoot().getPath();

        if (!root.endsWith("/")) {
            root += "/";
        }

        try {
            ObjectData objectData = objectService
                    .getObjectByPath(session.getRepositoryInfo().getId(), root + path, context.getFilterString(),
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

    public long size(CmisObject obj) {
        return obj.<BigInteger>getPropertyValue(PropertyIds.CONTENT_STREAM_LENGTH).longValue();
    }

    private Session getSession() {
        if (session == null) {
            session = Cmis.factory.createSession(Cmis.createSessionSettings(account, repo));
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

    public void delete(Node node) {
        if (node.getType() == Node.Type.FOLDER) {
            getSession().getBinding().getObjectService()
                    .deleteTree(repo.getUuid(), node.getUuid(), true, null, false, null);
        } else {
            getSession().delete(new ObjectIdImpl(node.getUuid()));
        }
    }

    public CmisObject getObjectById(String uuid) {
        return getSession().getObject(new ObjectIdImpl(uuid));
    }

    public Account getAccount() {
        return account;
    }

    public ContentStream getStream(Node node) {
        return getSession().getBinding().getObjectService()
                .getContentStream(repo.getUuid(), node.getUuid(), null, null, null, null);
    }

    public CmisObject createDocument(Node folder, String name, FileInfo info) throws IOException {
        FileInputStream is = null;

        try {
            Session session = getSession();
            Map<String, Serializable> properties = new HashMap<>();
            properties.put(PropertyIds.OBJECT_TYPE_ID, BaseTypeId.CMIS_DOCUMENT.value());
            properties.put(PropertyIds.NAME, name);

            if (info != null) {
                File f = info.getFile();
                is = new FileInputStream(f);

                ObjectFactory objectFactory = session.getObjectFactory();
                Document doc = null;
                long pos = 0, total = f.length();

                try {
                    while (pos < total) {
                        LimitInputStream lis = new LimitInputStream(is, CHUNK_SIZE);
                        long sz = Math.min(CHUNK_SIZE, total - pos);
                        pos += sz;
                        ContentStream c = objectFactory.createContentStream(f.getName(), sz, info.getMimeType(), lis);

                        if (doc == null) {
                            String newId = session.getBinding().getObjectService().createDocument(repo.getUuid(),
                                    session.getObjectFactory()
                                            .convertProperties(properties, null, null, CREATE_UPDATABILITY),
                                    folder.getUuid(), null, VersioningState.CHECKEDOUT, null, null, null, null);

                            if (newId != null) {
                                doc = (Document) session.getObject(newId);
                            } else {
                                break;
                            }
                        }

                        doc.appendContentStream(c, pos < total, true);
                    }
                } catch (Exception ex) {
                    if (doc != null) {
                        doc.cancelCheckOut();
                    }

                    throw ex;
                }

                if (doc != null) {
                    doc.checkIn(true, null, null, null);
                }

                return doc;
            } else {
                String id = session.getBinding().getObjectService().createDocument(repo.getUuid(),
                        session.getObjectFactory().convertProperties(properties, null, null, CREATE_UPDATABILITY),
                        folder.getUuid(), null, VersioningState.MAJOR, null, null, null, null);

                return session.getObject(id);
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
