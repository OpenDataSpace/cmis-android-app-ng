package org.opendataspace.android.objects;

import org.apache.chemistry.opencmis.client.api.Repository;
import org.opendataspace.android.cmis.Cmis;

import java.util.List;
import java.util.Map;

public class Repositories {

    private final List<Repository> data;

    Repositories(Map<String, String> settings) {
        data = Cmis.factory.getRepositories(settings);
    }
}
