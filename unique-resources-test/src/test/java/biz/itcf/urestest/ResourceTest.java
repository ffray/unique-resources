package biz.itcf.urestest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

public class ResourceTest {

    @Test
    public void testTaggedIndex() throws IOException {
        final String name = "/tagged-index.properties";
        assertResourceExists(name);
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream(name));

        Assert.assertEquals("dir/test2_0.txt", props.get("dir/test2.txt"));
        Assert.assertEquals("test_0.png", props.get("test.png"));
        Assert.assertEquals("test_2494046904", props.get("test"));
        Assert.assertEquals("test_3871121566.txt", props.get("test.txt"));

        Assert.assertEquals("No. entries in " + name, 4, props.size());
    }

    @Test
    public void testResourceNames() {
        assertResourceExists("/dir/test2_0.txt");
        assertResourceExists("/test_0.png");
        assertResourceExists("/test_2494046904");
        assertResourceExists("/test_3871121566.txt");
    }

    @Test
    public void testForJunk() throws URISyntaxException {
        // should find the project directory based on target/test-classes
        File testClassesDir = new File(getClass().getResource("/").toURI());
        Assert.assertEquals("test-classes", testClassesDir.getName());
        File targetDir = testClassesDir.getParentFile();
        Assert.assertEquals("target", targetDir.getName());
        File projectDir = targetDir.getParentFile();
        File generatedResourcesDir = new File(projectDir, "target" + File.separatorChar + "generated-resources");
        Assert.assertTrue("Generated-Resources directory exists: " + generatedResourcesDir, generatedResourcesDir.exists() && generatedResourcesDir.isDirectory());
        Assert.assertEquals("No. of files in generated-resources", 5, generatedResourcesDir.list().length);
    }

    @Test
    public void testFiltering() throws IOException {
        assertResourceExists("/some.css");
        BufferedReader br = new BufferedReader(new InputStreamReader(getResourceAsStream("/some.css"), "UTF-8"));
        try {
            Assert.assertEquals("span {", br.readLine());
            Assert.assertEquals("  background-image: url(\"test_0.png\");", br.readLine());
            Assert.assertEquals("}", br.readLine());
            Assert.assertNull(br.readLine());
        } finally {
            try {
                br.close();
            } catch (IOException e) {
            }
        }
    }

    public void assertResourceExists(String res) {
        Assert.assertNotNull("Resource: " + res, getClass().getResource(res));
    }
    
    public URL getResource(String res) {
        return getClass().getResource(res);
    }
    
    public InputStream getResourceAsStream(String res) {
        return getClass().getResourceAsStream(res);
    }
}
