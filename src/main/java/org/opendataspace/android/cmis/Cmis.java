package org.opendataspace.android.cmis;

import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Repo;

import java.util.HashMap;
import java.util.Map;

public class Cmis {

    public static final SessionFactory factory = SessionFactoryImpl.newInstance();

    public static Map<String, String> createSessionSettings(Account account) {
        Map<String, String> settings = new HashMap<>();

        settings.put(SessionParameter.USER, account.getLogin());
        settings.put(SessionParameter.PASSWORD, account.getPassword());
        settings.put(SessionParameter.AUTHENTICATION_PROVIDER_CLASS, CmisAuthProvider.class.getCanonicalName());

        if (account.isUseJson()) {
            settings.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());
            settings.put(SessionParameter.BROWSER_URL, account.getUri().toString());
        } else {
            settings.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
            settings.put(SessionParameter.ATOMPUB_URL, account.getUri().toString());
        }

        return settings;
    }

    protected static Map<String, String> createSessionSettings(Account account, Repo repo) {
        Map<String, String> settings = createSessionSettings(account);
        settings.put(SessionParameter.REPOSITORY_ID, repo.getUuid());
        return settings;
    }
}
