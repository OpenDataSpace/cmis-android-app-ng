package org.opendataspace.android.objects;

import com.j256.ormlite.dao.CloseableIterator;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.cmis.Cmis;
import org.opendataspace.android.data.DaoRepo;

import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepoCollection {

    private final WeakReference<Account> account;
    private List<Repo> data = new ArrayList<>();

    RepoCollection(Account account) throws SQLException {
        this.account = new WeakReference<>(account);
        CloseableIterator<Repo> it = OdsApp.get().getDatabase().getRepos().forAccount(account);

        try {
            while (it.hasNext()) {
                data.add(it.next());
            }
        } finally {
            it.close();
        }
    }

    public void sync() throws SQLException {
        final Account acc = account.get();
        final DaoRepo dao = OdsApp.get().getDatabase().getRepos();
        final List<Repo> copy = new ArrayList<>();

        for (Repo cur : data) {
            copy.add(new Repo(cur));
        }

        dao.callBatchTasks(() -> {
            for (Repository cur : Cmis.factory.getRepositories(Cmis.createSessionSettings(acc))) {
                Repo repo = findByUuid(cur.getId(), copy);

                if (repo == null) {
                    repo = new Repo(cur, acc);
                    dao.create(repo);
                    copy.add(repo);
                } else if (repo.merge(cur)) {
                    dao.update(repo);
                }
            }

            return null;
        });

        data = copy;
    }

    private Repo findByUuid(String uuid, List<Repo> ls) {
        for (Repo cur : ls) {
            if (cur.getUuid().equals(uuid)) {
                return cur;
            }
        }

        return null;
    }
}
