package com.flexe.feedservice.Entity.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.UUID;


@Getter
@Setter
public class User{
    private String id;
    private String email;
    private Date emailVerified;
    private String username;
    private String name;
    private String image;
    private Boolean onboarded;

    public User() {
    }

}