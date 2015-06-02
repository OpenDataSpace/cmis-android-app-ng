package org.opendataspace.android.cmis;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.object.Node;

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
}
