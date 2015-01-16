package com.shadow.resource;

import com.shadow.resource.annotation.Id;
import com.shadow.resource.annotation.Resource;

/**
 * @author nevermore on 2015/1/15
 */
@Resource
public class TestResource {

    public static final String NAME_INDEX = "NAME_INDEX";

    @Id
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
