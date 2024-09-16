package com.flexe.feedservice.Entity.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Setter
@Getter
public class UserProfile{
    private String id;
    private String userId;
    private String job;
    private Integer followers;
    private Integer following;
    private String company;
    private String location;
    private UserExternalLinks external;
    private String bio;
    private String pronouns;

    public UserProfile() {
    }

}