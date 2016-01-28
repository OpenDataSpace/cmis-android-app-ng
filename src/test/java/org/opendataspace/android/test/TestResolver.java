package org.opendataspace.android.test;

import org.apache.maven.artifact.ant.DependenciesTask;
import org.apache.maven.artifact.ant.RemoteRepository;
import org.robolectric.internal.dependency.MavenDependencyResolver;

public class TestResolver extends MavenDependencyResolver {

    @Override
    protected void configureMaven(final DependenciesTask dependenciesTask) {
        RemoteRepository sonatypeRepository = new RemoteRepository();
        sonatypeRepository.setUrl("https://repo1.maven.org/maven2");
        sonatypeRepository.setId("central");
        dependenciesTask.addConfiguredRemoteRepository(sonatypeRepository);
    }
}
