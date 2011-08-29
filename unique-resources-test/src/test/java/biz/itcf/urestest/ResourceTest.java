package biz.itcf.urestest;

import org.junit.Test;

public class ResourceTest {

    @Test
    public void findTest() {
        System.out.println(getClass().getResource("/test_3871121566.txt"));
    }
}
