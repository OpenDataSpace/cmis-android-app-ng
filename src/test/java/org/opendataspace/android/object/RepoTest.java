package org.opendataspace.android.object;

import junit.framework.Assert;

import org.apache.chemistry.opencmis.client.api.Repository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.opendataspace.android.app.CompatObjects;
import org.opendataspace.android.cmis.Cmis;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;

import java.util.List;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class RepoTest {

    @Test
    public void checkType() throws Exception {
        List<Repository> ls = Cmis.factory.getRepositories(Cmis.createSessionSettings(TestUtil.getDefaultAccount()));

        checkRepo(ls, Repo.Type.PRIVATE, "my", false);
        checkRepo(ls, Repo.Type.SHARED, "shared", false);
        checkRepo(ls, Repo.Type.GLOBAL, "global", false);
        checkRepo(ls, Repo.Type.CONFIG, "config", true);
        checkRepo(ls, Repo.Type.PROJECTS, "projects", true);
    }

    @SuppressWarnings("ConstantConditions")
    private void checkRepo(final List<Repository> ls, final Repo.Type type, final String name,
            final boolean isOprional) {
        Repo repo = new Repo();
        Assert.assertEquals(Repo.Type.DEFAULT, repo.getType());

        final Repository mock = Mockito.mock(Repository.class);
        Mockito.when(mock.getName()).thenReturn(name);
        Assert.assertEquals(true, repo.merge(mock));
        Assert.assertEquals(type, repo.getType());

        Repository cmis = null;

        for (Repository cur : ls) {
            if (CompatObjects.equals(cur.getName(), name)) {
                cmis = cur;
                break;
            }
        }

        if (isOprional && cmis == null) {
            return;
        }

        repo = new Repo();
        Assert.assertEquals(true, cmis != null);
        Assert.assertEquals(true, repo.merge(cmis));
        Assert.assertEquals(type, repo.getType());
        Assert.assertEquals(cmis.getRootFolderId(), repo.getRootFolderUuid());
        Assert.assertEquals(cmis.getName(), repo.getName());
        Assert.assertEquals(cmis.getId(), repo.getUuid());
    }
}
