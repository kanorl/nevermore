package com.shadow.resource;

import com.shadow.resource.annotation.InjectedResource;
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

    @InjectedResource
    private ResourceHolder<TestResource> testResources;

    @Test
    public void test() {
        System.out.println(testResources.get(1).getName());

        long s1 = System.nanoTime();
        System.out.println(testResources.findFirst(o -> o.getName().equals("二")).get().getId());
        System.out.println(System.nanoTime() - s1);
    }
}
