package com.shadow.util.excel;

import com.shadow.resource.ResourceHolder;
import com.shadow.resource.annotation.InjectedResource;
import com.shadow.util.codec.JsonUtil;
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
        for (TestResource testResource : testResources.getAll()) {
            System.out.println(JsonUtil.toJson(testResource));
        }
    }
}
