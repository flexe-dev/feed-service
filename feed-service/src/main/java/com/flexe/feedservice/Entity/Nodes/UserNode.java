package com.flexe.feedservice.Entity.Nodes;


import com.flexe.feedservice.Entity.relationships.PostCreationRelationship;
import com.flexe.feedservice.Entity.relationships.PostInteractionRelationship;
import com.flexe.feedservice.Entity.relationships.PostShareRelationship;
import com.flexe.feedservice.Entity.relationships.UserInteractionRelationship;
import com.flexe.feedservice.Entity.user.UserDetails;
import com.flexe.feedservice.Entity.user.UserDisplay;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserNode extends UserDetails {
    private List<PostCreationRelationship> userPosts = new ArrayList<>();
    private List<UserInteractionRelationship> following = new ArrayList<>();
    private List<UserInteractionRelationship> followers = new ArrayList<>();
    private List<UserInteractionRelationship> blockedUsers = new ArrayList<>();
    private List<PostInteractionRelationship> likedPosts = new ArrayList<>();
    private List<PostInteractionRelationship> savedPosts = new ArrayList<>();
    private List<PostInteractionRelationship> repostedPosts = new ArrayList<>();
    private List<PostShareRelationship> sharedPosts = new ArrayList<>();

    public UserNode(){
    }

    public UserNode(UserDisplay user){
        super(user);
    }
}