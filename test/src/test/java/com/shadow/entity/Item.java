package com.shadow.entity;

import com.shadow.entity.annotation.RegionIndex;
import com.shadow.util.codec.JsonUtil;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author nevermore on 2015/1/5
 */
@Entity
public class Item extends CacheableEntity<Integer> {

    @Id
    private Integer id;

    private int count;

    @RegionIndex
    private long playerId;

    public static Item valueOf(int id) {
        Item i = new Item();
        i.id = id;
        return i;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
