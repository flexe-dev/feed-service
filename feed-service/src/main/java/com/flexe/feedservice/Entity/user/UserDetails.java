package com.flexe.feedservice.Entity.user;

import com.flexe.feedservice.Entity.Nodes.UserNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetails {

    private String userId;
    private String name;
    private String image;
    private String username;
    private String job;

    public UserDetails(){

    }

    public UserDetails(UserDisplay user){
        this.userId = user.getUser().getId();
        this.name = user.getUser().getName();
        this.username = user.getUser().getUsername();
        this.image = user.getUser().getImage();
        this.job = user.getProfile().getJob();
    }

}
