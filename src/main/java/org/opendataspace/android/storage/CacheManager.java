package org.opendataspace.android.storage;

import android.content.Context;
import android.text.TextUtils;

import com.j256.ormlite.dao.CloseableIterator;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.cmis.CmisOperations;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.data.DaoCacheEntry;
import org.opendataspace.android.data.DaoNode;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.CacheEntry;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.object.Repo;
import org.opendataspace.android.status.StatusContext;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class CacheManager {

    private final DaoCacheEntry dao;
    private final Context context;

    public CacheManager(Context context, DaoCacheEntry dao) {
        this.context = context;
        this.dao = dao;
    }

    public void prepareRemove(DaoNode nodes, Repo repo, Node node, CacheRemoveTransaction t) throws SQLException {
        checkId(node.getCacheEntryId(), t);
        //checkId(node.getPreviewCacheEntryId(), t);

        if (node.getType() != Node.Type.FOLDER) {
            return;
        }

        CloseableIterator<Node> it = nodes.forParent(repo, node.getId());

        try {
            while (it.hasNext()) {
                prepareRemove(nodes, repo, it.nextThrow(), t);
            }
        } finally {
            it.close();
        }
    }

    private void checkId(long id, CacheRemoveTransaction t) throws SQLException {
        CacheEntry entry = dao.get(id);

        if (entry == null) {
            return;
        }

        t.add(entry);
        dao.delete(entry);
    }

    public void repoDeleted(Account account, Repo repo) {
        Storage.deleteTree(Storage.getLocalFolder(context, account, repo, null));
    }

    public void accountDeleted(Account account) {
        Storage.deleteTree(Storage.getAccountFolder(context, account));
    }

    public File getLocal(Node node) throws Exception {
        CacheEntry ce = dao.get(node.getCacheEntryId());
        return ce != null ? ce.getFile(node) : null;
    }

    public File download(CmisSession session, Node node, StatusContext status) throws Exception {
        CacheEntry ce = dao.get(node.getCacheEntryId());
        File f = ce != null ? ce.getFile(node) : null;

        if (f != null) {
            return f;
        }

        File folder = Storage.getLocalFolder(context, session.getAccount(), session.getRepo(), Storage.CATEGORY_CACHE);

        if (folder == null) {
            return null;
        }

        try {
            if (ce == null || TextUtils.isEmpty(ce.getPath())) {
                f = File.createTempFile("lo-", "", folder);
            } else {
                f = new File(ce.getPath());
            }

            if (!CmisOperations.download(session, node, f, status)) {
                throw new IOException();
            }

            if (ce == null) {
                ce = new CacheEntry(f, session.getRepo());
            } else {
                ce.update(f);
            }

            dao.createOrUpdate(ce);
        } catch (Exception ex) {
            OdsLog.ex(getClass(), ex);

            if (f != null) {
                //noinspection ResultOfMethodCallIgnored
                f.delete();
            }

            return null;
        }

        node.setCacheEntry(ce);
        OdsApp.get().getDatabase().getNodes().update(node);
        return f;
    }
}
