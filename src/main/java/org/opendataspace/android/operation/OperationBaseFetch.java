package org.opendataspace.android.operation;

import com.j256.ormlite.dao.CloseableIterator;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.object.ObjectBase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

abstract class OperationBaseFetch<T extends ObjectBase, U> extends OperationBaseCmis {

    public static <T extends ObjectBase, U> void process(OperationBaseFetch<T, U> fetch, OperationBase owner) throws
            Exception {
        List<T> data = new ArrayList<>();
        CloseableIterator<T> it = fetch.localObjects();

        try {
            while (it.hasNext()) {
                data.add(it.nextThrow());
            }
        } finally {
            it.close();
        }

        if (owner.isCancel()) {
            throw new InterruptedException();
        }

        final List<T> deleted = new ArrayList<>();

        OdsApp.get().getDatabase().transact(() -> {
            final List<T> copy = new ArrayList<>();

            for (U cur : fetch.fetch()) {
                T obj = fetch.find(cur, data);

                if (obj == null) {
                    fetch.create(cur);
                } else {
                    fetch.merge(obj, cur);
                }

                copy.add(obj);

                if (owner.isCancel()) {
                    throw new InterruptedException();
                }
            }

            for (T cur : data) {
                if (copy.indexOf(cur) == -1) {
                    deleted.add(cur);
                    fetch.delete(cur);
                }
            }

            if (owner.isCancel()) {
                throw new InterruptedException();
            }

            return null;
        });

        if (!deleted.isEmpty()) {
            fetch.cleanup(deleted);
        }
    }

    protected abstract CloseableIterator<T> localObjects() throws SQLException;

    protected abstract List<U> fetch();

    protected abstract T find(U val, List<T> ls);

    protected abstract void create(U val) throws SQLException;

    protected abstract void merge(T obj, U val) throws SQLException;

    protected abstract void delete(T obj) throws SQLException;

    protected abstract void cleanup(List<T> ls) throws SQLException;
}
