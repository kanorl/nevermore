package com.shadow.resource;

import com.shadow.common.util.codec.JsonUtil;
import com.shadow.resource.annotation.AfterPropertiesSet;
import com.shadow.resource.annotation.Id;
import com.shadow.resource.annotation.Resource;

import javax.sound.midi.Soundbank;

/**
 * @author nevermore on 2015/1/15
 */
@Resource
public class TestResource implements Comparable<TestResource>, Randomable {

    public static final String NAME_INDEX = "NAME_INDEX";

    @Id
    private int id;
    private String name = "default";
    private int rate;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(TestResource o) {
        return this.getName().compareTo(o.getName());
    }

    @AfterPropertiesSet
    private void afterPropertiesSet() {
        System.out.println("afterPropertiesSet invoked.");
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
