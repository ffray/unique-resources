package biz.itcf.uniqueres;

import java.io.File;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;

public class IndexedResourceFiltering implements MavenResourcesFiltering {

    @Override
    public void filterResources(List resources, File outputDirectory, MavenProject mavenProject, String encoding,
            List fileFilters, List nonFilteredFileExtensions, MavenSession mavenSession) throws MavenFilteringException {
    }

    @Override
    public void filterResources(List resources, File outputDirectory, String encoding, List filterWrappers,
            File resourcesBaseDirectory, List nonFilteredFileExtensions) throws MavenFilteringException {
    }

    @Override
    public List getDefaultNonFilteredFileExtensions() {
        return null;
    }

    @Override
    public boolean filteredFileExtension(String fileName, List userNonFilteredFileExtensions) {
        return false;
    }

    @Override
    public void filterResources(MavenResourcesExecution mavenResourcesExecution) throws MavenFilteringException {
//        mavenResourcesExecution.getMavenProject().getRuntimeClasspathElements();
    }

}
