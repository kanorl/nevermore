package com.shadow.resource;

import com.shadow.util.injection.Injected;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author nevermore on 2015/1/15
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class ResourceTest {

    @Injected
    private ResourceHolder<TestResource> testResources;

    @Test
    public void test() {
        System.out.println(testResources.getAll());
    }
}
