package com.shadow.entity;

import com.shadow.entity.annotation.AutoSave;
import com.shadow.entity.annotation.IndexedProperty;
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

    @IndexedProperty
    private int count;

    @IndexedProperty
    private long playerId;

    public static Item valueOf(int id) {
        Item i = new Item();
        i.id = id;
        return i;
    }

    @AutoSave
    public void increase() {
        count++;
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
