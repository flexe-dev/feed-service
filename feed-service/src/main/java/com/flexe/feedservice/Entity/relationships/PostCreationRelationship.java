package com.flexe.feedservice.Entity.relationships;

import com.flexe.feedservice.Entity.Nodes.PostNode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class PostCreationRelationship {

    private Long id;

    @Getter
    @Setter
    private Date createdAt;

    @Getter
    @Setter
    private PostNode post;

    public PostCreationRelationship(){
    }

    public PostCreationRelationship(PostNode postNode){
        this.post = postNode;
        this.createdAt = new Date();
    }

}