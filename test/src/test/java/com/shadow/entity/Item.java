package com.shadow.entity;

import com.shadow.entity.annotation.AutoSave;
import com.shadow.entity.annotation.CacheIndex;
import com.shadow.util.codec.JsonUtil;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author nevermore on 2015/1/5
 */
@Entity
public class Item extends CacheableEntity<Long> {

    @Id
    private Long id;

    private int count;

    @CacheIndex
    private long playerId;

    public static Item valueOf(long id) {
        Item i = new Item();
        i.id = id;
        return i;
    }

    @AutoSave(withIndexValueChanged = true)
    public void increase() {
        count++;
        playerId = 1;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }

    public void setId(Long id) {
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
