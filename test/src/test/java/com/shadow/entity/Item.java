package com.shadow.entity;

import com.shadow.common.util.codec.JsonUtil;
import com.shadow.entity.cache.annotation.AutoSave;
import com.shadow.entity.cache.annotation.CacheIndex;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * @author nevermore on 2015/1/5
 */
@Entity
public class Item implements IEntity<Long> {

    @Id
    private Long id;

    private int count;

    @CacheIndex
    private long playerId;

    public static Item valueOf(long id, long playerId) {
        Item i = new Item();
        i.id = id;
        i.playerId = playerId;
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
