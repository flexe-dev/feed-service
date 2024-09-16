package com.flexe.feedservice.Entity.relationships;

import com.flexe.feedservice.Entity.Nodes.PostNode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PostInteractionRelationship {

    private Long id;
    private Date timestamp;
    private PostNode post;

    public PostInteractionRelationship(){}

}

