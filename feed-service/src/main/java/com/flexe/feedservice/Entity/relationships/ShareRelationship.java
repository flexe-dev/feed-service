package com.flexe.feedservice.Entity.relationships;

import com.flexe.feedservice.Entity.Nodes.UserNode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ShareRelationship<T> {

    private Long id;
    private Date timeStamp;
    private UserNode receiver;
    private T root;

    public ShareRelationship(){

    }
}

