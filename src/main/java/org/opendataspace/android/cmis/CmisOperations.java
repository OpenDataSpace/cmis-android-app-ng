package org.opendataspace.android.cmis;

import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.object.Node;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.sql.SQLException;

public class CmisOperations {

    public static Node createFolder(CmisSession session, Node parent, String name) throws SQLException {
        Node node = new Node(session.createFolder(parent, name), parent);
        OdsApp.get().getDatabase().getNodes().create(node);
        return node;
    }

    public static void deleteNode(CmisSession session, Node node) throws SQLException {
        session.delete(node);
        OdsApp.get().getDatabase().getNodes().delete(node);
    }

    public static boolean download(CmisSession session, Node node, File file) throws IOException {
        if (!file.exists() && !file.createNewFile()) {
            return false;
        }

        ContentStream stream = session.getStream(node);
        long size = stream.getLength();

        if (size < 0) {
            size = node.getSize();
        }

        FileOutputStream fs = new FileOutputStream(file);

        try {
            fs.getChannel().transferFrom(Channels.newChannel(stream.getStream()), 0, size);
        } finally {
            fs.close();
        }

        return true;
    }
}
