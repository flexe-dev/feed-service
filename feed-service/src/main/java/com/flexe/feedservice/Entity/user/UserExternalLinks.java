package com.flexe.feedservice.Entity.user;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserExternalLinks{
    private String facebook;
    private String twitter;
    private String linkedin;
    private String github;
    private String website;

    public UserExternalLinks() {
    }

}