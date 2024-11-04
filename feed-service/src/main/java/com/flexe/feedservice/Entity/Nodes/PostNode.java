package com.flexe.feedservice.Entity.Nodes;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class PostNode {
    //Identifiers
    private String postId;
    private String userId;
    private PostType type;
    private Date postDate;

    //Metadata
    private List<String> tags;
    public enum PostType{
        TEXT,
        MEDIA
    }

    public PostNode(){

    }

}