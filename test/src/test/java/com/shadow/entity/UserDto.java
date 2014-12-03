package com.shadow.entity;

import java.util.Date;

/**
 * @author nevermore on 2014/12/3.
 */
public class UserDto {
    private int id;
    private String username;
    private String password;
    private Date date;

    public static UserDto valueOf(User user) {
        UserDto dto = new UserDto();
        dto.id = user.getId();
        dto.username = user.getUsername();
        dto.password = user.getPassword();
        dto.date = user.getDate();
        return dto;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Date getDate() {
        return date;
    }
}
