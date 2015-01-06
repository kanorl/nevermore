package com.shadow.entity;

import com.shadow.entity.annotation.AutoSave;
import com.shadow.entity.cache.annotation.CacheIndex;
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

    @CacheIndex
    private int count;

    @CacheIndex
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

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public int getCount() {
        return count;
    }

    public long getPlayerId() {
        return playerId;
    }
}
