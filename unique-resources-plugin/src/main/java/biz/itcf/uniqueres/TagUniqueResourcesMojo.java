package biz.itcf.uniqueres;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.Scanner;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * 
 * @author Florian
 * @goal tag
 * @phase generate-resources
 * @requiresProject true
 */
public class TagUniqueResourcesMojo extends AbstractMojo {

    /**
     * @readonly
     * @parameter default-value="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * @parameter
     * @required
     */
    private List<Resource> resources;

    /**
     * @parameter default-value="${project.build.directory}/generated-resources"
     */
    private File ouputDirectory;

    /**
     * @component
     */
    private BuildContext buildContext;

    /**
     * Search-pattern matching the un-tagged resource name.
     * The default pattern provides two match-groups:
     * 1. source-path up to the file extension
     * 2. the file extension including the dot
     * 
     * These groups can be referenced by the {@link #taggedReplacementPattern}.
     * 
     * @parameter default-value="(.*?)(\\.[^.]*)"
     */
    private String untaggedSearchPattern;

    /**
     * Replacement pattern to create names for the tagged files.
     * A special value is @{unique.id} which addresses the generated Unique-ID of a file.
     * One can use $ plus a number to create back-references to matching groups of {@link #untaggedSearchPattern}.
     * See {@link Matcher#replaceAll(String)} for more information.
     * 
     * @parameter default-value="$1_@{unique.id}$2"
     */
    private String taggedReplacementPattern;

    protected MavenProject getProject() {
        return project;
    }

    protected File getOuputDirectory() {
        return ouputDirectory;
    }

    protected List<Resource> getResources() {
        return resources;
    }

    protected BuildContext getBuildContext() {
        return buildContext;
    }

    protected String getUntaggedSearchPattern() {
        return untaggedSearchPattern;
    }

    protected String getTaggedReplacementPattern() {
        return taggedReplacementPattern;
    }


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Pattern untaggedSearchPattern;
        try {
            untaggedSearchPattern = Pattern.compile(getUntaggedSearchPattern());
        } catch (PatternSyntaxException e) {
            throw new MojoExecutionException("Failed to compile untaggedSearchPattern", e);
        }

        Pattern uniqueIdPattern = Pattern.compile("@\\{unique\\.id\\}");

        for (Resource res : getResources()) {
            File baseDir = new File(res.getDirectory());
            if (!baseDir.isAbsolute()) {
                baseDir = new File(getProject().getBasedir(), res.getDirectory());
            }

            Scanner scanner = makeScanner(res);
            scanner.scan();

            UniqueIDBuilder builder = new CRC32Builder();
            for (String resourceName : scanner.getIncludedFiles()) {
                File resource = new File(baseDir, resourceName);
                String uniqueID = builder.buildID(resource);

                String replacement = uniqueIdPattern.matcher(taggedReplacementPattern).replaceAll(uniqueID);
                String taggedResourceName = untaggedSearchPattern.matcher(resourceName).replaceAll(replacement);

                File taggedResource = new File(getOuputDirectory(), taggedResourceName);

                try {
                    FileUtils.copyFile(resource, taggedResource);
                } catch (IOException e) {
                    throw new MojoExecutionException("Failed to copy file " + resource.getPath() + " to " + taggedResource.getPath(), e);
                }
            }

            Resource outputResource = new Resource();
            outputResource.setDirectory(getOuputDirectory().getAbsolutePath());

            if (getLog().isDebugEnabled()) {
                getLog().debug("Adding resources " + outputResource);
            }

            getProject().addResource(outputResource);
        }
    }

    protected Scanner makeScanner(Resource res) {
        List<String> includes = res.getIncludes();
        if (includes.isEmpty()) {
            includes.add("**/*");
        }

        List<String> excludes = res.getExcludes();

        File baseDir = new File(res.getDirectory());
        Scanner scanner = getBuildContext().newScanner(baseDir);
        scanner.setIncludes(includes.toArray(new String[includes.size()]));
        scanner.setExcludes(excludes.toArray(new String[excludes.size()]));
        return scanner;
    }

}
