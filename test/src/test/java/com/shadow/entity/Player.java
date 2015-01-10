package com.shadow.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author nevermore on 2015/1/10
 */
@Entity
public class Player extends CacheableEntity<Long> {

    @Id
    private Long id;

    public static Player valueOf(long id) {
        Player p = new Player();
        p.id = id;
        return p;
    }

    @Override
    public Long getId() {
        return id;
    }
}
