package com.shadow.test.module.player.entity;

import com.shadow.entity.IEntity;
import com.shadow.test.module.player.model.Country;
import com.shadow.test.module.player.model.Gender;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * @author nevermore on 2015/3/1
 */
@Entity
public class Player implements IEntity<Long> {

    public static final String QUERY_NAME_AND_ID = "Player.name2Id";

    @Id
    private Long id;
    private String name;
    private Gender gender = Gender.MALE;
    private Country country = Country.ONE;
    private int level;
    private long exp;

    public static Player valueOf(Long id, String playerName, Gender gender, Country country) {
        Player player = new Player();
        player.id = id;
        player.name = playerName;
        player.gender = gender;
        player.country = country;
        return player;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Gender getGender() {
        return gender;
    }

    public Country getCountry() {
        return country;
    }

    public int getLevel() {
        return level;
    }

    public long getExp() {
        return exp;
    }
}
