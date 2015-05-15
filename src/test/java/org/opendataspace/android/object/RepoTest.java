package org.opendataspace.android.object;

import junit.framework.Assert;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.CompatObjects;
import org.opendataspace.android.cmis.Cmis;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;

import java.util.List;

@RunWith(TestRunner.class)
public class RepoTest {

    @Test
    public void checkType() throws Exception {
        List<Repository> ls = Cmis.factory.getRepositories(Cmis.createSessionSettings(TestUtil.getDefaultAccount()));

        checkRepo(ls, Repo.Type.PRIVATE, "my");
        checkRepo(ls, Repo.Type.SHARED, "shared");
        checkRepo(ls, Repo.Type.GLOBAL, "global");
        checkRepo(ls, Repo.Type.CONFIG, "config");
    }

    @SuppressWarnings("ConstantConditions")
    private void checkRepo(List<Repository> ls, Repo.Type type, String name) {
        Repository cmis = null;

        for (Repository cur : ls) {
            if (CompatObjects.equals(cur.getName(), name)) {
                cmis = cur;
                break;
            }
        }

        Assert.assertEquals(true, cmis != null);
        Repo repo = new Repo();
        Assert.assertEquals(Repo.Type.DEFAULT, repo.getType());

        boolean res = repo.merge(cmis);
        Assert.assertEquals(true, res);
        Assert.assertEquals(type, repo.getType());
        Assert.assertEquals(cmis.getRootFolderId(), repo.getRootFolderUuid());
        Assert.assertEquals(cmis.getName(), repo.getName());
        Assert.assertEquals(cmis.getId(), repo.getUuid());
    }
}
