package org.opendataspace.android.cmis;

import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.data.DaoNode;
import org.opendataspace.android.data.DataBase;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.status.StatusContext;
import org.opendataspace.android.storage.CacheRemoveTransaction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.sql.SQLException;

public class CmisOperations {

    public static Node createFolder(CmisSession session, Node parent, String name, StatusContext status)
            throws SQLException {
        Node node = new Node(session.createFolder(parent, name, status), parent);
        OdsApp.get().getDatabase().getNodes().create(node);
        return node;
    }

    public static void deleteNode(CmisSession session, Node node, StatusContext status) throws SQLException {
        session.delete(node, status);
        OdsApp app = OdsApp.get();
        DataBase db = app.getDatabase();
        DaoNode nodes = db.getNodes();
        CacheRemoveTransaction t = new CacheRemoveTransaction();

        db.transact(() -> {
            nodes.delete(node);
            app.getCacheManager().prepareRemove(nodes, session.getRepo(), node, t);
            return null;
        });

        t.commit();
    }

    public static boolean download(CmisSession session, Node node, File file, StatusContext status) throws IOException {
        if (!file.exists() && !file.createNewFile()) {
            return false;
        }

        ContentStream stream = session.getStream(node, status);
        long size = stream.getLength();

        if (size < 0) {
            size = node.getSize();
        }

        FileOutputStream fs = new FileOutputStream(file, false);

        //noinspection TryFinallyCanBeTryWithResources
        try {
            fs.getChannel().transferFrom(Channels.newChannel(stream.getStream()), 0, size);
        } finally {
            fs.close();
        }

        return true;
    }
}
