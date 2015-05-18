package org.opendataspace.android.operation;

import com.j256.ormlite.dao.CloseableIterator;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.data.DaoBase;
import org.opendataspace.android.object.ObjectBase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class OperationBaseFetch<T extends ObjectBase, U> extends OperationBaseCmis {

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        List<T> data = new ArrayList<>();
        DaoBase<T> dao = dao();
        CloseableIterator<T> it = localObjects(dao);

        try {
            while (it.hasNext()) {
                data.add(it.next());
            }
        } finally {
            it.close();
        }

        if (isCancel()) {
            throw new InterruptedException();
        }

        OdsApp.get().getDatabase().transact(() -> {
            final List<T> copy = new ArrayList<>();

            for (U cur : fetch()) {
                T obj = find(cur, data);

                if (obj == null) {
                    dao.create(createObject(cur));
                } else if (merge(obj, cur)) {
                    dao.update(obj);
                }

                copy.add(obj);

                if (isCancel()) {
                    throw new InterruptedException();
                }
            }

            for (T cur : data) {
                if (copy.indexOf(cur) == -1) {
                    dao.delete(cur);
                }
            }

            if (isCancel()) {
                throw new InterruptedException();
            }

            return null;
        });
    }

    protected abstract CloseableIterator<T> localObjects(DaoBase<T> dao) throws SQLException;

    protected abstract DaoBase<T> dao();

    protected abstract List<U> fetch();

    protected abstract T find(U val, List<T> ls);

    protected abstract T createObject(U val);

    protected abstract boolean merge(T obj, U val);
}
