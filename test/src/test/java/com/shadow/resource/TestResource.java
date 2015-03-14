package com.shadow.resource;

import com.shadow.resource.annotation.Id;
import com.shadow.resource.annotation.Resource;
import com.shadow.util.codec.JsonUtil;

/**
 * @author nevermore on 2015/1/15
 */
@Resource
public class TestResource implements Comparable<TestResource>, Randomable {

    public static final String NAME_INDEX = "NAME_INDEX";

    @Id
    private int id;
    private String name;
    private int rate;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(TestResource o) {
        if (this.id == 1) {
            return 1;
        }
        if (o.id == 1) {
            return -1;
        }
        return Integer.compare(this.id, o.id);
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }

    @Override
    public int getRate() {
        return rate;
    }
}
